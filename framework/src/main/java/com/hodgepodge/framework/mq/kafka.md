# 延迟队列 时间轮
* TimingWheel是kafka时间轮的实现，内部包含了一个TimerTaskList数组，每个数组包含了一些链表组成的TimerTaskEntry事件，每个TimerTaskList表示时间轮的某一格，这一格的时间跨度为tickMs，同一个TimerTaskList中的事件都是相差在一个tickMs跨度内的，整个时间轮的时间跨度为interval = tickMs * wheelSize，改时间轮能处理的时间范围在cuurentTime到currentTime + interval之间的事件。
* 当添加一个时间他的超时时间大于整个时间轮的跨度时， expiration >= currentTime + interval，则会将该事件向上级传递，上级的tickMs是下级的interval，传递直到某一个时间轮满足expiration < currentTime + interval，
* 然后计算对应位于哪一格，然后将事件放进去，重新设置超时时间，然后放进jdk延迟队列
```
    else if (expiration < currentTime + interval) {
    // Put in its own bucket
    val virtualId = expiration / tickMs
    val bucket = buckets((virtualId % wheelSize.toLong).toInt)
    bucket.add(timerTaskEntry)
      // Set the bucket expiration time
      if (bucket.setExpiration(virtualId * tickMs)) {
        // The bucket needs to be enqueued because it was an expired bucket
        // We only need to enqueue the bucket when its expiration time has changed, i.e. the wheel has advanced
        // and the previous buckets gets reused; further calls to set the expiration within the same wheel cycle
        // will pass in the same value and hence return false, thus the bucket with the same expiration will not
        // be enqueued multiple times.
        queue.offer(bucket)
      }
```     
* SystemTimer会取出queue中的TimerTaskList，根据expiration将currentTime往前推进，然后把里面所有的事件重新放进时间轮中，因为ct推进了，所以有些事件会在第0格，表示到期了，直接返回。
* else if (expiration < currentTime + tickMs) {
* 然后将任务提交到java线程池中处理。

# 生产者
* ProducerInterceptor.onSend()方法对消息进行拦截处理
* 调用waitOnMetadata方法获取集群元数据
* 将topic添加到metadata的topics集合中，获取集群中分区数cluster.partitionCountForTopic(topic);，
* 如果不满足则会调用metadata.requestUpdate();将needUpdate设置为true，唤醒sender线程，
* 阻塞直到集群版本号大于当前版本号metadata.awaitUpdate(version, remainingWaitMs);
* 然后返回集群信息
* 序列化key和value
* 调用partition方法选择合适的分区
* 消息没有key则随机选择，有key则使用murmur2hash算出分区
* 调用accumulator.append方法扔进消息累加器，返回RecordAppendResult
* 消息累加器维护了一个map, ConcurrentMap<TopicPartition, Deque<ProducerBatch>>
* 根据消息对应的分区，取出对应的双端队列，没有则创建
* 对队列加锁，取出最后的一块ProducerBatch，然后调用tryAppend追加record，返回不为null得到一个future（FutureRecordMetadata）表示成功，封装成RecordAppendResult返回，然后解锁
* 如果上诉失败，则先申请内存 buffer = free.allocate(size, maxTimeToBlock);
* 然后加锁，这时候也许其他线程已经创建了新的ProducerBatch，所以在做一遍上面的操作（加锁-解锁那一段），如果成功解锁，在finally块释放buffer，否则创建一个ProducerBatch，然后调用tryAppend，将新建的ProducerBatch放到队尾，这时候因为还没有释放锁，所以肯定能加成功。
* 最后返回RecordAppendResult。
* 当我们使用RecordAppendResult.get()的时候会阻塞，什么时候能返回呢？
* FutureRecordMetadata 和ProducerBatch 都引用了同一个ProduceRequestResult，这个ProduceRequestResult维护了一个
* CountDownLatch，RecordAppendResult.get()阻塞，等到当sender线程完成消息的发送，会调用ProducerBatch的done方法，然后调用ProduceRequestResult.done()唤醒（在这之前会先回调callback），这时候使用ProduceRequestResult中的信息封装成RecordMetadata返回。
* 唤醒sender线程，由sender线程发送累加器中缓存的消息
* 如果返回的结果if (result.batchIsFull || result.newBatchCreated) {，则唤醒sender线程
* 返回RecordAppendResult中的future

# 消息可靠性 数据一致性
* AR Assigned Replicas 所有副本
* ISR是AR中的一个子集，由leader维护ISR列表，follower从leader同步数据有一些延迟（包括延迟时间replica.lag.time.max.ms和延迟条数replica.lag.max.messages两个维度, 当前最新的版本0.10.x中只支持replica.lag.time.max.ms这个维度），任意一个超过阈值都会把follower剔除出ISR,存入OSR（Outof-Sync Replicas）列表，新加入的follower也会先存放在OSR中。
* AR=ISR+OSR。

* HW HighWatermark consumer能够看到的此partition的位置 取一个partition对应的ISR中最小的LEO作为HW，consumer最多只能消费到HW所在的位置
* LEO LogEndOffset的缩写，表示每个partition的log最后一条Message的位置
![](../../../../../resources/pic/kafka/kafka1.png)
* request.required.acks
* 1（默认）：这意味着producer在ISR中的leader已成功收到数据并得到确认。如果leader宕机了，则会丢失数据。
* 0：这意味着producer无需等待来自broker的确认而继续发送下一批消息。这种情况下数据传输效率最高，但是数据可靠性确是最低的。
* -1：producer需要等待ISR中的所有follower都确认接收到数据后才算一次发送完成，可靠性最高。但是这样也不能保证数据不丢失，比如当ISR中只有leader时（前面ISR那一节讲到，ISR中的成员由于某些情况会增加也会减少，最少就只剩一个leader），这样就变成了acks=1的情况。所以需要将min.insync.replicas至少设置为2。
* min.insync.replicas 设定ISR中的最小副本数是多少, 默认值为1，当且仅当request.required.acks参数设置为-1时，此参数才生效。
* unclean.leader.election.enable=false时，leader只能从ISR中选举。

## 1.什么时候HW追上LEO, 使得消费者能够消费到对应的数据，如7 8 9 10。
* 当所有ISR集合中的分区都同步到了7, leader才会将HW设置到7的offset, 当ISR中只有leader这个主分区时, 意味着不需要副本的同步就能直接将HW往后移动, 此时如果主分区异常， 选择
* Kafka的复制机制既不是完全的同步复制，也不是单纯的异步复制。当request.required.acks=1就是有多个从的异步复制, request.required.acks=-1 并且min.insync.replicas>=2相当于同步复制, 起码有一主一从。
* 本质上也是cap中, ap和cp的权衡。
## 2.request.required.acks=-1  min.insync.replicas = 2 ISR集合中少于2会怎么样？
* 如果ISR中的副本数少于min.insync.replicas配置的数量时，客户端进行写操作会返回异常：org.apache.kafka.common.errors.NotEnoughReplicasExceptoin: Messages are rejected since there are fewer in-sync replicas than required。生产者需要使用同步的api, 对该异常进行处理, 如反馈系统异常给上游。但此时假如ISR集合中只存在一个分区的话还是可以read的。
## 3.leader crash
* leader crash之后，假如unclean.leader.election.enable=false 必须从ISR集合中选择作为新的leader, 如果配合request.required.acks=-1 min.insync.replicas>=2, 则不会数据丢失。
## 4.request.required.acks=-1  min.insync.replicas = 2 假如主分区写入数据之后 副本分区没来得及同步或者同步一半主分区就crash

![](../../../../../resources/pic/kafka/kafka2.png)
* 生产者使用同步的api, 主分区写入4 5消息, 此时还没有响应给客户端结果，需要等待所有ISR集合都同步完成才能返回给客户端结果。
* 然后副本1同步了4 5消息, LEO为5, 副本2同步4消息, LEO为4, 三个分区的HW都为3,当重新选举leader之后会进行截断到HW。


CMS GC发生的场景

扩容缩容会触发 CMS GC
Old 区达到回收阈值
MetaSpace 空间不足
Young 区晋升失败
大对象担保失败

只单独回收Old区的只有CMS GC
CMS中需要设置了-XX:+CMSScavengeBeforeRemark才会在发生CMS的remark阶段触发一次YGC

一般CMS的GC耗时 80%都在remark阶段


cms 出现allocation failure 表示fullgc

CMS GC 共分为 Background 和 Foreground 两种模式，前者就是我们常规理解中的并发收集，可以不影响正常的业务线程运行，但 Foreground Collector 却有很大的差异，他会进行一次压缩式 GC。此压缩式 GC 使用的是跟 Serial Old GC 一样的 LISP2 算法，其使用 Mark-Compact 来做 Full GC，一般称之为 MSC（Mark-Sweep-Compact），它收集的范围是 Java 堆的 Young 区和 Old 区以及 MetaSpace。由上面的算法章节中我们知道 compact 的代价是巨大的，那么使用 Foreground Collector 时将会带来非常长的 STW。如果在应用程序中 System.gc 被频繁调用，那就非常危险了。

一般的建议是cms阶段两次STW的时间不超过200ms
一般CMS的GC耗时 80%都在remark阶段
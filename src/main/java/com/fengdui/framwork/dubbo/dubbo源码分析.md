# dubbo服务暴露过程
* serviceconfig#doExportUrlsFor1Protocol方法会先做一个代理，ref就是业务实现类， 
```
* Invoker<?> invoker = proxyFactory.getInvoker(ref, (Class) interfaceClass, registryURL.addParameterAndEncoded(Constants.EXPORT_KEY, url.toFullString()));
```
* 使用代理 javassist或者jdk代理转换成一个invoker
* 接下来会使用protocol.export(wrapperInvoker); 将invoker转换成exporter，
* 注意一下ServiceConfig里面的protocol是ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
* 返回的结果是protocol&Adaptive ->protocolListenerWraper->protocolFilterWraper->DubboProtocol
* protocolFilterWraper#export方法会调用buildInvokerChain，即这部分
* DubboProtocol的export方法将invoker转换成了exporter，然后返回，即这部分
* DubboProtocol内部维护了一个Map<String, ExchangeServer> serverMap，调用server = Exchangers.bind(url, requestHandler); 得到一个ExchangeServer，它的子类有ExchangeClient和ExchangeServer2种，内部是通过HeaderExchanger得到的。
* HeaderExchangeServer内部有一个server，默认是nettyServer，它是调用Transport生成的，即这部分
* 总体来说，就是DubboProtocol维护了HeaderExchangeServer，ExchangeServer维护了一个nettyServer。
* nettyserver的父类AbstractEndpoint维护了一个codec2

* 还是以dubbo协议为例，默认使用的是DubboCountCodec，它内部又使用了DubboCodec，encode和decode方法后获取
* Serialization进行序列化和反序列化，CodecSupport.getSerialization(channel.getUrl()); 

# dubbo消费者流程
* ReferenceConfig#createProxy 创建代理
* refprotocol#refer 返回invoker
* refprotocol为Protocol$Adpative，是动态生成的
```
com.alibaba.dubbo.rpc.Protocol extension = (com.alibaba.dubbo.rpc.Protocol)ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.Protocol.class).getExtension(extName);  
return extension.refer(arg0, arg1);
```
* 因此实际上又获取了一个protocol，然后调用它的refer方法，这个protocol是ProtocolListenerWrapper
* 简单说一下这里ExtensionLoader#getExtension 参数是registry，首先从cachedInstances缓存获取，没有使用createExtension创建
* createExtension方法也会从EXTENSION_INSTANCES缓存中获取，没有的话会实例化一个（这里实力化了一个RegistryProtocol）
* 然后调用injectExtension方法进行依赖注入
* 然后使用cachedWrapperClasses包装，这里cachedWrapperClasses有两个
* ProtocolListenerWrapper#refer
```
public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
    if (Constants.REGISTRY_PROTOCOL.equals(url.getProtocol())) {
    return protocol.refer(type, url);
}
return new ListenerInvokerWrapper<T>(protocol.refer(type, url),
    Collections.unmodifiableList(
    ExtensionLoader.getExtensionLoader(InvokerListener.class)
    .getActivateExtension(url, Constants.INVOKER_LISTENER_KEY)));
}
```

* 实际上又调用了ProtocolFilterWrapper#refer
```
public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
    if (Constants.REGISTRY_PROTOCOL.equals(url.getProtocol())) {
        return protocol.refer(type, url);
    }
    return buildInvokerChain(protocol.refer(type, url), Constants.REFERENCE_FILTER_KEY, Constants.CONSUMER);
}
```
* 又调用了RegistryProtocol#refer
* 这里创建了一个RegistryDirectory subscribe开始订阅服务，实际上还是使用了ZooKeeperRegistry注册。
* 请求过程
* InvokerInvocationHandler#invoke方法
* MockClusterInvoker#invoke方法
* AbstractClusterInvoker#invoke方法 这里会获取负载均衡类loadbalance
* 根据配置例如FailoverClusterInvoker#doInvoke方法 这里会做负载均衡
* RegistryDirectory的静态内部类InvokerDelegete#invoke 这里是个拦截器链 是在ProtocolFilterWrapper类的buildInvokerChain方法创建的
* 我这里经过了几个filter 最后到达了DubboInvoker(dubbo协议)
* client一个线程调用远程接口，会生成一个唯一的id，并且封装调用信息和处理结果的回调对象callback成一个object，存在concurrenthashmap，使用iosession.write(connrequest)异步发送出去，当前线程再使用callback的get()方法获取远程返回的结果，在get方法内部，使用synchronize获取回调对象callback的锁，再检车是否已经获取结果，如果没有，则调用callback的wait()方法，释放callback的锁，线程等待。
* 服务端将结果发送client后，clientsocket连接上监听线程，获取id，再从concurrenthashmap找到object，并且将结果存在callback里面，监听线程获取callback的锁，会notifyall(),继续执行

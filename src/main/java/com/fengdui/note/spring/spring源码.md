# spring aop原理
* <aop:aspectj-autoproxy proxy-target-class="true" expose-proxy="true"/>
* AopNamespaceHandler 注册AspectJAutoProxyBeanDefinitionParser，
* AspectJAutoProxyBeanDefinitionParser又注册了AnnotationAwareAspectJAutoProxyCreator。
* AnnotationAwareAspectJAutoProxyCreator实现了BeanPostProcessor，实现此接口的类会在获取bean的时候调用postProcessAfterInitialization方法，
* 继而又会调用wrapIfNecessary方法。
* wrapIfNecessary方法：
  * 1 getAdvicesAndAdvisorsForBean 获取增强器, 获取增强器分两步，第一步获得所有的增强器，第二部获取适合当前类的增强器，
  * 获得所有的增强器
    * 调用过程是委托了aspectJAdvisorsBuilder找出所有的增强器，然后aspectJAdvisorsBuilder又使用了advisorFactory获取增强器。
    * 在advisorFactory的getAdvisor方法中，根据不同的注解创建了不同的增强器，before，after等等。
    * 然后使用InstantiationModelAwarePointcutAdvisorImpl封装起来。
  * 获得适合的增强器
    * 获取到所有的增强器之后在使用aoputils的canApply找出适合的增强器。
      * 如果advisor是PointcutAdvisor子类，就会使用这个advisor的pointcut来匹配， 
      * 例如spring的事务BeanFactoryTransactionAttributeSourceAdvisor，它会使用TransactionAttributeSourcePointcut来查看是否使用事
      * 务，如果可以就使用这个advisor。

  * 2 createProxy 创建代理
  * 创建代理类是在ProxyFactory的getProxy方法中，其中又调用了AopProxyFactory来创建aopProxy（例如JdkDynamicAopProxy或者
  * ObjenesisCglibAopProxy），AopProxyFactory 默认的实现类是DefaultAopProxyFactory，得到aopProxy之后在使用aopProxy创建代理。
  * 熟悉动态代理的都知道，代理类会调用invoke方法，invoke方法主要做了以下2个工作。
  * 2.1.获得拦截器链 
    * List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass); 
    * advised是之前创建的proxyfactory，它又委托了AdvisorChainFactory，AdvisorChainFactory只有一个默认的实现
    * 类，DefaultAdvisorChainFactory，它将advice封装成MethodInterceptor，之后又封装成InterceptorAndDynamicMethodMatcher。
  * 2.2 使用ReflectiveMethodInvocation封装拦截器链，然后调用。
  * invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
  * // Proceed to the joinpoint through the interceptor chain.
  * retVal = invocation.proceed();
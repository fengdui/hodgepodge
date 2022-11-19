# zuul+feign+consul
* Zuul的核心是一系列过滤器，可以在Http请求的发起和响应返回期间执行一系列的过滤器。
* SimpleRouteLocator是@EnableZuulServer注入的，DiscoveryClientRouteLocator是@EnableZuulProxy注入的
* RibbonConsulAutoConfiguration
* RibbonRoutingFilter
* ConsulServerList
* serverListQueryTags
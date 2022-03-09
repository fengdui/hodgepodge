1.Repository并不与某种持久化机制绑定，一个被抽象出来的Repository向外暴露的功能“接口”始终是向领域模型提供聚合根对象 控制反转
2.聚合根一定是实体对象，但是并不是所有实体对象都是聚合根，同时聚合根还可以拥有其他子实体对象。聚合根的ID在整个软件系统中全局唯一，而其下的子实体对象的ID只需在单个聚合根下唯一即可
3.创建聚合根通常通过设计模式中的工厂(Factory)模式完成
4.聚合根是业务逻辑的主要载体，也就是说业务逻辑的实现代码应该尽量地放在聚合根或者聚合根的边界之内。但有些业务逻辑并不适合于放在聚合根上，在这种的情况下，我们引入领域服务。比如生成工单号的逻辑。将聚合根作为入参。
5.应用层入参command对象, Command对象只是一种类型的DTO对象，它封装了客户端发过来的请求数据。
6.读模式
6.1 先使用资源库获取聚合根, 然后调用领域服务转成对应的vo.
6.2直接使用sql,不使用资源库，领域服务直接查询，不借助领域模型。微软也提倡过这种方式
    https://docs.microsoft.com/en-us/dotnet/architecture/microservices/microservice-ddd-cqrs-patterns/apply-simplified-microservice-cqrs-ddd-patterns
6.3 cqrs    
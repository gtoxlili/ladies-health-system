## 毕设待吹事项

> 用于项目中的技术点以用于届时写毕设论文

##### 完善的权限控制机制 (基于Spring Security)

1. 基于 `ROLE` 的接口权限控制
2. 基于 JWT 的鉴权机制 (利好分布式)

##### [基于贫血模型的MVC框架](https://zh.wikipedia.org/zh-cn/MVC)

> 数据流向  
> DTO -> Controller -> Service -> DAO -> PO -> Service -> Controller -> VO

##### [ORM](https://en.wikipedia.org/wiki/Object%E2%80%93relational_mapping)

> 技术：JPA  
> 主要目的是解决在数据库和程序语言间的不匹配问题，通过使用 ORM 技术，可以使得程序员在编写代码时使用对象的形式来操作数据库，而不需要手动编写
> SQL 语句。   
> 使用 ORM 技术可以提高代码的可维护性和可读性，因为程序员不需要了解复杂的数据库操作，只需要关注与业务逻辑相关的代码。同时，ORM
> 还可以解决数据库的跨平台问题，因为 ORM 会根据不同的数据库系统自动生成合适的 SQL 语句。

- 定义了一个基础的 PO 类，其中包含 ID、创建时间、更新时间以及创建人ID等基础字段，并通过`@JpaAuditing`完成自动填充
- 逻辑删除与数据库唯一约束冲突问题 (delete_time)

##### 接口设计

1. 基于 REST API 的接口设计风格
2. 通过 Validation 保证 DTO 以及 Controller层其余入参 的合法性
3. 制定统一的返回格式 `Res` (包含状态码、状态信息、数据)
4. 通过 AOP 保证 Service 层的幂等性
5. 对于所有`行为`接口，提供了 Rollback 方法

##### 杂项

1. 完善的全局异常处理机制
2. 基于 Swagger 的 API 文档生成

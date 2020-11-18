[![Build Status](https://travis-ci.org/sqshq/PiggyMetrics.svg?branch=master)](https://travis-ci.org/sqshq/PiggyMetrics)
[![codecov.io](https://codecov.io/github/sqshq/PiggyMetrics/coverage.svg?branch=master)](https://codecov.io/github/sqshq/PiggyMetrics?branch=master)
[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/sqshq/PiggyMetrics/blob/master/LICENCE)
[![Join the chat at https://gitter.im/sqshq/PiggyMetrics](https://badges.gitter.im/sqshq/PiggyMetrics.svg)](https://gitter.im/sqshq/PiggyMetrics?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Piggy Metrics

**一个简单的记账系统**

这是一个概念验证应用程序，它使用Spring Boot、Spring Cloud和Docker演示了微服务体系结构模式。顺便说一下，它的用户界面非常整洁.

![](https://cloud.githubusercontent.com/assets/6069066/13864234/442d6faa-ecb9-11e5-9929-34a9539acde0.png)
![Piggy Metrics](https://cloud.githubusercontent.com/assets/6069066/13830155/572e7552-ebe4-11e5-918f-637a49dff9a2.gif)

## Functional services(功能服务)

PiggyMetrics被分解为三个核心微服务。它们都是独立部署的应用程序，围绕特定的业务领域组织起来。

<img width="880" alt="Functional services" src="https://cloud.githubusercontent.com/assets/6069066/13900465/730f2922-ee20-11e5-8df0-e7b51c668847.png">

#### Account service(认证服务)
包含一般用户输入逻辑和验证：收入/支出项目，储蓄和帐户设置

Method	| Path	| Description	| User authenticated	| Available from UI
------------- | ------------------------- | ------------- |:-------------:|:----------------:|
GET	| /accounts/{account}	| Get specified account data	|  | 	
GET	| /accounts/current	| Get current account data	| × | ×
GET	| /accounts/demo	| Get demo account data (pre-filled incomes/expenses items, etc)	|   | 	×
PUT	| /accounts/current	| Save current account data	| × | ×
POST	| /accounts/	| Register new account	|   | ×


#### Statistics service(统计服务)
对主要统计参数执行计算，并获取每个帐户的时间序列。数据点包含标准化为基本货币和时间段的值。该数据用于跟踪帐户生命周期内的现金流动态。
Method	| Path	| Description	| User authenticated	| Available from UI
------------- | ------------------------- | ------------- |:-------------:|:----------------:|
GET	| /statistics/{account}	| Get specified account statistics	          |  | 	
GET	| /statistics/current	| Get current account statistics	| × | × 
GET	| /statistics/demo	| Get demo account statistics	|   | × 
PUT	| /statistics/{account}	| Create or update time series datapoint for specified account	|   | 


#### Notification service(通知服务)
存储用户联系信息和通知设置(如提醒和备份频率)。预定工作人员从其他服务收集所需信息，并向订阅的客户发送电子邮件。
Method	| Path	| Description	| User authenticated	| Available from UI
------------- | ------------------------- | ------------- |:-------------:|:----------------:|
GET	| /notifications/settings/current	| Get current account notification settings	| × | ×	
PUT	| /notifications/settings/current	| Save current account notification settings	| × | ×

#### Notes
- 每个微服务都有自己的数据库，因此无法绕过API并直接访问持久性数据。
- 在这个项目中，我将MongoDB用作每个服务的主数据库。 拥有多语言持久性体系结构（选择最适合服务需求的db类型）也可能是有意义的。
- 服务到服务的通信已大大简化：微服务仅使用同步REST API进行通话。 实际系统中的常见做法是使用交互样式的组合。 例如，执行同步的GET请求以检索数据，并通过Message Broker使用异步方法进行创建/更新操作，以使服务和消息缓冲脱钩。 但是，这将我们带到了最终的一致性世界。 REST API. Common practice in a real-world systems is to use combination of interaction styles. For example, perform synchronous GET request to retrieve data and use asynchronous approach via Message broker for create/update operations in order to decouple services and buffer messages. However, this brings us to the [eventual consistency](http://martinfowler.com/articles/microservice-trade-offs.html#consistency) world.

## Infrastructure services(基础设施服务)
在分布式系统中有一组常见的模式，它们可以帮助我们使所描述的核心服务工作。Spring cloud提供了强大的工具来增强Spring引导应用程序的行为，从而实现这些模式。我将简要介绍它们。
<img width="880" alt="Infrastructure services" src="https://cloud.githubusercontent.com/assets/6069066/13906840/365c0d94-eefa-11e5-90ad-9d74804ca412.png">
### Config service(统一配置中心)
[Spring Cloud Config](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html) is horizontally scalable centralized configuration service for distributed systems. It uses a pluggable repository layer that currently supports local storage, Git, and Subversion. 

在此项目中，我使用本机配置文件，该配置文件仅从本地类路径加载配置文件。 您可以在Config服务资源中看到共享目录。 现在，当Notification-service请求其配置时，Config服务将使用shared / notification-service.yml和shared / application.yml（在所有客户端应用程序之间共享）进行响应。
##### Client 使用
只需使用spring-cloud-starter-config依赖关系构建Spring Boot应用程序，其余的工作就由自动配置完成。

现在，您在应用程序中不需要任何嵌入式属性。 只需为bootstrap.yml提供应用程序名称和配置服务网址:
```yml
spring:
  application:
    name: notification-service
  cloud:
    config:
      uri: http://config:8888
      fail-fast: true
```

##### 使用Spring Cloud Config，您可以动态更改应用程序配置
例如，EmailService bean用@RefreshScope注释。 这意味着，您可以更改电子邮件文本和主题，而无需重建和重新启动Notification Service应用程序。

首先，在配置服务器中更改必需的属性。 然后，执行对通知服务的刷新请求：curl -H“授权：承载＃token＃” -XPOST http://127.0.0.1:8000/notifications/refresh

另外，您可以使用Repository webhooks自动执行此过程

##### Notes
- 但是，动态刷新存在一些限制。 @RefreshScope不适用于@Configuration类，也不影响@Scheduled方法
  
- fail-fast属性意味着，如果Spring Boot应用程序无法连接到Config Service，它将立即启动失败。
  
- 以下是重要的安全说明

### Auth service(认证服务)
授权职责已完全提取到单独的服务器，该服务器为后端资源服务授予OAuth2令牌。 Auth Server用于用户授权以及外围内部安全的机器对机器通信。

在此项目中，我将密码凭据授予类型用于用户授权（因为仅本机PiggyMetrics UI使用它），而将客户端凭据授予用于微服务授权。

Spring Cloud Security提供了方便的批注和自动配置，以使从服务器和客户端两者都真正易于实现。 您可以在文档中了解更多信息，并在Auth Server代码中查看配置详细信息。

从客户端来看，一切工作都与传统的基于会话的授权完全相同。 您可以从请求中检索Principal对象，并使用基于表达式的访问控制和@PreAuthorize批注检查用户的角色和其他内容。

PiggyMetrics中的每个客户端（帐户服务，统计服务，通知服务和浏览器）都有一个范围：用于后端服务的服务器，以及用于浏览器的ui。 因此，我们还可以保护控制器免受外部访问，例如:

``` java
@PreAuthorize("#oauth2.hasScope('server')")
@RequestMapping(value = "accounts/{name}", method = RequestMethod.GET)
public List<DataPoint> getStatisticsByAccountName(@PathVariable String name) {
	return statisticsService.findByAccountName(name);
}
```

### API Gateway(API 网关)
如您所见，有三个核心服务，它们向客户端公开外部API。 在现实世界的系统中，此数字以及整个系统的复杂性都可以非常快速地增长。 实际上，渲染一个复杂的网页可能涉及数百种服务。

从理论上讲，客户端可以直接向每个微服务发出请求。 但是很明显，此选项存在挑战和局限性，例如必须知道所有端点地址，分别对每条信息执行http请求，将结果合并到客户端。 另一个问题是可能在后端使用的非Web友好协议。

通常，更好的方法是使用API​​网关。 它是系统的单个入口点，用于通过将请求路由到适当的后端服务或通过调用多个后端服务并汇总结果来处理请求。 此外，它还可用于身份验证，见解，压力和金丝雀测试，服务迁移，静态响应处理，主动流量管理。

Netflix开源了这种边缘服务，现在借助Spring Cloud，我们可以使用一个@EnableZuulProxy注释启用它。 在这个项目中，我使用Zuul存储静态内容（ui应用程序）并将请求路由到适当的微服务。 这是通知服务的基于前缀的简单路由配置
```yml
zuul:
  routes:
    notification-service:
        path: /notifications/**
        serviceId: notification-service
        stripPrefix: false

```

这意味着所有以/ notifications开头的请求都将被路由到Notification Service。 如您所见，没有硬编码的地址。 Zuul使用服务发现机制来定位Notification服务实例以及断路器和负载均衡器，如下所述

### Service discovery(服务发现)
另一个众所周知的体系结构模式是服务发现。 它允许自动检测服务实例的网络位置，这些服务实例由于自动缩放，故障和升级而可能具有动态分配的地址。

服务发现的关键部分是注册表。 我在此项目中使用Netflix Eureka。 当客户端负责确定可用服务实例的位置（使用注册表服务器）并在它们之间负载均衡请求时，Eureka是客户端发现模式的一个很好的例子。

使用Spring Boot，您可以使用spring-cloud-starter-eureka-server依赖项，@EnableEurekaServer批注和简单的配置属性轻松构建Eureka Registry。

通过@EnableDiscoveryClient注释启用的客户端支持带有应用程序名称的bootstrap.yml:

``` yml
spring:
  application:
    name: notification-service
```

现在，在应用程序启动时，它将向Eureka Server注册并提供元数据，例如主机和端口，运行状况指示器URL，主页等。Eureka从属于服务的每个实例接收心跳消息。 如果心跳在可配置的时间表上进行故障转移，则该实例将从注册表中删除。

此外，Eureka还提供了一个简单的界面，您可以在其中跟踪正在运行的服务和许多可用实例：http：// localhost：8761

### Load balancer, Circuit breaker and Http client(负载平衡器，断路器和Http客户端)

Netflix OSS提供了另一套很棒的工具。

#### Ribbon
Ribbon是客户端负载平衡器，它使您可以对HTTP和TCP客户端的行为进行大量控制。 与传统的负载平衡器相比，无需每次通过线路调用都需要额外的跃点-您可以直接联系所需的服务。

开箱即用，它与Spring Cloud和Service Discovery本地集成。 Eureka Client提供了可用服务器的动态列表，因此Ribbon可以在它们之间进行平衡

#### Hystrix
Hystrix是Circuit Breaker模式的实现，它可以控制通过网络访问的依赖项引起的延迟和故障。 主要思想是在具有大量微服务的分布式环境中停止级联故障。 这有助于快速故障并尽快恢复-自我修复的容错系统的重要方面。

除了断路器控制之外，使用Hystrix，您还可以添加一个后备方法，如果主命令失败，该方法将被调用以获取默认值。

此外，Hystrix会针对每个命令生成有关执行结果和延迟的度量，我们可以使用这些度量来监视系统行为。

#### Feign
Feign是一个声明性的Http客户端，它与Ribbon和Hystrix无缝集成。 实际上，通过一个spring-cloud-starter-feign依赖关系和@EnableFeignClients批注，您就具有了完整的负载均衡器，断路器和Http客户端，并具有明智的随时可用的默认配置。

这是来自Account Service 的示例:

``` java
@FeignClient(name = "statistics-service")
public interface StatisticsServiceClient {

	@RequestMapping(method = RequestMethod.PUT, value = "/statistics/{accountName}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	void updateStatistics(@PathVariable("accountName") String accountName, Account account);

}
```

- 您所需的一切只是一个界面
  
- 您可以在Spring MVC控制器和Feign方法之间共享@RequestMapping部分
  
- 上面的示例仅指定了所需的服务ID-statistics-service，这要归功于通过Eureka的自动发现（但显然您可以使用特定的URL访问任何资源）

### Monitor dashboard (监视器)

在此项目配置中，每个带有Hystrix的微服务都通过Spring Cloud Bus（带有AMQP代理）将指标推送到Turbine。 Monitoring项目只是一个带有Turbine和Hystrix Dashboard的小型Spring引导应用程序。

参见下面的如何启动和运行它。

让我们看看负载下的系统行为：帐户服务调用了统计信息服务，它以不同的模拟延迟进行响应。 响应超时阈值设置为1秒.

<img width="880" src="https://cloud.githubusercontent.com/assets/6069066/14194375/d9a2dd80-f7be-11e5-8bcc-9a2fce753cfe.png">

<img width="212" src="https://cloud.githubusercontent.com/assets/6069066/14127349/21e90026-f628-11e5-83f1-60108cb33490.gif">	| <img width="212" src="https://cloud.githubusercontent.com/assets/6069066/14127348/21e6ed40-f628-11e5-9fa4-ed527bf35129.gif"> | <img width="212" src="https://cloud.githubusercontent.com/assets/6069066/14127346/21b9aaa6-f628-11e5-9bba-aaccab60fd69.gif"> | <img width="212" src="https://cloud.githubusercontent.com/assets/6069066/14127350/21eafe1c-f628-11e5-8ccd-a6b6873c046a.gif">
--- |--- |--- |--- |
| `0 ms delay` | `500 ms delay` | `800 ms delay` | `1100 ms delay`
| 行为良好的系统。 吞吐量约为22个请求/秒。 统计信息服务中活动线程的数量很少。 中位服务时间约为50毫秒 | 活动线程的数量正在增长。 我们可以看到紫色的线程池拒绝数，因此大约有30-40％的错误，但是电路仍处于关闭状态. | 半开状态：失败命令的比率超过50％，断路器将启动。在睡眠窗口时间后，下一个请求将通过. | 00％的请求失败。 电路现在永久断开。 睡眠时间后重试不会再次闭合，因为单个请求太慢.

### Log analysis(日志分析)

在尝试确定分布式环境中的问题时，集中日志记录可能非常有用。 Elasticsearch，Logstash和Kibana堆栈使您可以轻松搜索和分析日志，利用率和网络活动数据

### Distributed tracing(分布式链路追踪)

分析分布式系统中的问题可能很困难，例如，跟踪从一个微服务传播到另一个微服务的请求。 试图找出请求如何在系统中传输可能是一个很大的挑战，特别是如果您对微服务的实现没有任何了解的话。 即使有日志记录，也很难说出哪个动作与单个请求相关。

Spring Cloud Sleuth通过提供对分布式跟踪的支持来解决此问题。 它将两种类型的ID添加到日志记录中：traceId和spanId。 spanId表示基本的工作单位，例如发送HTTP请求。 traceId包含一组形成树状结构的跨度。 例如，对于分布式大数据存储，跟踪可能由PUT请求形成。 通过对每个操作使用traceId和spanId，我们可以知道应用程序在处理请求时的时间和位置，从而使读取日志变得更加容易。

日志如下，注意Slf4J MDC的[appname，traceId，spanId，exportable]条目:


```text
2018-07-26 23:13:49.381  WARN [gateway,3216d0de1384bb4f,3216d0de1384bb4f,false] 2999 --- [nio-4000-exec-1] o.s.c.n.z.f.r.s.AbstractRibbonCommand    : The Hystrix timeout of 20000ms for the command account-service is set lower than the combination of the Ribbon read and connect timeout, 80000ms.
2018-07-26 23:13:49.562  INFO [account-service,3216d0de1384bb4f,404ff09c5cf91d2e,false] 3079 --- [nio-6000-exec-1] c.p.account.service.AccountServiceImpl   : new account has been created: test
```

- *`appname`*: 从属性记录跨度的应用程序的名称 `spring.application.name`
- *`traceId`*: 这是分配给单个请求、作业或操作的ID
- *`spanId`*: 发生的特定操作的ID
- *`exportable`*: 日志是否应该导出到 [Zipkin](https://zipkin.io/)

## Security(安全)

高级安全配置不在此概念验证项目的范围之内。 为了更真实地模拟真实系统，请考虑使用https，JCE密钥库来加密微服务密码和Config服务器属性内容（有关详细信息，请参阅文档）。

## Infrastructure automation(基础设施自动化)

部署微服务及其相互依赖关系，比部署整体应用程序要复杂得多。 拥有完全自动化的基础架构非常重要。 我们可以通过持续交付方法获得以下好处：

- 随时发布软件的能力
- 任何构建都可能最终成为一个版本
- 一次构建工件—根据需要部署

这是一个简单的连续交付工作流，在这个项目中实现

<img width="880" src="https://cloud.githubusercontent.com/assets/6069066/14159789/0dd7a7ce-f6e9-11e5-9fbb-a7fe0f4431e3.png">

在此配置中，Travis CI为每次成功的git push构建标记的图像。 因此，对于Docker Hub上的每个微服务，总会有最新的映像和较旧的映像，并用git commit hash标记。 如果需要，可以轻松部署它们中的任何一个并快速回滚。

## 如何运行所有东西

请记住，您将启动8个Spring Boot应用程序，4个MongoDB实例和RabbitMq。 确保计算机上有4 Gb RAM可用。 但是，您始终可以只运行重要的服务：网关，注册表，配置，身份验证服务和帐户服务。

#### Before you start
- 安装 Docker and Docker Compose.
- 更改.env文件中的环境变量值以提高安全性或保持原样.
- 确保构建项目: `mvn package [-DskipTests]`

#### 生产环境

在此模式下，所有最新的图像都将从Docker Hub中提取。只是docker-compose副本。点击docker-compose up

#### 开发环境
如果您想自己构建映像（例如，对代码进行一些更改），则必须克隆所有存储库并使用maven构建工件。 然后，运行docker-compose -f docker-compose.yml -f docker-compose.dev.yml

docker-compose.dev.yml继承了docker-compose.yml，还具有在本地构建映像并公开所有容器端口的可能性，以方便开发。

如果要在Intellij Idea中启动应用程序，则需要使用EnvFile插件或手动导出.env文件中列出的环境变量（确保已导出它们：printenv）

#### 重要的节点
- http://localhost:80 - Gateway
- http://localhost:8761 - Eureka Dashboard
- http://localhost:9000/hystrix - Hystrix Dashboard (Turbine stream link: `http://turbine-stream-service:8080/turbine/turbine.stream`)
- http://localhost:15672 - RabbitMq management (default login/password: guest/guest)

#### 注意
所有Spring Boot应用程序都需要已运行的Config Server才能启动。 但是由于depends_on docker-compose选项，我们可以同时启动所有容器。

此外，所有应用程序启动后，服务发现机制都需要一些时间。 在实例，Eureka服务器和客户端在其本地缓存中都具有相同的元数据之前，客户端无法发现任何服务，因此可能需要3个心跳。 默认心跳周期为30秒。

## Contributions are welcome!

PiggyMetrics是开源的，非常感谢您的帮助。 随时提出建议并实施改进。
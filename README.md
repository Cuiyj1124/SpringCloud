#  Spring cloud 学习笔记

##  stream的使用

| 组成            | 说明                                                         |
| --------------- | ------------------------------------------------------------ |
| Middleware      | 中间件，目前只支持RabbitMQ和Kafka                            |
| Binder          | 应用与消息中间件之间的封装，通过Binder可以很方便的链接中间件，可以动态的改变消息类型（Kafka的topic和RabbitMQ的exchange） |
| @Input          | 注解标识输入通道，通过输入通道接收到的消息进入应用程序       |
| @Output         | 注解标识输出通道，发布的消息将通过该通道离开应用程序         |
| @StreamListener | 监听队列，用于消费者的队列的消息接收                         |
| @EnableBinding  | 指通道channel和exchange绑定在一起                            |

**（3.1版本弃用四个注解，推荐使用函数编程的方式）**

新版springcloud的函数编程

### 生产者业务代码实现

```yaml
spring:
  application:
    name: cloud-stream-provider

  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  cloud:
    stream:
      bindings: # 服务的整合处理
        myChannel-out-0: # 这个名字是一个通道的名称
          destination: studyExchange # 表示要使用的Exchange名称定义
          content-type: application/json # 设置消息类型，本次为json，文本则设置“text/plain”
```

Service层代码，不使用@EnableBinding，通过streamBridge.send完成发送消息

```java
@Service
public class MessageProvideImpl implements IMessageProvide{


    @Autowired
    private  StreamBridge streamBridge;


    @Override
    public String send() {

        String serial = UUID.randomUUID().toString();
        streamBridge.send("myChannel-out-0", MessageBuilder.withPayload(serial).build());
        System.out.println("发送消息：  "+serial);
        return null;
    }
}
```

Controller层代码调用service层方法

```java
@RestController
@Slf4j
public class SendMessageController {

    @Resource
    private IMessageProvide messageProvide;

    @GetMapping("/sendMessage")
    public String sendMessage(){

        return messageProvide.send();
    }
}
```

### 消费者业务代码实现

```yaml
spring:
  application:
    name: cloud-stream-consumer
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  cloud:
    stream:
      bindings:
        myChannel-in-0: #这个名字是一个通道的名字
          destination: studyExchange #表示要使用Exchange名称定义
          contentType: application/json #设置消息类型，本次为json，文本则设置为“text/plain”
```

接收消息的代码

```java
@Controller
@Slf4j
public class ReceiveMessage {


    @Value("${server.port}")
    private String port;

    @Bean
    public Consumer<String> myChannel(){
        return message -> log.info("消费者一号接收消息："+message+"\t"+"port:   "+port);
    }
}
```

Consumer<T>  是函数式编程的消费者泛型的表示



### 重复消费问题

##### 原理：微服务应用放置于同一个group中，就能够保证消息只会被其中一个应用消费一次

#####      	不同的组是可以重复消费的，同一个组内会发生竞争关系，只有其中一个可以消费

#### 消息持久化

RabbitMQ自带消息持久化，当消费者微服务的分组存在时，当消费者微服务关闭，生产者持续发送消息，当消费者微服务重新启动时，会接收到生产者之前发送的消息。

------

# Spring cloud Alibaba学习

###  NACOS

单机启动nacos指令

` startup.cmd -m standalone`

​	 新版springcloud nacos不再包含Ribbon依赖，所以需要单独引入负载均衡的依赖包

```xml
<!-- nacos的依赖包 -->
<dependency>
        <groupId>com.alibaba.cloud</groupId>
      	<artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<!-- 负载均衡的依赖包 -->
<dependency>
           <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

然后配置文件

``

```yaml
spring:
  application:
    name: nacos-payment-provider
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848 #配置Nacos地址
    loadbalancer:
      enabled: true   #开启负载均衡
```



#####  服务注册到Nacos时，可以选择注册为临时或非临时实例，通过下面的配置来设置

```yaml
cloud:
  nacos:
    discovery:
      ephemeral: false  #非临时实例
```



#### Nacos和eureka的共同点

​	1.都支持服务注册和服务拉取

​	2.都支持服务提供者心跳方式做健康监测

#### Nacos和eureka的区别

​	1.Nacos支持服务端主动检测提供者状态：临时实例采用心跳模式，非临时实例采用主动检测模式

​	2.临时实例心跳不正常会被剔除，非临时实例则不会被剔除

​	3.Nacos支持服务列表变更的消息推送模式，服务列表更新更加及时

​	4.Nacos集群默认采用AP方式，当集群中存在非临时实例时，采用CP模式；Eureka采用AP方式



#### Nacos配置管理

#####   统一配置管理：

​		在nacos客户端完成配置

![image-20230608113756918](D:\study\typoraimage\image-20230608113756918.png)

![image-20230608114038160](D:\study\typoraimage\image-20230608114038160.png)

​    		微服务配置拉取

​				1.引入Nacos的配置管理客户端依赖

```xml
<!--nacos的配置管理依赖-->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

​				2.在resource目录中添加一个bootstrap.yml文件，这个文件是引导文件，优先级高于application.yml

```yaml
spring:
  application:
    name: userservice
  profiles:
    active: dev  #环境
  cloud:
    nacos:
      server-addr: localhost:8848  #nacos地址
      config:
        file-extension: yaml  #文件后缀名
```

​		nacos配置更改后，微服务可以实现热更新，方式：

​			1.通过@Value注解注入，结合@RefreshScope来刷新

​			2.通过@ConfigurationProperties注入，自动刷新

​	注意事项：

- 不是所有的配置都适合放到配置中心，维护起来比较麻烦
- 建议将一些关键参数，需要运行时调整的参数放到nacos配置中心，一般都是自定义配置		

  多服务共享配置：

 优先级： 服务名-profile.yaml > 服务名称.yaml > 本地配置

### Feign

#### 使用步骤：

​	1.引入依赖

​	2.添加@EnableFeignClients注解

​	3.编写FeignClient接口

```java
@FeignClient("userservice")
public interface UserClient {

    @GetMapping("/user/{id}")
    User findById(@PathVariable("id") Long id);
}
```

​	4.使用FeignClient中定义的方法代替RestTemplate

```java
@Autowired
private UserClient userClient;

public Order queryOrderById(Long orderId){

    Order order = orderMapper.findById(orderId);
    //利用Feign进行远程调用
    User user = userClient.findById(order.getUserId());
    order.setUser(user);
    return order;
}
```

#### 自定义Feign配置

|        类型         |       作用       |                          说明                          |
| :-----------------: | :--------------: | :----------------------------------------------------: |
| feign.Logger.Level  |   修改日志级别   |       包含四种不同级别：NONE,BASIC,HEADERS,FULL        |
| feign.codec.Decoder | 响应结果的解析器 | http远程调用的结果做解析，例如解析json字符串为java对象 |
| feign.codec.Encoder |   请求参数编码   |          将请求参数编码，便于通过http请求发送          |
|   feign.Contract    |  支持的注解格式  |                 默认是springMVC的注解                  |
|    feign.Retryer    |   失败重试机制   | 请求失败的重试机制，默认是没有，不过会使用Ribbon的重试 |

一般我们需要配置的就是日志级别

有两种方法配置：

1.配置文件配置

```yaml
feign:
  client:
    config:
      default:
        logger-level: full
```

2.java代码配置

```java
public class DefaultFeignConfiguration {
    @Bean
    public Logger.Level loglevel(){
        
        return Logger.Level.BASIC;
    }
}
```

通过加到类上使它生效

1.如果是全局配置

```java
@EnableFeignClients(defaultConfiguration = DefaultFeignConfiguration.class)
```

2.如果是局部配置

```java
@FeignClient(value = "userservice",configuration = DefaultFeignConfiguration.class)
```

#### Feign的性能优化

 	feign底层的客户端实现：

- URLConnection：默认实现，不支持连接池
- Apache HttpClient：支持连接池
- OKHttp：支持连接池

​     因此优化Feign的性能主要包括：

1. 使用连接池代替默认的URLConnection
2. 日志级别，最好用basic或none

### 统一网关GateWay

#####  网关功能：

- 身份认证和权限校验
- 服务路由、负载均衡
- 请求限流

##### 网关的技术实现：

- gateway
- zuul

Zuul是基于Servlet的实现，属于阻塞式编程，而GateWay则是基于Spring5中提供的WebFlux，属于响应式编程的实现，具有更好的性能

##### 网关搭建步骤：

1. 创建项目，引入依赖

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

2. 配置application.yml

```yaml
server:
  port: 10010
spring:
  application:
    name: gateway
  cloud:
    nacos:
      server-addr: localhost:8848
    gateway:
      routes: #网关路由配置
        - id: user-service #路由id，自定义，唯一即可
          uri: lb://userservice #路由目标地址
          predicates: #路由断言，也就是判断请求是否符合路由规则的条件
            - Path=/user/**
        - id: order-service
          uri: lb://orderservice
          predicates:
            - Path=/order/**
```

##### 路由过滤器 GateWayfilter

 GateWayFilter是网关中提供的一种过滤器，可以对进入网关的请求和微服务返回的响应做处理

##### 全局过滤器 GlobalFilter

  全局过滤器的作用也是处理一切进入网关的请求和微服务相应，与GatewayFilter的作用一样，区别是GateWayfilter通过配置定义，处理逻辑是固定的，而GlobalFilter的逻辑需要自己写代码实现，定义的方式是实现GlobalFilter接口

##### 过滤器执行顺序

- 每一个过滤器都必须指定一个int类型的order值，order值越小，优先级越高，执行顺序越靠前
- GlobalFilter通过实现Ordered接口，或提娜佳@Order注解来指定order值，由我们自己指定
- 路由过滤器和defaultFilter的order由spring指定，默认是按照声明顺序从1递增
- 当过滤器的order值一样时，会按照defaultFilter>路由过滤器>GlobalFilter的顺序执行

##### 跨域请求配置

```yaml
spring:
  cloud:
    gateway:
        globalcors: # 全局的跨域处理
          add-to-simple-url-handler-mapping: true # 解决options请求被拦截问题
          corsConfigurations:
            '[/**]':
              allowedOrigins: # 允许哪些网站的跨域请求
                - "http://localhost:63343"
                - "http://www.leyou.com"
              allowedMethods: # 允许的跨域ajax的请求方式
                - "GET"
                - "POST"
                - "DELETE"
                - "PUT"
                - "OPTIONS"
              allowedHeaders: "*" # 允许在请求中携带的头信息
              allowCredentials: true # 是否允许携带cookie
              maxAge: 360000 # 这次跨域检测的有效期
```



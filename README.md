# KaTool Security鉴权框架文档

![template](http://gd.7n.cdn.wzl1.top/typora/img/template.png)

![SpringBoot-2.7.0](https://img.shields.io/badge/SpringBoot-2.7.2-green)![SpringCloudGateWay-2.7.0](https://img.shields.io/badge/SpringCloudGateWay-2.2.10.RELEASE-orange)![SpringCloudZuul-2.7.0](https://img.shields.io/badge/SpringCloudZuul-2.2.10.RELEASE-orange)![KaTool-1.9.5.GAMA](https://img.shields.io/badge/KaTool-1.9.5.GAMA-blue)

- 官网：[KaTool-Security](https://security.katool.cn/)
- GetStart：[KaTool Security鉴权框架文档 | KaTool-Security](https://doc.security.katool.cn/)

## 1. 简介

KaTool Security鉴权框架是KaTool提供的权限管理工具，基于Spring Boot和Dubbo实现，可以针对SpringCloudGateWay和Zuul等流行网关框架进行自定义鉴权逻辑适配，使开发者更注重逻辑开发，而不被项目架构所影响，使用TokenUtil也能够快速进行框架转换而不用改编原本的代码逻辑。

## 2. 模块简介

### 2.1 主要模块

- katool-security-starter-parent 								主工程父模块
  - katool-security-core  										 核心模块
  - katool-security-interceptor                               AOP注解拦截器
  - katool-security-spring-boot-starter                 给外部项目引用的Starter包
  - katool-security-interface                                   微服务模式 - Dubbo远程调用Interface层
  - katool-security-auth                                           微服务模式 - 鉴权中心服务
  - katool-security-gateway-starter-parent          微服务 - 网关层面鉴权starter父模块
    - katool-security-gateway-core                                              
    - katool-security-gateway-spring-cloud-gateway-starter	      Spring Cloud GateWay 网关鉴权Starter
    - katool-security-gateway-zuul-starter                                         Zuul 网关鉴权Starter

```markdown
└─katool-security-starter-parent
   └─katool-security-auth						# 微服务鉴权中心服务
   │  └─src/main/java
   │          └─cn.katool.security
   │                      └─auth
   │                          ├─aop				# 日志记录切面
   │                          ├─controller		# 鉴权服务控制器
   │                          ├─exception		# 异常处理
   │                          ├─job				# 定时任务 - 每10分钟自动关闭所有接口鉴权
   │                          ├─mapper			# MyBatisPlus-Mapper层
   │                          ├─model			# Auth实体类、KaSecurityUser实体类
   │                          ├─service			# AuthController对应的service层,以及KaSecurityUser
   │                          └─utils			# 用到的一些工具类
   ├─katool-security-core
   │  └─src/main/java
   │         └─cn.katool.security
   │                     └─common
   │                         ├─annotation		# 鉴权注解（@AuthCheck）
   │                         ├─constant		# 常量
   │                         ├─logic			# 微服务鉴权处理逻辑层
   │                         ├─model
   │                         │  ├─dto/auth
   │                         │  ├─entity
   │                         │  └─vo
   │                         └─utils			# JSON转换工具
   ├─katool-security-gateway-starter-parent	# 微服务 - 网关层面鉴权starter父模块
   │  ├─katool-security-gateway-core		# 统一接口（用于获取Token，但是Request由于不同框架实现不同，各自自行实现Request上下文获取）
   │  │  └─src/main/java
   │  │              └─cn.katool.security.gateway
   │  │                              └─service
   │  ├─katool-security-gateway-spring-cloud-gateway-starter		# Spring Cloud GateWay Starter
   │  │  └─src/main/java
   │  │           └─cn.katool.security.starter
   │  │                           ├─gateway
   │  │                           └─utils
   │  └─katool-security-gateway-zuul-starter						# Zuul Stater
   ├─katool-security-interceptor
   │  └─src/main/java
   │              └─cn.katool.security
   │                          └─interceptor	# AOP鉴权拦截切面逻辑
   ├─katool-security-interface
   │  └─src/main/java
   │              └─cn.katool.security
   │                          └─service		# Dubbo 远程调用接口
   └─katool-security-spring-boot-starter		# 单体项目引入的Starter
       └─src/main/java
                   └─cn.katool.security
                               └─starter
```

### 2.2 其他模块

- katool-security-demo	一些使用样例
  - katool-security-boot-simple-demo	    在单体项目下的使用样例
  - katool-security-gateway-simple-demo  微服务模式，在SpringCloudGateWay作为网关的前提下的使用样例
  - katool-security-zuul-simple-demo          微服务模式，在Zuul作为网关的前提下的使用样例

```markdown
└─katool-security-demo
   ├─katool-security-boot-simple-demo
   ├─katool-security-gateway-simple-demo
   └─katool-security-zuul-simple-demo
```

## 3. 鉴权逻辑实现

### 3.1 实现KaSecurityAuthLogic接口继承工具类

```java
@Component
public class AuthConfig extends KaSecurityAuthUtil<需要鉴权的类型> implements KaSecurityAuthLogic{
    @Override
    public KaSecurityValidMessage doCheckLogin(Boolean onlyCheckLogin) {
       
        return KaSecurityValidMessage.unKnow();
    }

    @Override
    public KaSecurityValidMessage doAuth(List<String> roleList) {
        // 这里可以根据角色列表进行鉴权，返回鉴权失败或者鉴权成功的消息
        System.out.println("进入鉴权，roleList:" + roleList);
        return KaSecurityValidMessage.success();
    }

    @Bean
    private void initAuth(){
        // 加入鉴权
        KaToolSecurityAuthQueue.add(this);
    }
}

```

### 3.2 将MySecurityAuthLogic加入鉴权执行队列

定义一个Bean，执行语句就可以了，你也可以使用自动装配，在这里由于使@Bean，并且MySecurityAuthLogic被@Component标记，所以会自动对MySecurityAuthLogic的成员属性进行自动装配

```java
public class BeanClass{
    
    @Bean
    void run(List<String> routeList){
        KaToolSecurityAuthQueue.add(new MySecurityAuthLogic());
    }
    
}
```

## 4. 贡献提交规范

先将代码fork到自己的仓库，新建一个分支，代码编写好之后，请自行测试并配上测试截图到根目录下的`test.md`文件

test.md格式：

```markdown
# 新增/修复 贡献名称
# 你的个人简介（如果进入被参考，那么我们会将您计入到官网的贡献者中）
- 头像：
- Name：
- 简介/签名：
- 其他（包括但不限于邮箱/联系方式/博客/github/gitee）：
# 简介：
.....
# 测试结果：
.....
# 使用教程：
.....
```

KaTool-SpringBootTest测试框架github地址：https://github.com/Karosown/KaToolTest.git

## 5. Git/Issues提交规范

### 5.1 什么是Git提交规范

Git是目前最流行的分布式版本控制系统，它能够帮助开发者高效管理项目代码。在进行Git操作时，我们需要对代码进行提交，以记录下每一次修改的内容。而Git提交规范则是指在代码提交时，根据一定的格式要求进行提交信息的书写，并在注释中尽可能详细地记录修改的内容，以方便其他人查看。

### 5.2 Git提交规范的重要性

1. 提高协作效率：当多人协同开发时，不规范的提交信息很容易让别人无法理解代码的变更，从而延误项目进度。
2. 方便代码审查：优秀的提交注释能够帮助代码审核人员快速了解修改的内容，减轻审核负担。
3. 方便代码回退：在需要回退代码到某一个具体版本时，合理规范的Git提交信息能够方便地找到对应的版本，并快速恢复代码。
4. 维护项目历史记录：清晰明了的提交注释可以记录项目开发的历程，方便后期的维护和追溯。

### 5.3 Git提交规范的要求

Git提交规范通常包括以下信息：

1. 标题（必填）：一句话简述本次提交的内容。
2. 空行：用于分隔标题和正文。
3. 正文（选填）：详细阐述本次提交的内容，可以包括具体修改的文件、代码功能、修复了哪些bug等。
4. 空行：用于分隔正文和注释。
5. 注释（选填）：对本次提交补充说明的信息，可以包括相关链接、参考文献等。

Git提交规范要求的格式通常如下：

```
<type>(<scope>): <subject>

<body>

<footer>
```

其中，表示本次提交的类型，常见的有以下几种：

- feat：新增功能
- fix：修复bug
- docs：修改文档
- style：修改代码风格
- refactor：重构代码
- test：增加或修改测试代码
- chore：修改构建过程或辅助工具

表示本次提交涉及到的模块或功能点。如果本次提交不涉及到具体模块或功能点，可以省略。

表示本次提交的简要说明，一般不超过50个字符。



表示本次提交的详细描述，可以包括多行。表示本次提交的注释，可以包括多行。 ## Git提交规范的代码示例 下面是一个示例代码，演示了如何按照Git提交规范进行代码提交： ```javascript git add . git commit -m "feat(login): 新增用户登录功能 新增了用户登录页面、登录表单提交接口及相关验证逻辑" ``` 在这个示例中，我们按照Git提交规范的格式书写了一条提交信息，其中为feat，表示本次提交新增了功能；为login，表示本次提交涉及到用户登录模块；为“新增用户登录功能”，简要说明了本次提交的内容；为“新增了用户登录页面、登录表单提交接口及相关验证逻辑”，详细描述了本次提交的内容。

# GetStart

## 1. 快速启动

跟着下面的教程来就可以了，但是我们还是给了个demo：

[katool-security-demo/katool-security-zuul-simple-demo/katool-security-zuul-simple-demo-app · Karos/katool-security - 码云 - 开源中国 (gitee.com)](https://gitee.com/karosown/katool-security/tree/master/katool-security-demo/katool-security-zuul-simple-demo/katool-security-zuul-simple-demo-app)

### 1.1 引入依赖

```xml
<dependency>
    <groupId>cn.katool.security</groupId>
    <artifactId>katool-security-spring-boot-starter</artifactId>
    <version>1.1.0.SNAPSHOT</version>
</dependency>
```

### 1.2 实现AuthConfig.java

```java
@Component
public class AuthConfig extends KaSecurityAuthUtil<需要鉴权的类型> implements KaSecurityAuthLogic{
    @Override
    public KaSecurityValidMessage doCheckLogin(Boolean onlyCheckLogin) {
       
        return KaSecurityValidMessage.unKnow();
    }

    @Override
    public KaSecurityValidMessage doAuth(List<String> roleList) {
        // 这里可以根据角色列表进行鉴权，返回鉴权失败或者鉴权成功的消息
        System.out.println("进入鉴权，roleList:" + roleList);
        return KaSecurityValidMessage.success();
    }

    @Bean
    private void initAuth(){
        // 加入鉴权
        KaToolSecurityAuthQueue.add(this);
    }
}
```

如果要根据具体业务来，我这里给大家一个常用的，也是我微服务中鉴权中心用的配置

```java
@Slf4j
@Component
public class AuthConfig extends KaSecurityAuthUtil<KaSecurityUser> implements KaSecurityAuthLogic {
    @Override
    public KaSecurityValidMessage doCheckLogin(Boolean onlyCheckLogin) {
        KaSecurityUser payLoad = this.getPayLoad();
        if (ObjectUtils.isEmpty(payLoad)){
            return KaSecurityValidMessage.unLogin();
        }
        if (BooleanUtils.isFalse(onlyCheckLogin)){
            log.info("当前接口不仅仅检查登录情况");
            return KaSecurityValidMessage.success();
        }
        return KaSecurityValidMessage.onlyLogin();
    }

    @Override
    public KaSecurityValidMessage doAuth(List<String> roleList) {
        KaSecurityUser payLoad = this.getPayLoad();
        String userRole = payLoad.getUserRole();
        if (roleList.contains(userRole)){
            return KaSecurityValidMessage.success();
        }
        return KaSecurityValidMessage.noAuth();
    }
    
    @Bean
    public void initer(){
        log.info("AuthConfig init");
        KaToolSecurityAuthQueue.add(this);
    }
}
```

### 1.3 控制层编写

```java
@RestController
@RequestMapping("/checklogin")
@AuthControllerCheck(onlyCheckLogin = true,
    excludeMethods = {"exclude(String testName)"}
)
public class CheckLoginTestController {

    @GetMapping
    public String lock() {
        return "不出意外这个接口需要检查登录";
    }

    @GetMapping("/unclude")
    public String exclude(String testName) {
        return "这个接口是排除了的";
    }
}
```

在这里，我们用了一个注解`@AuthControllerChech`，这个注解的功能是在这个类ixa的所有接口都需要经过鉴权，当然可以进行方法排除，更多接口我们在后面的文档里面细说。

### 1.4 启动项目

到这里，一个简单的鉴权流程就已经做好了，你可以跑起来试一试。

## 2. 进阶

进阶主要是对微服务项目的鉴权升级，不管是使用Spring Cloud GateWay作为网关，还是采用Zuul作为网关，都离不开鉴权中心

### 2.1 启动Auth中心

Auth中心的作用是起到从各个服务和网关之间的鉴权名单同步。

#### 2.1.1 下载解压

首先到Release中下载katool-security-auth，解压即可

#### 2.1.2 编写application.yml

进入./config文件夹，对application.yml进行配置，如果没有，那么创建一个

配置如下

```yaml
spring:
  web:
    resources:
      static-locations: [ classpath:/META-INF/resources/, classpath:/resources/, classpath:/static/, classpath:/public/ ]
  main:
    allow-bean-definition-overriding: true
  application:
    name: KaTool-Security-Auth
  # todo
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848 # 自行配置
      config:
        username: nacos
        password: nacos
  # 默认 dev 环境
  profiles:
    active: dev
  # 支持 swagger3
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
  # session 配置
  session:
    # todo 取消注释开启分布式 session（须先配置 Redis）
    store-type: redis
    # 30 天过期
    timeout: 2592000
  # 数据库配置
  # todo
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/katool_security?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: # 自行填写
    password: # 自行填写

  # Redis 配置
    # todo
  redis:
    database: 0
    host:
    port:
    timeout: 5000
    password:
  servlet:
    multipart:
      location: /data/upload_tmp
# 大小限制
      max-file-size: 10GB
      max-request-size: 100GB
  aop:
    proxy-target-class: true
# MybatisPlus 配置
server:
  # todo 自行配置
  address: 0.0.0.0
  port: 7777
  servlet:
    # cookie 30 天过期
    session:
      cookie:
        max-age: 2592000
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: isDelete # 全局逻辑删除的实体字段名
      logic-delete-value: 1 # 逻辑已删除值（默认为 1）
      logic-not-delete-value: 0 # 逻辑未删除值（默认为 0）
katool:
  security:
    mode: single	# 对于网关来说，只能选择单体模式，因为网关通常只进行转发
    core:
      token-header: "Authorization"
  util:
    redis:
      policy: caffeine
      time-unit: days
      exp-time: 7
  lock:
    internal-lock-lease-time: 30
    time-unit: minutes
dubbo:
  application:
    name: dubbo-KaTool-Security-Auth
  protocol:
    name: dubbo
    port: -1
  registry:
    id: nacos-registry
    address: nacos://localhost:8848 # todo: 自行配置
```

其中，标记了todo的都是需要自行配置的

#### 2.1.3 项目启动

进入./bin/目录，选择startup.cmd进行启动

### 2.2 单体项目到微服务升级

配置类不用动，主要还是对以来进行修改

无非下面两个步骤：网关排除并引入依赖，各个服务修改yml

这里先把网关的yml统一下

```yaml
katool:
  security:
    mode: single
    core:
      token-header: "Authorization"	# 这个适用于标识token存放在哪个header中
```

同时每个服务的主函数上面打一个注解`@EnableKaSecurityAuthCenter`即可

```java
@ConfigurationPropertiesScan(basePackages = "cn.katool.security")
@SpringBootApplication
@EnableKaSecurityAuthCenter
public class ZuulSimpleDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulSimpleDemoApplication.class, args);
    }
}
```

#### 2.2.1 适配SpringCloudGateWay

具体的demo，可以看看git

[katool-security-demo/katool-security-gateway-simple-demo · Karos/katool-security - 码云 - 开源中国 (gitee.com)](https://gitee.com/karosown/katool-security/tree/master/katool-security-demo/katool-security-gateway-simple-demo)

##### 2.2.1.1 网关排除依赖

```xml
<dependency>
    <groupId>cn.katool.security.demo.gateway</groupId>
    <artifactId>katool-security-gateway-simple-demo-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <exclusions>
        <exclusion>
            <groupId>cn.katool.security</groupId>
            <artifactId>katool-security-spring-boot-starter</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

##### 2.2.1.2 网关引入依赖

```xml
<!--引入SpringCloudGateWay对应的Starter-->
<dependency>
    <groupId>cn.katool.security</groupId>
    <artifactId>katool-security-gateway-spring-cloud-gateway-starter</artifactId>
    <version>1.1.0.SNAPSHOT</version>
</dependency>
```

##### 2.2.1.3 各个服务修改application.yaml

这里可以统一用个配置中心吧

```yaml
katool:
  security:
    mode: gateway	# 选择通过SpringCloudGateWay进行鉴权，其实选择Zuul也是可以的，但是gateway我们更建议
    core:
      token-header: "Authorization"
```

#### 2.2.1 适配Zuul

具体的demo，可以看看git

[katool-security-demo/katool-security-zuul-simple-demo · Karos/katool-security - 码云 - 开源中国 (gitee.com)](https://gitee.com/karosown/katool-security/tree/master/katool-security-demo/katool-security-zuul-simple-demo)

##### 2.2.1.1 网关排除依赖

```xml
<dependency>
    <groupId>cn.katool.security.demo.gateway</groupId>
    <artifactId>katool-security-zuul-simple-demo-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <exclusions>
        <exclusion>
            <groupId>cn.katool.security</groupId>
            <artifactId>katool-security-spring-boot-starter</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

##### 2.2.1.2 网关引入依赖

```xml
<!--引入Zuul对应的Starter-->
<dependency>
    <groupId>cn.katool.security</groupId>
    <artifactId>katool-security-gateway-zuul-starter</artifactId>
    <version>1.1.0.SNAPSHOT</version>
</dependency>
```

##### 2.2.1.3 各个服务修改application.yaml

这里可以统一用个配置中心吧

```yaml
katool:
  security:
    mode: zuul	# 选择通过Zuul进行鉴权，其实选择SpringCloudGateWay也是可以的，但是Zuul我们更建议
    core:
      token-header: "Authorization"
```

### 2.3 网关组件更换

直接修改依赖就行了，严格来说各个服务还要修改下katool.security.mode

# 更新历程

- V1.0.0
  - SNAPSHOT
    - 基本功能实现、网关组件快速切换
  - RELEASE
    - 新增@AuthPayLoad注解进行登录用户快速注入
- V1.1.0
  - todo:SNAPSHOT
    - 新增登录状态管理
    - 踢人下线
    - 多端登录管理

# API接口文档

## 注解文档

### @AuthCheck

用于对某一特定的方法进行拦截，通常标记在接口函数上

- anyRole	拥有任一角色即可通过
- mustRole 必须是某一角色才能通过（目前已过期，已经和anyRole一起使用）
- onlyCheckLogin 当前接口是否仅检查登录

### @AuthControllerCheck

用于对某些特定的方法进行拦截，通常标记在Controller层大类上

- anyRole	拥有任一角色即可通过

- mustRole 必须是某一角色才能通过（目前已过期，已经和anyRole一起使用）

- onlyCheckLogin 当前接口是否仅检查登录

- excludeMethods 排除的方法列表

  > 由于方法名可能重复，所以每个方法的填写需要满足表达式：`方法名（参数类型 参数名...）`，有多少个参数就要填写多少个

### @AuthServiceCheck

用于对某些特定的方法进行拦截，通常标记在Service层大类上，需要注意的是，当使用Dubbo作为远程调用时，当Servcie作为生产者，那么@AuthServiceCheck可能会失效，解决方法看下文

- anyRole	拥有任一角色即可通过

- mustRole 必须是某一角色才能通过（目前已过期，已经和anyRole一起使用）

- onlyCheckLogin 当前接口是否仅检查登录

- excludeMethods 排除的方法列表

  > 由于方法名可能重复，所以每个方法的填写需要满足表达式：`方法名（参数类型 参数名...）`，有多少个参数就要填写多少个

#### 鉴权失效的解决方法

**实现Dubbo的拦截SPI即可**

类我写好了，具体的xml还得各位自行配置，这个类的位置

```java
package cn.katool.security.filter;

import cn.katool.constant.AuthConstant;
import cn.katool.util.auth.AuthUtil;
import com.alibaba.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Activate(group = {Constants.PROVIDER})
public class DubboProviderAuthFilter implements Filter {


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = AuthUtil.getToken(requestAttributes.getRequest());
        RpcContext.getContext().setAttachment(AuthConstant.TOKEN_HEADER, token);
        return invoker.invoke(invocation);
    }
}
```

### @EnableKaSecurityAuthCenter

用于分布式网关鉴权开启鉴权中心，本质上是开启了Dubbo和定时任务

## 返回信息文档

### KaSecurityValidMessage

#### 概述

`KaSecurityValidMessage` 是一个用于定义安全验证消息的Java类。该类包含了常见的安全验证结果及对应的消息，以及一些静态方法用于获取特定结果的实例。

#### 类定义

```java
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class KaSecurityValidMessage {
    // ... （类定义，包括静态常量、属性、构造方法等）

    Integer code;
    String message;

    // ... （静态方法，用于获取特定结果的实例）
}
```

#### 静态常量

- `SUNKNOW_CODE`: 表示成功的代码。
- `UNAUTH_CODE`: 表示未授权的代码。
- `UNLOGIN_CODE`: 表示未登录的代码。
- `UNKNOW_CODE`: 表示未知错误的代码。

#### 静态实例

- `SUCCESS_MESSAGE`: 表示成功的消息实例。
- `INLOGIN_MESSAGE`: 表示仅检查登录的消息实例。
- `NOAUTH_MESSAGE`: 表示未授权的消息实例。
- `UNLOGIN_MESSAGE`: 表示未登录的消息实例。
- `UNKNOW_MESSAGE`: 表示未知错误的消息实例。

#### 静态方法

##### `success()`

用于获取成功的消息实例。

```java
public static KaSecurityValidMessage success(){
    return  SUCCESS_MESSAGE;
}
```

##### `onlyLogin()`

用于获取仅检查登录的消息实例。

```java
public static KaSecurityValidMessage onlyLogin(){
    return INLOGIN_MESSAGE;
}
```

##### `noAuth()`

用于获取未授权的消息实例。

```java
public static KaSecurityValidMessage noAuth(){
    return  NOAUTH_MESSAGE;
}
```

##### `unLogin()`

用于获取未登录的消息实例。

```java
public static KaSecurityValidMessage unLogin(){
    return  UNLOGIN_MESSAGE;
}
```

##### `unKnow()`

用于获取未知错误的消息实例。

```java
public static KaSecurityValidMessage unKnow(){
    return  UNKNOW_MESSAGE;
}
```

#### 使用示例

```java
// 获取成功的消息实例
KaSecurityValidMessage successMessage = KaSecurityValidMessage.success();

// 获取未登录的消息实例
KaSecurityValidMessage unLoginMessage = KaSecurityValidMessage.unLogin();

// 获取未知错误的消息实例
KaSecurityValidMessage unknowErrorMessage = KaSecurityValidMessage.unKnow();
```

以上示例展示了如何使用 `KaSecurityValidMessage` 类的静态方法获取特定消息实例。可以根据实际需要选择相应的实例，以便在应用中进行安全验证。

同时您可以通过Setter来进行自定义返回消息

## 方法文档

### KaSecurityAuthLogic

#### 概述

`KaSecurityAuthLogic` 是一个用于定义安全验证逻辑的函数式接口。它提供了一些默认方法，以及一个静态工具方法用于执行权限验证逻辑。

#### 接口定义

```java
@FunctionalInterface
public interface KaSecurityAuthLogic {
        default KaSecurityValidMessage doCheckLogin(Boolean onlyCheckLogin) {
        // ...（默认方法，用于执行执行检查登录逻辑，如果只需要检查登录，那么就不会执行doAuth，注意，需要重写！）
    }

    KaSecurityValidMessage doAuth(List<String> roleList);	// 进行角色判断的业务逻辑


    default HttpServletRequest getRequest() {
        // ...（默认方法，用于获取HttpServletRequest对象）
    }
}
```

#### 方法说明

##### `doAuth(List<String> roleList)`

该方法用于执行权限验证逻辑，接受一个角色列表作为参数，并返回一个 `KaSecurityValidMessage` 对象。

##### `doCheckLogin(Boolean onlyCheckLogin)`

默认方法，用于执行仅检查登录逻辑。如果 `onlyCheckLogin` 为 `true`，则返回一个提示消息；否则，返回成功的消息。

##### `allValid(KaSecurityValidMessage[] messages)`

**已废弃的方法**，用于执行一组验证消息的逻辑。建议使用 `ValidFilter` 方法替代。

##### `ValidFilter(KaSecurityAuthLogic kaSecurityAuthLogic, List<String> roleList, Boolean onlyCheckLogin)`

静态方法，用于执行权限验证逻辑。首先执行 `doCheckLogin` 方法，然后根据返回结果决定是否继续执行 `doAuth` 方法。如果 `doAuth` 返回 `onlyLogin` 状态，则抛出异常。如果 `doAuth` 返回成功，则返回成功的消息。

##### `getRequest()`

默认方法，用于获取当前请求的 `HttpServletRequest` 对象。在 `KaSecurityMode.GATEWAY` 模式下，通过 `RequestContextHolder` 获取请求对象。若为该模式，会发出警告建议使用 `KaSecurityAuthUtil` 获取 Token，而不是直接使用请求对象。

### KaToolSecurityAuthQueue

#### 概述

`KaToolSecurityAuthQueue` 是一个简单的权限验证逻辑队列管理工具类。它提供了添加、获取、清空队列。

#### 类定义

```java
public class KaToolSecurityAuthQueue {

  private static final LinkedBlockingQueue<KaSecurityAuthLogic> list = new LinkedBlockingQueue<>();

  public static void add(KaSecurityAuthLogic logic) {
    // ...（添加逻辑到队列的方法）
  }

  public static KaSecurityAuthLogic get() {
    // ...（从队列获取逻辑的方法）
  }

  public static void clear() {
    // ...（清空队列的方法）
  }

  public static int size() {
    // ...（获取队列大小的方法）
  }

  public static boolean isEmpty() {
    // ...（检查队列是否为空的方法）
  }
}
```

#### 方法说明

##### `add(KaSecurityAuthLogic logic)`

将指定的 `KaSecurityAuthLogic` 对象添加到队列中。

##### `get()`

从队列中获取并移除一个 `KaSecurityAuthLogic` 对象。

##### `clear()`

清空队列中的所有 `KaSecurityAuthLogic` 对象。

##### `size()`

获取队列中的逻辑数量。

##### `isEmpty()`

检查队列是否为空。

### KaSecurityAuthUtil

#### 概述

`KaSecurityAuthUtil` 是一个用于处理安全认证相关操作的工具类。它提供了获取请求中的信息，如Payload、Token等的方法。本文档将介绍三个不同版本的 `KaSecurityAuthUtil` 类，它们分别实现了不同的接口，并在不同的场景中使用。

#### 类定义

##### 1. **KaSecurityAuthUtil 实现 DefaultKaSecurityAuthUtilInterface 接口**，SpringCloudGateWay使用

```java
package cn.katool.security.starter.utils;

import cn.katool.security.starter.gateway.gateway.utils.RequestContextUtil;
import reactor.core.publisher.Mono;

public class KaSecurityAuthUtil<T> implements DefaultKaSecurityAuthUtilInterface<T> {

    // ...（实现 DefaultKaSecurityAuthUtilInterface 接口的各种方法）
}
```

##### 2. **KaSecurityAuthUtil 实现 AbstractKaSecurityAuthUtil 接口，Zuul使用**

```java
package cn.katool.security.starter.utils;

import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class KaSecurityAuthUtil<T> implements AbstractKaSecurityAuthUtil<T> {

    // ...（实现 AbstractKaSecurityAuthUtil 接口的各种方法，并使用 Zuul）
}
```

##### 3. **KaSecurityAuthUtil 实现 AbstractKaSecurityAuthUtil 接口，各个服务/单体项目使用**

```java
package cn.katool.security.starter.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KaSecurityAuthUtil<T> implements AbstractKaSecurityAuthUtil<T> {

    // ...（实现 AbstractKaSecurityAuthUtil 接口的各种方法）
}
```

#### 方法说明

以下是 `KaSecurityAuthUtil` 类的一些主要方法（调用通过Dubbo获取记得在生产者进行Dubbo-SPI扩展）：

##### `getPayLoadWithHeader()`

获取带有头部信息的 Payload。

##### `getPayLoadWithDubboRPC()`

获取通过Dubbo RPC传递的Payload。

##### `getPayLoad()`

获取Payload。

##### `getTokenWithDubboRPC()`

获取通过Dubbo RPC传递的Token。

##### `getTokenWithHeader()`

获取请求头中的Token。

##### `getTokenWithHeader(String headerName)`

获取指定请求头中的Token。

##### `getTokenWithParameter(String parameterName)`

获取指定请求参数中的Token。

##### `getTokenWithCookie(String cookieName)`

获取指定Cookie中的Token。

##### `getTokenWithHeaderOrParameter(String headerName, String parameterName)`

获取请求头或请求参数中的Token，优先使用请求头。

##### `getRequest`

获取请求上下文

##### `getResponse`

获取响应上下文

##### `login(T payLoad)`

一个登录函数，用于返回一个jwt，并放入请求头里面，这个方法SpringCloudGateWay没有实现

#### 使用示例

```java
// 示例 1：使用 DefaultKaSecurityAuthUtilInterface 接口的 KaSecurityAuthUtil
KaSecurityAuthUtil<String> authUtil1 = new KaSecurityAuthUtil<>();
String payload1 = authUtil1.getPayLoadWithHeader();
String token1 = authUtil1.getTokenWithHeader();

// 示例 2：使用 AbstractKaSecurityAuthUtil 接口的 KaSecurityAuthUtil，并在 Zuul 中使用
KaSecurityAuthUtil<String> authUtil2 = new KaSecurityAuthUtil<>();
String payload2 = authUtil2.getPayLoadWithDubboRPC();
String token2 = authUtil2.getTokenWithCookie("authToken");

// 示例 3：使用 AbstractKaSecurityAuthUtil 接口的 KaSecurityAuthUtil
KaSecurityAuthUtil<String> authUtil3 = new KaSecurityAuthUtil<>();
String payload3 = authUtil3.getPayLoad();
String token3 = authUtil3.getTokenWithParameter("token");
```

以上示例展示了不同场景下如何使用 `KaSecurityAuthUtil` 类的实例进行安全认证相关的操作。用户可以根据实际需求选择适合的类版本并调用相应的方法。

### AuthUtils

现在支持泛型了，更为智能

[AuthUtil 使用文档 | KaTool](https://www.katool.cn/Verify/AuthUtil/#注意事项)

## Dubbo-RPC接口

### AuthService

#### 概述

`AuthService` 是一个针对表【auth】的数据库操作Service接口，提供了对权限数据的增、删、改、查等操作。

#### 接口定义

```java
package cn.katool.security.service;

import cn.katool.security.core.model.dto.auth.AuthAddRequest;
import cn.katool.security.core.model.dto.auth.AuthUpdateRequest;
import cn.katool.security.core.model.vo.AuthVO;

import java.util.List;

/**
 * 针对表【auth】的数据库操作Service
 * @createDate 2023-05-27 11:29:05
 */
public interface AuthService {

    /**
     * 插入权限数据
     * @param addRequest 权限新增请求
     * @return 插入成功返回 true，否则返回 false
     */
    Boolean insert(AuthAddRequest addRequest);

    /**
     * 更新权限数据
     * @param authUpdateRequest 权限更新请求
     * @return 更新成功返回 true，否则返回 false
     */
    Boolean change(AuthUpdateRequest authUpdateRequest);

    /**
     * 开启指定权限
     * @param method 请求方法
     * @param uri 请求路径
     * @param route 路由
     * @return 开启成功返回 true，否则返回 false
     */
    Boolean open(String method, String uri, String route);

    /**
     * 开启指定权限
     * @param id 权限ID
     * @return 开启成功返回 true，否则返回 false
     */
    Boolean open(String id);

    /**
     * 判断指定权限是否开启
     * @param method 请求方法
     * @param uri 请求路径
     * @param route 路由
     * @return 开启返回 true，否则返回 false
     */
    Boolean isOpen(String method, String uri, String route);

    /**
     * 重新加载权限数据
     * @return 重新加载成功返回 true，否则返回 false
     */
    Boolean reload();

    /**
     * 开启多个权限
     * @param ids 权限ID列表
     * @return 开启成功返回 true，否则返回 false
     */
    Boolean open(List<String> ids);

    /**
     * 关闭多个权限
     * @param ids 权限ID列表
     * @return 关闭成功返回 true，否则返回 false
     */
    Boolean close(List<String> ids);

    /**
     * 关闭指定权限
     * @param id 权限ID
     * @return 关闭成功返回 true，否则返回 false
     */
    Boolean close(String id);

    /**
     * 关闭指定权限
     * @param method 请求方法
     * @param uri 请求路径
     * @param route 路由
     * @return 关闭成功返回 true，否则返回 false
     */
    Boolean close(String method, String uri, String route);

    /**
     * 根据请求信息获取权限详情
     * @param method 请求方法
     * @param requestURI 请求路径
     * @param contextPath 上下文路径
     * @return 权限详情
     */
    AuthVO getOne(String method, String requestURI, String contextPath);

    /**
     * 获取所有已开启的权限列表
     * @return 已开启的权限列表
     */
    List<AuthVO> getListByIsOpen();

    /**
     * 保存或更新权限数据
     * @param one 权限详情
     * @return 保存或更新成功返回 true，否则返回 false
     */
    Boolean saveOrUpdate(AuthVO one);
}
```

#### 方法说明

##### `insert(AuthAddRequest addRequest)`

插入权限数据。

##### `change(AuthUpdateRequest authUpdateRequest)`

更新权限数据。

##### `open(String method, String uri, String route)`

开启指定权限。

##### `open(String id)`

开启指定权限。

##### `isOpen(String method, String uri, String route)`

判断指定权限是否开启。

##### `reload()`

重新加载权限数据。

##### `open(List<String> ids)`

开启多个权限。

##### `close(List<String> ids)`

关闭多个权限。

##### `close(String id)`

关闭指定权限。

##### `close(String method, String uri, String route)`

关闭指定权限。

##### `getOne(String method, String requestURI, String contextPath)`

根据请求信息获取权限详情。

##### `getListByIsOpen()`

获取所有已开启的权限列表。

##### `saveOrUpdate(AuthVO one)`

保存或更新权限数据。

#### 使用示例

```java
// 示例：在服务中使用AuthService接口
@DubboReference(check = false)
private AuthService authService;
// 插入权限数据
Boolean insertResult = authService.insert(authAddRequest);

// 更新权限数据
Boolean changeResult = authService.change(authUpdateRequest);

// 开启指定权限
Boolean openResult = authService.open("GET", "/api/resource", "/route");

// 判断指定权限是否开启
Boolean isOpenResult = authService.isOpen("GET", "/api/resource", "/route");

// 重新加载权限数据
Boolean reloadResult = authService.reload();

// 开启多个权限
Boolean openMultipleResult = authService.open(List.of("id1", "id2", "id3"));

// 关闭多个权限
Boolean closeMultipleResult = authService.close(List.of("id1", "id2", "id3"));

// 关闭指定权限
Boolean closeResult = authService.close("id");

// 获取权限详情
AuthVO authDetail = authService.getOne("GET", "/api/resource", "/route");

// 获取所有已开启的权限列表
List<AuthVO> openAuthList = authService.getListByIsOpen();

// 保存或更新权限数据
Boolean saveOrUpdateResult = authService.saveOrUpdate(authVO);
```

以上示例展示了如何在服务中使用 `AuthService` 接口的各个方法进行权限数据的增、删、改、查等操作。用户可以根据实际需求调用相应的方法。

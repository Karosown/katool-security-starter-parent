# KaTool Security鉴权框架文档

## 1. 简介

KaTool Security鉴权框架是KaTool提供的权限管理工具，基于Spring Boot和Dubbo实现，可以针对SpringCloudGateWay和Zuul等流行网关框架进行自定义鉴权逻辑适配，使开发者更注重逻辑开发，而不被项目架构所影响，使用TokenUtil也能够快速进行框架转换而不用改编原本的代码逻辑。

## 2. 模块简介

- katool-security-starter-parent 								主工程父模块
  - katool-security-core  										 核心模块
  - katool-security-interceptor                               AOP注解拦截器
  - katool-security-spring-boot-starter                 给外部项目引用的Starter包
  - katool-security-interface                                   微服务模式 - Dubbo远程调用Interface层
  - katool-security-auth                                           微服务模式 - 鉴权中心服务
  - katool-security-gateway-starter-parent          微服务 - 网关层面鉴权starter父模块
    - katool-security-gateway-interface                                              
    - katool-security-gateway-spring-cloud-gateway-starter	      Spring Cloud GateWay 网关鉴权Starter
    - katool-security-gateway-zuul-starter                                         Zuul 网关鉴权Starter

```bash
└─katool-security-starter-parent
    └─katool-security-auth						# 微服务鉴权中心服务
    │  └─src/main/java
    │          └─cn.katool.security
    │                      └─auth
    │                          ├─aop			# 日志记录切面
    │                          ├─controller		# 鉴权服务控制器
    │                          ├─exception		# 异常处理
    │                          ├─job			# 定时任务 - 每10分钟自动关闭所有接口鉴权
    │                          ├─mapper
    │                          ├─model			# Auth实体类
    │                          ├─service
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
    │  ├─katool-security-gateway-interface		# 统一接口（用于获取Token，但是Request由于不同框架实现不同，各自自行实现Request上下文获取）
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

## 3. 鉴权逻辑实现

### 3.1 实现KaSecurityAuthLogic接口

```java
@Component
public class MySecurityAuthLogic implements KaSecurityAuthLogic {

   void doAuth(){
       // 你的业务逻辑
   }
    
}
```

### 3.2 将MySecurityAuthLogic加入鉴权执行队列

定义一个Bean，执行语句就可以了，你也可以使用自动装配，在这里由于使@Bean，并且MySecurityAuthLogic被@Component标记，所以会自动对MySecurityAuthLogic的成员属性进行自动装配

```java
public class BeanClass{
    
    @Bean
    void run(){
        KaToolSecurityAuthQueue.add(new MySecurityAuthLogic());
    }
    
}
```


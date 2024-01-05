# KaTool Security鉴权框架文档

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
public class AuthConfig extends KaSecurityAuthUtil<鉴权出来的类型> implements KaSecurityAuthLogic{
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


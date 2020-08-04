# gateway

CubeAI ★ 智立方 HTTP API网关

CubeAI基于微服务架构进行开发和部署，gateway是在其中起重要作用的一个微服务，基于Spring Boot框架，用Java编写。

Gateway作为API网关，用于将来自客户端浏览器的HTTP请求路由（转发）至后端相应的微服务进行处理，同时将来自后端微服务的HTTP应答消息返回至浏览器。此外，gateway还具有客户端负载均衡（反向代理）、断路保护、QoS流量限制、接入控制等功能。

Gateway使用Consul来进行微服务注册和发现，以及集中的配置管理。在gateway启动时，它首先尝试与Consul建立连接。如果Consul未就绪，则gateway将启动失败。

Gateway使用UAA来进行用户管理（用户认证、授权、基于角色的访问控制等）。在gateway启动时，它会尝试与uaa建立连接。如果uaa未就绪，则用户无法成功登录并执行相应的业务操作。


## 基本配置

- 监听端口：8080

- 服务注册与发现，中心配置：Consul(8500)

- 用户认证授权：uaa(9999)

- 数据库：无


## 开发环境

- 操作系统

    - Linux，建议Ubuntu 16.04 LTS
    
- Java JDK

    - openjdk-8-jdk

- Java build工具

    - Maven

- Docker

- Idea IntelliJ集成开发环境

## 开发

1. 开发环境中运行gateway之前，需要先拉起项目依赖的后台docker（参见docker/dev文件夹下的README文档），以及uaa微服务。

2. 在命令行使用dev profile运行gateway：

        cd ~/cubeai/gateway
        ./mvnw
        
   或者在IDE（如IntelliJ Idea ）中打开gateway项目（默认使用dev profile），直接运行或调试程序。

## 部署

1. 编译并生成微服务docker镜像：

        cd ~/cubeai/gateway
        yarn install
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
2. docker-compose部署

    参见docker/prod文件夹下面的README文档。

3. k8s部署

    参见docker/k8s文件夹下面的README文档。

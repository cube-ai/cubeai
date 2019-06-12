# gateway

CubeAI ★ 智立方 Portal and API gateway

CubeAI基于微服务架构进行开发和部署，gateway是在其中起重要作用的一个微服务。

本应用中gateway的代码由两部分组成：Portal前端UI界面，基于Angular框架，用TypeScript/HTML/CSS编写；API网关，基于Spring Boot框架，用Java编写。

Gateway作为API网关，用于将来自客户端浏览器的HTTP请求路由（转发）至后端相应的微服务进行处理，同时将来自后端微服务的HTTP应答消息返回至浏览器。此外，gateway还具有客户端负载均衡（反向代理）、断路保护、QoS流量限制、接入控制等功能。

Gateway使用Consul来进行微服务注册和发现，以及集中的配置管理。在gateway启动时，它首先尝试与Consul建立连接。如果Consul未就绪，则gateway将启动失败。

Gateway使用UAA来进行用户管理（用户认证、授权、基于角色的访问控制等）。在gateway启动时，它会尝试与uaa建立连接。如果uaa未就绪，则用户无法成功登录并执行相应的业务操作。

本微服务初始代码框架使用开源工具Jhipster( https://www.jhipster.tech )生成。

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

- Node.js
           
- 前端build工具

    - Yarn
        
- JHipster

- Docker

- Idea IntelliJ集成开发环境

## 开发

1. 前端开发环境准备

    第一次从Github克隆本项目代码后，应先在本项目目录下执行如下命令以安装前端开发需要的Node依赖以及webpack：

        yarn install
        yarn webpack:build
    
2. 开发环境中运行gateway之前，需要先拉起项目依赖的后台docker（参见docker/dev文件夹下的README文档），以及uaa微服务。

3. 在命令行使用dev profile运行gateway：

        cd ~/cubeai/gateway
        ./mvnw
        
   或者在IDE（如IntelliJ Idea ）中打开gateway项目（默认使用dev profile），直接运行或调试程序。
   
4. 在另一个terminal窗口中运行：

        yarn start
        
    将会自动弹出浏览器，并在localhost的9000端口打开Portal的UI界面。
    之后在IDE中修改前端代码时，webpack会自动重新编译TypeScript代码，并自动刷新浏览器UI界面。
    
5. 本项目中，编译后的前端服务代码放在target/www目录下。如果target/www不存在（例如执行过./mvnw clean操作），则在运行./mvnw之前需要先执行如下操作，在target/www下重新生成前端服务代码：
   
        yarn webpack:build

## 部署

- 使用war包

1. 使用prod profile来构建用于生产环境性能优化的war包:

        cd ~/cubeai/gateway
        yarn install
        ./mvnw -Pprod clean package -DskipTests

2. 运行war包:

        java -jar target/*.war
        
3. 浏览器访问：

        http://127.0.0.1:8080
        

- Docker方式部署

1. 构建gateway docker：

        cd ~/cubeai/gateway
        yarn install
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
2. 拉起docker

    参见docker/prod文件夹下面的README文档。

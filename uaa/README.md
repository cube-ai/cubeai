# uaa

CubeAI ★ 智立方 用户认证授权中心

CubeAI基于微服务架构进行开发和部署。UAA（User Authentication and Authorization，用户认证授权中心）主要使用OAuth2和JWT技术进行用户认证和授权，以及基于角色的访问控制。

UAA使用Consul来进行微服务注册和发现，以及集中的配置管理。在UAA启动时，它首先尝试与Consul建立连接。如果Consul未就绪，则UAA将启动失败。

## 基本配置

- 监听端口：9999

- 服务注册与发现，中心配置： Consul(8500)

- 数据库

    - MySql
    
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

1. 开发环境中运行uaa之前，需要先拉起项目依赖的所有后台docker（参见docker/dev文件夹下的README文档）。

2. 在命令行使用dev profile运行uaa：

        cd ~/cubeai/uaa
        ./mvnw
        
   或者在IDE（如IntelliJ Idea ）中打开uaa项目（默认使用dev profile），直接运行或调试程序。
   

## 部署

1. 编译并生成微服务docker镜像：

        cd ~/cubeai/uaa
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
2. docker-compose部署

    参见docker/prod文件夹下面的README文档。

3. k8s部署

    参见docker/k8s文件夹下面的README文档。


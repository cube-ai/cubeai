# umu

CubeAI ★ 智立方 AI模型导入

CubeAI基于微服务架构进行开发和部署，umu是其中负责AI模型导入的微服务。

umu使用Consul来进行微服务注册和发现，以及集中的配置管理。在umu启动时，它首先尝试与Consul建立连接。如果Consul未就绪，则umu将启动失败。

umu使用uaa来进行用户管理（用户认证、授权、基于角色的访问控制等）。在umu启动时，它会尝试与uaa建立连接。如果uaa未就绪，则用户无法成功登录并执行相应的业务操作。

## 基本配置

- 监听端口：8082

- 服务注册与发现，中心配置： Consul(8500)
    
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

1. 开发环境中运行umu之前，需要先拉起项目依赖的所有后台docker（参见docker/dev文件夹下的README文档）, 以及uaa和gateway。

2. 在命令行使用dev profile运行umu：

        cd ~/cubeai/umu
        ./mvnw
        
   或者在IDE（如IntelliJ Idea ）中打开umu项目（默认使用dev profile），直接运行或调试程序。
   

## 部署

1. 编译并生成微服务docker镜像：

        cd ~/cubeai/umu
        yarn install
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
2. docker-compose部署

    参见docker/prod文件夹下面的README文档。

3. k8s部署

    参见docker/k8s文件夹下面的README文档。


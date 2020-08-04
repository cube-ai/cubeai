# ability

CubeAI ★ 智立方 AI能力开放网关

## 基本配置

- 监听端口：8086

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

1. 开发环境中运行ability之前，需要先拉起项目依赖的所有后台docker（参见docker/dev文件夹下的README文档）, 以及uaa、gateway、portal等其他微服务。

2. 在命令行使用dev profile运行ability：

        cd ~/cubeai/ability
        ./mvnw
        
   或者在IDE（如IntelliJ Idea ）中打开ability项目（默认使用dev profile），直接运行或调试程序。
   

## 部署

1. 编译并生成微服务docker镜像：

        cd ~/cubeai/ability
        yarn install
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
2. docker-compose部署

    参见docker/prod文件夹下面的README文档。

3. k8s部署

    参见docker/k8s文件夹下面的README文档。


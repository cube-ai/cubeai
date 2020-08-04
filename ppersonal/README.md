# ppersonal

CubeAI ★ 智立方 模型共享平台前端门户

CubeAI基于微服务架构进行开发和部署，ppersonal是其中的个人中心前端门户微服务，位于主portal之后。Ppersonal前端基于Angular框架，使用TypeScript/HTML/CSS等语言开发。

## 基本配置

- 服务端口：8003

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

- Docker

- Idea IntelliJ集成开发环境

## 开发

1. 前端开发环境准备

    第一次从Github克隆本项目代码后，应先在本项目目录下执行如下命令以安装前端开发需要的Node依赖以及webpack：

        yarn install
        yarn webpack:build
    
2. 开发环境中运行ppersonal之前，需要先拉起项目依赖的后台docker（参见docker/dev文件夹下的README文档），以及uaa和gateway等微服务。

3. 在命令行使用dev profile运行ppersonal：

        cd ~/cubeai/ppersonal
        ./mvnw
        
   或者在IDE（如IntelliJ Idea ）中打开ppersonal项目（默认使用dev profile），直接运行或调试程序。
   
4. 在另一个terminal窗口中运行：

        yarn webpack:build
    
5. 在浏览器中打开：
   
        http://127.0.0.1:8080
        
   从portal主界面中导航至ppersonal界面来进行调试。
   
6. Angular前端源代码修改之后，重复执行上述第4-5步。

## 部署

1. 编译并生成微服务docker镜像：

        cd ~/cubeai/ppersonal
        yarn install
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
2. docker-compose部署

    参见docker/prod文件夹下面的README文档。

3. k8s部署

    参见docker/k8s文件夹下面的README文档。

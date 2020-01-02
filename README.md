# [![](https://code.ihub.org.cn/projects/348/repository/revisions/master/entry/cubeai-logo.jpg)](https://cubeai.dimpt.com)

版权所有 © 2019 中国联通网络技术研究院

https://cubeai.dimpt.com

---

## 平台简介

[**CubeAI ★ 智立方**](https://cubeai.dimpt.com) 是参考 [Linux AI基金会（LFAI）](https://lfai.foundation/)开源项目[Acumos](https://www.acumos.org)的设计理念，由中国联通网络技术研究院完全自主开发的集AI模型开发、模型共享和能力开放等功能于一体的开源网络AI平台。

CubeAI平台目前提供AI模型打包、模型导入、容器化封装、模型编排、模型发布、模型搜索、模型部署、AI能力开放、能力编排、能力演示等功能，支持AI模型的docker容器化封装和微服务化部署。

CubeAI致力于在AI模型开发者和模型的实际使用者之间架设一条互通的桥梁。开发者无需关心具体的部署环境，最终用户无需了解AI算法的具体实现细节，使得开发者和使用者能够专注于各自最擅长的领域进行创新，从而加速AI创新和应用进程，促进AI算法从设计、开发直到部署、应用整个生命周期的快速迭代和演进。

## 系统架构

![](./cubeai-arch.jpg)

本系统由AI建模、AI模型共享（AI商城）和AI能力开放三大平台组成。其中AI建模目前暂采用线下形式，使用Acumos提供的客户端工具来实现模型打包。

## 软件架构

![](./cubeai-soft.jpg)

本系统AI模型共享平台（AI商城）和AI能力开放平台基于[Spring Cloud](https://spring.io/projects/spring-cloud)微服务架构进行开发。前端采用[Angular 6.0](https://angular.io/)框架实现，编程语言主要为TypeScript和HTML；后端采用[Spring Boot](https://spring.io/projects/spring-boot)框架实现，编程语言主要为Java。部分微服务初始代码框架采用[Jhipster](https://www.jhipster.tech/)代码脚手架工具生成。

### 微服务基础组件

- Consul

使用Consul作为微服务注册/发现中心和数据配置中心。

- API网关

API网关是一个特殊的微服务，用于为后端的业务应用微服务提供一个统一的访问入口，主要功能包括：HTTP路由，负载均衡，安全控制，QoS控制，接入控制，熔断机制等等。

本系统将API网关功能与采用Angular编写的前端Portal页面集成于同一个微服务之中，命名为：gateway。

- uaa

uaa（用户认证授权中心）是一个特殊的微服务，为系统提供统一的安全控制服务，主要用于用户的认证、鉴权、授权，微服务的访问控制，以及基于角色的访问控制。

- 消息中间件

消息中间件由一组特殊的微服务组成，主要用于系统中微服务间异步数据和消息的高效传输和处理。本系统采用开源软件Kafka来作为消息中间件。

- 搜索/日志/可视化套件

搜索/日志/可视化采用ELK套件来实现。ELK Stack构建在开源基础之上，能够安全可靠地获取微服务架构中任何来源、任何格式的数据，并且能够实时地对数据进行搜索、分析和可视化呈现。

### CubeAI应用微服务

- Portal

前端采用Angular框架进行开发，将其代码托管于API网关所在的后端微服务gateway之中。

- umm

AI模型管理。下挂一个MySql数据库，统一管理CubeAI应用中所有需要持久化的数据模型。

- umu

AI模型导入。负责将建模阶段打包好的AI模型导入CubeAI平台，并生成docker形式的微服务镜像。

- umo

AI模型编排。以可视化图形界面将多个基础模型组件组合编排成一个功能更为复杂的AI模型。

- umd

AI模型部署。将CubeAI平台中已发布模型部署至Kubernetes云平台，以docker容器的方式运行，以RESTful API的形式向用户提供AI能力开放接口。

- ability

AI能力开放网关。对Kubernetes平台中docker容器提供的AI能力接口进行封装，增强API访问的安全性。

- udemo

AI能力开放示范平台。为Kubernetes中已部署的部分典型AI开放能力接口提供图形化演示界面。

### CubeAI应用支撑组件

- nginx

Web反向代理服务器，主要用于在生产环境中向互联网用户提供HTTPS服务。开发环境中不需要。

- Docker打包服务器

用于在模型导入过程中根据Dockerfile文件生成docker镜像。任选一台Linux服务器，安装docker驱动，并开放2375端口，然后执行以下命令拉取需要的docker基础镜像即可。

        docker pull unicom.gq:8801/ubuntu/python:3.0.3
        docker tag unicom.gq:8801/ubuntu/python:3.0.3 ubuntu/python:3.0.3
        docker pull unicom.gq:8801/ubuntu/python3.6:0.0.9
        docker tag unicom.gq:8801/ubuntu/python3.6:0.0.9 ubuntu/python3.6:0.0.9

- Nexus

提供用于存储AI模型相关构件和文档的文件服务器，以及用于存贮AI模型docker镜像的docker仓库。

请参考Nexus官方文档进行安装和配置。Nexus服务器需要配置为通过HTTPS协议进行访问。

- Kubernates

AI模型部署目标平台，以docker容器形式为AI能力开放提供微服务化容器编排和调度平台。

请参考Kubernates官方文档进行安装和配置。

## 开发环境

- 操作系统

    - Linux，建议Ubuntu 16.04 LTS
    
- 版本管理

    - git
      
- Java JDK

    - 安装

            # apt install openjdk-8-jdk

    - 配置环境变量： 

            export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
            export PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin
        
 - Java build工具
 
    - Maven

    - 安装
    
            # apt-get install maven
            
    - 查看maven版本号
    
            # mvn --version
            
    - 配置本地仓库
    
        Maven缺省使用当前用户登录目录下的.m2目录作为本地仓库。
        
- Node.js

    - 从 https://nodejs.org/en/download/ 下载相应版本，拷贝至/opt。
    
    - 解压，创建符号连接（用实际版本号替换以下版本号）：
    
            cd /opt
            # tar -xvf node-v8.10.0-linux-x64.tar.xz 
            # ln -s node-v8.10.0-linux-x64 nodejs
            # ln -s /opt/nodejs/bin/node /usr/bin/node
            # ln -s /opt/nodejs/bin/npm /usr/bin/npm
            # node -v
            
- 前端build工具

    - Yarn
    
    - 安装

            # curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add -
            # echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list
            # apt-get update
            # apt-get install yarn
        
- 微服务代码脚手架工具

    - [JHipster](https://www.jhipster.tech)
    
    - 安装

            # yarn global add yo
            # yarn global add generator-jhipster

- Docker

    - 卸载旧版本（如果非初次安装）
    
            # apt-get remove docker docker-engine docker.io
    
    - 配置仓库
    
        - 更新apt-get包索引
    
                apt-get update
                
        - 安装软件包使apt可使用HTTPS:
    
                # apt-get install  apt-transport-https  ca-certificates  curl  software-properties-common
    
        - 添加Docker官方GPG key:
    
                # curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
    
        - 验证指纹，是否有 9DC8 5822 9FC7 DD38 854A E2D8 8D81 803C 0EBF CD88
    
                # apt-key fingerprint 0EBFCD88

        - Set up the stable repository:
    
                # add-apt-repository  "deb [arch=amd64] https://download.docker.com/linux/ubuntu  $(lsb_release -cs)  stable"
    
    - 安装DOCKER CE
  
            # apt-get update
            # apt-get install docker-ce
    
    - 安装docker-compose
    
            # apt-get install docker-compose
            
    - 验证安装成功：
    
            # docker run hello-world
            
- 集成开发环境

    - 建议： Idea IntelliJ
      
## 安装、开发和部署

1. 代码下载

        # git clone https://github.com/cube-ai/cubeai.git
        
2. 开发

    - 参照docker/dev文件夹下的README文档，拉起平台开发需要依赖的所有基础微服务。
    
    - 参照uaa、gateway、umm、umu、umd、ability等文件夹下的README文档，分别进行各微服务的开发调试。
    
3. 部署

    - 参照docker/prod文件夹下的README文档，对整个平台运行所需要的所有微服务进行部署。
    

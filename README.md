CubeAI★智立方 v2.3.1

---

## 平台简介

CubeAI★智立目前包括AI在线训练、自动化模型发布与部署、可视化AI能力开放等子平台和功能模块。其核心作用在于打通AI模型开发至实际生产应用之间的壁垒，加速AI创新和应用进程，促进AI应用从设计、开发直到部署、运营整个生命周期的自动化快速迭代和演进。

CubeAI v2.3.0: 隆重推出原创性 Python 微服务框架 —— ServiceBoot，基于 ServiceBoot 框架对 CubeAI 代码进行整体重构。

CubeAI v2.2.0：推出全新的AI模型微服务框架 —— cubeai_model_runner ，实现彻底的去Acumos化。

CubeAI v2.1.0：采用Python开发并完全重构微服务框架，实现去Spring Cloud化，支持基于k8s和istio的云原生服务网格部署。

## 系统架构

### 微服务基础组件

- Consul

使用Consul作为微服务注册/发现中心和数据配置中心。

- gateway

gateway作为微服务API网关，用于为后端的业务应用微服务提供一个统一的访问入口，提供HTTP路由功能，并充当Oauth2的client身份。

- uaa

uaa（用户认证授权中心）是一个特殊的微服务，为系统提供统一的安全控制服务，主要用于用户的认证、鉴权、授权，微服务的访问控制，以及基于角色的访问控制。

### CubeAI应用微服务

- portal

前端主门户微服务。portal前端基于Angular框架，使用TypeScript/HTML/CSS等语言开发。

- ppersonal

个人中心前端门户微服务，位于主portal之后。ppersonal前端基于Angular框架，使用TypeScript/HTML/CSS等语言开发。

- pmodelhub

模型共享平台前端门户微服务，位于主portal之后。pmodelhub前端基于Angular框架，使用TypeScript/HTML/CSS等语言开发。

- popen

能力开放平台前端门户微服务，位于主portal之后。Popen前端基于Angular框架，使用TypeScript/HTML/CSS等语言开发。

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

### CubeAI应用支撑组件

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
      
- Python

    - Python 3.5.2以上版本

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

    - 建议： PyCharm
      
## 安装、开发和部署

1. 代码下载

        # git clone http://bggit.ihub.org.cn/p91275860/cubeai_ihub.git
        
2. 开发

    - 参照docker/dev-python文件夹下的README文档，拉起平台开发需要依赖的所有基础微服务。
    
    - 参照uaa、gateway、portal、ppersonal、pmodelhub、popen、umm、umu、umd、ability等文件夹下的README文档，分别进行各微服务的开发调试。
    
3. 部署

    - 参照docker/prod-python文件夹下的README文档，对整个平台运行所需要的所有微服务进行部署。
    

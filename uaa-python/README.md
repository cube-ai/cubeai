# uaa

用户认证授权中心

UAA（User Authentication and Authorization，用户认证授权中心）主要使用OAuth2和JWT技术进行用户认证、授权、访问控制，以及其他相关服务。

UAA使用Consul来进行微服务注册和发现，以及集中的配置管理。在UAA启动时，它首先尝试与Consul建立连接。如果Consul未就绪，则UAA将启动失败。


## 基本配置

- 监听端口：9999

- 服务注册与发现，中心配置： Consul(8500)

- 数据库

    - MySql


## 开发环境

- 操作系统

    - Linux，建议Ubuntu 16.04 LTS
    
- Python 3.5 以上

- Docker

- 建议Pycharm集成开发环境

## 开发

1. 开发环境中运行uaa之前，需要先拉起项目依赖的后台docker。

        cd ~/cubeai/docker/dev-python
        docker-compose up
        
    参见docker/dev-python目录下的README文档。

2. 使用PyCharm打开本project所在目录。

3. 建议在PyCharm中专门为本project新建一个专用Python虚拟环境，Python版本选择3.5以上。

4. 在PyCharm的terminal窗口中执行如下命令安装依赖包：

        sh pip-install-reqs.sh

5. 在PyCharm窗口中右键单击“start.py”文件，选择“run 'start'”或者“debug 'start'”来运行或调试程序。

6. 开发完成后，在terminal窗口中执行如下命令来生成微服务docker镜像：

        sh build-docker.sh


## 部署

1. docker-compose部署

- 在docker目录下，执行如下命令来打包所有微服务镜像：
    
        cd ~/cubeai/docker
        sh build-all-python.sh
        
- 然后cd到cubeai/docker/prod-python，执行docker-compose命令拉起并运行所有微服务：

        cd ~/cubeai/docker
        docker-compose up
    
    参见docker/prod-python目录下面的README文档。

3. k8s部署

    参见docker/k8s目录下面的README文档。

# umu

CubeAI ★ 智立方中用于提供模型导入服务的微服务。

## 基本配置

- 监听端口：8101（可任意）

- 服务注册与发现，中心配置：Consul(8500)

- 用户认证授权：uaa(9999)

- 数据库： 无



## 开发环境

- 操作系统

    - Linux，建议Ubuntu 16.04 LTS
    
- Python 3.5 以上

- Docker

    1. 卸载旧版本（如果非初次安装）
    
            # apt-get remove docker docker-engine docker.io
    
    2. 配置仓库
    
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
    
    3. 安装DOCKER CE
    
            # apt-get update
            # apt-get install docker-ce
    
    4. 验证安装成功：
    
            # docker run hello-world
            
- docker-compose

        # apt-get install docker-compose

- 集成开发环境

    - 建议 Pycharm

## 开发

1. 开发环境中运行本微服务之前，需要先拉起项目依赖的后台docker。

        # cd ~/cubeai/docker/dev-python
        # docker-compose up
        
    参见docker/dev-python目录下的README文档。

2. 使用PyCharm打开本project所在目录。

3. 建议在PyCharm中专门为本project新建一个专用Python虚拟环境，Python版本选择3.5以上。

4. 在PyCharm的terminal窗口中执行如下命令安装依赖包：

        # sh pip-install-reqs.sh
        
   依赖包安装完成后，可在terminal窗口中执行如下命令来查看serviceboot所有命令行格式：
   
        # serviceboot

5. 在PyCharm窗口中右键单击“start.py”文件，选择“run 'start'”或者“debug 'start'”来运行或调试程序。

6. 开发完成后，可在terminal窗口中执行如下命令来生成微服务docker镜像：

        # serviceboot build_docker
        或者
        # sh build-docker.sh


## 部署

1. docker-compose部署

    1. 在docker目录下，执行如下命令来打包所有微服务镜像：
    
            # cd ~/cubeai/docker
            # sh build-all-python.sh
        
    2. 然后cd到cubeai/docker/prod-python，执行docker-compose命令拉起并运行所有微服务：

            # cd ~/cubeai/docker
            # docker-compose up
    
    参见docker/prod-python目录下面的README文档。

2. k8s部署

    参见docker/k8s目录下面的README文档。

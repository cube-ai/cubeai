# portal

CubeAI ★ 智立方 主门户微服务

portal前端基于Angular框架，使用TypeScript/HTML等语言开发。


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

- Node.js
    
    1. 从 https://nodejs.org/en/download/ 下载最新版本的node，拷贝至/opt。
    
    2. 解压，创建符号连接以便可以调用（用实际版本号替换其中node版本号）：
        
            # cd /opt
            # tar -xvf node-v10.15.0-linux-x64.tar.xz 
            # ln -s node-v10.15.0-linux-x64 nodejs
            # ln -s /opt/nodejs/bin/node /usr/bin/node
            # ln -s /opt/nodejs/bin/npm /usr/bin/npm
            # node -v
            # npm -v
            
    3. 配置npm源国内镜像
    
            # npm config set registry https://registry.npm.taobao.org
            # npm config get registry

- Angular CLI

    1. 安装

            # npm install -g @angular/cli@10.1.1

    2. 创建符号链接以便可以调用（用实际版本号替换其中node版本号）

            # ln -s /opt/node-v10.15.0-linux-x64/bin/ng /usr/bin/ng

- 集成开发环境

    - Pycharm

    - Idea IntelliJ

## 开发

1. 克隆代码并安装前端依赖包

    第一次从Git服务器克隆本项目代码后，应先在本项目 webapp 目录下执行如下命令以安装前端开发需要的Node依赖：

        # cd webapp
        # npm install

2. 开发环境中运行portal之前，需要先拉起项目依赖的后台docker，以及uaa和gateway等微服务。

        # cd ~/cubeai/docker/dev-python
        # docker-compose up
        
    参见docker/dev-python目录下的README文档。

3. 使用PyCharm打开本project所在目录。

4. 建议在PyCharm中专门为本project新建一个专用Python虚拟环境，Python版本选择3.5以上。

5. 在PyCharm的terminal窗口中执行如下命令安装依赖包：

        # sh pip-install-reqs.sh

6. 在PyCharm窗口中右键单击“start.py”文件，选择“run 'start'”来启动前端服务。

7. 建议使用Idea IntelliJ打开本project来进行前端Angular代码调试。

8. 每次前端代码改动后，在terminal窗口中运行：

        # cd webapp
        # ng build
        或者
        # ng build --prod
        
   来完成代码编译。
    
9. 然后在浏览器中打开或刷新页面：
   
        http://127.0.0.1:8080
        
   gateway网关会自动将主页面路由至portal微服务来提供前端界面服务。
   
10. Angular前端源代码修改之后，重复执行上述第8-9步,。

11. 开发完成后，可在terminal窗口中执行如下命令来生成微服务docker镜像：

        # serviceboot build_docker
        或者
        # sh build-docker.sh


## 部署

1. docker-compose部署

- 在docker目录下，执行如下命令来打包所有微服务镜像：
    
        # cd ~/cubeai/docker
        # sh build-all-python.sh
        
- 然后cd到cubeai/docker/prod-python，执行docker-compose命令拉起并运行所有微服务：

        # cd ~/cubeai/docker
        # docker-compose up
    
    参见docker/prod-python目录下面的README文档。

3. k8s部署

    参见docker/k8s目录下面的README文档。

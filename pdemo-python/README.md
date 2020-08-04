# pdemo

CubeAI ★ 智立方 AI示范应用前端门户

pdemo前端基于Angular框架，使用TypeScript/HTML/CSS等语言开发。


## 基本配置

- 监听端口：8205（可任意）

- 服务注册与发现，中心配置：Consul(8500)


## 开发环境

- 操作系统

    - Linux，建议Ubuntu 16.04 LTS
    
- Python 3.5 以上

- Docker

- 前端build工具

    - Node.js

    - Yarn

- 集成开发环境

    - Pycharm

    - Idea IntelliJ

## 开发

1. 前端开发环境准备

    第一次从Github克隆本项目代码后，应先在本项目目录下执行如下命令以安装前端开发需要的Node依赖：

        yarn install

2. 开发环境中运行pdemo之前，需要先拉起项目依赖的后台docker，以及uaa微服务和gateway等微服务。

        cd ~/cubeai/docker/dev-python
        docker-compose up
        
    参见docker/dev-python目录下的README文档。

3. 使用PyCharm打开本project所在目录。

4. 建议在PyCharm中专门为本project新建一个专用Python虚拟环境，Python版本选择3.5以上。

5. 在PyCharm的terminal窗口中执行如下命令安装依赖包：

        sh pip-install-reqs.sh

6. 在PyCharm窗口中右键单击“start.py”文件，选择“run 'start'”来启动服务。

7. 建议使用Idea IntelliJ打开本project来进行前端Angular代码调试。

8. 每次前端代码改动后，在另一个terminal窗口中运行：

        yarn webpack:build 或者 yarn webpack:prod
        
   来完成代码编译。
    
9. 然后在浏览器中打开或刷新页面：
   
        http://127.0.0.1:8080
        
   gateway网关会自动将相关页面路由至popen微服务来提供前端界面服务。
   
10. Angular前端源代码修改之后，重复执行上述第8-9步,。

11. 开发完成后，在terminal窗口中执行如下命令来生成微服务docker镜像：

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

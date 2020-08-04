# 生产环境部署

本目录存储用于在生产环境中部署dockers的docker-compose配置脚本文件，以及微服务应用统一配置中心配置文件。

部署过程如下：

1. 各微服务源代码编译

        cd ~/cubeai/docker
        sh build-all-python.sh

2. 拉起并运行所有docker：

        cd ~/cubeai/docker/prod-python
        docker-compose up -d
        
3. 等待若干分钟后，用docker ps查看所有docker是否都已拉起，没有的话需要单个重启失败的微服务。
    以uaa为例：

        docker-compose -f uaapy.yml up -d
        
4. 停止并删除docker容器：

        docker-compose down

5. 系统运行过程中，可以单独停掉某个docker并重启。以uaa为例:

        docker-compose -f uaapy.yml down
 
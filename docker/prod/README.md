# 生产环境（prod profile）部署

本目录存储用于在生产环境中部署dockers的docker-compose配置文件，以及微服务应用统一配置中心配置文件。

1. 构建gateway docker：

        cd ~/cubeai/gateway
        yarn install   (第一次下载源码后运行)
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
2. 构建uaa docker:

        cd ~/cubeai/uaa
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
3. 构建umm docker:

        cd ~/cubeai/umm
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
4. 构建umu docker:

        cd ~/cubeai/umu
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests

5. 构建umo docker:

        cd ~/cubeai/umo
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests

6. 构建umd docker:

        cd ~/cubeai/umd
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
7. 构建ability docker:

        cd ~/cubeai/ability
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
8. 构建umo docker:

        cd ~/cubeai/umo
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
9. 构建ucomposer docker:

        cd ~/cubeai/ucomposer
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
9. 构建udemo docker（可选，如存在的话）:

        cd ~/cubeai/udemo
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests
        
10. 构建uapp docker（可选，如存在的话）:

        cd ~/cubeai/uapp
        ./mvnw clean verify -Pprod dockerfile:build -DskipTests

11. 拉起运行所有docker：

        cd ~/cubeai/docker/prod
        docker-compose up -d
        
12. 等待若干分钟后，拉起微服务监控相关docker：

        docker-compose -f monitor.yml up -d 
        
13. 等待若干分钟后，用docker ps查看所有docker是否都已拉起，没有的话需要单个重启失败的微服务。
    以uaa为例：

        docker-compose -f uaa.yml up -d
        
14. 停止并删除docker容器：

        docker-compose -f monitor.yml down
        docker-compose down

15. 系统运行过程中，可以单独停掉某个docker并重启。以uaa为例:

        docker-compose -f uaa.yml down
        docker-compose -f udemo.yml up -d 
 
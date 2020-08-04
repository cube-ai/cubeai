# CubeAI智立方 部署脚本

## 集中配置参数修改

第一次从git仓库下载本代码后，需要进入到docker下各个子目录中的central-server-config子目录，修改application.yml文件中的相关配置项，改为与自己系统中的配置相一致。

## 开发环境

- 参见dev-java目录下README文件

## 生产环境

- docker-compose部署
 
    - Java版本部署
    
            cd cubeai/docker
            sh build-all-java.sh
            cd cubeai/docker/prod-java
            docker-compose up
    
    - 详情参见prod-java目录下README文件

- k8s部署


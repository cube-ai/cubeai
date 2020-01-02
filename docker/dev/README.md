# 开发环境（dev profile）

本目录存储用于在开发环境中拉起依赖dockers的docker-compose配置文件，以及微服务应用统一配置中心配置文件。

开发环境中调试微服务应用步骤如下：

1. 拉起所有依赖dockers：

    打开一个terminal窗口，运行：

        cd ~/cubeai/docker/dev
        docker-compose up
        
2. 在集成开发环境（例如Intellij IDEA）中分别打开uaa, gateway, umm, umu, umo, umd, ability等微服务工程，编译并运行。参见各工程目录下的README文件。


>注意：在部署之前， dev/central-server-cnfig目录下的 application.yml文件中的相关配置项需要根据自身的网络进行修改。

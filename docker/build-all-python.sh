pip3 install -i https://pypi.tuna.tsinghua.edu.cn/simple serviceboot==0.1.2

cd ~/cubeai/uaa-python
sh build-docker.sh

cd ~/cubeai/gateway-python
sh build-docker.sh

cd ~/cubeai/portal-python
sh build-docker.sh

cd ~/cubeai/ppersonal-python
sh build-docker.sh

cd ~/cubeai/pmodelhub-python
sh build-docker.sh

cd ~/cubeai/popen-python
sh build-docker.sh

cd ~/cubeai/umm-python
sh build-docker.sh

cd ~/cubeai/umu-python
sh build-docker.sh

cd ~/cubeai/umd-python
sh build-docker.sh

cd ~/cubeai/ability-python
sh build-docker.sh

cd ~/cubeai/pface-python
sh build-docker.sh

cd ~/cubeai/uface-python
sh build-docker.sh

cd ~/cubeai/uaa-python
sh build-docker.sh

cd ~/cubeai/gateway-python
sh build-docker.sh

cd ~/cubeai/portal-python
yarn install
sh build-docker.sh

cd ~/cubeai/ppersonal-python
yarn install
sh build-docker.sh

cd ~/cubeai/pmodelhub-python
yarn install
sh build-docker.sh

cd ~/cubeai/popen-python
yarn install
sh build-docker.sh

cd ~/cubeai/umm-python
sh build-docker.sh

cd ~/cubeai/umu-python
sh build-docker.sh

cd ~/cubeai/umd-python
sh build-docker.sh

cd ~/cubeai/ability-python
sh build-docker.sh

cd ~/cubeai/pdemo-python
yarn install
sh build-docker.sh

cd ~/cubeai/pface-python
yarn install
sh build-docker.sh

cd ~/cubeai/uface-python
sh build-docker.sh

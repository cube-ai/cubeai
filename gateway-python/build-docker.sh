rm -rf build
mkdir build
cp ./requirements.txt build
cp ./Dockerfile build
cp ./start.py build
cp -rf ./app build

docker image rm gatewaypy:latest
docker build -t gatewaypy ./build

rm -rf build

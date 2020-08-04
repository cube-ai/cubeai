rm -rf build
mkdir build
cp ./requirements.txt build
cp ./Dockerfile build
cp ./start.py build
cp -rf ./app build

docker image rm umupy:latest
docker build -t umupy ./build

rm -rf build

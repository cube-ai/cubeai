rm -rf build
mkdir build
cp ./requirements.txt build
cp ./Dockerfile build
cp ./start.py build
cp -rf ./app build

docker image rm umdpy:latest
docker build -t umdpy ./build

rm -rf build

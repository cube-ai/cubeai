rm -rf build
mkdir build
cp ./requirements.txt build
cp ./Dockerfile build
cp ./start.py build
cp -rf ./app build

docker image rm uaapy:latest
docker build -t uaapy ./build

rm -rf build

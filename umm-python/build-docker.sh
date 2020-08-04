rm -rf build
mkdir build
cp ./requirements.txt build
cp ./Dockerfile build
cp ./start.py build
cp -rf ./app build

docker image rm ummpy:latest
docker build -t ummpy ./build

rm -rf build

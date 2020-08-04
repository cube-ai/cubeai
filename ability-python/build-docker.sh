rm -rf build
mkdir build
cp ./requirements.txt build
cp ./Dockerfile build
cp ./start.py build
cp -rf ./app build

docker image rm abilitypy:latest
docker build -t abilitypy ./build

rm -rf build

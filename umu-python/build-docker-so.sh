rm -rf build
mkdir build
cp ./requirements.txt build
cp ./Dockerfile build
cp ./start.py build

python build_so.py

docker image rm umupy:latest
docker build -t umupy ./build

rm -rf build

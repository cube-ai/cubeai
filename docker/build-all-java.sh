cd ~/cubeai/uaa
docker image rm uaa:latest
./mvnw clean verify -Pprod dockerfile:build -DskipTests

cd ~/cubeai/gateway
docker image rm gateway:latest
./mvnw clean verify -Pprod dockerfile:build -DskipTests

cd ~/cubeai/portal
yarn install
docker image rm portal:latest
./mvnw clean verify -Pprod dockerfile:build -DskipTests

cd ~/cubeai/ppersonal
yarn install
docker image rm ppersonal:latest
./mvnw clean verify -Pprod dockerfile:build -DskipTests

cd ~/cubeai/pmodelhub
yarn install
docker image rm pmodelhub:latest
./mvnw clean verify -Pprod dockerfile:build -DskipTests

cd ~/cubeai/popen
yarn install
docker image rm popen:latest
./mvnw clean verify -Pprod dockerfile:build -DskipTests

cd ~/cubeai/umm
docker image rm umm:latest
./mvnw clean verify -Pprod dockerfile:build -DskipTests

cd ~/cubeai/umu
docker image rm umu:latest
./mvnw clean verify -Pprod dockerfile:build -DskipTests

cd ~/cubeai/umd
docker image rm umd:latest
./mvnw clean verify -Pprod dockerfile:build -DskipTests

cd ~/cubeai/ability
docker image rm ability:latest
./mvnw clean verify -Pprod dockerfile:build -DskipTests

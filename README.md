# reactive-native-service
It is an example of a reactive and native spring-boot service application.

## Usage
To run the application by compiling native, run the following command:
```
./gradlew nativeCompile
```
then you can use the following command to test it by running the application.
```
./build/native/nativeCompile/native-service
```
If you want to create a docker image to deploy to `Kubernetes`, run the following command:
```
./gradlew bootBuildImage
```
If you want to test the image on Docker, you can run the following command:
```
docker run docker.io/library/native-service:0.0.1-SNAPSHOT
```
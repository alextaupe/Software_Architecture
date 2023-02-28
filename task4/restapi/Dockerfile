# build java restapi
FROM gradle:5.0.0-jdk8 as builder
WORKDIR /home/gradle
COPY . . 
RUN gradle --stacktrace bootJar

# deploy java restapi
# TODO: change image to alpine version and run seperately?
CMD [ "java", "-jar", "build/libs/restapi-1.0-SNAPSHOT.jar" ]
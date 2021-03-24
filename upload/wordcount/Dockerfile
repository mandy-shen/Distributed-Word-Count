FROM openjdk:8
MAINTAINER mandy

COPY . /project
#run the spring boot application
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dblabla", "-jar", "/project/wordcount-1.0.0.jar"]

EXPOSE 8080
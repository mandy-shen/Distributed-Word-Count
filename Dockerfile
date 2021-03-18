FROM openjdk:11

MAINTAINER mandy

RUN apt-get update && apt-get install -y maven
COPY . /project
RUN  cd /project && mvn clean package

RUN echo "hello world" > /tmp/apps

#run the spring boot application
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dblabla", "-jar","/project/target/wordcount-1.0.0.jar"]
#http://containertutorials.com/docker-compose/spring-boot-app.html

FROM openjdk:8-jdk-alpine

ADD target/final.jar /final.jar
RUN sh -c 'touch /final.jar'
ENTRYPOINT ["java","-Dspring.profiles.active=local", "-Djava.security.egd=file:/dev/./urandom","-jar","/final.jar"]




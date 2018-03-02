FROM openjdk:8-jre

MAINTAINER Alexander Orlov <alexander.orlov@intrafind.de>

ADD build/libs/*.jar service/
ADD config config
ENV SPRING_CONFIG_NAME application, prod

EXPOSE 8001

CMD java -jar -Xms64m -Xmx64m service/*.jar
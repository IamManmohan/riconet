FROM gradle:5.2 as builder
COPY --chown=gradle:gradle  .  /home/gradle/src
COPY --chown=gradle:gradle  ../core  /home/gradle/src
ARG PROPERTIES_PATH=src/main/resources
RUN echo $PROPERTIES_PATH
ARG ZOOM_VERSION=4.1-SNAPSHOT
RUN echo $ZOOM_VERSION
COPY $PROPERTIES_PATH/application.properties /home/gradle/src/application.properties
COPY src/main/resources/logback.xml /home/gradle/src/logback.xml
WORKDIR /home/gradle/src
RUN gradle build -x test -i -Dzoom.version=$ZOOM_VERSION

FROM openjdk:jre-alpine
RUN mkdir /etc/zoom/
WORKDIR /code/
COPY --from=builder /home/gradle/src/build/libs/eventConsumer-1.0-SNAPSHOT.jar /code/
COPY --from=builder /home/gradle/src/logback.xml /code/logback.xml
COPY --from=builder /home/gradle/src/application.properties /etc/zoom/application.properties
ARG LOGIN_PROFILE=local
RUN echo $LOGIN_PROFILE
ENV JAVA_OPTS="-Xms1024m -Xmx1024m -Dspring.config.location=/etc/zoom/ -Dlogin.profiles.active=$LOGIN_PROFILE"
RUN echo $JAVA_OPTS
ENTRYPOINT exec java -jar $JAVA_OPTS eventConsumer-1.0-SNAPSHOT.jar
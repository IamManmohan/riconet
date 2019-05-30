FROM gradle:5.2 as builder
COPY --chown=gradle:gradle  .  /home/gradle/src
ARG PROPERTIES_PATH_EVENT=eventConsumer/src/main/resources
RUN echo $PROPERTIES_PATH_EVENT
ARG ZOOM_VERSION=4.1-SNAPSHOT
RUN echo $ZOOM_VERSION
COPY $PROPERTIES_PATH_EVENT/application.properties /home/gradle/src/application_event.properties
COPY core/src/main/resources/logback.xml /home/gradle/src/logback.xml
WORKDIR /home/gradle/src
RUN gradle build -x test -i -Dzoom.version=$ZOOM_VERSION

FROM openjdk:jre-alpine
RUN mkdir /etc/zoom/event
WORKDIR /code/
COPY --from=builder /home/gradle/src/eventConsumer/build/libs/eventConsumer-1.0-SNAPSHOT.jar /code/
COPY --from=builder /home/gradle/src/logback.xml /code/logback.xml
COPY --from=builder /home/gradle/src/application_event.properties /etc/zoom/application.properties
ARG LOGIN_PROFILE=local
RUN echo $LOGIN_PROFILE
ENV JAVA_OPTS="-Xms1024m -Xmx1024m -Dspring.config.location=/etc/zoom/event -Dlogin.profiles.active=$LOGIN_PROFILE"
RUN echo $JAVA_OPTS
ENTRYPOINT exec java -jar $JAVA_OPTS eventConsumer-1.0-SNAPSHOT.jar
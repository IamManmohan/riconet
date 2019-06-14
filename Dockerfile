FROM gradle:5.2 as builder
COPY --chown=gradle:gradle  .  /home/gradle/src
ARG PROPERTIES_PATH_EVENT=eventConsumer/src/main/resources
RUN echo $PROPERTIES_PATH_EVENT
ARG ZOOM_VERSION
RUN echo $ZOOM_VERSION
COPY $PROPERTIES_PATH_EVENT/application.properties /home/gradle/src/application_event.properties
COPY core/src/main/resources/logback.xml /home/gradle/src/logback.xml
WORKDIR /home/gradle/src
RUN gradle distZip -i -Dzoom.version=$ZOOM_VERSION
RUN unzip /home/gradle/src/eventConsumer/build/distributions/eventConsumer.zip
WORKDIR /home/gradle/src/eventConsumer/build/distributions/eventConsumer
RUN ls -l

FROM openjdk:jre-alpine
RUN mkdir -p /etc/zoom/event
WORKDIR /code/
COPY --from=builder /home/gradle/src/eventConsumer/build/distributions/eventConsumer /code/
COPY --from=builder /home/gradle/src/logback.xml /code/logback.xml
COPY --from=builder /home/gradle/src/application_event.properties /etc/zoom/event/application.properties
ARG LOGIN_PROFILE
RUN echo $LOGIN_PROFILE
ENV JAVA_OPTS="-Xms1024m -Xmx1024m -Dspring.config.location=/etc/zoom/event -Dlogin.profiles.active=$LOGIN_PROFILE"
RUN echo $JAVA_OPTS
RUN ls -l
WORKDIR bin
RUN ls -l
RUN pwd
RUN sed -i 's%DEFAULT_JVM_OPTS=""%DEFAULT_JVM_OPTS="-Xms1024m -Xmx1024m -Dspring.config.location=/etc/zoom/event -Dlogin.profiles.active=$LOGIN_PROFILE"%g' eventConsumer
ENTRYPOINT exec ./eventConsumer

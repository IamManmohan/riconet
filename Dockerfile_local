FROM openjdk:jre-alpine
RUN mkdir -p /etc/zoom/event
WORKDIR /code/
COPY --from=builder /src/eventConsumer/build/distributions/eventConsumer.zip /code/
RUN unzip /code/eventConsumer.zip
ARG LOGBACK_PATH=core/src/main/resources/logback.xml
RUN echo $LOGBACK_PATH
COPY --from=builder $LOGBACK_PATH /code/logback.xml
ARG PROPERTIES_PATH=src/main/resources
RUN echo $PROPERTIES_PATH
COPY --from=builder $PROPERTIES_PATH /etc/zoom/event/application.properties
ARG SPRING_PROFILE=staging
RUN echo $SPRING_PROFILE
ARG XMX_XMS_VALUE=512m
RUN echo $XMX_XMS_VALUE
ENV JAVA_OPTS="-Xms$XMX_XMS_VALUE -Xmx$XMX_XMS_VALUE -Dspring.config.location=/etc/zoom/event -Dlogin.profiles.active=$LOGIN_PROFILE"
RUN echo $JAVA_OPTS
WORKDIR /code/eventConsumer/bin
RUN sed -i 's%DEFAULT_JVM_OPTS=""%DEFAULT_JVM_OPTS="-Xms1024m -Xmx1024m -Dspring.config.location=/etc/zoom/event -Dlogin.profiles.active=$SPRING_PROFILE"%g' eventConsumer
ENTRYPOINT exec ./eventConsumer
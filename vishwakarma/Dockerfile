FROM openjdk:jre-alpine
WORKDIR /code/
ARG SPRING_PROFILE=vishwakarma
RUN echo $SPRING_PROFILE
ARG LOGIN_PROFILE=staging
RUN echo $LOGIN_PROFILE
ARG XMX_XMS_VALUE=512m
RUN echo $XMX_XMS_VALUE
ARG BACKEND_WRITE_JDBC_URL=jdbc:mysql://host.docker.internal:3306/rivigo_zoom
RUN echo $BACKEND_WRITE_JDBC_URL
ARG OUT_TOPIC_PREFIX=local
RUN echo $OUT_TOPIC_PREFIX
ARG NEO4J_URL=http://host.docker.internal:7474
RUN echo $NEO4J_URL
ARG MONGO_HOST=host.docker.internal
RUN echo $MONGO_HOST
ARG MONGO_PORT=27017
RUN echo $MONGO_PORT
ARG MONGO_DB=rivigo_zoom
RUN echo $MONGO_DB
ENV COMMON_JAVA_OPTS="-Xms$XMX_XMS_VALUE -Xmx$XMX_XMS_VALUE -Dlogin.profiles.active=$LOGIN_PROFILE -Dspring.profiles.active=$SPRING_PROFILE  -Dzoom.neo4j.url=$NEO4J_URL -Dzoom.mongo.hostname=$MONGO_HOST -Dzoom.mongo.port=$MONGO_PORT -Dzoom.mongo.db=$MONGO_DB -Dread.zoom.mysql.jdbcUrl=$BACKEND_READ_JDBC_URL -Dzoom.mysql.url=$BACKEND_WRITE_JDBC_URL"
RUN echo $COMMON_JAVA_OPTS

RUN mkdir -p /etc/zoom/event
WORKDIR /code/
COPY eventConsumer/build/distributions/eventConsumer.zip /code/
RUN unzip /code/eventConsumer.zip
RUN rm /code/eventConsumer.zip
ARG PROPERTIES_PATH_EVENT=eventConsumer/src/main/resources/vishwakarma/
RUN echo $PROPERTIES_PATH_EVENT
COPY $PROPERTIES_PATH_EVENT /etc/zoom/event/
ENV EVENT_JAVA_OPTS=$COMMON_JAVA_OPTS" -Dspring.config.location=/etc/zoom/event/db.properties,/etc/zoom/event/application.properties,/etc/zoom/event/application_core.properties"
RUN echo $EVENT_JAVA_OPTS
WORKDIR /code/eventConsumer/bin
RUN sed -i "s%DEFAULT_JVM_OPTS=\"\"%DEFAULT_JVM_OPTS=\"$EVENT_JAVA_OPTS\"%g" eventConsumer

RUN mkdir -p /etc/zoom/notification
WORKDIR /code/
COPY notificationConsumer/build/distributions/notificationConsumer.zip /code/
RUN unzip /code/notificationConsumer.zip
RUN rm /code/notificationConsumer.zip
ARG PROPERTIES_PATH_NOTIFICATION=notificationConsumer/src/main/resources/vishwakarma/
RUN echo $PROPERTIES_PATH_NOTIFICATION
COPY $PROPERTIES_PATH_NOTIFICATION /etc/zoom/notification/
ENV NOTIFICATION_JAVA_OPTS=$COMMON_JAVA_OPTS" -Dspring.config.location=/etc/zoom/notification/db.properties,/etc/zoom/notification/application.properties,/etc/zoom/notification/application_core.properties"
RUN echo $NOTIFICATION_JAVA_OPTS
WORKDIR /code/notificationConsumer/bin
RUN sed -i "s%DEFAULT_JVM_OPTS=\"\"%DEFAULT_JVM_OPTS=\"$NOTIFICATION_JAVA_OPTS\"%g" notificationConsumer

WORKDIR /code/
RUN apk add supervisor
COPY core/src/main/resources/vishwakarma/supervisord.conf /etc/supervisord.conf
ENTRYPOINT exec /usr/bin/supervisord
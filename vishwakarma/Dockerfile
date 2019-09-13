FROM rivigotech/openjdk-supervisor:8-jre-alpine
WORKDIR /code/
ARG SPRING_PROFILE=vishwakarma
ARG LOGIN_PROFILE=staging
ARG XMX_XMS_VALUE=512m
ARG BACKEND_READ_JDBC_URL=jdbc:mysql://host.docker.internal:3306/rivigo_zoom
ARG OUT_TOPIC_PREFIX=local
ARG NEO4J_URL=http://host.docker.internal:7474
ARG MONGO_HOST=host.docker.internal
ARG MONGO_PORT=27017
ARG MONGO_DB=rivigo_zoom
ARG PROPERTIES_PATH_EVENT=eventConsumer/src/main/resources/vishwakarma/
ARG PROPERTIES_PATH_NOTIFICATION=notificationConsumer/src/main/resources/vishwakarma/

COPY eventConsumer/build/distributions/eventConsumer.zip /code/
COPY notificationConsumer/build/distributions/notificationConsumer.zip /code/
COPY core/src/main/resources/vishwakarma/supervisord.conf /etc/supervisord.conf
COPY $PROPERTIES_PATH_EVENT /etc/zoom/event/
COPY $PROPERTIES_PATH_NOTIFICATION /etc/zoom/notification/

ENV COMMON_JAVA_OPTS="-XX:+UseG1GC -Xms$XMX_XMS_VALUE \
    -Xmx$XMX_XMS_VALUE \
    -Dlogin.profiles.active=$LOGIN_PROFILE \
    -Dspring.profiles.active=$SPRING_PROFILE  \
    -Dzoom.neo4j.url=$NEO4J_URL \
    -Dzoom.mongo.hostname=$MONGO_HOST \
    -Dzoom.mongo.port=$MONGO_PORT \
    -Dzoom.mongo.db=$MONGO_DB \
    -Dread.zoom.mysql.jdbcUrl=$BACKEND_READ_JDBC_URL \
    -Dzoom.mysql.url=$BACKEND_READ_JDBC_URL \
    -Dtopic.prefix.out=$OUT_TOPIC_PREFIX"

ENV EVENT_JAVA_OPTS=$COMMON_JAVA_OPTS" \
    -Dspring.config.location=/etc/zoom/event/application_core.properties,/etc/zoom/event/application_event.properties,/etc/zoom/event/db.properties"

ENV NOTIFICATION_JAVA_OPTS=$COMMON_JAVA_OPTS" \
    -Dspring.config.location=/etc/zoom/notification/application_core.properties,/etc/zoom/notification/application_notification.properties,/etc/zoom/notification/db.properties"


RUN sh -c "printenv | grep JAVA_OPTS" \
    && unzip -q /code/eventConsumer.zip \
    && rm /code/eventConsumer.zip \
    && unzip -q /code/notificationConsumer.zip \
    && rm /code/notificationConsumer.zip \
    && sed -i "s%DEFAULT_JVM_OPTS=\"\"%DEFAULT_JVM_OPTS=\"$EVENT_JAVA_OPTS\"%g" /code/eventConsumer/bin/eventConsumer \
    && sed -i "s%DEFAULT_JVM_OPTS=\"\"%DEFAULT_JVM_OPTS=\"$NOTIFICATION_JAVA_OPTS\"%g" /code/notificationConsumer/bin/notificationConsumer

ENTRYPOINT exec /usr/bin/supervisord
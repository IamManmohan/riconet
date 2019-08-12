FROM palashgoel/supervisord-jre
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

ENV COMMON_JAVA_OPTS="-Xms$XMX_XMS_VALUE \
    -Xmx$XMX_XMS_VALUE \
    -Dlogin.profiles.active=$LOGIN_PROFILE \
    -Dspring.profiles.active=$SPRING_PROFILE  \
    -Dzoom.neo4j.url=$NEO4J_URL \
    -Dzoom.mongo.hostname=$MONGO_HOST \
    -Dzoom.mongo.port=$MONGO_PORT \
    -Dzoom.mongo.db=$MONGO_DB \
    -Dread.zoom.mysql.jdbcUrl=$BACKEND_READ_JDBC_URL \
    -Dzoom.mysql.url=$BACKEND_READ_JDBC_URL" \
    EVENT_JAVA_OPTS=$COMMON_JAVA_OPTS" \
    -Dspring.config.location=file:/etc/zoom/event/core/application_core.properties,file:/etc/zoom/event/application.properties,file:/etc/zoom/event/core/db.properties" \
    NOTIFICATION_JAVA_OPTS=$COMMON_JAVA_OPTS" \
    -Dspring.config.location=file:/etc/zoom/notification/core/application_core.properties,file:/etc/zoom/notification/application.properties,file:/etc/zoom/notification/core/db.properties"


RUN printenv | grep JAVA_OPTS && $COMMON_JAVA_OPTS unzip -q /code/eventConsumer.zip \
    && rm /code/eventConsumer.zip \
    && unzip -q /code/notificationConsumer.zip \
    && rm /code/notificationConsumer.zip \
    && sed -i "s%DEFAULT_JVM_OPTS=\"\"%DEFAULT_JVM_OPTS=\"$EVENT_JAVA_OPTS\"%g" /code/eventConsumer/bin/eventConsumer \
    && sed -i "s%DEFAULT_JVM_OPTS=\"\"%DEFAULT_JVM_OPTS=\"$NOTIFICATION_JAVA_OPTS\"%g" /code/notificationConsumer/bin/notificationConsumer

ENTRYPOINT exec /usr/bin/supervisord
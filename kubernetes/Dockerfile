FROM rivigotech/openjdk-supervisor:8u212-jre-alpine
WORKDIR /code/
ARG SPRING_PROFILE=kubernetes
ARG LOGIN_PROFILE=staging
ARG XMX_XMS_VALUE=512m
ARG BACKEND_READ_JDBC_URL=jdbc:mysql://host.docker.internal:3306/rivigo_zoom
ARG OUT_TOPIC_PREFIX=local
ARG NEO4J_URL=http://host.docker.internal:7474
ARG PROPERTIES_PATH_EVENT=eventConsumer/src/main/resources/kubernetes/
ARG PROPERTIES_PATH_NOTIFICATION=notificationConsumer/src/main/resources/kubernetes/
ARG EXTRA_ARGS="-XX:+UseG1GC"

COPY eventConsumer/build/distributions/eventConsumer.zip /code/
COPY notificationConsumer/build/distributions/notificationConsumer.zip /code/
COPY core/src/main/resources/kubernetes/supervisord.conf /etc/supervisord.conf
COPY $PROPERTIES_PATH_EVENT /etc/zoom/event/
COPY $PROPERTIES_PATH_NOTIFICATION /etc/zoom/notification/

ENV COMMON_JAVA_OPTS="$EXTRA_ARGS -Xms$XMX_XMS_VALUE \
    -Xmx$XMX_XMS_VALUE \
    -Dlogin.profiles.active=$LOGIN_PROFILE \
    -Dspring.profiles.active=$SPRING_PROFILE  \
    -Dzoom.neo4j.url=$NEO4J_URL \
    -Dread.zoom.mysql.jdbcUrl=$BACKEND_READ_JDBC_URL \
    -Dzoom.mysql.url=$BACKEND_READ_JDBC_URL \
    -Dtopic.prefix.out=$OUT_TOPIC_PREFIX"


#https://stackoverflow.com/questions/35184558/multiple-app-nodes-how-to-expose-jmx-in-kubernetes?rq=1

ENV EVENT_JAVA_OPTS="$COMMON_JAVA_OPTS \
    -Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.local.only=false \
    -Dcom.sun.management.jmxremote.port=1098 \
    -Dcom.sun.management.jmxremote.rmi.port=1098 \
    -Djava.rmi.server.hostname=127.0.0.1 \
    -Dspring.config.location=/etc/zoom/event/application_core.properties,/etc/zoom/event/application_event.properties,/etc/zoom/event/db.properties"

ENV NOTIFICATION_JAVA_OPTS=" $COMMON_JAVA_OPTS \
    -Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.local.only=false \
    -Dcom.sun.management.jmxremote.port=1097 \
    -Dcom.sun.management.jmxremote.rmi.port=1097 \
    -Djava.rmi.server.hostname=127.0.0.1 \
    -Dspring.config.location=/etc/zoom/notification/application_core.properties,/etc/zoom/notification/application_notification.properties,/etc/zoom/notification/db.properties"


RUN sh -c "printenv | grep JAVA_OPTS" \
    && unzip -q /code/eventConsumer.zip \
    && rm /code/eventConsumer.zip \
    && unzip -q /code/notificationConsumer.zip \
    && rm /code/notificationConsumer.zip \
    && sed -i "s%DEFAULT_JVM_OPTS=\"\"%DEFAULT_JVM_OPTS=\"$EVENT_JAVA_OPTS\"%g" /code/eventConsumer/bin/eventConsumer \
    && sed -i "s%DEFAULT_JVM_OPTS=\"\"%DEFAULT_JVM_OPTS=\"$NOTIFICATION_JAVA_OPTS\"%g" /code/notificationConsumer/bin/notificationConsumer

ENTRYPOINT exec /usr/bin/supervisord
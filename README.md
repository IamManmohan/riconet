## Setting up the environment

##### git repository: https://bitbucket.org/rivigotech/riconet

The project assumes running zookeeper instance on localhost:2181 and a running kafka instance at localhost:9092

##### Setting up:
Macos
-----
Installing:

$:/> brew install zookeeper
$:/> brew install kafka
<br>
<br>
Running:

$:/> brew services start zookeeper
$:/> brew services start kafka

Creating a topic via command line:
$:/> kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic com.rivigo.riconet.core.test

Linux
-----

Installing:
sudo apt-get install zookeeperd
Download Kafka : wget "http://mirror.cc.columbia.edu/pub/software/apache/kafka/0.8.2.1/kafka_2.11-0.8.2.1.tgz" -O ~/Downloads/kafka.tgz
Untar and go to source.

Running:
In Source, Run the following
Start ZooKeeper: ./bin/zookeeper-server-start.sh config/zookeeper.properties
Start Kafka: ./bin/kafka-server-start.sh config/server.properties

Running the producer:
---------------------
$:/> ./gradlew :producer:run

Running the consumer:
---------------------
$:/> ./gradlew :consumer:run

###### Producer properties can be viewed at
<project_root>/producer/src/main/resources/application.conf

###### Producer properties can be viewed at
<project_root>/producer/src/main/resources/application.conf

Reference link for the library:
http://doc.akka.io/docs/akka-stream-kafka/current/home.html




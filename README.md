## Setting up the environment

##### git repository: https://ashu_rivigo@bitbucket.org/ashu_rivigo/kafka-demo.git

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
$:/> kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test

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




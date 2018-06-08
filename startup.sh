#!/bin/bash

# folder where your app is located
APP_PATH='<APP_PATH>'

### BUILD
echo 'Building projects...'
cd $APP_PATH
cd batch/spark && sbt clean package assembly && cd ../..
cd streaming/spark && sbt clean package assembly && cd ../..
cd streaming/flink && sbt clean package assembly && cd ../..
cd docker

# LOAD CONTAINERS
echo 'Loading Docker containers...'
docker network create vnet
docker-compose -f mongo.yml -f zookeeper.yml -f kafka.yml -f hadoop-hbase.yml -f flink.yml up -d
docker-compose -f dev/webapp.yml up -d --build
docker-compose -f dev/batch-spark.yml up -d --build
docker-compose -f dev/streaming-spark.yml up -d --build
docker-compose -f dev/streaming-flink.yml up -d --build

# OPEN TERMINALS WITH LOGS
echo 'Opening Logs...'
gnome-terminal -- docker logs -f docker_mongo_1 &
gnome-terminal -- docker logs -f docker_kafka_1 &
gnome-terminal -- docker logs -f zookeeper-1 &
gnome-terminal -- docker logs -f hmaster-1 &
gnome-terminal -- docker logs -f regionserver-1 &
gnome-terminal -- docker logs -f docker_jobmanager_1 &
gnome-terminal -- docker logs -f namenode-1 &
gnome-terminal -- docker logs -f datanode-1 &
gnome-terminal -- docker logs -f docker_taskmanager_1 &
gnome-terminal -- docker logs -f streaming_flink_dev &
gnome-terminal -- docker logs -f webapp-dev &

echo 'Done.'
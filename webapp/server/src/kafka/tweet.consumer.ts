import { Consumer, KeyedMessage, KafkaClient, Client } from 'kafka-node';
import { Type as AvroType } from 'avsc/lib';
import * as winston from 'winston';
import { pubsub } from '../schema';
import { ITweet } from '../db';

export class KafkaTweetConsumer {

    private client: KafkaClient;
    private consumer: Consumer;
    private topic = 'tweetsTopic';

    constructor() {
        this.client = new KafkaClient({ kafkaHost: 'kafka:9092' });
        this.client.on('connect', () => {
            winston.info('Tweet Kafka Client connected to Kafka');
        });
        this.client.on('error', (error) => {
            winston.error('Tweet Kafka Client - error > ', error);
        });

        this.consumer = new Consumer(this.client,
            [
                { topic: this.topic, partition: 0 }
            ],
            {
                autoCommit: false
            });

        this.consumer.on('error', (error) => {
            winston.error('Tweet Kafka Consumer - error > ', error);
        });
        this.consumer.on('message', (message: any) => {
            this.consumeTweets(message);
        });
    }

    async consumeTweets(message) {
        let tweets = message.value as Array<ITweet>;
        pubsub.publish('tweets', { getTweets: tweets });
    }
}

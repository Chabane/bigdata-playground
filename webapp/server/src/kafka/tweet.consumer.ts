import { Consumer, KeyedMessage, KafkaClient, Client } from 'kafka-node';
import { Type as AvroType } from 'avsc/lib';
import * as winston from 'winston';
import { ITweet } from '../db';
import { pubsub } from '../schema';

export class KafkaTweetConsumer {

    private client: KafkaClient;
    private consumer: Consumer;
    private topic = 'tweetsTopic';

    constructor() {
        this.client = new KafkaClient({ kafkaHost: 'kafka.vnet:9092' });
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
        let tweets: Array<ITweet>;
        const schemaType = AvroType.forSchema({
            type: 'array',
            name: 'tweets',
            items: {
                type: 'record', 
                name: 'tweet',
                fields:
                    [
                        { name: 'id', type: 'string' }
                    ]
            }
        });
        const buf = new Buffer(message.value, 'binary');
        tweets = schemaType.fromBuffer(buf);
        pubsub.publish('tweets', { data: tweets });
    }
}

import { Consumer, KeyedMessage, KafkaClient, Client } from 'kafka-node';
import { parse, Type as AvroType } from 'avsc/lib';
import * as winston from 'winston';
import { ITweet } from "../db";
import { pubsub } from '../schema';

export class KafkaTweetConsumer {

    private client: KafkaClient;
    private consumer: Consumer;
    private topic = 'flightInfoTopic';

    constructor() {
        this.client = new KafkaClient({ kafkaHost: 'kafka.vnet:9092' });
        this.client.on('connect', () => {
            winston.info('Connected to Kafka');
        });
        this.client.on('error', (error) => {
            winston.error("Kafka - Client error > ", error);
        });

        this.consumer = new Consumer(this.client,
        [
            { topic: 'tweetsTopic', partition: 0 }
        ],
        { 
            autoCommit: false, 
            fetchMaxWaitMs: 1000, 
            fetchMaxBytes: 1024 * 1024 
        });

        this.consumer.on('error', (error) => {
            winston.error("Kafka - consumer error > ", error);
        });
        this.consumer.on('message', (message: any) => {
            this.consumeTweets(message);
        });
    }

    async consumeTweets(message) {
        let tweets: Array<ITweet>;
        const schemaType = AvroType.forSchema({
            type: 'record',
            name: 'tweet',
            fields: [
                { name: 'id', type: 'string' }
            ]
        });

        tweets = schemaType.fromBuffer(message);
        pubsub.publish('tweets', { data: tweets });
    }
}

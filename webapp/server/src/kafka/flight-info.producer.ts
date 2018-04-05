import { Producer, KeyedMessage, KafkaClient, Client } from 'kafka-node';
import { Type as AvroType } from 'avsc/lib';
import * as winston from 'winston';
import { FlightInfoAvro } from './flight-info-avro';
import { FlightInfoAvroMapper } from './flight-info-avro.mapper';
import { IFlightInfo } from '../db';

export class KafkaFlightInfoProducer {

    private client: KafkaClient;
    private producer: Producer;
    private topic = 'flightInfoTopic';

    constructor() {
        this.client = new KafkaClient({ kafkaHost: 'kafka:9092' });
        this.client.on('connect', () => {
            winston.info('Flight-Info Kafka Client connected to Kafka');
        });
        this.client.on('error', (error) => {
            winston.error('Flight-Info Kafka Client - error > ', error);
        });

        this.producer = new Producer(this.client, { requireAcks: 1 });
        this.producer.on('error', (error) => {
            winston.error('Flight-Info Kafka Producer - error > ', error);
        });
        this.producer.on('ready', () => {
            winston.info('Flight-Info Kafka Producer ready');
        });
    }

    sendFlightInfo(flightInfo: IFlightInfo) {
        const schemaType = AvroType.forSchema({
            type: 'record',
            name: 'flightInfo',
            fields: [
                { name: 'departingId', type: 'string' },
                { name: 'arrivingId', type: 'string' },
                { name: 'tripType', type: { type: 'enum', name: 'TripType', symbols: ['ONE_WAY', 'ROUND_TRIP'] } },
                { name: 'departureDate', type: 'string' },
                { name: 'arrivalDate', type: 'string' },
                { name: 'passengerNumber', type: 'int' },
                { name: 'cabinClass', type: { type: 'enum', name: 'CabinClass', symbols: ['ECONOMY', 'PRENIUM', 'BUSINESS'] } }
            ]
        });

        const flightInfoAvro: FlightInfoAvro = FlightInfoAvroMapper.toFlightInfoAvro(flightInfo);
        const buffer = schemaType.toBuffer(flightInfoAvro);
        const keyedMessage = new KeyedMessage('flightInfo', <any>buffer);

        this.producer.send([
            { topic: this.topic, partition: 0, messages: keyedMessage }
        ], (error, result) => {
            if (error) {
                winston.error('Flight-Info Kafka Producer - Message was not sent to consumers > ', error);
            }
            if (result) {
                winston.info('Flight-Info Kafka Producer - Message sent to consumers > ', result);
            }
        });
    }
}

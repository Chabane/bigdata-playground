import { Producer, KeyedMessage, KafkaClient, Client } from 'kafka-node';
import { parse, Type as AvroType } from 'avsc/lib';
import * as winston from 'winston';
import { FlightInfoAvro } from './flight-info-avro';
import { FlightInfoAvroMapper } from './flight-info-avro.mapper';
import { IFlightInfo } from "../db";

export class KafkaProducer {

    private producer: Producer;
    private topic = 'flightInfoTopic';

    constructor() {
        const client = new KafkaClient({ kafkaHost: 'kafka.vnet:9092' });
        // const client = new Client('zookeeper-1.vnet:2181');
        client.on('connect', () => {
            winston.info('Connected to Kafka');
        });
        client.on('error', (error) => {
            winston.error("Kafka Client error >", error);
        });

        this.producer = new Producer(client, { requireAcks: 1 });
        this.producer.on('error', (error) => {
            winston.error("Kafka Producer error >", error);
        });
    }

    sendFlightInfo(flightInfo: IFlightInfo) {
        this.producer.on('ready', () => {
            const schemaType = AvroType.forSchema({
                type: 'record',
                name: 'flightInfo',
                fields: [
                    { name: 'departingId', type: 'string' },
                    { name: 'arrivingId', type: 'string' },
                    { name: 'tripType', type: { type: 'enum', name: 'TripType', symbols: ['ONE_WAY', 'ROUND_TRIP'] } },
                    { name: 'departureDate', type: { type: 'long', logicalType: 'timestamp-millis' } },
                    { name: 'arrivalDate', type: { type: 'long', logicalType: 'timestamp-millis' } },
                    { name: 'passengerNumber', type: 'int' },
                    { name: 'cabinClass', type: { type: 'enum', name: 'CabinClass', symbols: ['ECONOMY', 'PRENIUM', 'BUSINESS'] } }
                ]
            });

            const flightInfoAvro: FlightInfoAvro = FlightInfoAvroMapper.toFlightInfoAvro(flightInfo);
            const buffer = schemaType.toBuffer(flightInfoAvro);
            const keyedMessage = new KeyedMessage('key', <any>buffer);

            winston.info('Message sent to consumers');
            this.producer.send([
                { topic: this.topic, partition: 0, messages: keyedMessage }
            ], (error) => {
                winston.error("Kafka Send error >", error);
            });
        });
    }
}

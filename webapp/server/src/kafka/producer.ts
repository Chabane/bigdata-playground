import { Producer, KeyedMessage, KafkaClient } from 'kafka-node';
import { parse, Type as AvroType } from 'avsc/lib';
import * as winston from 'winston';
import { FlightInfoAvro } from './flight-info-avro';
import { FlightInfoAvroMapper } from './flight-info-avro.mapper';
import { IFlightInfo } from "../db";

export class KafkaProducer {

    producer: Producer;
    private topic = 'flightInfoTopic';

    constructor() {
        const client = new KafkaClient({ kafkaHost: 'kafka.vnet:9092' });
        client.on('connect', () => {
            winston.info('Connected to Kafka');
        });

        this.producer = new Producer(client, { requireAcks: 1 });
        this.producer.on('ready', () => {
            winston.info('Producer ready to send messages');
        });
        this.producer.on('error', this.onError);
    }

    sendFlightInfo(flightInfo: IFlightInfo) {
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
        this.producer.send([
            { topic: this.topic, partition: 0, messages: keyedMessage }
        ], (err, result) => {
            winston.error(err || result);
        });

    }

    private onError(err) {
        winston.error('error', err);
    }
}

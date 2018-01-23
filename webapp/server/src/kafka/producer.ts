import { Producer, KeyedMessage, Client } from 'kafka-node';
import { parse, Type as AvroType } from 'avsc/lib';
import * as winston from 'winston';

export class KafkaProducer {

    producer: Producer;
    private topic = 'flightInfoTopic';

    constructor() {
        const client = new Client('zookeeper-1.vnet:2181');
        this.producer = new Producer(client, { requireAcks: 1 });
    }

    sendFlightInfo(flightInfo) {
        winston.info('Initializing producer');
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

            const buffer = schemaType.toBuffer(flightInfo); // Encoded buffer.
            const keyedMessage = new KeyedMessage('key', <any>buffer);

            this.producer.send([
                { topic: this.topic, partition: 0, messages: keyedMessage }
            ], (err, result) => {
                winston.error(err || result);
                process.exit();
            });
        });
        this.producer.on('error', this.onError);
    }

    private onError(err) {
        winston.error('error', err);
    }
}

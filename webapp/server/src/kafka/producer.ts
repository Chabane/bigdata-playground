import { Producer, KeyedMessage, Client } from 'kafka-node';
import { parse, Type } from 'avsc/lib';

export class KafkaProducer {

    producer: Producer;
    private topic = 'flightInfoTopic';

    constructor() {
        const client = new Client('192.168.0.32:2181');
        this.producer = new Producer(client, { requireAcks: 1 });
    }

    sendFlightInfo(flightInfo) {
        console.log('Initializing');
        this.producer.on('ready', () => {

            const schemaType = Type.forSchema({
                type: 'record',
                fields: [
                    { name: 'departing', type: 'string' },
                    { name: 'arriving', type: 'string' },
                    { name: 'tripType', type: { type: 'enum', symbols: ['ONE_WAY', 'ROUND_TRIP'] } },
                    { name: 'departingDate', type: {type: 'long', logicalType: 'timestamp-millis'} },
                    { name: 'arrivingDate', type: {type: 'long', logicalType: 'timestamp-millis'} },
                    { name: 'passengerNumber', type: 'int' },
                    { name: 'cabinClass', type: { type: 'enum', symbols: ['ECONOMY', 'PRENIUM', 'BUSINESS'] } }
                ]
            });

            // const buffer = toMessageBuffer({}, schemaType, 1);
            // const keyedMessage = new KeyedMessage('event', <any>buffer);
            const buffer = schemaType.toBuffer(<any>flightInfo);

            const keyedMessage = new KeyedMessage('event', buffer);

            this.producer.send([
                { topic: this.topic, partition: 0, messages: [keyedMessage] }
            ], function (err, result) {
                console.log(err || result);
                process.exit();
            });
        });
        this.producer.on('error', this.onError);
    }

    private onError(err) {
        console.log('error', err);
    }
}

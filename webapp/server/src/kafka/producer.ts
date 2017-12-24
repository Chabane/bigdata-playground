import { Producer, KeyedMessage, Client } from 'kafka-node';
import { parse, Type } from 'avsc/lib';

export class KafkaProducer {

    producer: Producer;
    private topic = 'flightInfoTopic';

    constructor() {
        const client = new Client('192.168.0.32:2181');
        this.producer = new Producer(client, { requireAcks: 1 });
    }

    initialize() {
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
            const buffer = schemaType.toBuffer(<any>{
                departing: 'now',
                arriving: 'bidule',
                tripType: 'ONE_WAY',
                departingDate: 1514125874,
                arrivingDate: 1514125874,
                passengerNumber: 3,
                cabinClass: 'ECONOMY'
            });

            const keyedMessage = new KeyedMessage('event', buffer);

            this.producer.send([
                { topic: this.topic, partition: 1, messages: [keyedMessage] }
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


    /**
     * Encode an Avro value into a message, as expected by Confluent's Kafka Avro
     * deserializer.
     *
     * @param val {...} The Avro value to encode.
     * @param type {Type} Your value's Avro type.
     * @param schemaId {Integer} Your schema's ID (inside the registry).
     * @param length {Integer} Optional initial buffer length. Set it high enough
     * to avoid having to resize. Defaults to 1024.
     *
     */
    private toMessageBuffer(val: any, type, schemaId, length?): any {
        const buf = new Buffer(length || 1024);
        buf[0] = 0; // Magic byte.
        buf.writeInt32BE(schemaId, 1);

        const pos = type.encode(val, buf, 5);
        // if (pos < 0) {
        // The buffer was too short, we need to resize.
        //  return getMessageBuffer(type, val, schemaId, length - pos);
        // }
        return buf.slice(0, pos);
    }
}

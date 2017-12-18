import { Producer, KeyedMessage, Client } from 'kafka-node';
import { parse, Type } from 'avsc/lib';

export class KafkaProducer {

    producer: Producer;
    private topic = 'flightInfoTopic';

    constructor() {
        const client = new Client('zookeeper:2181');
        this.producer = new Producer(client, { requireAcks: 1 });
    }

    initialize() {
        this.producer.on('ready', this.onReady);
        this.producer.on('error', this.onError);
    }

    onReady() {

        console.log("Ready to send!");

        const type = Type.forSchema({
            type: 'record',
            fields: [
                { name: 'departing', type: 'string' },
                { name: 'arriving', type: 'string' },
                { name: 'tripType', type: { type: 'enum', symbols: ['ONE_WAY', 'ROUND_TRIP'] } },
                { name: 'departingDate', type: 'timestamp-millis' },
                { name: 'arrivingDate', type: 'timestamp-millis' },
                { name: 'passengerNumber', type: 'int' },
                { name: 'cabinClass', type: { type: 'enum', symbols: ['ECONOMY', 'PRENIUM', 'BUSINESS'] } },
            ]
        });

        let buffer = this.toMessageBuffer({}, type, 1);
        let keyedMessage = new KeyedMessage("event", <any>buffer);

        let message = 'a message';
        this.producer.send([
            { topic: this.topic, partition: 1, messages: [message, keyedMessage] }
        ], function (err, result) {
            console.log(err || result);
            process.exit();
        });
    }

    onError(err) {
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
    toMessageBuffer(val: any, type, schemaId, length?) {
        var buf = new Buffer(length || 1024);
        buf[0] = 0; // Magic byte.
        buf.writeInt32BE(schemaId, 1);

        var pos = type.encode(val, buf, 5);
        //if (pos < 0) {
        // The buffer was too short, we need to resize.
        //  return getMessageBuffer(type, val, schemaId, length - pos);
        //}
        return buf.slice(0, pos);
    }
}
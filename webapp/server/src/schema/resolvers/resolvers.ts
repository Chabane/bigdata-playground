import { IFlightInfo } from '../../db';
import { KafkaProducer } from '../../kafka';

const producer = new KafkaProducer();

export const resolvers = {
    Query: {
      sendFlightInfo: async (_, { fightInfo }) => {
        producer.sendFlightInfo(fightInfo as IFlightInfo);
        return fightInfo;
      }
  }
};

import { IFlightInfo } from '../../db';
import { KafkaProducer } from '../../kafka';

const producer = new KafkaProducer();

export const resolvers = {
  Mutation: {
      sendFlightInfo: async (_, { fightInfo }) => {
        producer.sendFlightInfo(fightInfo as IFlightInfo);
        return fightInfo;
      }
  }
};

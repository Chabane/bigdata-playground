import { IFlightInfo } from '../../db';
import { KafkaProducer } from '../../kafka';
import { Airport } from '../../db/models/airport';

const producer = new KafkaProducer();

export const resolvers = {
  Query: {
    sendFlightInfo: async (_, { fightInfo }) => {
      producer.sendFlightInfo(fightInfo as IFlightInfo);
      return fightInfo;
    },
    fetchAirports: async (_, { departingFrom }) => {
      const airports = await Airport.findAirports(departingFrom);
      return airports;
    }
  }
};

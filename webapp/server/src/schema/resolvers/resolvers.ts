import { IFlightInfo } from '../../db';
import { KafkaProducer } from '../../kafka';
import { Airport } from '../../db/models/airport';


export const resolvers = {
  Query: {
    fetchAirports: async (_, { airportToSearch, airportId }) => {
      const airports = await Airport.findAirports(airportToSearch, airportId);
      return airports;
    }
  },
  Mutation: {
    sendFlightInfo: async (_, { flightInfo }) => {
      const producer = new KafkaProducer();
      producer.sendFlightInfo(flightInfo as IFlightInfo);
      return flightInfo;
    }
  }
};

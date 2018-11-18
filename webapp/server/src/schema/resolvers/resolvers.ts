import { IFlightInfo } from '../../db';
import { KafkaFlightInfoProducer } from '../../kafka';
import { Airport } from '../../db/models/airport';
import { PubSub, withFilter } from 'graphql-subscriptions';

const producer = new KafkaFlightInfoProducer();
export const pubsub = new PubSub();

export const resolvers = {
  Query: {
    fetchAirports: async (_, { airportToSearch, airportId }) => {
      const airports = await Airport.findAirports(airportToSearch, airportId);
      return airports;
    }
  },
  Mutation: {
    sendFlightInfo: async (_, { flightInfo }) => {
      producer.sendFlightInfo(flightInfo as IFlightInfo);
      return flightInfo;
    }
  },
  Subscription: {
    getTweets: {
      subscribe: () => pubsub.asyncIterator('tweets')
    }
  }
};

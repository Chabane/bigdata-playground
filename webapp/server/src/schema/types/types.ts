/* tslint:disable */
export const typeDefs = `

scalar Date

type FlightInfoType {
  departingId: String
  arrivingId: String
  tripType: String
  departureDate: String
  arrivalDate: String
  passengerNumber: Int
  cabinClass: String
}

input FlightInfoInput {
  departingId: String
  arrivingId: String
  tripType: String
  departureDate: String
  arrivalDate: String
  passengerNumber: Int
  cabinClass: String
}

type Airport {
  AirportID: String
  City: String
  Country: String
  Name: String
  destinations: [String]
}

type Tweet {
  id: Int
}

type Query {
  fetchAirports(airportToSearch: String, airportId: String): [Airport]
}
type Mutation {
  sendFlightInfo(flightInfo: FlightInfoInput): FlightInfoType
}
type Subscription {
  getTweets: [Tweet]
}

schema {
  query: Query,
  mutation : Mutation,
  subscription : Subscription
}
`;
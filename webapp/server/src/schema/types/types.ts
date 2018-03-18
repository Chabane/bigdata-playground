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

# the schema allows the following query:
type Query {
  fetchAirports(airportToSearch: String, airportId: String): [Airport]
}
type Mutation {
  sendFlightInfo(flightInfo: FlightInfoInput): FlightInfoType
}

schema {
  query: Query,
  mutation : Mutation
}
`;
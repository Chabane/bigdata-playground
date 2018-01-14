/* tslint:disable */
export const typeDefs = `

type FlightInfo {
  departing: String
  arriving: String
  tripType: String
  departingDate: Int
  arrivingDate: Int
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
  sendFlightInfo(flightInfo: FlightInfo): FlightInfo
  fetchAirports(departingFrom: String): [Airport]
  fetchDestinationAirports(departingAirportId: String, arrivingAt: String): [Airport]
}

schema {
  query: Query
}
`;
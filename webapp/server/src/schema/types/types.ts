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
   city: String
   airport: String
   code: String
}

# the schema allows the following query:
type Query {
    sendFlightInfo(flightInfo: FlightInfo): FlightInfo
}

schema {
  query: Query
}
`;
/* tslint:disable */
export const typeDefs = `

# temporary fixing 
input FlightInfoInput {
  departing: String
  arriving: String
  tripType: String
  departingDate: Int
  arrivingDate: Int
  passengerNumber: Int
  cabinClass: String
}

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
    sendFlightInfo(flightInfo: FlightInfoInput): FlightInfo
}

schema {
  query: Query
}
`;
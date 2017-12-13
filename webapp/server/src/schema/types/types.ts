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

type PointOfOrigin {
   city: String
   airport: String
   code: String
}

# the schema allows the following query:
type Query {
 
}

schema {
  query: Query
}
`
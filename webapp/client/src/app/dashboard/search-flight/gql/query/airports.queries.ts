export const AIRPORTS_QUERY =
    `query airports($departingFrom:String) {
    fetchAirports(departingFrom:$departingFrom) {
        AirportID
        City
        Country
        Name
        destinations
    }
  }
`;

export const DESTINATION_AIRPORTS_QUERY =
    `query destinationAirports($departingAirportId:String, $arrivingAt:String) {
    fetchDestinationAirports(departingAirportId:$departingAirportId, arrivingAt:$arrivingAt) {
        AirportID
        City
        Country
        Name
        destinations
    }
  }
`;


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

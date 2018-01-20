export const AIRPORTS_QUERY =
    `query airports($airportToSearch:String, $airportId:String) {
        fetchAirports(airportToSearch:$airportToSearch, airportId:$airportId) {
        AirportID
        City
        Country
        Name
        destinations
    }
  }
`;


export const AIRPORTS_QUERY =
    `query airports {
    fetchAirports {
        AirportID
        City
        Country
        Name
        destinations
    }
  }
`;

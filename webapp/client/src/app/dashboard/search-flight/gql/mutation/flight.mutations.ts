export const FLIGHT_INFO_MUTATION =
    `
    mutation flightInfo($flightInfo:FlightInfoInput) {
        sendFlightInfo(flightInfo:$flightInfo){
            departingId
            arrivingId
            tripType
            departureDate
            arrivalDate
            passengerNumber
            cabinClass
        }
  }
`;


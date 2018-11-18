import { AirportDto } from './../gql/dto/';
import { Airport } from './../../../shared/model/';

/**
 * Details of a AirportMapper
 */

export class AirportMapper {

    /**
     * Map Array<AirportDto> to Array<Airport>
     * @param airportsDtoList
     */
    public static toAirports(airportsDtoList: Array<AirportDto>): Array<Airport> {
        let airports;
        if (airportsDtoList) {
            airports = new Array<Airport>();
            airportsDtoList.forEach(airportDtoList => {
                airports.push(this.toAirport(airportDtoList));
            });
        }
        return airports;
    }

    /**
     * Map Array<AirportDto> to Airport
     * @param airportDtoList
     */
    public static toAirport(airportDtoList: AirportDto): Airport {
        let airport;
        if (airportDtoList) {
            airport = new Airport();
            airport.AirportID = airportDtoList.AirportID;
            airport.City = airportDtoList.City;
            airport.Country = airportDtoList.Country;
            airport.Name = airportDtoList.Name;
            airport.destinations = airportDtoList.destinations;
        }
        return airport;
    }
}

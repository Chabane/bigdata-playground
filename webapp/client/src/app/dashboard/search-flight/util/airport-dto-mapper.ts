import { AirportDto } from './../gql/dto/';

/**
 * Details of a AirportDtoMapper
 */

export class AirportDtoMapper {

    /**
     * Map response to Array<AirportDto>
     * @param airports
     */
    public static toAirportsDto(airports: any): Array<AirportDto> {
        let airportDto;
        if (airports) {
            airportDto = new Array<AirportDto>();
            airports.forEach(airport => {
                airportDto.push(this.toAirportDto(airport));
            });
        }
        return airportDto;
    }

    /**
     * Map response to AirportDto
     * @param airport
     */
    public static toAirportDto(airport: AirportDto): AirportDto {
        let airportDto;
        if (airport) {
            airportDto = new AirportDto();
            airportDto.AirportID = airport.AirportID;
            airportDto.City = airport.City;
            airportDto.Country = airport.Country;
            airportDto.Name = airport.Name;
            airportDto.destinations = airport.destinations;
        }
        return airportDto;
    }
}

import { IFlightInfo } from '../db';
import { FlightInfoAvro } from './flight-info-avro';

export class FlightInfoAvroMapper {

    public static toFlightInfoAvro(flightInfo: IFlightInfo): FlightInfoAvro {
        let flightInfoAvro: FlightInfoAvro;
        if (flightInfo) {
            flightInfoAvro = new FlightInfoAvro();
            flightInfoAvro.arrivalDate = flightInfo.arrivalDate;
            flightInfoAvro.arrivingId = flightInfo.arrivingId;
            flightInfoAvro.cabinClass = flightInfo.cabinClass;
            flightInfoAvro.departingId = flightInfo.departingId;
            flightInfoAvro.departureDate = flightInfo.departureDate;
            flightInfoAvro.ipAddress = undefined;
            flightInfoAvro.latitude = undefined;
            flightInfoAvro.longitude = undefined;
            flightInfoAvro.passengerNumber = flightInfo.passengerNumber;
            flightInfoAvro.tripType = flightInfo.tripType;
            flightInfoAvro.eventTime = new Date().getTime();
        }
        return flightInfoAvro;
    }
}

import { Airport } from './airport.model';

export enum TripType {
  ONE_WAY = 'ONE_WAY',
  ROUND_TRIP = 'ROUND_TRIP'
}

export enum CabinClass {

  ECONOMY = 'ECONOMY',
  PRENIUM = 'PRENIUM',
  BUSINESS = 'BUSINESS'
}

export class FlightInfo {
  departing: Airport;
  arriving: Airport;
  tripType: TripType;
  departingDate: Date;
  arrivingDate: Date;
  passengerNumber: number;
  cabinClass: CabinClass;
}

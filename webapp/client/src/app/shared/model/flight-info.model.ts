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
  departingId: string;
  arrivingId: string;
  tripType: TripType;
  departureDate: string;
  arrivalDate: string;
  passengerNumber: number;
  cabinClass: CabinClass;
}

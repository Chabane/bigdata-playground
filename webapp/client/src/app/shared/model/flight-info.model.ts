import { PointOfOrigin } from './point-of-origin.model';

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
  departing: PointOfOrigin;
  arriving: PointOfOrigin;
  tripType: TripType;
  departingDate: Date;
  arrivingDate: Date;
  passengerNumber: number;
  cabinClass: CabinClass;
}

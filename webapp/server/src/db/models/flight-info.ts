
export interface IFlightInfo extends Document {
  departing: string;
  arriving: string;
  tripType: string;
  departingDate: number;
  arrivingDate: number;
  passengerNumber: number;
  cabinClass: string;
}

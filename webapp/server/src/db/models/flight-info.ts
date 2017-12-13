import { mongoose } from '../config/database';
import { Schema, Document, Model } from 'mongoose';
import * as winston from 'winston';

export interface IFlightInfo extends Document {
  departing: string;
  arriving: string;
  tripType: string;
  departingDate: number;
  arrivingDate: number;
  passengerNumber: number;
  cabinClass: string;
}

export interface IFlightInfoModel extends Model<IFlightInfo> {
}

// create a schema
const schema = new Schema({
  id: String,
  departing: String,
  arriving: String,
  tripType: String,
  departingDate: Number,
  arrivingDate: Number,
  passengerNumber: Number,
  cabinClass: String
});

export const flight = mongoose.model<IFlightInfo>('flightInfo', schema) as IFlightInfoModel;

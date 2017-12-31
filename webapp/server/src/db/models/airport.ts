import { mongoose } from '../config/database';
import { Schema, Document, Model } from 'mongoose';
import * as winston from 'winston';

export interface IAirport extends Document {
  AirportID: string;
  City: string;
  Country: string;
  Name: string;
  destinations: Array<string>;
}

export interface IAirportModel extends Model<IAirport> {
  findAirports(): Promise<IAirport>;
}

// create a schema
const schema = new Schema({
  AirportID: String,
  City: String,
  Country: String,
  Name: String,
  destinations: Array<String>()
});

// retrieve list of airports
schema.static('findAirports', () => {
  return Airport
    .find()
    .exec();
});

export const Airport = mongoose.model<IAirport>('Airport', schema) as IAirportModel;


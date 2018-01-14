import { mongoose } from '../config/database';
import { Schema, Document, Model } from 'mongoose';
import * as winston from 'winston';
import { Decimal128 } from 'bson';

export interface IAirport extends Document {
  AirportID: string;
  City: string;
  Country: string;
  Name: string;
  destinations: Array<string>;
}

export interface IAirportModel extends Model<IAirport> {
  findAirports(departingFrom: string): Promise<IAirport>;
  findDestinationAirports(departingAirportId: string, arrivingAt: string): Promise<IAirport>;
}

// create a schema
const schema = new Schema({
  AirportID: String,
  City: String,
  Country: String,
  Name: String,
  destinations: Array<String>()
});

schema.index(
  {
    City: 'text',
    Country: 'text',
    Name: 'text'
  },
);

// retrieve list of airports
schema.static('findAirports', (departingFrom) => {
  return Airport
    .find()
    .or([
      { 'Name': new RegExp(departingFrom, 'i') },
      { 'Country': new RegExp(departingFrom, 'i') },
      { 'City': new RegExp(departingFrom, 'i') }])
    .limit(10)
    .exec();
});

// retrieve list of destination airports
schema.static('findDestinationAirports', (departingAirportId, arrivingAt) => {
  return Airport
    .find()
    .where('destinations', departingAirportId)
    .or([
      { 'Name': new RegExp(arrivingAt, 'i') },
      { 'Country': new RegExp(arrivingAt, 'i') },
      { 'City': new RegExp(arrivingAt, 'i') }])
    .limit(10)
    .exec();
});
export const Airport = mongoose.model<IAirport>('Airport', schema) as IAirportModel;


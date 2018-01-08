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
  console.log('------------', '\/' + departingFrom + '\/');
  return Airport
    .find()
    .or([
      { 'Name': new RegExp(departingFrom + '$', 'i') },
      { 'Country': new RegExp(departingFrom + '$', 'i') },
      { 'City': new RegExp(departingFrom + '$', 'i') }])
    .limit(10)
    .exec();
});
export const Airport = mongoose.model<IAirport>('Airport', schema) as IAirportModel;


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
  findAirports(airportToSearch: string, airportId?: string): Promise<IAirport>;
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
schema.static('findAirports', (airportToSearch, airportId) => {

  if (airportId === undefined) {
    return Airport
      .find()
      .or([
        { 'Name': new RegExp(airportToSearch, 'i') },
        { 'Country': new RegExp(airportToSearch, 'i') },
        { 'City': new RegExp(airportToSearch, 'i') }])
      .limit(10)
      .exec();
  } else {
    return Airport
      .find()
      .where('destinations', airportId)
      .or([
        { 'Name': new RegExp(airportToSearch, 'i') },
        { 'Country': new RegExp(airportToSearch, 'i') },
        { 'City': new RegExp(airportToSearch, 'i') }])
      .limit(10)
      .exec();
  }

});
export const Airport = mongoose.model<IAirport>('Airport', schema) as IAirportModel;


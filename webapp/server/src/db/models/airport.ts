import { mongoose } from '../config/database';
import { Schema, Document, Model } from 'mongoose';
import * as winston from 'winston';

export interface IAirport extends Document {
  city: string;
  airport: string;
  code: string;
}

export interface IAirportModel extends Model<IAirport> {
}

// create a schema
const schema = new Schema({
  city: String,
  airport: String,
  code: String
});

export const Airport = mongoose.model<IAirport>('Airport', schema) as IAirportModel;


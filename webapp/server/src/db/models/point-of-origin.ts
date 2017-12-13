import { mongoose } from '../config/database';
import { Schema, Document, Model } from 'mongoose';
import * as winston from 'winston';

export interface IPointOfOrigin extends Document {
  city: string;
  airport: string;
  code: string;
}

export interface IpointOfOriginModel extends Model<IPointOfOrigin> {
  
}

// create a schema
const schema = new Schema({
  city: String,
  airport: String,
  code: String
});

export const PointOfOrigin = mongoose.model<IPointOfOrigin>('pointOfOrigin', schema) as IpointOfOriginModel;


import * as mongoose from 'mongoose';
import * as winston from 'winston';

mongoose.connect('mongodb://mongo:27017/mitosis');
const db = mongoose.connection;

db.on('error', console.error.bind(console, 'connection error:'));
db.once('openUri', function () {
  winston.info('Connected to MongoDB');
});

export { mongoose };



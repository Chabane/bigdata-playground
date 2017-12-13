import * as express from "express";
import { createServer } from 'http';
import { graphqlExpress, graphiqlExpress } from 'apollo-server-express';
import { makeExecutableSchema } from 'graphql-tools';
import * as path from 'path';
import * as morgan from 'morgan';
import * as bodyParser from 'body-parser';
import * as winston from 'winston';
import { serveStatic } from 'serve-static';
import { execute, subscribe } from 'graphql';

import * as db from './db';
import { typeDefs, resolvers } from './schema';

export class Server {
  app: express.Application;

  /**
   * Bootstrap the application.
   *
   * @class Server
   * @method bootstrap
   * @static
   * @return {ng.auto.IInjectorService} Returns the newly created injector for this app.
   */
  static bootstrap(): Server {
    return new Server();
  }

  constructor() {
    this.app = express();
    this.app.use(bodyParser.json());
    this.app.use(bodyParser.urlencoded({ extended: false }));

    this.app.use(morgan('dev'));

    /*
    let executableSchema = makeExecutableSchema({
      typeDefs: typeDefs as any,
      resolvers: resolvers as any
    });

    this.app.use('/gql', graphqlExpress({
      schema: executableSchema
    }));
    */

    this.app.use('/', express.static(path.join(__dirname, '../public')));
    // all other routes are handled by Angular
    this.app.get('/*', function (req, res) {
      res.sendFile(path.join(__dirname, '../public/index.html'));
    });
  }
}
import * as express from 'express';
import { graphqlExpress } from 'apollo-server-express';
import { makeExecutableSchema } from 'graphql-tools';
import { SubscriptionServer } from 'subscriptions-transport-ws';
import { execute, subscribe } from 'graphql';
import { createServer } from 'http';

import * as path from 'path';
import * as morgan from 'morgan';
import * as bodyParser from 'body-parser';
import * as winston from 'winston';

import { typeDefs, resolvers } from './schema';
import { KafkaTweetConsumer } from './kafka';

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

    const executableSchema = makeExecutableSchema({
      typeDefs: typeDefs as any,
      resolvers: resolvers as any
    });

    this.app.use('/gql', graphqlExpress({
      schema: executableSchema
    }));

    const websocketServer = createServer((request, response) => {
      response.writeHead(404);
      response.end();
    });

    websocketServer.listen(5000, () => winston.info('Listening on port ' + 5000));

    const subscriptionServer = SubscriptionServer.create(
      {
        schema: executableSchema,
        execute: execute,
        subscribe: subscribe
      },
      {
        server: websocketServer,
        path: '/gql-ws',
      },
    );

    this.app.use('/', express.static(path.join(__dirname, '../public')));
    // all other routes are handled by Angular
    this.app.get('/*', function (req, res) {
      res.sendFile(path.join(__dirname, '../public/index.html'));
    });

    const consumer = new KafkaTweetConsumer();
  }
}

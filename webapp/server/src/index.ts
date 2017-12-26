import * as express from 'express';
import { graphqlExpress } from 'apollo-server-express';
import { makeExecutableSchema } from 'graphql-tools';
import * as path from 'path';
import * as morgan from 'morgan';
import * as bodyParser from 'body-parser';

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

    const executableSchema = makeExecutableSchema({
      typeDefs: typeDefs as any,
      resolvers: resolvers as any
    });

    this.app.use('/gql', graphqlExpress({
      schema: executableSchema
    }));

    this.app.use('/', express.static(path.join(__dirname, '../public')));
    // all other routes are handled by Angular
    this.app.get('/*', function (req, res) {
      res.sendFile(path.join(__dirname, '../public/index.html'));
    });

  }
}

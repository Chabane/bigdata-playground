#!/usr/bin/env node
'use strict';

//module dependencies
var server = require('./dist/src');
var debug = require('debug')('express:server');
var http = require('http');
var winston = require('winston');

//create http server
var port = normalizePort(process.env.PORT || 4000);

var bootstrap = server.Server.bootstrap();
var app = bootstrap.app;
var apolloServer = bootstrap.apolloServer;

apolloServer.applyMiddleware({ app });

app.set('port', port);

app.listen({ port: port }, () =>
  console.log(`🚀 Server ready at http://localhost:4000${apolloServer.graphqlPath}`)
);

var httpServer = http.createServer(app);

//add error handler
httpServer.on('error', onError);

//start listening on port
httpServer.on('listening', onListening);

/**
 * Normalize a port into a number, string, or false.
 */
function normalizePort(val) {
  var port = parseInt(val, 10);

  if (isNaN(port)) {
    // named pipe
    return val;
  }

  if (port >= 0) {
    // port number
    return port;
  }

  return false;
}

/**
 * Event listener for HTTP server 'error' event.
 */
function onError(error) {
  if (error.syscall !== 'listen') {
    throw error;
  }

  var bind = typeof port === 'string'
    ? 'Pipe ' + port
    : 'Port ' + port;

  // handle specific listen errors with friendly messages
  switch (error.code) {
    case 'EACCES':
      winston.error(bind + ' requires elevated privileges');
      process.exit(1);
      break;
    case 'EADDRINUSE':
      winston.error(bind + ' is already in use');
      process.exit(1);
      break;
    default:
      throw error;
  }
}

/**
 * Event listener for HTTP server 'listening' event.
 */
function onListening() {
  var addr = httpServer.address();
  var bind = typeof addr === 'string'
    ? 'pipe ' + addr
    : 'port ' + addr.port;
  winston.info('Listening on ' + bind);
}
FROM node:boron

# Create app directory
RUN mkdir -p /usr/src/app/client
RUN mkdir -p /usr/src/app/server

WORKDIR /usr/src/app/client
# Bundle client source
ADD client .
# Install client dependencies
RUN yarn

WORKDIR /usr/src/app/server
# Bundle server source
ADD server .
# Install server dependencies
RUN yarn

WORKDIR /usr/src/app
ADD package.json package.json

RUN yarn

RUN npm run build:prod

EXPOSE 3000 5000
CMD [ "npm", "run", "start:prod" ]
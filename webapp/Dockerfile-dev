FROM node:boron


# Create app directory
RUN mkdir -p /usr/src/app/server

WORKDIR /usr/src/app/server
# Bundle server source
ADD server .

WORKDIR /usr/src/app
ADD package.json package.json

RUN yarn

EXPOSE 4000 5000
CMD [ "npm", "run", "start:prod" ]
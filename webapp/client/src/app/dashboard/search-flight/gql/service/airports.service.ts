import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import { AIRPORTS_QUERY } from '../query/airports.queries';

import gql from 'graphql-tag';

@Injectable()
export class AirportsService {
    constructor(private apollo: Apollo) {
    }

    getAirports() {
        return this.apollo.query({ query: gql`${AIRPORTS_QUERY}` });
    }
}

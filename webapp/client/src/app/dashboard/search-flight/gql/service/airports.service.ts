import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import gql from 'graphql-tag';

import { AIRPORTS_QUERY } from '../query/airports.queries';

@Injectable()
export class AirportsService {
    constructor(private apollo: Apollo) {
    }

    getAirports(airportToSearch: string, airportId?: string) {
        return this.apollo.query({
            query: gql`${AIRPORTS_QUERY}`,
            variables: {
                airportId: airportId,
                airportToSearch: airportToSearch
            },
            fetchPolicy: 'network-only'
        });
    }


}

import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import gql from 'graphql-tag';

import { AIRPORTS_QUERY } from '../query/airports.queries';
import { FLIGHT_INFO_MUTATION } from '../mutation/flight.mutations';
import { FlightInfo } from '../../../../shared/model/flight-info.model';


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

    sendFlightInfo(flightInfo: FlightInfo) {
        return this.apollo.mutate({
            mutation: gql`${FLIGHT_INFO_MUTATION}`,
            variables: {
                flightInfo: flightInfo
            }
        });
    }
}

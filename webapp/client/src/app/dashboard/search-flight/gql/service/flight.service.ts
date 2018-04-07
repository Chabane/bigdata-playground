import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import gql from 'graphql-tag';

import { FLIGHT_INFO_MUTATION } from '../mutation/flight.mutations';
import { FlightInfo } from '../../../../shared/model/flight-info.model';

@Injectable()
export class FlightService {
    constructor(private apollo: Apollo) {
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

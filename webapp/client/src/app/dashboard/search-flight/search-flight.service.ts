import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { Apollo } from 'apollo-angular';
import gql from 'graphql-tag';

import { FlightInfo } from '../../shared/model/flight-info.model';

@Injectable()
export class SearchFlightService {
  constructor(private apollo: Apollo) {
  }
}

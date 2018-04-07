import { Injectable } from '@angular/core';
import { Apollo } from 'apollo-angular';
import gql from 'graphql-tag';
/* tslint:disable */
import { Observable } from 'rxjs/Rx';

import { GET_TWEETS } from '../subscription/tweet.subscription';

@Injectable()
export class TweetService {
    constructor(private apollo: Apollo) {
    }

    getTweets(): Observable<any> {
      return this.apollo.subscribe({
        query: gql`${GET_TWEETS}`
      });
    }

}

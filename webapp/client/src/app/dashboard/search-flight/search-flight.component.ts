import { Component, OnInit } from '@angular/core';

import { SearchFlightService } from './search-flight.service';
import { FlightInfo } from '../../shared/model';

@Component({
  moduleId: module.id,
  selector: 'sf-search-flight',
  templateUrl: './search-flight.component.html',
  styleUrls: ['./search-flight.component.scss']
})
export class SearchFlightComponent implements OnInit {

  flights: FlightInfo = new FlightInfo();

  constructor(private searchFlightService: SearchFlightService) { }

  ngOnInit() {
   
  }
}

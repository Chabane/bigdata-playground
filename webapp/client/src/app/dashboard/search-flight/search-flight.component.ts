import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'rxjs/Observable';

import { SearchFlightService } from './search-flight.service';
import { FlightInfo, TripType, CabinClass } from '../../shared/model/flight-info.model';

@Component({
  moduleId: module.id,
  selector: 'sf-search-flight',
  templateUrl: './search-flight.component.html',
  styleUrls: ['./search-flight.component.scss']
})
export class SearchFlightComponent implements OnInit {

  searchFlightForm: FormGroup;

  flightInfo: FlightInfo = new FlightInfo();
  passengersNumberOptions = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
  cabinsClassOptions = Object.keys(CabinClass).filter(k => typeof CabinClass[k as any] === 'number');
  tripTypeOptions = Object.keys(TripType).filter(k => typeof TripType[k as any] === 'number');

  minDate = new Date();
  maxDate = new Date(2020, 0, 1);

  constructor(private searchFlightService: SearchFlightService,
    private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.searchFlightForm = this.formBuilder.group({
      departingFrom: [null, [Validators.required]],
      arrivingAt: [null],
      departureDate: [null, [Validators.required]],
      arrivalDate: [null, [Validators.required]],
      passengerNumber: [1, [Validators.required]],
      cabinClass: [this.cabinsClassOptions[0], [Validators.required]],
    });
  }
}

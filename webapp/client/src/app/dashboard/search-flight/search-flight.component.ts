import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'rxjs/Observable';

import { SearchFlightService } from './search-flight.service';
import { FlightInfo, TripType, CabinClass } from '../../shared/model/flight-info.model';
import { PointOfOrigin } from '../../shared/model/point-of-origin.model';
import { AirportsService } from './gql/service/airports.service';

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
  cabinsClassOptions = Object.keys(CabinClass).filter(k => typeof CabinClass[k as any] === 'string');
  tripTypeOptions = Object.keys(TripType).filter(k => typeof TripType[k as any] === 'string');

  minDate = new Date();
  maxDate = new Date(2020, 0, 1);

  private airportsList: Array<PointOfOrigin> = new Array<PointOfOrigin>();

  constructor(private searchFlightService: SearchFlightService,
    private formBuilder: FormBuilder,
    private airportsService: AirportsService) {
  }

  ngOnInit() {
    this.getAirportsList();
    this.searchFlightForm = this.formBuilder.group({
      hideRequired: false,
      departingFrom: [null, [Validators.required]],
      arrivingAt: [null, [Validators.required]],
      departureDate: [null, [Validators.required]],
      arrivalDate: [null, [Validators.required]],
      passengerNumber: [1, [Validators.required]],
      cabinClass: [CabinClass.ECONOMY, [Validators.required]],
      tripType: [TripType.ROUND_TRIP, [Validators.required]]
    });
  }
  /**
   * method called when on submitting the form
   */
  searchFlight() {
  }
  /**
   * retrieve the list of airports
   */
  getAirportsList() {
    this.airportsService.getAirports().subscribe(response => {
      let airportsData = (<any>response.data).fetchAirports;
      airportsData.forEach(airportData => {
        let pointOfOrigin = new PointOfOrigin();
        pointOfOrigin.AirportID = airportData.AirportID;
        pointOfOrigin.City = airportData.City;
        pointOfOrigin.Country = airportData.Country;
        pointOfOrigin.destinations = airportData.destinations;
        pointOfOrigin.Name = airportData.Name;
        this.airportsList.push(pointOfOrigin);
      });
    });
  }
}

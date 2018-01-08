import { Component, OnInit, DoCheck } from '@angular/core';
import { FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'apollo-client/util/Observable';
import { switchMap } from 'rxjs/operators/switchMap';
import { take } from 'rxjs/operators/take';
import { debounceTime } from 'rxjs/operators/debounceTime';
import { distinctUntilChanged } from 'rxjs/operators/distinctUntilChanged';

import { SearchFlightService } from './search-flight.service';
import { FlightInfo, TripType, CabinClass } from '../../shared/model/flight-info.model';
import { Airport } from '../../shared/model/airport.model';
import { AirportsService } from './gql/service/airports.service';
import { filter } from 'graphql-anywhere/lib/utilities';

@Component({
  moduleId: module.id,
  selector: 'sf-search-flight',
  templateUrl: './search-flight.component.html',
  styleUrls: ['./search-flight.component.scss']
})
export class SearchFlightComponent implements OnInit, DoCheck {

  searchFlightForm: FormGroup;
  flightInfo: FlightInfo = new FlightInfo();
  passengersNumberOptions = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
  cabinsClassOptions = Object.keys(CabinClass).filter(k => typeof CabinClass[k as any] === 'string');
  tripTypeOptions = Object.keys(TripType).filter(k => typeof TripType[k as any] === 'string');
  minDate = new Date();
  maxDate = new Date(2020, 0, 1);

  public airports: Array<Airport> = new Array<Airport>();
  filteredAirports: Observable<Array<Airport>>;

  constructor(private searchFlightService: SearchFlightService,
    private formBuilder: FormBuilder,
    private airportsService: AirportsService) {
  }

  ngOnInit() {
    // this.getAirports();
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

    this.filteredAirports = <any>this.searchFlightForm.get('departingFrom').valueChanges.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(departingFrom => this.airportsService.getAirports(departingFrom))
    );
  }

  ngDoCheck() {
    this.filteredAirports.subscribe(res => {
      console.log('------------', res);
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
  getAirports() {
    /* this.airportsService.getAirports().subscribe(response => {
       console.log('*********', response);
       const airportsData = (<any>response.data).fetchAirports;
       airportsData.forEach(airportData => {
         const airport = new Airport();
         airport.AirportID = airportData.AirportID;
         airport.City = airportData.City;
         airport.Country = airportData.Country;
         airport.destinations = airportData.destinations;
         airport.Name = airportData.Name;
         this.airports.push(airport);
       });
     });*/
  }

  /*filterAirpots(airport): Observable<Array<Airport>> {
    console.log('filterAirpots ---', airport);
    if (airport === '') {
      return Observable.of(this.airports.slice(0, 250));
    }
    return Observable.of(this.airports.slice().filter((item) => {
      const searchStr = (item.Name + item.Country).toLowerCase();
      return searchStr.indexOf(airport.toLowerCase()) > -1;
    }).slice(0, 250));
  }*/
}

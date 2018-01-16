import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'rxjs/rx';
import { map } from 'rxjs/operators/map';
import { switchMap } from 'rxjs/operators/switchMap';
import { debounceTime } from 'rxjs/operators/debounceTime';
import { startWith } from 'rxjs/operators/startWith';
import { distinctUntilChanged } from 'rxjs/operators/distinctUntilChanged';

import { SearchFlightService } from './search-flight.service';
import { FlightInfo, TripType, CabinClass } from '../../shared/model/flight-info.model';
import { Airport } from '../../shared/model/airport.model';
import { AirportsService } from './gql/service/airports.service';

@Component({
  moduleId: module.id,
  selector: 'sf-search-flight',
  templateUrl: './search-flight.component.html',
  styleUrls: ['./search-flight.component.scss']
})
export class SearchFlightComponent implements OnInit {

  @ViewChild('filter') filter: ElementRef;
  @ViewChild('arrivalAirportInput') arrivalAirportInput: ElementRef;
  searchFlightForm: FormGroup;
  flightInfo: FlightInfo = new FlightInfo();
  passengersNumberOptions = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];
  cabinsClassOptions = Object.keys(CabinClass).filter(k => typeof CabinClass[k as any] === 'string');
  tripTypeOptions = Object.keys(TripType).filter(k => typeof TripType[k as any] === 'string');
  minDate = new Date();
  maxDate = new Date(2020, 0, 1);
  minDateTo = new Date();
  maxDateTo = new Date(2020, 0, 1);
  filteredAirports: Observable<Array<Airport>>;
  filteredDestinationAirports: Observable<Array<Airport>>;

  private departureAirport: Airport = null;
  private arrivalAirport: Airport = null;

  constructor(private searchFlightService: SearchFlightService,
    private formBuilder: FormBuilder,
    private airportsService: AirportsService) {
  }

  ngOnInit() {
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

    // load departure airports
    this.filteredAirports = <any>this.searchFlightForm.get('departingFrom').valueChanges.pipe(
      distinctUntilChanged(),
      debounceTime(300),
      switchMap(airportToSearch => this.loadAirports(airportToSearch, this.arrivalAirport))
    );

    // load destinations airports
    this.filteredDestinationAirports = <any>this.searchFlightForm.get('arrivingAt').valueChanges.pipe(
      distinctUntilChanged(),
      debounceTime(300),
      switchMap(airportToSearch => this.loadAirports(airportToSearch, this.departureAirport))
    );

    // arrivalDate must be greater than the departureDate
    this.searchFlightForm.get('departureDate').valueChanges.subscribe(response => {
      this.minDateTo = response === null ? new Date() : response;
    });

    Observable.fromEvent(this.filter.nativeElement, 'keyup')
      .debounceTime(150)
      .distinctUntilChanged()
      .subscribe((res) => {
        this.departureAirport = this.filter.nativeElement.value === '' ? null : this.departureAirport;
        console.log('keyuppppppppppppppppppppp', this.departureAirport);
        console.log('keyupppppppppppppppppppp------p', this.filter.nativeElement.value);
      });

    Observable.fromEvent(this.arrivalAirportInput.nativeElement, 'keyup')
      .debounceTime(150)
      .distinctUntilChanged()
      .subscribe((res) => {
        this.arrivalAirport = this.filter.nativeElement.value === '' ? null : this.arrivalAirport;
        console.log('keyuppppppppppppppppppppp', this.arrivalAirport);
        console.log('keyupppppppppppppppppppp------p', this.filter.nativeElement.value);
      });
  }

  /**
   * method called when on submitting the form
   */
  searchFlight() {
  }

  /**
   *
   * @param airport put the departingAirport in a property to use it
   *  for loading the destination airports
   */
  setDepartureAirport(airport: Airport) {
    console.log('*-*-*-*-**-departure airport', airport);
    this.departureAirport = airport;
  }
  /**
   *
   * @param airport put the arrivalAirport in a property to use it
   *  for loading the departure airports
   */
  setArrivalAirport(airport: Airport) {
    console.log('*-*-*-*-**-arrival airport', airport);
    this.arrivalAirport = airport;
  }

  private loadAirports(airportToSearch: string, airportToFilterBy: Airport) {
    console.log('---------------loadDestinationAirports -- 2', airportToSearch);
    if (airportToFilterBy === null) {
      return this.airportsService.getAirports(airportToSearch).pipe(
        map(response => {
          const airportsData = (<any>response.data).fetchAirports;
          const destinationAirport: Array<Airport> = new Array<Airport>();
          airportsData.forEach(airportData => {
            const airport = new Airport();
            airport.AirportID = airportData.AirportID;
            airport.City = airportData.City;
            airport.Country = airportData.Country;
            airport.destinations = airportData.destinations;
            airport.Name = airportData.Name;
            destinationAirport.push(airport);
          });
          console.log('---------', destinationAirport);
          return destinationAirport;
        }));
    } else {

      console.log('---------------loadDestinationAirports -- departureAirport', airportToFilterBy);
      return this.airportsService.getAirports(airportToSearch, airportToFilterBy.AirportID).pipe(
        map(response => {
          const airportsData = (<any>response.data).fetchAirports;
          const destinationAirport: Array<Airport> = new Array<Airport>();
          airportsData.forEach(airportData => {
            const airport = new Airport();
            airport.AirportID = airportData.AirportID;
            airport.City = airportData.City;
            airport.Country = airportData.Country;
            airport.destinations = airportData.destinations;
            airport.Name = airportData.Name;
            destinationAirport.push(airport);
          });
          console.log('---------', destinationAirport);
          return destinationAirport;
        }));
    }

  }
}

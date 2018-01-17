import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Observable } from 'rxjs/rx';
import { map } from 'rxjs/operators/map';
import { switchMap } from 'rxjs/operators/switchMap';
import { debounceTime } from 'rxjs/operators/debounceTime';
import { distinctUntilChanged } from 'rxjs/operators/distinctUntilChanged';

import { SearchFlightService } from './search-flight.service';
import { FlightInfo, TripType, CabinClass } from '../../shared/model/flight-info.model';
import { Airport } from '../../shared/model/airport.model';
import { AirportsService } from './gql/service/airports.service';
import { AirportDtoMapper, AirportMapper } from './util/';


@Component({
  moduleId: module.id,
  selector: 'sf-search-flight',
  templateUrl: './search-flight.component.html',
  styleUrls: ['./search-flight.component.scss']
})
export class SearchFlightComponent implements OnInit {

  @ViewChild('departureAirportInput') departureAirportInput: ElementRef;
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

  private departureAirport: Airport;
  private arrivalAirport: Airport;

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

    this.checkDepartureAirportInputChanges();
    this.checkArrivalAirportInputChanges();
  }
  /**
   * track departure airtport input for changes
   */
  private checkDepartureAirportInputChanges() {
    return Observable.fromEvent(this.departureAirportInput.nativeElement, 'keyup')
      .debounceTime(150)
      .distinctUntilChanged()
      .subscribe((res) => {
        this.departureAirport = this.departureAirportInput.nativeElement.value === '' ? undefined : this.departureAirport;
        console.log('keyuppppppppppppppppppppp', this.departureAirport);
        console.log('keyupppppppppppppppppppp------p', this.departureAirportInput.nativeElement.value);
      });
  }

  /**
   * track arrival airtport input for changes
   */
  private checkArrivalAirportInputChanges() {
    return Observable.fromEvent(this.arrivalAirportInput.nativeElement, 'keyup')
      .debounceTime(150)
      .distinctUntilChanged()
      .subscribe((res) => {
        this.arrivalAirport = this.arrivalAirportInput.nativeElement.value === '' ? undefined : this.arrivalAirport;
        console.log('keyuppppppppppppppppppppp', this.arrivalAirport);
        console.log('keyupppppppppppppppppppp------p', this.arrivalAirportInput.nativeElement.value);
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
    const airportId = airportToFilterBy === undefined ? undefined : airportToFilterBy.AirportID;
    console.log('---------------loadDestinationAirports -- 2', airportToSearch);
    console.log('---------------loadDestinationAirports -- departureAirport', airportToFilterBy);
    return this.airportsService.getAirports(airportToSearch, airportId).pipe(
      map(response => {
        const airportsData = (<any>response.data).fetchAirports;
        const airportsDtoList = AirportDtoMapper.toAirportsDto(airportsData);
        return AirportMapper.toAirports(airportsDtoList);
      }));
  }
}

import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
/* tslint:disable */
import { Observable } from 'rxjs/Rx';
import { Subscription } from 'rxjs/Subscription';
import { map } from 'rxjs/operators/map';
import { switchMap } from 'rxjs/operators/switchMap';
import { debounceTime } from 'rxjs/operators/debounceTime';
import { distinctUntilChanged } from 'rxjs/operators/distinctUntilChanged';

import { FlightInfo, TripType, CabinClass } from '../../shared/model/flight-info.model';
import { Airport } from '../../shared/model/airport.model';
import { AirportsService, FlightService, TweetService } from './gql/service';
import { AirportDtoMapper, AirportMapper } from './util/';
import * as moment from 'moment';

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
  private tweetSubscriber: Subscription;

  constructor(private flightService: FlightService,
    private airportsService: AirportsService,
    private tweetService: TweetService,
    private formBuilder: FormBuilder,
  ) {
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
      switchMap((airportToSearch: any) => this.loadAirports(airportToSearch, this.arrivalAirport))
    );

    // load destinations airports
    this.filteredDestinationAirports = <any>this.searchFlightForm.get('arrivingAt').valueChanges.pipe(
      distinctUntilChanged(),
      debounceTime(300),
      switchMap((airportToSearch: any) => this.loadAirports(airportToSearch, this.departureAirport))
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
      });
  }
  /**
   * method called when on submitting the form
   */
  searchFlight() {
    const flightInfo: FlightInfo = new FlightInfo();
    flightInfo.departingId = this.departureAirport.AirportID;
    flightInfo.arrivingId = this.arrivalAirport.AirportID;
    const dateFormat = 'YYYY-MM-DD';
    const departureDate = this.searchFlightForm.get('departureDate').value;
    flightInfo.departureDate = moment.utc(departureDate, dateFormat).local().format(dateFormat);
    const arrivalDate = this.searchFlightForm.get('arrivalDate').value;
    flightInfo.arrivalDate = moment.utc(arrivalDate, dateFormat).local().format(dateFormat);
    flightInfo.passengerNumber = this.searchFlightForm.get('passengerNumber').value;
    flightInfo.cabinClass = this.searchFlightForm.get('cabinClass').value;
    flightInfo.tripType = this.searchFlightForm.get('tripType').value;
    this.flightService.sendFlightInfo(flightInfo).subscribe(res => {
      if (this.tweetSubscriber) {
        this.tweetSubscriber.unsubscribe();
      }
      this.tweetSubscriber = this.tweetService.getTweets().subscribe({
        next(data) {
          const tweets = data.getTweets;
        }
      });
    });
  }

  /**
   *
   * @param airport put the departingAirport in a property to use it
   *  for loading the destination airports
   */
  setDepartureAirport(airport: Airport) {
    this.departureAirport = airport;
  }
  /**
   *
   * @param airport put the arrivalAirport in a property to use it
   *  for loading the departure airports
   */
  setArrivalAirport(airport: Airport) {
    this.arrivalAirport = airport;
  }

  private loadAirports(airportToSearch: string, airportToFilterBy: Airport) {
    const airportId = airportToFilterBy === undefined ? undefined : airportToFilterBy.AirportID;
    return this.airportsService.getAirports(airportToSearch, airportId).pipe(
      map(response => {
        const airportsData = (<any>response).data.fetchAirports;
        const airportsDtoList = AirportDtoMapper.toAirportsDto(airportsData);
        return AirportMapper.toAirports(airportsDtoList);
      }));
  }
}

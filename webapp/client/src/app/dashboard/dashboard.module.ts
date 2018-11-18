import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SearchFlightComponent } from './search-flight/search-flight.component';
import { DashboardComponent } from './dashboard.component';
import { dashboardRoutes } from './dashboard.routes';
import { AirportsService, FlightService, TweetService } from './search-flight/gql/service';

import { MaterialModule } from '../shared/material.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(dashboardRoutes),
    MaterialModule
  ],
  declarations: [SearchFlightComponent, DashboardComponent],
  providers: [FlightService, AirportsService, TweetService]
})
export class DashboardModule { }


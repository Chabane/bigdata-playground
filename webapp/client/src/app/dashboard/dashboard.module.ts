import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SearchFlightComponent } from './search-flight/search-flight.component';
import { DashboardComponent } from './dashboard.component';
import { dashboardRoutes } from './dashboard.routes';
import { SearchFlightService } from './search-flight/search-flight.service';

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
  providers: [SearchFlightService]
})
export class DashboardModule { }


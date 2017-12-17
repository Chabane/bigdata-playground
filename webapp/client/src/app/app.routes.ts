import { AppComponent } from './app.component';

export const appRoutes = [
  { path: 'dashboard', loadChildren: './dashboard/dashboard.module#DashboardModule' },
  {
    path: '**', redirectTo: 'dashboard', pathMatch: 'full'
  }
];

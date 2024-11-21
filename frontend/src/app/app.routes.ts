import { Routes } from '@angular/router';
import { NOTES_ROUTES } from './feature/note/notes.routes';
import { HomeComponent } from './feature/home/home.component';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'home',
  },
  {
    path: 'home',
    component: HomeComponent,
  },
  ...NOTES_ROUTES,
];

import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { CommonModule } from '@angular/common';
import { SideNavComponent } from "./shared/side-nav.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, RouterModule, SideNavComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  navItems: any = [
    { label: 'Home', active: false, routerLink: '/home' },
    { icon: ['far', 'user'], label: 'Profile', badge: 'active', routerLink:"/profile" },
    { icon: ['far', 'bell'], label: 'Notifications', badge: 4, routerLink:"/notifications" },
    { icon: ['far', 'message'], label: 'Messages', badge: 6, routerLink:'/messages' },
    { icon: ['fas', 'chart-line'], label: 'Activity', badge: 6, routerLink:"/activities" },
    { label: 'Task', badge: 2, routerLink:"/notes/all"},
    // { label: 'Settings', badge: 'new' },
    // { label: 'Logout' },
  ];
}

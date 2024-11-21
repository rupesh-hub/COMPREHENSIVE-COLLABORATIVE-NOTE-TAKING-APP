import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import {
  FaIconLibrary,
  FontAwesomeModule,
} from '@fortawesome/angular-fontawesome';
import { far } from '@fortawesome/free-regular-svg-icons';
import { fas } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'ccnta-note',
  standalone: true,
  imports: [CommonModule, RouterModule, FontAwesomeModule],
  templateUrl: './note.component.html',
  styleUrl: './note.component.scss',
})
export class NoteComponent {
  constructor(library: FaIconLibrary) {
    library.addIconPacks(fas, far);
  }

  protected tabs: any[] = [
    {
      id: 1,
      label: 'NOTES',
      icon: ['far', 'folder'],
      active: false,
      routerLink:"/notes/all"
    },
    // {
    //   id: 2,
    //   label: 'NEW',
    //   icon: ['far', 'file-alt'],
    //   active: false,
    //   routerLink:"new/1"
    // },
    {
      id: 3,
      label: 'EDITOR',
      icon: ['far', 'file-alt'],
      active: false,
      routerLink:"edit/1"
    },
    {
      id: 4,
      label: 'DRAFTS',
      icon: ['far', 'file'],
      active: false,
      routerLink:'draft/1'
    },
  ];

  // protected active(id: any) {
  //   this.tabs.forEach((tab) => {
  //     tab.active = tab.id === id;
  //   });
  // }
  
}

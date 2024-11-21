import { CommonModule } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import {
  FaIconLibrary,
  FontAwesomeModule,
} from '@fortawesome/angular-fontawesome';
import { far } from '@fortawesome/free-regular-svg-icons';
import { fas } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'ccnta-side-nav',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, RouterModule],
  template: `
    <aside
      class="fixed left-0 top-0 h-full text-gray-600 z-50 w-[260px] bg-white flex flex-col justify-start items-start py-[16px] px-[16px] gap-y-[16px] min-h-screen"
    >
      <div class="h-[40px] flex justify-start items-center gap-x-[12px] w-full">
        <img
          src="https://static.thenounproject.com/png/2892274-200.png"
          alt=""
          class="h-[40px] w-[40px] bg-white rounded-lg"
        />
        <p class="text-gray-700 font-bold text-xl">CCNTA</p>
      </div>

      <div
        class="search flex justify-start items-center bg-white h-[40px] w-full gap-x-[12px] px-[12px] rounded-lg border"
      >
        <fa-icon [icon]="['fas', 'search']" class="text-gray-500"></fa-icon>
        <input
          type="text"
          class="border-none bg-white outline-none"
          placeholder="Search"
        />
      </div>

      <div class="flex flex-col w-full justify-between items-center h-screen">
        <div class="menu w-full">
          <p class="text-gray-400">Menu</p>

          <ul
            *ngFor="let nav of navItems"
            class="flex flex-col justify-start items-start py-[4px]"
          >
            <li
              class="flex justify-between items-center hover:bg-gray-100 hover:cursor-pointer rounded-lg w-full px-[12px] group"
              [ngClass]="nav.active ? 'bg-gray-100' : ''"
              [routerLink]="nav.routerLink"
              [routerLinkActive]="'bg-gray-100 text-primary'"
              [ngClass]="nav.active ? 'active-class' : ''"
            >
              <div
                class="h-[44px] flex gap-x-[12px] bg-transparent text-gray-600 justify-start items-center"
              >
                @if(nav.icon){
                <fa-icon
                  [icon]="nav.icon"
                  class="flex h-[20px] w-[20px] justify-center items-center"
                ></fa-icon>
                } @if(nav.label==='Home' || nav.label === 'home'){
                <svg
                  width="20px"
                  height="20px"
                  viewBox="-3.2 -3.2 38.40 38.40"
                  enable-background="new 0 0 32 32"
                  id="Stock_cut"
                  version="1.1"
                  xml:space="preserve"
                  xmlns="http://www.w3.org/2000/svg"
                  xmlns:xlink="http://www.w3.org/1999/xlink"
                  fill="#000000"
                >
                  <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
                  <g
                    id="SVGRepo_tracerCarrier"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke="#CCCCCC"
                    stroke-width="0.192"
                  ></g>
                  <g id="SVGRepo_iconCarrier">
                    <desc></desc>
                    <g>
                      <polygon
                        fill="none"
                        points="27,12 16,1 5,12 5,31 27,31 "
                        stroke="#000000"
                        stroke-linejoin="round"
                        stroke-miterlimit="10"
                        stroke-width="2"
                      ></polygon>
                      <rect
                        fill="none"
                        height="12"
                        stroke="#000000"
                        stroke-linejoin="round"
                        stroke-miterlimit="10"
                        stroke-width="2"
                        width="10"
                        x="11"
                        y="19"
                      ></rect>
                      <polyline
                        fill="none"
                        points="1,16 16,1 31,16 "
                        stroke="#000000"
                        stroke-linejoin="round"
                        stroke-miterlimit="10"
                        stroke-width="2"
                      ></polyline>
                    </g>
                  </g>
                </svg>

                } @if(nav.label==='Settings' || nav.label === 'settings'){
                <svg
                  width="20px"
                  height="20px"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
                  <g
                    id="SVGRepo_tracerCarrier"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  ></g>
                  <g id="SVGRepo_iconCarrier">
                    <rect width="24" height="24" fill="white"></rect>
                    <path
                      d="M13.5 2L13.9961 1.93798C13.9649 1.68777 13.7522 1.5 13.5 1.5V2ZM10.5 2V1.5C10.2478 1.5 10.0351 1.68777 10.0039 1.93798L10.5 2ZM13.7747 4.19754L13.2786 4.25955C13.3047 4.46849 13.4589 4.63867 13.6642 4.68519L13.7747 4.19754ZM16.2617 5.22838L15.995 5.6513C16.1731 5.76362 16.4024 5.75233 16.5687 5.62306L16.2617 5.22838ZM18.0104 3.86826L18.364 3.51471C18.1857 3.3364 17.9025 3.31877 17.7034 3.47359L18.0104 3.86826ZM20.1317 5.98958L20.5264 6.29655C20.6812 6.09751 20.6636 5.81434 20.4853 5.63603L20.1317 5.98958ZM18.7716 7.73831L18.3769 7.43134C18.2477 7.59754 18.2364 7.82693 18.3487 8.00503L18.7716 7.73831ZM19.8025 10.2253L19.3148 10.3358C19.3613 10.5411 19.5315 10.6953 19.7404 10.7214L19.8025 10.2253ZM22 10.5H22.5C22.5 10.2478 22.3122 10.0351 22.062 10.0039L22 10.5ZM22 13.5L22.062 13.9961C22.3122 13.9649 22.5 13.7522 22.5 13.5H22ZM19.8025 13.7747L19.7404 13.2786C19.5315 13.3047 19.3613 13.4589 19.3148 13.6642L19.8025 13.7747ZM18.7716 16.2617L18.3487 15.995C18.2364 16.1731 18.2477 16.4025 18.3769 16.5687L18.7716 16.2617ZM20.1317 18.0104L20.4853 18.364C20.6636 18.1857 20.6812 17.9025 20.5264 17.7034L20.1317 18.0104ZM18.0104 20.1317L17.7034 20.5264C17.9025 20.6812 18.1857 20.6636 18.364 20.4853L18.0104 20.1317ZM16.2617 18.7716L16.5687 18.3769C16.4024 18.2477 16.1731 18.2364 15.995 18.3487L16.2617 18.7716ZM13.7747 19.8025L13.6642 19.3148C13.4589 19.3613 13.3047 19.5315 13.2786 19.7404L13.7747 19.8025ZM13.5 22V22.5C13.7522 22.5 13.9649 22.3122 13.9961 22.062L13.5 22ZM10.5 22L10.0039 22.062C10.0351 22.3122 10.2478 22.5 10.5 22.5V22ZM10.2253 19.8025L10.7214 19.7404C10.6953 19.5315 10.5411 19.3613 10.3358 19.3148L10.2253 19.8025ZM7.73832 18.7716L8.00504 18.3487C7.82694 18.2364 7.59756 18.2477 7.43135 18.3769L7.73832 18.7716ZM5.98959 20.1317L5.63604 20.4853C5.81435 20.6636 6.09752 20.6812 6.29656 20.5264L5.98959 20.1317ZM3.86827 18.0104L3.4736 17.7034C3.31878 17.9025 3.33641 18.1857 3.51472 18.364L3.86827 18.0104ZM5.22839 16.2617L5.62307 16.5687C5.75234 16.4025 5.76363 16.1731 5.65131 15.995L5.22839 16.2617ZM4.19754 13.7747L4.68519 13.6642C4.63867 13.4589 4.46849 13.3047 4.25955 13.2786L4.19754 13.7747ZM2 13.5H1.5C1.5 13.7522 1.68777 13.9649 1.93798 13.9961L2 13.5ZM2 10.5L1.93798 10.0039C1.68777 10.0351 1.5 10.2478 1.5 10.5H2ZM4.19754 10.2253L4.25955 10.7214C4.46849 10.6953 4.63867 10.5411 4.68519 10.3358L4.19754 10.2253ZM5.22839 7.73831L5.65131 8.00503C5.76363 7.82693 5.75234 7.59755 5.62307 7.43134L5.22839 7.73831ZM3.86827 5.98959L3.51472 5.63603C3.33641 5.81434 3.31878 6.09751 3.47359 6.29656L3.86827 5.98959ZM5.98959 3.86827L6.29656 3.47359C6.09752 3.31878 5.81434 3.33641 5.63604 3.51471L5.98959 3.86827ZM7.73832 5.22839L7.43135 5.62306C7.59755 5.75233 7.82694 5.76363 8.00504 5.6513L7.73832 5.22839ZM10.2253 4.19754L10.3358 4.68519C10.5411 4.63867 10.6953 4.46849 10.7214 4.25955L10.2253 4.19754ZM13.5 1.5H10.5V2.5H13.5V1.5ZM14.2708 4.13552L13.9961 1.93798L13.0039 2.06202L13.2786 4.25955L14.2708 4.13552ZM16.5284 4.80547C15.7279 4.30059 14.8369 3.92545 13.8851 3.70989L13.6642 4.68519C14.503 4.87517 15.2886 5.20583 15.995 5.6513L16.5284 4.80547ZM16.5687 5.62306L18.3174 4.26294L17.7034 3.47359L15.9547 4.83371L16.5687 5.62306ZM17.6569 4.22182L19.7782 6.34314L20.4853 5.63603L18.364 3.51471L17.6569 4.22182ZM19.7371 5.68261L18.3769 7.43134L19.1663 8.04528L20.5264 6.29655L19.7371 5.68261ZM20.2901 10.1149C20.0746 9.16313 19.6994 8.27213 19.1945 7.47158L18.3487 8.00503C18.7942 8.71138 19.1248 9.49695 19.3148 10.3358L20.2901 10.1149ZM22.062 10.0039L19.8645 9.72917L19.7404 10.7214L21.938 10.9961L22.062 10.0039ZM22.5 13.5V10.5H21.5V13.5H22.5ZM19.8645 14.2708L22.062 13.9961L21.938 13.0039L19.7404 13.2786L19.8645 14.2708ZM19.1945 16.5284C19.6994 15.7279 20.0746 14.8369 20.2901 13.8851L19.3148 13.6642C19.1248 14.503 18.7942 15.2886 18.3487 15.995L19.1945 16.5284ZM20.5264 17.7034L19.1663 15.9547L18.3769 16.5687L19.7371 18.3174L20.5264 17.7034ZM18.364 20.4853L20.4853 18.364L19.7782 17.6569L17.6569 19.7782L18.364 20.4853ZM15.9547 19.1663L17.7034 20.5264L18.3174 19.7371L16.5687 18.3769L15.9547 19.1663ZM13.8851 20.2901C14.8369 20.0746 15.7279 19.6994 16.5284 19.1945L15.995 18.3487C15.2886 18.7942 14.503 19.1248 13.6642 19.3148L13.8851 20.2901ZM13.9961 22.062L14.2708 19.8645L13.2786 19.7404L13.0039 21.938L13.9961 22.062ZM10.5 22.5H13.5V21.5H10.5V22.5ZM9.72917 19.8645L10.0039 22.062L10.9961 21.938L10.7214 19.7404L9.72917 19.8645ZM7.4716 19.1945C8.27214 19.6994 9.16314 20.0746 10.1149 20.2901L10.3358 19.3148C9.49696 19.1248 8.71139 18.7942 8.00504 18.3487L7.4716 19.1945ZM6.29656 20.5264L8.04529 19.1663L7.43135 18.3769L5.68262 19.7371L6.29656 20.5264ZM3.51472 18.364L5.63604 20.4853L6.34315 19.7782L4.22183 17.6569L3.51472 18.364ZM4.83372 15.9547L3.4736 17.7034L4.26295 18.3174L5.62307 16.5687L4.83372 15.9547ZM3.70989 13.8851C3.92545 14.8369 4.30059 15.7279 4.80547 16.5284L5.65131 15.995C5.20584 15.2886 4.87517 14.503 4.68519 13.6642L3.70989 13.8851ZM1.93798 13.9961L4.13552 14.2708L4.25955 13.2786L2.06202 13.0039L1.93798 13.9961ZM1.5 10.5V13.5H2.5V10.5H1.5ZM4.13552 9.72917L1.93798 10.0039L2.06202 10.9961L4.25955 10.7214L4.13552 9.72917ZM4.80547 7.47159C4.30059 8.27213 3.92545 9.16313 3.70989 10.1149L4.68519 10.3358C4.87517 9.49696 5.20583 8.71138 5.65131 8.00503L4.80547 7.47159ZM3.47359 6.29656L4.83371 8.04528L5.62307 7.43134L4.26295 5.68262L3.47359 6.29656ZM5.63604 3.51471L3.51472 5.63603L4.22182 6.34314L6.34314 4.22182L5.63604 3.51471ZM8.04529 4.83371L6.29656 3.47359L5.68262 4.26294L7.43135 5.62306L8.04529 4.83371ZM10.1149 3.70989C9.16313 3.92545 8.27214 4.30059 7.4716 4.80547L8.00504 5.6513C8.71139 5.20583 9.49696 4.87517 10.3358 4.68519L10.1149 3.70989ZM10.0039 1.93798L9.72917 4.13552L10.7214 4.25955L10.9961 2.06202L10.0039 1.93798Z"
                      fill="#000000"
                    ></path>
                    <circle
                      cx="12"
                      cy="12"
                      r="4"
                      stroke="#000000"
                      stroke-linejoin="round"
                    ></circle>
                  </g>
                </svg>
                } @if(nav.label==='Task' || nav.label === 'task'){
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  fill="currentColor"
                  class="h-[20px] w-[20px]"
                >
                  <path
                    d="M4 4h16v2H4V4zm0 5h10v2H4V9zm0 5h8v2H4v-2zm10.59 0l2.83 2.83 4.24-4.24-1.42-1.41-2.82 2.82-1.41-1.41L14.59 14z"
                  />
                </svg>
                } @if(nav.label==='Logout' || nav.label === 'logout'){
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  fill="currentColor"
                  class="h-[20px] w-[20px]"
                >
                  <path
                    d="M16 13v-2H7V8l-5 4 5 4v-3h9zm5-10H12v2h9v14H12v2h9a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2z"
                  />
                </svg>
                }

                <p class="text-sm">{{ nav.label }}</p>
              </div>
              @if(nav.badge){
              <p
                class="bg-gray-100 text-gray-700 px-3 rounded-2xl text-sm flex justify-center items-center group-hover:bg-gray-300"
                [ngClass]="
                  nav.badge == 'active'
                    ? 'border border-green-400 text-green-600 bg-white'
                    : ''
                "
                [ngClass]="nav.active ? 'bg-gray-300' : ''"
              >
                {{ nav.badge }}
              </p>
              }
            </li>
          </ul>
        </div>

        <div
          class="w-full max-w-md bg-black rounded-lg shadow-2xl overflow-hidden py-2"
        >
          <div class="p-2">
            <div class="flex justify-center mb-4">
              <fa-icon
                [icon]="['fas', 'arrow-alt-circle-up']"
                class="flex justify-center items-center text-white text-2xl"
              ></fa-icon>
            </div>
            <h2 class="text-md font-bold text-white text-center mb-3">
              Activate Our Pro Model
            </h2>
            <p class="text-gray-400 text-center mb-6 text-sm">
              Upgrade to unlock premium features and take your experience to the
              next level.
            </p>
            <button
              class="w-full transition-colors duration-300 ease-in-out border rounded-md px-2 py-1 text-sm text-white"
            >
              Activate Now
            </button>
          </div>
        </div>
      </div>
    </aside>
  `,
  styles: [
    `
      .active-class {
        background-color: #e0f7fa; /* Example active color */
        color: #00796b; /* Example text color */
      }
    `,
  ],
})
export class SideNavComponent {
  @Input() navItems: any[] = [];
  private router: Router = inject(Router);

  constructor(library: FaIconLibrary) {
    library.addIconPacks(fas, far);
  }
}

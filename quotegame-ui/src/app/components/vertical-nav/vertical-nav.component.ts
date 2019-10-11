import { Component, OnInit, TemplateRef, ViewEncapsulation } from '@angular/core';

import { AuthenticationService } from "../../services/auth.service";


// Thanks to https://github.com/onokumus/metismenu/issues/110#issuecomment-317254128
//import * as $ from 'jquery';
declare let $: any;

@Component({
  selector: 'vertical-nav',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './vertical-nav.component.html',
  styleUrls: ['./vertical-nav.component.css']
})
export class VerticalNavComponent implements OnInit {

  constructor(protected authService: AuthenticationService) {
  }

  ngOnInit() {
  }

  ngAfterViewInit() {
    $().setupVerticalNavigation(true);
  }

  public openHelpDialog() {
  }

  public openAboutModal(template: TemplateRef<any>): void {
  }
  
  public user(): string {
    return this.authService.getAuthenticatedUser()
  }

  public logout(): void {
    console.log("[AppComponent] logout() called");
    this.authService.logout();
  }
}

import { Component, OnInit } from '@angular/core';

import { VerticalNavigationItem } from 'patternfly-ng/navigation/vertical-navigation/vertical-navigation-item';
import { AuthenticationService } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'quotegame-ui';

  navigationItems: VerticalNavigationItem[];
  actionText: string = '';

  constructor(protected authService: AuthenticationService) {}
  
  ngOnInit(): void {
    this.navigationItems = [
      {
        title: 'Dashboard',
        iconStyleClass: 'fa fa-dashboard',
        url: '/'
      }
    ]
  }

  public isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  public logout(): void {
    console.log("[AppComponent] logout() called");
    this.authService.logout();
  }

  onItemClicked($event: VerticalNavigationItem): void {
    console.log("[AppComponent] Item clicked: " + $event.title);
  }

  onNavigation($event: VerticalNavigationItem): void {
    console.log("[AppComponent] Navigation event fired: " + $event.title);
  }
}

import { Component, OnInit } from '@angular/core';

import { VerticalNavigationItem } from 'patternfly-ng/navigation/vertical-navigation/vertical-navigation-item';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'quotegame-ui';

  navigationItems: VerticalNavigationItem[];
  actionText: string = '';

  ngOnInit(): void {
    this.navigationItems = [
      {
        title: 'Dashboard',
        iconStyleClass: 'fa fa-dashboard',
        url: '/'
      }
    ]
  }

  onItemClicked($event: VerticalNavigationItem): void {
    this.actionText += 'Item Clicked: ' + $event.title + '\n';
  }

  onNavigation($event: VerticalNavigationItem): void {
    this.actionText += 'Navigation event fired: ' + $event.title + '\n';
  }
}

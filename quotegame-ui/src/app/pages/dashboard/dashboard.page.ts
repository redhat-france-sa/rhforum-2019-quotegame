import { Component, OnInit } from '@angular/core';

import { BackendService } from '../../services/backend.service';
import { AuthenticationService } from '../../services/auth.service';

@Component({
  selector: "dashboard-page",
  templateUrl: "dashboard.page.html",
  styleUrls: ["dashboard.page.css"]
})
export class DashboardPageComponent implements OnInit {

  private username: string;
  private password: string;

  constructor(protected authService: AuthenticationService, protected backendService: BackendService) { }

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      //this.loadStatus();
    }
  }

  public isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  public login(): void {
    console.log("[DashboardPageComponent] login() called with " + this.username);

  }
}

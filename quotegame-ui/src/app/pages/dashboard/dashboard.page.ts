import { Component, OnInit } from '@angular/core';

import { CardConfig } from 'patternfly-ng/card';

import { BackendService } from '../../services/backend.service';
import { AuthenticationService } from '../../services/auth.service';

@Component({
  selector: "dashboard-page",
  templateUrl: "dashboard.page.html",
  styleUrls: ["dashboard.page.css"]
})
export class DashboardPageComponent implements OnInit {

  private username: string;
  private email: string;

  chartCardConfig: CardConfig;

  /*
  chartDates: any[] = ['dates'];
  chartConfig: SparklineChartConfig = {
		chartId: 'quotesSparkline',
		chartHeight: 150,
		tooltipType: 'default'
  };
  chartData: SparklineChartData = {
    dataAvailable: false,
    total: 100,
    xData: this.chartDates,
    yData: ['used']
  };
  */

  constructor(protected authService: AuthenticationService, protected backendService: BackendService) { }

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      //this.loadStatus();
    }

    this.chartCardConfig = {
      action: {
      },
      filters: [{
				title: 'Last 5 Hours',
        value: '5'
      }, {
				default: true,
        title: 'Last 2 Hours',
        value: '2'
      }, {
        title: 'Last Hour',
        value: '1'
      }],
      title: 'RHT Stock',
    } as CardConfig;
  }

  public isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }
}

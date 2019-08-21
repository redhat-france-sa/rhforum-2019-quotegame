import { Component, OnInit, NgZone } from '@angular/core';

import { CardConfig } from 'patternfly-ng/card';
import { SparklineChartConfig, SparklineChartData } from 'patternfly-ng/chart';

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

  fiveSecLong: number = (1000 * 5);
  numberOfTicks: number = 30;
  today = new Date();

  rhChartCardConfig: CardConfig;
  ibmChartCardConfig: CardConfig;

  chartDates: any[] = ['dates'];

  rhChartConfig: SparklineChartConfig = {
		chartId: 'rhQuotesSparkline',
		chartHeight: 150,
		tooltipType: 'default'
  };
  ibmChartConfig: SparklineChartConfig = {
		chartId: 'ibmQuotesSparkline',
		chartHeight: 150,
		tooltipType: 'default'
  };

  rhChartData: SparklineChartData = {
    dataAvailable: false,
    total: 100,
    xData: this.chartDates,
    yData: ['used']
  };
  ibmChartData: SparklineChartData = {
    dataAvailable: false,
    total: 100,
    xData: this.chartDates,
    yData: ['used']
  };

  constructor(protected authService: AuthenticationService, protected backendService: BackendService) { }

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      //this.loadStatus();
    }
    this.connectQuoteStream();
    this.rhChartCardConfig = { title: 'RHT Stock' } as CardConfig;
    this.ibmChartCardConfig = { title: 'IBM Stock' } as CardConfig;

    // Initialize charts data.
    this.rhChartData.dataAvailable = false;
    this.ibmChartData.dataAvailable = false;
    this.rhChartData.xData = ['dates'];
    this.ibmChartData.xData = ['dates'];
    this.rhChartData.yData = ['dollars'];
    this.ibmChartData.yData = ['dollars'];

    for (let i = this.numberOfTicks - 1; i >= 0; i--) {
      var pastDate: Date = new Date(this.today.getTime() - (i * this.fiveSecLong));
      this.rhChartData.xData.push(pastDate);
      this.ibmChartData.xData.push(pastDate);
      this.rhChartData.yData.push(0);
      this.ibmChartData.yData.push(0);
    }
    this.rhChartData.dataAvailable = true;
    this.ibmChartData.dataAvailable = true;
  }

  public isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  connectQuoteStream(): void {
    this.backendService.getQuoteStreaming().subscribe(
      results => {
        //console.log("Message: " + JSON.stringify(results.data));
        var now = new Date();
        const quotes = JSON.parse(results.data);

        this.rhChartData.dataAvailable = false;
        this.ibmChartData.dataAvailable = false;

        quotes.forEach(quote => {
          if (quote.symbol === 'RHT') {
            this.rhChartData.xData.push(now);
            this.rhChartData.yData.push(quote.price);
            this.rhChartData.xData.splice(1, 1);
            this.rhChartData.yData.splice(1, 1);
          } else if (quote.symbol === 'IBM') {
            this.ibmChartData.xData.push(now);
            this.ibmChartData.yData.push(quote.price);
            this.ibmChartData.xData.splice(1, 1);
            this.ibmChartData.yData.splice(1, 1);
          }
        });
        this.rhChartData.dataAvailable = true;
        this.ibmChartData.dataAvailable = true;
      }    
    );
  }
}

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
  
  money: number = 0;
  quotes: any = {};
  prices: any = {};

  fiveSecLong: number = (1000 * 5);
  numberOfTicks: number = 30;
  today = new Date();

  rhChartCardConfig: CardConfig;
  rhQuoteCardConfig: CardConfig;
  ibmChartCardConfig: CardConfig;
  ibmQuoteCardConfig: CardConfig;

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
      this.loadStatus();
      this.connectQuoteStream();
      setInterval(() => this.loadStatus(), 30000);
    }

    this.rhChartCardConfig = { title: 'RHT Stock', titleBorder: false, subTitle: 'Last 3 Minutes' } as CardConfig;
    this.ibmChartCardConfig = { title: 'IBM Stock', titleBorder: false, subTitle: 'Last 3 Minutes' } as CardConfig;
    this.rhQuoteCardConfig = { title: 'RHT Portfolio', titleBorder: false, topBorder: true } as CardConfig;
    this.ibmQuoteCardConfig = { title: 'IBM Portfolio', titleBorder: false, topBorder: true } as CardConfig;

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

  public portfolioQuotes(symbol: string): number {
    if (this.quotes == undefined) {
      return 0;
    }
    const result = this.quotes[symbol];
    if (result == null) {
      return 0;
    }
    return result;
  }

  public canBuy(symbol: string, numOfQuotes: number): boolean {
    const price = this.prices[symbol];
    if (price != undefined) {
      const totalPrice = numOfQuotes * +price;
      if (totalPrice < this.money) {
        return true;
      }
    }
    return false;
  }

  public buy(symbol: string, numOfQuotes: number): void {
    const price = this.prices[symbol];
    this.backendService.placeOrder(this.username, true, symbol, numOfQuotes, price).subscribe(
      {
        next: res => {
          this.money = this.money - (price * numOfQuotes);
          var symbolQuotes = this.quotes[symbol];
          if (symbolQuotes == undefined){
            this.quotes[symbol] = numOfQuotes;
          } else {
            this.quotes[symbol] = this.quotes[symbol] + numOfQuotes;
          }
        },
        error: err => {
     
        },
        complete: () => console.log('Observer got a complete notification'),
      }
    );
  }

  public sell(symbol: string, numOfQuotes: number): void {
    const price = this.prices[symbol];
    this.backendService.placeOrder(this.username, false, symbol, numOfQuotes, price).subscribe(
      {
        next: res => {
          this.money = this.money + (price * numOfQuotes);
          this.quotes[symbol] = this.quotes[symbol] - numOfQuotes;
        },
        error: err => {
     
        },
        complete: () => console.log('Observer got a complete notification'),
      }
    );
  }

  public canSell(symbol: string, numOfQuotes: number): boolean {
    const result = this.quotes[symbol];
    if (result == undefined || result < numOfQuotes) {
      return false;
    }
    return true;
  }

  loadStatus(): void {
    console.log("Loading status...");
    this.username = this.authService.getAuthenticatedUser();
    this.backendService.getUserPortfolio(this.username).subscribe(
      {
        next: res => {
          this.money = res.money;
          this.quotes = res.quotes;
        },
        error: err => {
     
        },
        complete: () => console.log('Observer got a complete notification'),
      }
    );
  }

  connectQuoteStream(): void {
    this.backendService.getQuoteStreaming().subscribe(
      results => {
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
            this.rhChartCardConfig.subTitle = "" + quote.price + " dollars";
            this.prices['RHT'] = quote.price;
          } else if (quote.symbol === 'IBM') {
            this.ibmChartData.xData.push(now);
            this.ibmChartData.yData.push(quote.price);
            this.ibmChartData.xData.splice(1, 1);
            this.ibmChartData.yData.splice(1, 1);
            this.ibmChartCardConfig.subTitle = "" + quote.price + " dollars";
            this.prices['IBM'] = quote.price;
          }
        });
        this.rhChartData.dataAvailable = true;
        this.ibmChartData.dataAvailable = true;
      }    
    );
  }
}

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

  tyrChartCardConfig: CardConfig;
  tyrQuoteCardConfig: CardConfig;
  cybChartCardConfig: CardConfig;
  cybQuoteCardConfig: CardConfig;

  chartDates: any[] = ['dates'];

  tyrChartConfig: SparklineChartConfig = {
		chartId: 'tyrQuotesSparkline',
		chartHeight: 150,
		tooltipType: 'default'
  };
  cybChartConfig: SparklineChartConfig = {
		chartId: 'cybQuotesSparkline',
		chartHeight: 150,
		tooltipType: 'default'
  };

  tyrChartData: SparklineChartData = {
    dataAvailable: false,
    total: 100,
    xData: this.chartDates,
    yData: ['used']
  };
  cybChartData: SparklineChartData = {
    dataAvailable: false,
    total: 100,
    xData: this.chartDates,
    yData: ['used']
  };

  constructor(protected authService: AuthenticationService, protected backendService: BackendService) { }

  ngOnInit() {
    (this.tyrChartConfig as any).color = { pattern: ['#42b883'] };
    (this.cybChartConfig as any).color = { pattern: ['#ff7e67'] };

    if (this.authService.isAuthenticated()) {
      this.loadStatus();
      this.connectQuoteStream();
      setInterval(() => this.loadStatus(), 30000);
    }

    this.tyrChartCardConfig = { title: 'TYRELL Stock', titleBorder: false, subTitle: 'Last 3 Minutes' } as CardConfig;
    this.cybChartCardConfig = { title: 'CYBERDINE Stock', titleBorder: false, subTitle: 'Last 3 Minutes' } as CardConfig;
    this.tyrQuoteCardConfig = { title: 'TYRELL Portfolio', titleBorder: false, topBorder: true } as CardConfig;
    this.cybQuoteCardConfig = { title: 'CYBERDINE Portfolio', titleBorder: false, topBorder: true } as CardConfig;

    // Initialize charts data.
    this.tyrChartData.dataAvailable = false;
    this.cybChartData.dataAvailable = false;
    this.tyrChartData.xData = ['dates'];
    this.cybChartData.xData = ['dates'];
    this.tyrChartData.yData = ['dollars'];
    this.cybChartData.yData = ['dollars'];

    for (let i = this.numberOfTicks - 1; i >= 0; i--) {
      var pastDate: Date = new Date(this.today.getTime() - (i * this.fiveSecLong));
      this.tyrChartData.xData.push(pastDate);
      this.cybChartData.xData.push(pastDate);
      this.tyrChartData.yData.push(0);
      this.cybChartData.yData.push(0);
    }
    this.tyrChartData.dataAvailable = true;
    this.cybChartData.dataAvailable = true;
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
          console.log("Catch an error when loading status...");
        },
        complete: () => console.log('Observer got a complete notification'),
      }
    );
  }

  connectQuoteStream(): void {
    this.backendService.getQuoteStreaming().subscribe(
      {
        next: results => {
          var now = new Date();
          const quotes = JSON.parse(results.data);

          this.tyrChartData.dataAvailable = false;
          this.cybChartData.dataAvailable = false;

          quotes.forEach(quote => {
            if (quote.symbol === 'TYR') {
              this.tyrChartData.xData.push(now);
              this.tyrChartData.yData.push(quote.price);
              this.tyrChartData.xData.splice(1, 1);
              this.tyrChartData.yData.splice(1, 1);
              this.tyrChartCardConfig.subTitle = "" + quote.price + " dollars";
              this.prices['TYR'] = quote.price;
            } else if (quote.symbol === 'CYB') {
              this.cybChartData.xData.push(now);
              this.cybChartData.yData.push(quote.price);
              this.cybChartData.xData.splice(1, 1);
              this.cybChartData.yData.splice(1, 1);
              this.cybChartCardConfig.subTitle = "" + quote.price + " dollars";
              this.prices['CYB'] = quote.price;
            }
          });
          this.tyrChartData.dataAvailable = true;
          this.cybChartData.dataAvailable = true;
        },
        error: err => {
          console.log("Subscribe on error, retrying in a few seconds...");
          setTimeout(() => this.connectQuoteStream(), 2000);
        }
      }    
    );
  }
}

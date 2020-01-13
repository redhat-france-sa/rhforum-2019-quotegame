import { Injectable, NgZone } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BackendService {

  private rootUrl: string = '/api';

  constructor(private http: HttpClient, private zone: NgZone) {}

  public getConfig(): Observable<any> {
    return this.http.get<any>(this.rootUrl + '/config');
  }

  public getUserPortfolio(username: string): Observable<any> {
    return this.http.get<any>(this.rootUrl + '/portfolio/' + username);
  }

  public placeOrder(username: string, buy: boolean, symbol: string, numOfQuotes: number, price: number): Observable<any> {
    var order = {
      username: username,
      quote: symbol,
      number: numOfQuotes,
      price: price,
      orderType: "SELL"
    }
    if (buy) {
      order.orderType = "BUY"
    }
    return this.http.post<any>(this.rootUrl + '/order', order);
  }

  public getQuoteStreaming(): Observable<any> {
    return Observable.create(observer => {
      const eventSource = new EventSource(this.rootUrl + "/quote/streaming");

      eventSource.onmessage = event => {
        this.zone.run(() => {
          observer.next(event);
        })
      };
      eventSource.onerror = event => {
        this.zone.run(() => {
          observer.error(event);
        })
      };
      return () => eventSource.close();
    });
  }
}
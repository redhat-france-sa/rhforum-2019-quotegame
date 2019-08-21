import { Injectable, NgZone } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BackendService {

  private rootUrl: string = '/api';

  constructor(private zone: NgZone) {}

  public getQuoteStreaming(): Observable<any> {
    console.log("Creating observer");
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
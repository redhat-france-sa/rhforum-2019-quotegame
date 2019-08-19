import { Observable, BehaviorSubject } from "rxjs";

import { HttpClient } from "@angular/common/http";

import { LocalStorageService } from "./local-storage.service";

export class AuthenticationService {

  private credentials: any = null;
  private encodedCredentials: string = null;

  constructor(private http: HttpClient, private storageService: LocalStorageService) { }

  public isAuthenticated(): boolean {
    return this.storageService.readCredentials() != null;
  }

  public getAuthenticatedUser(): string {
    if (this.isAuthenticated()) {
      return this.storageService.readCredentials().username;
    }
    return null;
  }

  public login(username: string, password: string): Observable<any> {
    return this.http.post<any>("/api/admin", {username: username, password: password}); 
  }

  public notifyLoginSuccessfull(username: string, password: string): void {
    this.storageService.storeCredentials(username, password);
    this.credentials = this.storageService.readCredentials();
    this.encodedCredentials = btoa(this.credentials.username + ':' + this.credentials.password);
  }
  
  public logout(): void {
    this.storageService.removeCredentials();
  }

  public injectAuthHeaders(headers: { [header: string]: string }): void {
    if (!this.credentials) {
      this.credentials = this.storageService.readCredentials();
      this.encodedCredentials = btoa(this.credentials.username + ':' + this.credentials.password);
    }
    let authHeader: string = "Basic " + this.encodedCredentials;
    headers["Authorization"] = authHeader;
  }
}
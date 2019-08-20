import { Inject, Injectable } from '@angular/core';
import { LOCAL_STORAGE, StorageService } from 'ngx-webstorage-service';

@Injectable()
export class LocalStorageService {
  
  private CREDENTIALS_STORAGE_KEY = 'quotegame_credentials'

  constructor(@Inject(LOCAL_STORAGE) private storage: StorageService) { }

  public readCredentials(): any {
    return this.storage.get(this.CREDENTIALS_STORAGE_KEY);
  }

  public storeCredentials(username: string, email: string): void {
    // Overwrite values.
    let currentCredentials = {
      username: username,
      email: email
    };

    // Insert updated credentials to local storage.
    this.storage.set(this.CREDENTIALS_STORAGE_KEY, currentCredentials);
  }

  public removeCredentials(): void {
    this.storage.remove(this.CREDENTIALS_STORAGE_KEY);
  }
}
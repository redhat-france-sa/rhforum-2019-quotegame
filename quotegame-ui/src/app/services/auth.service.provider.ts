import { HttpClient } from "@angular/common/http";
import { AuthenticationService } from "./auth.service";
import { LocalStorageService } from "./local-storage.service";

export function AuthenticationServiceFactory(http: HttpClient, storageService: LocalStorageService): AuthenticationService {
  console.info("[AuthenticationServiceFactory] Creating AuthenticationService...");
  return new AuthenticationService(http, storageService);
};
  
export let AuthenticationServiceProvider =
{
  provide: AuthenticationService,
  useFactory: AuthenticationServiceFactory,
  deps: [HttpClient, LocalStorageService]
};
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { VerticalNavigationModule } from 'patternfly-ng/navigation';
import { BsDropdownConfig, BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { StorageServiceModule } from 'ngx-webstorage-service';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { AuthenticationServiceProvider } from './services/auth.service.provider';
import { LocalStorageService } from './services/local-storage.service';
import { DashboardPageComponent } from './pages/dashboard/dashboard.page';

@NgModule({
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    StorageServiceModule,
    VerticalNavigationModule,
    BsDropdownModule.forRoot()
  ],
  declarations: [
    AppComponent, DashboardPageComponent
  ],
  providers: [
    LocalStorageService,
    AuthenticationServiceProvider,
    BsDropdownConfig
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

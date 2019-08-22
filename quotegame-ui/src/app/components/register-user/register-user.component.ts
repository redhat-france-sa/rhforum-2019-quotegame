import { Component, OnInit } from '@angular/core';

import { AuthenticationService } from '../../services/auth.service';
import { BackendService } from '../../services/backend.service';

@Component({
  selector: 'register-user',
  templateUrl: './register-user.component.html',
  styleUrls: ['./register-user.component.css']
})
export class RegisterUserComponent implements OnInit {

  private username: string;
  private email: string;

  constructor(protected authService: AuthenticationService, protected backendService: BackendService) {}

  ngOnInit(): void {

  }

  public login(): void{
    console.log("[RegisterUserComponent] login() called with " + this.username);
    this.authService.login(this.username, this.email).subscribe(
      {
        next: res => {
          this.authService.notifyLoginSuccessfull(this.username, this.email);
        },
        error: err => {
          console.log("Obsever got error: " + JSON.stringify(err));
          if (err.status == 400) {
            this.authService.notifyLoginSuccessfull(this.username, this.email);
          }
        },
        complete: () => console.log('Observer got a complete notification'),
      });
  }
}
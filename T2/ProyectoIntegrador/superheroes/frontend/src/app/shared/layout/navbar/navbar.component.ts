import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { Router } from "@angular/router";
import { Store } from "@ngrx/store";
import { logout } from "../../../auth/state/auth.actions";
import { AuthenticateService } from "../../../core/services/authenticate.service";

@Component({
  selector: "app-navbar",
  templateUrl: "./navbar.component.html",
  styleUrls: ["./navbar.component.scss"],
  standalone: true,
  imports: [CommonModule, MatToolbarModule, MatButtonModule, MatIconModule],
})
export class NavbarComponent implements OnInit {
  constructor(
    private router: Router,
    private store: Store,
    private authService: AuthenticateService,
  ) {}

  ngOnInit(): void {}

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  loggedInUsername(): string | null {
    return this.authService.getLoggedInUsername();
  }

  onLogout(): void {
    this.authService.logout();
    this.store.dispatch(logout());
    this.router.navigate(["/login"]);
  }
}

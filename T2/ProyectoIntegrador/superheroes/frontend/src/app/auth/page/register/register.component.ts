import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Store } from "@ngrx/store";
import { User } from "../../models/user.interface";
import { AuthActions } from "../../state/auth.actions";
import { AuthFormComponent } from "../../components/auth-form/auth-form.component";

@Component({
  selector: "app-register",
  templateUrl: "./register.component.html",
  styleUrls: ["./register.component.scss"],
  standalone: true,
  imports: [CommonModule, AuthFormComponent],
})
export class RegisterComponent implements OnInit {
  error: string = "";

  constructor(private store: Store) {}

  ngOnInit(): void {}

  submit(data: User) {
    this.store.dispatch({ type: AuthActions.CREATE_USER, payload: data });
  }
}

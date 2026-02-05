import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  UntypedFormBuilder,
  FormControl,
  UntypedFormGroup,
  ReactiveFormsModule,
} from "@angular/forms";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { RouterModule } from "@angular/router";

@Component({
  selector: "app-auth-form",
  templateUrl: "./auth-form.component.html",
  styleUrls: ["./auth-form.component.scss"],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterModule,
  ],
})
export class AuthFormComponent implements OnInit {
  @Input() error: string = "";
  @Input() title: string = "Login";
  @Output() submitEmitter = new EventEmitter();
  form: UntypedFormGroup;
  constructor(private fb: UntypedFormBuilder) {
    this.form = this.fb.group({
      email: [""],
      password: [""],
    });
  }

  ngOnInit(): void {}

  submit() {
    this.submitEmitter.emit(this.form.value);
  }
}

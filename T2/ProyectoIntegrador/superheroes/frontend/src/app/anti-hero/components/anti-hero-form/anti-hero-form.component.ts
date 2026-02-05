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
import { AntiHero } from "../../models/anti-hero.interface";

@Component({
  selector: "app-anti-hero-form",
  templateUrl: "./anti-hero-form.component.html",
  styleUrls: ["./anti-hero-form.component.scss"],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
})
export class AntiHeroFormComponent implements OnInit {
  @Input() selectedAntiHero: AntiHero | null = null;
  @Input() actionButtonLabel: string = "Create";
  @Output() action = new EventEmitter();
  form: UntypedFormGroup;

  constructor(private fb: UntypedFormBuilder) {
    this.form = this.fb.group({
      id: [""],
      firstName: [""],
      lastName: [""],
      house: [""],
      knownAs: [""],
    });
  }

  ngOnInit(): void {
    this.checkAction();
  }

  checkAction() {
    if (this.selectedAntiHero) {
      this.actionButtonLabel = "Update";
      this.patchDataValues();
    }
  }

  patchDataValues() {
    if (this.selectedAntiHero) this.form.patchValue(this.selectedAntiHero);
  }

  emitAction() {
    this.action.emit({
      value: this.form.value,
      action: this.actionButtonLabel,
    });
  }

  clear() {
    this.form.reset();
  }
}

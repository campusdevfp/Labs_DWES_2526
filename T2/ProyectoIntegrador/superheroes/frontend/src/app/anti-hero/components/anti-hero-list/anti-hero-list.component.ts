import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatTableModule } from "@angular/material/table";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { TableActions } from "../../enums/table-actions.enum";
import { AntiHero } from "../../models/anti-hero.interface";

@Component({
  selector: "app-anti-hero-list",
  templateUrl: "./anti-hero-list.component.html",
  styleUrls: ["./anti-hero-list.component.scss"],
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule],
})
export class AntiHeroListComponent implements OnInit {
  @Input() headers: Array<{ headerName: string; fieldName: keyof AntiHero }> =
    [];
  @Input() antiHeroes: ReadonlyArray<AntiHero> = [];
  @Output() antiHero = new EventEmitter<{
    antiHero: AntiHero;
    action: TableActions;
  }>();
  headerFields: string[] = [];

  constructor() {}

  ngOnInit(): void {
    this.getHeaderFields();
  }

  getHeaderFields() {
    this.headerFields = this.headers.map((data) => data.fieldName);
    this.headerFields.push("actions");
  }

  selectAntiHero(antiHero: AntiHero, action: TableActions) {
    this.antiHero.emit({ antiHero, action });
  }
}

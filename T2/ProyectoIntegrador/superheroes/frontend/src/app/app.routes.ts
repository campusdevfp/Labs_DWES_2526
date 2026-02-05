import { Routes } from "@angular/router";
import { AuthGuard } from "./core/guards/auth.guard";

export const appRoutes: Routes = [
  { path: "", pathMatch: "full", redirectTo: "login" },
  {
    path: "login",
    loadComponent: () =>
      import("./auth/page/login/login.component").then((m) => m.LoginComponent),
  },
  {
    path: "register",
    loadComponent: () =>
      import("./auth/page/register/register.component").then(
        (m) => m.RegisterComponent,
      ),
  },
  {
    path: "anti-heroes",
    canActivate: [AuthGuard],
    children: [
      {
        path: "",
        loadComponent: () =>
          import("./anti-hero/pages/list/list.component").then(
            (m) => m.ListComponent,
          ),
      },
      {
        path: "form",
        loadComponent: () =>
          import("./anti-hero/pages/form/form.component").then(
            (m) => m.FormComponent,
          ),
      },
      {
        path: "form/:id",
        loadComponent: () =>
          import("./anti-hero/pages/form/form.component").then(
            (m) => m.FormComponent,
          ),
      },
    ],
  },
  { path: "**", redirectTo: "login" },
];

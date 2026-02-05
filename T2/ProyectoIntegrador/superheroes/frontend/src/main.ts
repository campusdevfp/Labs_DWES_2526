import {
  enableProdMode,
  importProvidersFrom,
  provideZonelessChangeDetection,
} from "@angular/core";
import { bootstrapApplication } from "@angular/platform-browser";
import { provideRouter } from "@angular/router";
import { provideAnimations } from "@angular/platform-browser/animations";
import {
  provideHttpClient,
  withInterceptorsFromDi,
  HTTP_INTERCEPTORS,
} from "@angular/common/http";
import { provideStore, provideState } from "@ngrx/store";
import { provideEffects } from "@ngrx/effects";
import { provideStoreDevtools } from "@ngrx/store-devtools";
import { JwtModule } from "@auth0/angular-jwt";
import { AppComponent } from "./app/app.component";
import { environment } from "./environments/environment";
import { appRoutes } from "./app/app.routes";
import { antiHeroReducer } from "./app/anti-hero/state/anti-hero.reducers";
import { AntiHeroEffects } from "./app/anti-hero/state/anti-hero.effects";
import { authReducer } from "./app/auth/state/auth.reducers";
import { AuthEffects } from "./app/auth/state/auth.effects";
import { HeaderInterceptor } from "./app/core/interceptors/header.interceptor";

export function tokenGetter() {
  return localStorage.getItem("token");
}

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    provideZonelessChangeDetection(),
    provideRouter(appRoutes),
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: HeaderInterceptor, multi: true },
    provideAnimations(),
    provideStore(),
    provideState("antiHeroState", antiHeroReducer),
    provideEffects(AntiHeroEffects),
    provideState("authState", authReducer),
    provideEffects(AuthEffects),
    provideStoreDevtools({ maxAge: 25, logOnly: environment.production }),
    importProvidersFrom(
      JwtModule.forRoot({
        config: {
          tokenGetter,
        },
      }),
    ),
  ],
}).catch((err) => console.error(err));

import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { map, mergeMap, catchError, tap } from "rxjs/operators";
import { AuthenticateService } from "src/app/core/services/authenticate.service";
import { User } from "../models/user.interface";
import { AuthActions, setToken } from "./auth.actions";

@Injectable()
export class AuthEffects {
  loginUser$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.LOGIN),
        mergeMap((data: { type: string; payload: User }) =>
          this.authService.login(data.payload).pipe(
            map((data) => ({ type: AuthActions.SET_TOKEN, token: data.token })),
            tap(() => this.router.navigate(["anti-heroes"])),
            catchError(async (data) => ({
              type: AuthActions.LOGIN_ERROR,
              error: data.error,
            })),
          ),
        ),
      );
    },
    { dispatch: true },
  );

  persistToken$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(setToken),
        tap(({ token }) => localStorage.setItem("token", token)),
      ),
    { dispatch: false },
  );

  createUser$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AuthActions.CREATE_USER),
        mergeMap((data: { type: string; payload: User }) =>
          this.authService.register(data.payload).pipe(
            tap(() => this.router.navigate(["login"])),
            map(() => ({ type: "[AUTH] User Created Success" })),
            catchError(async (data) => ({
              type: AuthActions.LOGIN_ERROR,
              error: data.error,
            })),
          ),
        ),
      );
    },
    { dispatch: true },
  );
  constructor(
    private actions$: Actions,
    private authService: AuthenticateService,
    private router: Router,
  ) {}
}

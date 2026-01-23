import { Injectable, computed, signal } from '@angular/core';

const LS_TOKEN = 'accessToken';
const LS_ROLES = 'roles';
const LS_USERNAME = 'username';

function loadRoles(): string[] {
  const raw = localStorage.getItem(LS_ROLES);
  if (!raw) return [];
  try {
    return JSON.parse(raw) as string[];
  } catch {
    return [];
  }
}

@Injectable({ providedIn: 'root' })
export class AuthStore {
  private readonly tokenSig = signal<string | null>(localStorage.getItem(LS_TOKEN));
  private readonly rolesSig = signal<string[]>(loadRoles());
  private readonly usernameSig = signal<string | null>(localStorage.getItem(LS_USERNAME));

  readonly token = computed(() => this.tokenSig());
  readonly roles = computed(() => this.rolesSig());
  readonly username = computed(() => this.usernameSig());

  readonly isAuthenticated = computed(() => !!this.tokenSig());

  setSession(token: string, username: string, roles: string[]): void {
    localStorage.setItem(LS_TOKEN, token);
    localStorage.setItem(LS_USERNAME, username);
    localStorage.setItem(LS_ROLES, JSON.stringify(roles ?? []));

    this.tokenSig.set(token);
    this.usernameSig.set(username);
    this.rolesSig.set(roles ?? []);
  }

  logout(): void {
    localStorage.removeItem(LS_TOKEN);
    localStorage.removeItem(LS_USERNAME);
    localStorage.removeItem(LS_ROLES);

    this.tokenSig.set(null);
    this.usernameSig.set(null);
    this.rolesSig.set([]);
  }

  hasAnyRole(allowed: string[]): boolean {
    const mine = this.rolesSig();
    return allowed.some((r) => mine.includes(r));
  }
}

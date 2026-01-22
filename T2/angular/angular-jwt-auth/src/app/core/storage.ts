// src/app/core/storage.ts
export const storageKeys = {
  token: 'accessToken',
  roles: 'roles',
  username: 'username',
} as const;

export function loadToken(): string | null {
  return localStorage.getItem(storageKeys.token);
}

export function saveSession(token: string, roles: string[], username: string): void {
  localStorage.setItem(storageKeys.token, token);
  localStorage.setItem(storageKeys.roles, JSON.stringify(roles));
  localStorage.setItem(storageKeys.username, username);
}

export function clearSession(): void {
  localStorage.removeItem(storageKeys.token);
  localStorage.removeItem(storageKeys.roles);
  localStorage.removeItem(storageKeys.username);
}

export function loadRoles(): string[] {
  const raw = localStorage.getItem(storageKeys.roles);
  if (!raw) return [];
  try {
    return JSON.parse(raw) as string[];
  } catch {
    return [];
  }
}

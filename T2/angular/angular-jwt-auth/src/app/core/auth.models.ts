// src/app/core/auth.models.ts
export interface LoginRequest {
  username: string;
  password: string;
}

export interface SignupRequest {
  username: string;
  email: string;
  password: string;
  role?: string[]; // ejemplo: ["user"]
}

export interface JwtResponse {
  id: number;
  username: string;
  email: string;
  roles: string[]; // ejemplo: ["ROLE_USER"]
  tokenType: string; // "Bearer"
  accessToken: string;
}

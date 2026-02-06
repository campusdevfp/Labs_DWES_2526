export interface User {
  id: number;
  email: string;
  name: string;
  role: 'customer' | 'admin';
  avatar: string;
  password?: string;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData {
  name: string;
  email: string;
  password: string;
  avatar?: string;
}

export interface AuthResponse {
  access_token: string;
  refresh_token: string;
}

export interface EmailAvailability {
  isAvailable: boolean;
}

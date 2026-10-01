import { apiClient } from '../lib/api/apiClient';
import { ApiResponse } from '../types';

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
}

export interface AuthBackendResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface UserBackendProfile {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role: string;
  status: string;
  loyaltyTier?: string;
  loyaltyPoints?: number;
  createdAt: string;
}

export const AuthApiService = {
  async login(payload: LoginPayload): Promise<AuthBackendResponse> {
    const res = await apiClient.post<AuthBackendResponse>('/auth/login', payload);
    if (res.data?.accessToken) {
      apiClient.setToken(res.data.accessToken);
    }
    return res.data;
  },

  async register(payload: RegisterPayload): Promise<AuthBackendResponse> {
    const res = await apiClient.post<AuthBackendResponse>('/auth/register', payload);
    if (res.data?.accessToken) {
      apiClient.setToken(res.data.accessToken);
    }
    return res.data;
  },

  async getMe(): Promise<UserBackendProfile> {
    const res = await apiClient.get<UserBackendProfile>('/auth/me');
    return res.data;
  },

  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    await apiClient.post<void>('/auth/change-password', {
      currentPassword,
      newPassword,
    });
  },

  async logout(): Promise<void> {
    try {
      if (apiClient.isAuthenticated()) {
        await apiClient.post<void>('/auth/logout');
      }
    } finally {
      apiClient.setToken(null);
    }
  },
};

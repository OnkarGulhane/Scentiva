import { apiClient, ApiError } from '../lib/api/apiClient';
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

export interface UserBackendProfile {
  userId?: number;
  customerId?: number;
  id?: number;
  email: string;
  firstName: string;
  lastName: string;
  fullName?: string;
  phone?: string;
  role: string;
  status?: string;
  loyaltyTier?: string;
  memberSince?: string;
  createdAt?: string;
}

export interface AuthBackendResponse {
  accessToken: string;
  tokenType: string;
  expiresInMs?: number;
  user?: UserBackendProfile;
  // Fallbacks if flattened
  userId?: number;
  email?: string;
  firstName?: string;
  lastName?: string;
  role?: string;
}

export const AuthApiService = {
  async login(payload: LoginPayload): Promise<AuthBackendResponse> {
    try {
      const res = await apiClient.post<AuthBackendResponse>('/auth/login', payload);
      if (res.data?.accessToken) {
        apiClient.setToken(res.data.accessToken);
      }
      return res.data;
    } catch (err: any) {
      if (err instanceof ApiError) {
        if (err.status === 401 || err.status === 400) {
          throw new Error('Email or password is incorrect.');
        }
        throw new Error(err.message || 'Something went wrong. Please try again.');
      }
      throw err;
    }
  },

  async register(payload: RegisterPayload): Promise<AuthBackendResponse> {
    try {
      const res = await apiClient.post<AuthBackendResponse>('/auth/register', payload);
      if (res.data?.accessToken) {
        apiClient.setToken(res.data.accessToken);
      }
      return res.data;
    } catch (err: any) {
      if (err instanceof ApiError) {
        if (err.status === 409) {
          throw new Error('An account already exists with this email. Please sign in instead.');
        }
        throw new Error(err.message || 'Unable to register account. Please check your details.');
      }
      throw err;
    }
  },

  async googleLogin(idToken: string): Promise<AuthBackendResponse> {
    try {
      const res = await apiClient.post<AuthBackendResponse>('/auth/google', { idToken });
      if (res.data?.accessToken) {
        apiClient.setToken(res.data.accessToken);
      }
      return res.data;
    } catch (err: any) {
      if (err instanceof ApiError) {
        throw new Error(err.message || 'Google authentication failed. Please try again.');
      }
      throw err;
    }
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
    } catch {
      // Ignore network errors during logout
    } finally {
      apiClient.setToken(null);
    }
  },
};


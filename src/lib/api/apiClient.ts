import { ApiResponse, ApiPaginatedResponse, ApiErrorResponse } from '../../types';

/**
 * SCENTIVA API Client Gateway (Section 32)
 * Typed, standardized HTTP request handler prepared for Spring Boot & PostgreSQL endpoints.
 */

export interface ApiConfig {
  baseUrl: string;
  timeout: number;
  apiKey?: string;
  maxRetries?: number;
}

export const DEFAULT_API_CONFIG: ApiConfig = {
  baseUrl: process.env.NEXT_PUBLIC_API_URL || 'https://api.scentiva.luxury/v1',
  timeout: 10000,
  maxRetries: 2,
};

export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
    public code?: string,
    public details?: Record<string, unknown>
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export class ApiClient {
  private config: ApiConfig;

  constructor(config: Partial<ApiConfig> = {}) {
    this.config = { ...DEFAULT_API_CONFIG, ...config };
  }

  private async executeWithTimeout<T>(
    url: string,
    options: RequestInit,
    retriesLeft: number = this.config.maxRetries || 0
  ): Promise<T> {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), this.config.timeout);

    try {
      const response = await fetch(url, {
        ...options,
        signal: controller.signal,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          ...(this.config.apiKey ? { 'Authorization': `Bearer ${this.config.apiKey}` } : {}),
          ...options.headers,
        },
      });

      clearTimeout(timeoutId);

      if (!response.ok) {
        let errorData: ApiErrorResponse | null = null;
        try {
          errorData = await response.json();
        } catch {
          // fallback if non-JSON error
        }

        throw new ApiError(
          errorData?.error?.message || `API request failed with HTTP ${response.status}`,
          response.status,
          errorData?.error?.code || `HTTP_${response.status}`,
          errorData?.error?.details
        );
      }

      return await response.json();
    } catch (err: any) {
      clearTimeout(timeoutId);

      if (err.name === 'AbortError') {
        throw new ApiError(`Request timeout after ${this.config.timeout}ms`, 408, 'TIMEOUT');
      }

      // Retry on network errors or 5xx if retries remaining
      if (retriesLeft > 0 && (err instanceof ApiError ? err.status >= 500 : true)) {
        return this.executeWithTimeout<T>(url, options, retriesLeft - 1);
      }

      if (err instanceof ApiError) throw err;
      throw new ApiError(err.message || 'Network communication error', 500, 'NETWORK_ERROR');
    }
  }

  async get<T>(path: string, params?: Record<string, string | number | boolean | undefined>): Promise<ApiResponse<T>> {
    const url = new URL(`${this.config.baseUrl}${path}`);
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          url.searchParams.append(key, String(value));
        }
      });
    }

    return this.executeWithTimeout<ApiResponse<T>>(url.toString(), { method: 'GET' });
  }

  async getPaginated<T>(path: string, params?: Record<string, string | number | boolean | undefined>): Promise<ApiPaginatedResponse<T>> {
    const url = new URL(`${this.config.baseUrl}${path}`);
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          url.searchParams.append(key, String(value));
        }
      });
    }

    return this.executeWithTimeout<ApiPaginatedResponse<T>>(url.toString(), { method: 'GET' });
  }

  async post<T>(path: string, body: unknown): Promise<ApiResponse<T>> {
    return this.executeWithTimeout<ApiResponse<T>>(`${this.config.baseUrl}${path}`, {
      method: 'POST',
      body: JSON.stringify(body),
    });
  }

  async put<T>(path: string, body: unknown): Promise<ApiResponse<T>> {
    return this.executeWithTimeout<ApiResponse<T>>(`${this.config.baseUrl}${path}`, {
      method: 'PUT',
      body: JSON.stringify(body),
    });
  }

  async delete<T>(path: string): Promise<ApiResponse<T>> {
    return this.executeWithTimeout<ApiResponse<T>>(`${this.config.baseUrl}${path}`, {
      method: 'DELETE',
    });
  }
}

export const apiClient = new ApiClient();

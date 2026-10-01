/**
 * SCENTIVA API Client & Backend Gateway Abstraction
 * Configured for Spring Boot + PostgreSQL microservices integration.
 * In local prototype/offline mode, falls back directly to high-fidelity mock repositories.
 */

export interface ApiConfig {
  baseUrl: string;
  timeout: number;
  apiKey?: string;
}

export const DEFAULT_API_CONFIG: ApiConfig = {
  baseUrl: process.env.NEXT_PUBLIC_API_URL || 'https://api.scentiva.luxury/v1',
  timeout: 10000,
};

export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
    public code?: string,
    public details?: any
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

  async get<T>(path: string, params?: Record<string, string | number | boolean | undefined>): Promise<T> {
    const url = new URL(`${this.config.baseUrl}${path}`);
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined) {
          url.searchParams.append(key, String(value));
        }
      });
    }

    try {
      const response = await fetch(url.toString(), {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          ...(this.config.apiKey ? { 'Authorization': `Bearer ${this.config.apiKey}` } : {}),
        },
      });

      if (!response.ok) {
        throw new ApiError(`API request failed with status ${response.status}`, response.status);
      }

      return await response.json();
    } catch (err: any) {
      if (err instanceof ApiError) throw err;
      throw new ApiError(err.message || 'Network communication error', 500);
    }
  }

  async post<T>(path: string, body: any): Promise<T> {
    try {
      const response = await fetch(`${this.config.baseUrl}${path}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          ...(this.config.apiKey ? { 'Authorization': `Bearer ${this.config.apiKey}` } : {}),
        },
        body: JSON.stringify(body),
      });

      if (!response.ok) {
        throw new ApiError(`API request failed with status ${response.status}`, response.status);
      }

      return await response.json();
    } catch (err: any) {
      if (err instanceof ApiError) throw err;
      throw new ApiError(err.message || 'Network communication error', 500);
    }
  }
}

export const apiClient = new ApiClient();

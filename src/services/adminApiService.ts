import { apiClient } from '../lib/api/apiClient';
import { ApiPaginatedResponse, ApiResponse } from '../types';

export interface DashboardSummaryBackendResponse {
  totalRevenue: number;
  totalOrders: number;
  totalCustomers: number;
  totalProducts: number;
  lowStockCount: number;
  pendingReviewsCount: number;
  pendingReturnsCount: number;
  completedOrdersCount: number;
  averageOrderValue: number;
}

export interface SalesTrendPointBackendDto {
  period: string;
  revenue: number;
  orderCount: number;
}

export interface SalesAnalyticsBackendResponse {
  totalPeriodRevenue: number;
  totalPeriodOrders: number;
  currency: string;
  trend: SalesTrendPointBackendDto[];
}

export interface TopProductPerformanceBackendDto {
  productId: number;
  productName: string;
  brandName: string;
  totalUnitsSold: number;
  totalRevenueGenerated: number;
}

export interface AdminCustomerBackendSummary {
  id: number;
  fullName: string;
  email: string;
  phone?: string;
  loyaltyTier: string;
  loyaltyPoints: number;
  totalOrders: number;
  totalSpent: number;
  isActive: boolean;
  registeredAt: string;
}

export interface AdminUserBackendResponse {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  status: string;
  createdAt: string;
}

export interface AdminSettingsBackendResponse {
  storeName: string;
  supportEmail: string;
  freeShippingThreshold: number;
  standardShippingFee: number;
  defaultTaxRatePercent: number;
  orderHoldMinutes: number;
  maintenanceMode: boolean;
}

export interface SystemMetricsBackendResponse {
  jvmVersion: string;
  uptimeSeconds: number;
  totalMemoryMb: number;
  freeMemoryMb: number;
  usedMemoryMb: number;
  availableProcessors: number;
  activeThreadCount: number;
  status: string;
}

export interface AuditLogBackendResponse {
  id: number;
  userEmail: string;
  action: string;
  entityName?: string;
  entityId?: string;
  ipAddress?: string;
  details?: string;
  createdAt: string;
}

export const AdminApiService = {
  async getDashboardSummary(): Promise<DashboardSummaryBackendResponse> {
    const res = await apiClient.get<DashboardSummaryBackendResponse>('/admin/dashboard/summary');
    return res.data;
  },

  async getSalesAnalytics(): Promise<SalesAnalyticsBackendResponse> {
    const res = await apiClient.get<SalesAnalyticsBackendResponse>('/admin/dashboard/sales-analytics');
    return res.data;
  },

  async getTopProducts(): Promise<TopProductPerformanceBackendDto[]> {
    const res = await apiClient.get<TopProductPerformanceBackendDto[]>('/admin/dashboard/top-products');
    return res.data;
  },

  async getCustomers(page: number = 0, size: number = 20): Promise<ApiPaginatedResponse<AdminCustomerBackendSummary>> {
    return apiClient.getPaginated<AdminCustomerBackendSummary>('/admin/customers', { page, size });
  },

  async getUsers(): Promise<AdminUserBackendResponse[]> {
    const res = await apiClient.get<AdminUserBackendResponse[]>('/admin/users');
    return res.data;
  },

  async getSettings(): Promise<AdminSettingsBackendResponse> {
    const res = await apiClient.get<AdminSettingsBackendResponse>('/admin/settings');
    return res.data;
  },

  async updateSettings(settings: AdminSettingsBackendResponse): Promise<AdminSettingsBackendResponse> {
    const res = await apiClient.put<AdminSettingsBackendResponse>('/admin/settings', settings);
    return res.data;
  },

  async getSystemMetrics(): Promise<SystemMetricsBackendResponse> {
    const res = await apiClient.get<SystemMetricsBackendResponse>('/observability/metrics');
    return res.data;
  },

  async getAuditLogs(action?: string, page: number = 0, size: number = 20): Promise<ApiPaginatedResponse<AuditLogBackendResponse>> {
    return apiClient.getPaginated<AuditLogBackendResponse>('/observability/audit-logs', { action, page, size });
  },
};

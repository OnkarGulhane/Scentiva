import { apiClient } from '../lib/api/apiClient';
import { ApiPaginatedResponse, ApiResponse } from '../types';

export interface OrderItemDetailBackendDto {
  id: number;
  variantId: number;
  productName: string;
  brandName: string;
  sku: string;
  volumeMl: number;
  concentration: string;
  unitPrice: number;
  quantity: number;
  lineTotal: number;
  imageUrl?: string;
}

export interface OrderDetailBackendResponse {
  id: number;
  orderNumber: string;
  status: string;
  items: OrderItemDetailBackendDto[];
  itemCount: number;
  subtotal: number;
  discountAmount: number;
  deliveryFee: number;
  taxAmount: number;
  finalTotal: number;
  currency: string;
  paymentMethod: string;
  paymentStatus: string;
  shippingAddress: {
    fullName: string;
    phone: string;
    streetAddress: string;
    city: string;
    state: string;
    postalCode: string;
    country: string;
  };
  trackingNumber?: string;
  carrierName?: string;
  createdAt: string;
  shippedAt?: string;
  deliveredAt?: string;
}

export interface OrderSummaryBackendResponse {
  id: number;
  orderNumber: string;
  status: string;
  itemCount: number;
  finalTotal: number;
  paymentStatus: string;
  createdAt: string;
}

export interface ShipmentTrackingEventBackendDto {
  status: string;
  location: string;
  description: string;
  timestamp: string;
}

export interface ShipmentTrackingBackendResponse {
  trackingNumber: string;
  carrierName: string;
  status: string;
  estimatedDeliveryDate?: string;
  originLocation: string;
  destinationLocation: string;
  events: ShipmentTrackingEventBackendDto[];
}

export const OrderApiService = {
  async getCustomerOrders(page: number = 0, size: number = 10): Promise<ApiPaginatedResponse<OrderSummaryBackendResponse>> {
    return apiClient.getPaginated<OrderSummaryBackendResponse>('/orders', { page, size });
  },

  async getOrderById(orderId: number): Promise<OrderDetailBackendResponse> {
    const res = await apiClient.get<OrderDetailBackendResponse>(`/orders/${orderId}`);
    return res.data;
  },

  async getOrderByNumber(orderNumber: string): Promise<OrderDetailBackendResponse> {
    const res = await apiClient.get<OrderDetailBackendResponse>(`/orders/number/${orderNumber}`);
    return res.data;
  },

  async cancelOrder(orderId: number, reason: string): Promise<OrderDetailBackendResponse> {
    const res = await apiClient.post<OrderDetailBackendResponse>(`/orders/${orderId}/cancel`, { reason });
    return res.data;
  },

  async trackShipment(trackingNumber: string): Promise<ShipmentTrackingBackendResponse> {
    const res = await apiClient.get<ShipmentTrackingBackendResponse>(`/shipping/track/${trackingNumber}`);
    return res.data;
  },
};

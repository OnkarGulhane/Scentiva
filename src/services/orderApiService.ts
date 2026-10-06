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

export interface InvoiceItemDto {
  id: number;
  sku: string;
  productName: string;
  brandName?: string;
  volumeMl?: number;
  concentration?: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface InvoiceResponseDto {
  invoiceNumber: string;
  orderNumber: string;
  invoiceDate: string;
  orderDate: string;
  invoiceStatus: string;
  orderStatus: string;
  companyName: string;
  brandTagline: string;
  registeredAddress: string;
  supportEmail: string;
  supportPhone: string;
  website: string;
  taxId: string;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  shippingAddress: string;
  billingAddress: string;
  subtotal: number;
  discountAmount: number;
  deliveryFee: number;
  taxAmount: number;
  grandTotal: number;
  currency: string;
  paymentProvider: string;
  paymentMethod: string;
  paymentStatus: string;
  razorpayOrderId?: string;
  razorpayPaymentId?: string;
  items: InvoiceItemDto[];
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

  async getOrderInvoice(orderNumber: string): Promise<InvoiceResponseDto> {
    const res = await apiClient.get<InvoiceResponseDto>(`/orders/${orderNumber}/invoice`);
    return res.data;
  },

  getInvoicePdfUrl(orderNumber: string): string {
    const baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';
    const cleanBase = baseUrl.endsWith('/') ? baseUrl.slice(0, -1) : baseUrl;
    return `${cleanBase}/orders/${orderNumber}/invoice/pdf`;
  },

  async downloadInvoicePdf(orderNumber: string, invoiceNumber?: string): Promise<void> {
    const token = apiClient.getToken();
    const url = this.getInvoicePdfUrl(orderNumber);
    const headers: Record<string, string> = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, { headers });
    if (!response.ok) {
      throw new Error(`Failed to download invoice PDF (HTTP ${response.status})`);
    }

    const blob = await response.blob();
    const blobUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = blobUrl;
    link.download = `${invoiceNumber || orderNumber}-invoice.pdf`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(blobUrl);
  },
};


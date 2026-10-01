import { apiClient } from '../lib/api/apiClient';
import { ApiResponse } from '../types';

export interface CheckoutSummaryBackendResponse {
  items: Array<{
    variantId: number;
    productName: string;
    brandName: string;
    volumeMl: number;
    unitPrice: number;
    quantity: number;
    lineTotal: number;
  }>;
  itemCount: number;
  subtotal: number;
  discountAmount: number;
  appliedCouponCode?: string;
  deliveryFee: number;
  taxAmount: number;
  finalTotal: number;
  isEligibleForFreeShipping: boolean;
}

export interface CheckoutProcessPayload {
  addressId: number;
  paymentMethod: string;
  couponCode?: string;
  orderNotes?: string;
  idempotencyKey: string;
}

export interface CheckoutProcessBackendResponse {
  orderId: number;
  orderNumber: string;
  status: string;
  totalAmount: number;
  currency: string;
  paymentId: number;
  paymentProvider: string;
  paymentTransactionId: string;
  clientSecret?: string;
  requiresAction: boolean;
  actionUrl?: string;
}

export interface CheckoutVerifyPayload {
  orderId: number;
  paymentId: number;
  transactionId: string;
  providerSignature?: string;
  rawResponsePayload?: string;
}

export interface CheckoutVerifyBackendResponse {
  orderId: number;
  orderNumber: string;
  status: string;
  paymentStatus: string;
  paidAt: string;
  estimatedDeliveryDate: string;
}

export interface CouponValidationBackendResponse {
  code: string;
  discountType: string;
  discountValue: number;
  discountAmount: number;
  finalSubtotal: number;
  isValid: boolean;
  message: string;
}

export const CheckoutApiService = {
  async getCheckoutSummary(couponCode?: string): Promise<CheckoutSummaryBackendResponse> {
    const res = await apiClient.get<CheckoutSummaryBackendResponse>('/checkout/summary', { couponCode });
    return res.data;
  },

  async processCheckout(payload: CheckoutProcessPayload): Promise<CheckoutProcessBackendResponse> {
    const res = await apiClient.post<CheckoutProcessBackendResponse>('/checkout/process', payload);
    return res.data;
  },

  async verifyPayment(payload: CheckoutVerifyPayload): Promise<CheckoutVerifyBackendResponse> {
    const res = await apiClient.post<CheckoutVerifyBackendResponse>('/checkout/verify', payload);
    return res.data;
  },

  async validateCoupon(code: string, subtotal: number): Promise<CouponValidationBackendResponse> {
    const res = await apiClient.get<CouponValidationBackendResponse>('/coupons/validate', {
      code,
      subtotal,
    });
    return res.data;
  },
};

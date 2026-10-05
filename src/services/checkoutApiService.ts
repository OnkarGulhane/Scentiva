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
  shippingAddressId: number;
  paymentMethod: string;
  paymentProvider?: 'RAZORPAY' | 'DEMO' | 'STRIPE';
  couponCode?: string;
  notes?: string;
  idempotencyKey?: string;
}

export interface CheckoutProcessBackendResponse {
  orderId: number;
  orderNumber: string;
  orderStatus: string;
  totalAmount: number;
  paymentId: number;
  paymentStatus: string;
  gatewayOrderId: string;
  keyId?: string;
  requiresAction: boolean;
  actionUrl?: string;
  message: string;
}

export interface CheckoutVerifyPayload {
  orderNumber: string;
  gatewayPaymentId: string;
  gatewaySignature?: string;
  otp?: string;
}

export interface CheckoutVerifyBackendResponse {
  orderNumber: string;
  orderStatus: string;
  paymentStatus: string;
  success: boolean;
  message: string;
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

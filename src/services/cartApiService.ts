import { apiClient } from '../lib/api/apiClient';
import { ApiResponse } from '../types';

export interface CartItemBackendResponse {
  id: number;
  variantId: number;
  productId: number;
  productName: string;
  brandName: string;
  sku: string;
  volumeMl: number;
  concentration: string;
  imageUrl?: string;
  unitPrice: number;
  quantity: number;
  itemTotal: number;
  isAvailable: boolean;
  availableStock: number;
}

export interface CartBackendResponse {
  id: number;
  items: CartItemBackendResponse[];
  totalQuantity: number;
  subtotal: number;
  appliedCouponCode?: string;
  discountAmount: number;
  deliveryFee: number;
  finalTotal: number;
  isEligibleForFreeDelivery: boolean;
  amountNeededForFreeDelivery: number;
}

export const CartApiService = {
  async getCart(): Promise<CartBackendResponse> {
    const res = await apiClient.get<CartBackendResponse>('/cart');
    return res.data;
  },

  async addItem(variantId: number, quantity: number = 1): Promise<CartBackendResponse> {
    const res = await apiClient.post<CartBackendResponse>('/cart/items', { variantId, quantity });
    return res.data;
  },

  async updateItemQuantity(itemId: number, quantity: number): Promise<CartBackendResponse> {
    const res = await apiClient.put<CartBackendResponse>(`/cart/items/${itemId}`, { quantity });
    return res.data;
  },

  async removeItem(itemId: number): Promise<CartBackendResponse> {
    const res = await apiClient.delete<CartBackendResponse>(`/cart/items/${itemId}`);
    return res.data;
  },

  async clearCart(): Promise<void> {
    await apiClient.delete<void>('/cart');
  },

  async mergeGuestCart(items: Array<{ variantId: number; quantity: number }>): Promise<CartBackendResponse> {
    const res = await apiClient.post<CartBackendResponse>('/cart/merge', { items });
    return res.data;
  },
};

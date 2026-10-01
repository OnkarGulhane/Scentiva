import { AnalyticsEventType, AnalyticsPayload } from '../types';

/**
 * SCENTIVA Universal Analytics Service
 * Implements Section 39 Analytics Events specification.
 * Backend-ready: batches and dispatches events to future analytics ingestion / data warehouse.
 */
class AnalyticsService {
  private isDevelopment = process.env.NODE_ENV !== 'production';

  /**
   * Universal track method
   */
  track(eventName: AnalyticsEventType, properties: Record<string, string | number | boolean | string[] | undefined> = {}) {
    const payload: AnalyticsPayload = {
      eventName,
      properties,
      timestamp: new Date().toISOString(),
    };

    if (this.isDevelopment) {
      console.log(`[Analytics Event: ${eventName}]`, payload);
    }

    // Future backend endpoint ingestion (e.g. POST /api/v1/analytics/events)
    if (typeof window !== 'undefined' && (window as any).dataLayer) {
      (window as any).dataLayer.push(payload);
    }
  }

  // Typed convenience trackers for Section 39 Core Events:

  trackScentFinderStarted(step: number = 1) {
    this.track('scent_finder_started', { step });
  }

  trackScentFinderCompleted(family?: string, occasion?: string, matchesCount?: number) {
    this.track('scent_finder_completed', { family, occasion, matchesCount });
  }

  trackFragranceNoteClicked(note: string, context: 'catalog' | 'pdp' | 'search' = 'catalog') {
    this.track('fragrance_note_clicked', { note, context });
  }

  trackFragranceProfileViewed(productId: string, productName: string, brandName: string) {
    this.track('fragrance_profile_viewed', { productId, productName, brandName });
  }

  track3DProductInteraction(action: 'drag' | 'rotate' | 'hover') {
    this.track(action === 'rotate' ? '3d_product_rotated' : '3d_product_interaction', { action });
  }

  trackProductViewed(productId: string, productName: string, price: number) {
    this.track('product_viewed', { productId, productName, price });
  }

  trackWishlistAdded(productId: string, productName: string) {
    this.track('wishlist_added', { productId, productName });
  }

  trackWishlistRemoved(productId: string) {
    this.track('wishlist_removed', { productId });
  }

  trackCartAdded(productId: string, variantSku: string, price: number, quantity: number = 1) {
    this.track('cart_added', { productId, variantSku, price, quantity });
  }

  trackCartRemoved(productId: string, variantSku: string) {
    this.track('cart_removed', { productId, variantSku });
  }

  trackSearchStarted(query: string) {
    this.track('search_started', { query });
  }

  trackSearchZeroResult(query: string) {
    this.track('search_zero_result', { query });
  }

  trackRecommendationViewed(recommendationType: string, count: number) {
    this.track('recommendation_viewed', { recommendationType, count });
  }

  trackRecommendationClicked(productId: string, recommendationType: string) {
    this.track('recommendation_clicked', { productId, recommendationType });
  }

  trackCheckoutStarted(cartTotal: number, itemCount: number) {
    this.track('checkout_started', { cartTotal, itemCount });
  }

  trackPurchaseCompleted(orderId: string, orderNumber: string, total: number, paymentMethod: string) {
    this.track('purchase_completed', { orderId, orderNumber, total, paymentMethod });
  }
}

export const analytics = new AnalyticsService();

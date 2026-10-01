import { Coupon } from '../types';
import { COUPONS } from '../data/coupons';

const COUPONS_STORAGE_KEY = 'scentiva_coupons';

export const getStoredCoupons = (): Coupon[] => {
  if (typeof window === 'undefined') return COUPONS;
  try {
    const raw = localStorage.getItem(COUPONS_STORAGE_KEY);
    if (!raw) return COUPONS;
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) && parsed.length > 0 ? parsed : COUPONS;
  } catch {
    return COUPONS;
  }
};

export const saveStoredCoupons = (coupons: Coupon[]): void => {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(COUPONS_STORAGE_KEY, JSON.stringify(coupons));
  } catch (err) {
    console.error('Failed to save coupons to localStorage:', err);
  }
};

export const PromotionService = {
  getAll: (): Coupon[] => {
    return getStoredCoupons();
  },

  getByCode: (code: string): Coupon | undefined => {
    const all = getStoredCoupons();
    return all.find(c => c.code.toUpperCase() === code.trim().toUpperCase());
  },

  validate: (code: string, subtotal: number): { valid: boolean; coupon?: Coupon; message: string; discountAmount: number } => {
    const cleanCode = code.trim().toUpperCase();
    const coupon = PromotionService.getByCode(cleanCode);

    if (!coupon) {
      return { valid: false, message: `Coupon code "${code}" is invalid or expired.`, discountAmount: 0 };
    }

    if (subtotal < coupon.minOrderValue) {
      const diff = coupon.minOrderValue - subtotal;
      return {
        valid: false,
        coupon,
        message: `Add ₹${diff.toLocaleString('en-IN')} more to your bag to apply coupon ${coupon.code}.`,
        discountAmount: 0
      };
    }

    let discountAmount = 0;
    if (coupon.discountType === 'percentage') {
      discountAmount = Math.round((subtotal * coupon.discountValue) / 100);
    } else {
      discountAmount = coupon.discountValue;
    }

    return {
      valid: true,
      coupon,
      message: `Coupon "${coupon.code}" successfully applied!`,
      discountAmount
    };
  },

  create: (coupon: Coupon): Coupon => {
    const all = getStoredCoupons();
    const cleanCode = coupon.code.toUpperCase().trim();
    const updated = [...all.filter(c => c.code !== cleanCode), { ...coupon, code: cleanCode }];
    saveStoredCoupons(updated);
    return coupon;
  },

  delete: (code: string): boolean => {
    const all = getStoredCoupons();
    const cleanCode = code.toUpperCase().trim();
    const filtered = all.filter(c => c.code !== cleanCode);
    if (filtered.length === all.length) return false;
    saveStoredCoupons(filtered);
    return true;
  }
};

import { Coupon } from '../types';

export const COUPONS: Coupon[] = [
  {
    code: 'WELCOME10',
    discountType: 'percentage',
    discountValue: 10,
    minOrderValue: 2999,
    description: '10% OFF on your first luxury order above ₹2,999',
    expiresAt: '2026-12-31'
  },
  {
    code: 'LUXURY20',
    discountType: 'percentage',
    discountValue: 20,
    minOrderValue: 15000,
    description: '20% OFF on prestige and niche orders above ₹15,000',
    expiresAt: '2026-11-30'
  },
  {
    code: 'FIRSTSCENT',
    discountType: 'flat',
    discountValue: 1000,
    minOrderValue: 6999,
    description: 'Flat ₹1,000 OFF on orders above ₹6,999',
    expiresAt: '2026-10-31'
  },
  {
    code: 'FESTIVE500',
    discountType: 'flat',
    discountValue: 500,
    minOrderValue: 4999,
    description: 'Instant ₹500 discount for festive celebrations',
    expiresAt: '2026-12-31'
  }
];

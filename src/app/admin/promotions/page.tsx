import { Metadata } from 'next';
import { AdminPromotionsPage } from '@/views/admin/AdminPromotionsPage';

export const metadata: Metadata = {
  title: 'Coupons & Promotional Campaigns | SCENTIVA Admin Console',
  description: 'Manage discount vouchers, flash sale thresholds, and exclusive customer promo codes.',
};

export default function AdminPromotionsRoute() {
  return <AdminPromotionsPage />;
}

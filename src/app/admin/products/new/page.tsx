import { Metadata } from 'next';
import { AdminNewProductPage } from '@/views/admin/AdminNewProductPage';

export const metadata: Metadata = {
  title: 'Add New Luxury Fragrance | SCENTIVA Admin Console',
  description: 'Upload new artisanal perfumes, define top/heart/base notes, set sizes, prices, and stock.',
};

export default function AdminNewProductRoute() {
  return <AdminNewProductPage />;
}

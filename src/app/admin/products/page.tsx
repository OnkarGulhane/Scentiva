import { Metadata } from 'next';
import { AdminProductsPage } from '@/views/admin/AdminProductsPage';

export const metadata: Metadata = {
  title: 'Fragrance Catalog Management | SCENTIVA Admin Console',
  description: 'Manage luxury perfume inventory, prices, olfactory pyramids, and variants.',
};

export default function AdminProductsRoute() {
  return <AdminProductsPage />;
}

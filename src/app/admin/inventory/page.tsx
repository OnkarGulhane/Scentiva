import { Metadata } from 'next';
import { AdminInventoryPage } from '@/views/admin/AdminInventoryPage';

export const metadata: Metadata = {
  title: 'Inventory & Stock Control | SCENTIVA Admin Console',
  description: 'Track stock reserves, low-inventory alerts, batch allocations, and climate-controlled storage counts.',
};

export default function AdminInventoryRoute() {
  return <AdminInventoryPage />;
}

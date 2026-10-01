import { Metadata } from 'next';
import { AdminOrdersPage } from '@/views/admin/AdminOrdersPage';

export const metadata: Metadata = {
  title: 'Order Dispatch & Shipments | SCENTIVA Admin Console',
  description: 'Manage order fulfillment pipelines, courier AWB tracking, dispatch manifests, and delivery status updates.',
};

export default function AdminOrdersRoute() {
  return <AdminOrdersPage />;
}

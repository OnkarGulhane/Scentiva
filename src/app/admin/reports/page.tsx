import { Metadata } from 'next';
import { AdminReportsPage } from '@/views/admin/AdminReportsPage';

export const metadata: Metadata = {
  title: 'Analytics & Financial Reports | SCENTIVA Admin Console',
  description: 'View sales breakdown by brand, average order value, conversion funnels, and fragrance family popularity.',
};

export default function AdminReportsRoute() {
  return <AdminReportsPage />;
}

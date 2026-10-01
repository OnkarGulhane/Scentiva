import { Metadata } from 'next';
import { AdminDashboardPage } from '@/views/admin/AdminDashboardPage';

export const metadata: Metadata = {
  title: 'Operations Dashboard | SCENTIVA Admin Console',
  description: 'Real-time sales velocity, order fulfillment metrics, and catalog oversight for SCENTIVA.',
};

export default function AdminDashboardRoute() {
  return <AdminDashboardPage />;
}

import { Metadata } from 'next';
import { AdminSettingsPage } from '@/views/admin/AdminSettingsPage';

export const metadata: Metadata = {
  title: 'Store Settings & Policies | SCENTIVA Admin Console',
  description: 'Configure store parameters, shipping fee thresholds, currency formats, and notification preferences.',
};

export default function AdminSettingsRoute() {
  return <AdminSettingsPage />;
}

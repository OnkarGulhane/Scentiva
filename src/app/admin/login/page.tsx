import { Metadata } from 'next';
import { AdminLoginPage } from '@/views/admin/AdminLoginPage';

export const metadata: Metadata = {
  title: 'Admin Authentication | SCENTIVA Console',
  description: 'Secure credentials gateway for SCENTIVA marketplace managers and inventory curators.',
};

export default function AdminLoginRoute() {
  return <AdminLoginPage />;
}

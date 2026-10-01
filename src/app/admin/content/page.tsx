import { Metadata } from 'next';
import { AdminContentPage } from '@/views/admin/AdminContentPage';

export const metadata: Metadata = {
  title: 'Editorial CMS & Stories | SCENTIVA Admin Console',
  description: 'Manage homepage hero campaigns, masterclasses, editorial articles, and fragrance journals.',
};

export default function AdminContentRoute() {
  return <AdminContentPage />;
}

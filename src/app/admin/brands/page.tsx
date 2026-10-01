import { Metadata } from 'next';
import { AdminBrandsPage } from '@/views/admin/AdminBrandsPage';

export const metadata: Metadata = {
  title: 'Brand Registry Management | SCENTIVA Admin Console',
  description: 'Manage master perfumery houses, origins, emblems, and editorial brand narratives.',
};

export default function AdminBrandsRoute() {
  return <AdminBrandsPage />;
}

import { Metadata } from 'next';
import { AdminCategoriesPage } from '@/views/admin/AdminCategoriesPage';

export const metadata: Metadata = {
  title: 'Category Taxonomy Management | SCENTIVA Admin Console',
  description: 'Manage fragrance groupings, gender segments, and discovery collections.',
};

export default function AdminCategoriesRoute() {
  return <AdminCategoriesPage />;
}

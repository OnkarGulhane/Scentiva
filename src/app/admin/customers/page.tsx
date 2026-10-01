import { Metadata } from 'next';
import { AdminCustomersPage } from '@/views/admin/AdminCustomersPage';

export const metadata: Metadata = {
  title: 'Customer Registry & VIP Loyalty | SCENTIVA Admin Console',
  description: 'Manage client accounts, lifetime purchase values, connoisseur tier rankings, and consultation notes.',
};

export default function AdminCustomersRoute() {
  return <AdminCustomersPage />;
}

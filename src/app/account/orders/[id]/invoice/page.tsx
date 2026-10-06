import { Metadata } from 'next';
import { InvoiceViewPage } from '@/views/InvoiceViewPage';

export const metadata: Metadata = {
  title: 'Tax Invoice & Receipt | SCENTIVA Haute Parfumerie',
  description: 'Official tax invoice and luxury order authenticity certificate for your SCENTIVA fragrance purchase.',
};

export default function OrderInvoiceRoute() {
  return <InvoiceViewPage />;
}

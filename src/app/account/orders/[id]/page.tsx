import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { OrderTrackingPage } from '@/views/OrderTrackingPage';

interface Props {
  params: {
    id: string;
  };
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  return {
    title: `Order #${params.id} Status | SCENTIVA Vault`,
    description: `Live tracking and status updates for order #${params.id}.`,
  };
}

export default function OrderTrackingRoute() {
  return <OrderTrackingPage />;
}

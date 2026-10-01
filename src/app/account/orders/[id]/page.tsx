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
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <OrderTrackingPage />
    </Suspense>
  );
}

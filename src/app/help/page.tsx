import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { HelpPolicyPages } from '@/views/HelpPolicyPages';

export const metadata: Metadata = {
  title: 'Customer Concierge & Help Center | SCENTIVA Haute Parfumerie',
  description: 'Access 24/7 client support, authenticity documentation, delivery guidelines, and return procedures.',
};

export default function HelpRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <HelpPolicyPages />
    </Suspense>
  );
}

import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { HelpPolicyPages } from '@/views/HelpPolicyPages';

export const metadata: Metadata = {
  title: 'Customer Concierge & Help Center | SCENTIVA Haute Parfumerie',
  description: 'Access 24/7 client support, authenticity documentation, delivery guidelines, and return procedures.',
};

export default function HelpRoute() {
  return <HelpPolicyPages />;
}

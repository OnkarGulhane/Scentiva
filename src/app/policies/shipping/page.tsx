import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { HelpPolicyPages } from '@/views/HelpPolicyPages';

export const metadata: Metadata = {
  title: 'Thermal Courier & Shipping Policy | SCENTIVA Haute Parfumerie',
  description: 'Details regarding temperature-regulated air delivery, transit insurance, and tamper-sealed shipping across India.',
};

export default function ShippingPolicyRoute() {
  return <HelpPolicyPages />;
}

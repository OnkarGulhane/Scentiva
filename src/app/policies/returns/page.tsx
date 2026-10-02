import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { HelpPolicyPages } from '@/views/HelpPolicyPages';

export const metadata: Metadata = {
  title: '7-Day Seal Return & Exchange Guarantee | SCENTIVA Haute Parfumerie',
  description: 'Understand our hassle-free return policy and complimentary discovery vial sampling guarantee.',
};

export default function ReturnsPolicyRoute() {
  return <HelpPolicyPages />;
}

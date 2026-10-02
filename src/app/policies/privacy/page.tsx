import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { HelpPolicyPages } from '@/views/HelpPolicyPages';

export const metadata: Metadata = {
  title: 'Privacy Policy & Data Security | SCENTIVA Haute Parfumerie',
  description: 'How SCENTIVA safeguards your personal preferences, order details, and browsing data.',
};

export default function PrivacyPolicyRoute() {
  return <HelpPolicyPages />;
}

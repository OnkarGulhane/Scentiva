import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { HelpPolicyPages } from '@/views/HelpPolicyPages';

export const metadata: Metadata = {
  title: 'Terms of Service & Marketplace Guidelines | SCENTIVA Haute Parfumerie',
  description: 'Terms and conditions governing the purchase of genuine fragrance products on SCENTIVA.',
};

export default function TermsRoute() {
  return <HelpPolicyPages />;
}

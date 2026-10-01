import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { HelpPolicyPages } from '@/views/HelpPolicyPages';

export const metadata: Metadata = {
  title: 'Terms of Service & Marketplace Guidelines | SCENTIVA Haute Parfumerie',
  description: 'Terms and conditions governing the purchase of genuine fragrance products on SCENTIVA.',
};

export default function TermsRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <HelpPolicyPages />
    </Suspense>
  );
}

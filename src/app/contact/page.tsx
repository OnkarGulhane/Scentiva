import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { HelpPolicyPages } from '@/views/HelpPolicyPages';

export const metadata: Metadata = {
  title: 'Contact Concierge & Private Appointments | SCENTIVA Haute Parfumerie',
  description: 'Connect with SCENTIVA’s master fragrance advisors for bespoke gifting and private sourcing.',
};

export default function ContactRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <HelpPolicyPages />
    </Suspense>
  );
}

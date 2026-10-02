import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { HelpPolicyPages } from '@/views/HelpPolicyPages';

export const metadata: Metadata = {
  title: 'Contact Concierge & Private Appointments | SCENTIVA Haute Parfumerie',
  description: 'Connect with SCENTIVA’s master fragrance advisors for bespoke gifting and private sourcing.',
};

export default function ContactRoute() {
  return <HelpPolicyPages />;
}

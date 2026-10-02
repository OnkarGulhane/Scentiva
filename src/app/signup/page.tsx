import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { SignUpPage } from '@/views/SignUpPage';

export const metadata: Metadata = {
  title: 'Join Privé | SCENTIVA Haute Parfumerie',
  description: 'Create a SCENTIVA Privé membership for early access, fragrance concierge, and curated orders.',
};

export default function SignupRoute() {
  return (
    <Suspense fallback={null}>
      <SignUpPage />
    </Suspense>
  );
}

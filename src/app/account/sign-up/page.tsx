import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { SignUpPage } from '@/views/SignUpPage';

export const metadata: Metadata = {
  title: 'Join SCENTIVA Society | SCENTIVA Haute Parfumerie',
  description: 'Create an account to join the SCENTIVA Connoisseur Club and receive 500 complimentary welcome points and sample privileges.',
};

export default function SignUpRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <SignUpPage />
    </Suspense>
  );
}

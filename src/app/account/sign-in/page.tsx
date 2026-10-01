import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { SignInPage } from '@/views/SignInPage';

export const metadata: Metadata = {
  title: 'Sign In | SCENTIVA Haute Parfumerie',
  description: 'Sign in to access your personal vault, order tracking, and exclusive connoisseur benefits.',
};

export default function SignInRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <SignInPage />
    </Suspense>
  );
}

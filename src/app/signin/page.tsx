import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { SignInPage } from '@/views/SignInPage';

export const metadata: Metadata = {
  title: 'Sign In | SCENTIVA Haute Parfumerie',
  description: 'Sign in to access your luxury fragrance cart, order history, and concierge tier.',
};

export default function SigninRoute() {
  return (
    <Suspense fallback={null}>
      <SignInPage />
    </Suspense>
  );
}

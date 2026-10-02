import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { SignInPage } from '@/views/SignInPage';

export const metadata: Metadata = {
  title: 'Sign In | SCENTIVA Haute Parfumerie',
  description: 'Sign in to access your personal vault, order tracking, and exclusive connoisseur benefits.',
};

export default function SignInRoute() {
  return <SignInPage />;
}

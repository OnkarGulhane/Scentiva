import React from 'react';
import { Metadata } from 'next';
import { SignUpPage } from '@/views/SignUpPage';

export const metadata: Metadata = {
  title: 'Join SCENTIVA Society | SCENTIVA Haute Parfumerie',
  description: 'Create an account to join the SCENTIVA Connoisseur Club and receive 500 complimentary welcome points and sample privileges.',
};

export default function SignUpRoute() {
  return <SignUpPage />;
}

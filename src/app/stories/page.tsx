import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { StoriesPage } from '@/views/StoriesPage';

export const metadata: Metadata = {
  title: 'Fragrance Journal & Masterclasses | SCENTIVA Haute Parfumerie',
  description: 'Immerse yourself in editorial guides on fragrance layering, olfactory pyramids, and the history of haute perfumery.',
};

export default function StoriesRoute() {
  return <StoriesPage />;
}

import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { StoriesPage } from '@/views/StoriesPage';

export const metadata: Metadata = {
  title: 'Fragrance Journal & Masterclasses | SCENTIVA Haute Parfumerie',
  description: 'Immerse yourself in editorial guides on fragrance layering, olfactory pyramids, and the history of haute perfumery.',
};

export default function StoriesRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <StoriesPage />
    </Suspense>
  );
}

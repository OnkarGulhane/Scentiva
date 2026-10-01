import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { CategoryDetailPage } from '@/views/CategoryDetailPage';
import { CategoryService } from '@/services/categoryService';

interface Props {
  params: {
    slug: string;
  };
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const category = CategoryService.getBySlug(params.slug);

  if (!category) {
    return {
      title: 'Collection Not Found | SCENTIVA Haute Parfumerie',
      description: 'The requested fragrance category could not be located.',
    };
  }

  return {
    title: `${category.title} Perfume Collection | SCENTIVA Vault`,
    description: `Explore the finest ${category.title} fragrances curated by SCENTIVA. Certified authentic bottles with express delivery.`,
  };
}

export default function CategoryDetailRoute() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
      <CategoryDetailPage />
    </Suspense>
  );
}

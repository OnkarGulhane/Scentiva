import React from 'react';
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
      description: 'The requested fragrance collection could not be located in our cellar vault.',
    };
  }

  return {
    title: `${category.title} Perfume Collection | SCENTIVA Vault`,
    description: `Explore the finest ${category.title} fragrances curated by SCENTIVA. 100% authentic flacons with complimentary express delivery.`,
  };
}

export default function CollectionDetailRoute() {
  return <CategoryDetailPage />;
}

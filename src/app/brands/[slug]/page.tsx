import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { BrandDetailPage } from '@/views/BrandDetailPage';
import { BrandService } from '@/services/brandService';

interface Props {
  params: {
    slug: string;
  };
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const brand = BrandService.getBySlug(params.slug);

  if (!brand) {
    return {
      title: 'Brand Not Found | SCENTIVA Haute Parfumerie',
      description: 'The requested luxury fragrance house could not be located.',
    };
  }

  return {
    title: `${brand.name} Fragrance Collection | SCENTIVA Vault`,
    description: `Discover the luxury perfumes of ${brand.name} (${brand.origin}). 100% authentic curated flacons with batch verification.`,
  };
}

export default function BrandDetailRoute() {
  return <BrandDetailPage />;
}

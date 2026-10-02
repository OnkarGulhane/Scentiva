import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { ProductDetailPage } from '@/views/ProductDetailPage';
import { ProductService } from '@/services/productService';

interface Props {
  params: {
    slug: string;
  };
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const product = ProductService.getBySlug(params.slug);

  if (!product) {
    return {
      title: 'Fragrance Not Found | SCENTIVA Haute Parfumerie',
      description: 'The requested luxury fragrance could not be located in our vault.',
    };
  }

  const primaryImage = product.images?.[0] || '';
  const topNotes = product.notes?.top?.join(', ') || '';

  return {
    title: `${product.name} by ${product.brandName} | SCENTIVA Vault`,
    description: `${product.tagline || product.name}. Discover olfactory notes of ${topNotes} at SCENTIVA. 100% authentic luxury fragrance.`,
    openGraph: {
      title: `${product.name} | ${product.brandName}`,
      description: product.tagline || product.description,
      images: primaryImage ? [{ url: primaryImage, width: 800, height: 1000, alt: product.name }] : [],
    },
  };
}

export default function ProductPage({ params }: Props) {
  const product = ProductService.getBySlug(params.slug);

  const primaryImage = product?.images?.[0] || '';
  const primaryPrice = product?.variants?.[0]?.price || 0;

  const jsonLd = product ? {
    '@context': 'https://schema.org',
    '@type': 'Product',
    name: product.name,
    image: primaryImage,
    description: product.description,
    brand: {
      '@type': 'Brand',
      name: product.brandName,
    },
    offers: {
      '@type': 'Offer',
      priceCurrency: 'INR',
      price: primaryPrice,
      availability: product.stock > 0 ? 'https://schema.org/InStock' : 'https://schema.org/OutOfStock',
      url: `https://scentiva.luxury/product/${product.slug}`,
    },
    aggregateRating: product.rating ? {
      '@type': 'AggregateRating',
      ratingValue: product.rating,
      reviewCount: product.reviewCount || 12,
    } : undefined,
  } : null;

  return (
    <>
      {jsonLd && (
        <script
          type="application/ld+json"
          dangerouslySetInnerHTML={{ __html: JSON.stringify(jsonLd) }}
        />
      )}
      <ProductDetailPage />
    </>
  );
}

import React, { Suspense } from 'react';
import { HeroSection } from '@/components/home/HeroSection';
import { BrandTicker } from '@/components/home/BrandTicker';
import { CategoryCards } from '@/components/home/CategoryCards';
import { BestSellers } from '@/components/home/BestSellers';
import { ScentFinderTeaser } from '@/components/home/ScentFinderTeaser';
import { EditorialSection } from '@/components/home/EditorialSection';
import { TrustBadges } from '@/components/home/TrustBadges';

export default function HomePage() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-neutral-50 animate-pulse" />}>
      <div className="min-h-screen w-full">
        <HeroSection />
        <BrandTicker />
        <CategoryCards />
        <BestSellers />
        <ScentFinderTeaser />
        <EditorialSection />
        <TrustBadges />
      </div>
    </Suspense>
  );
}

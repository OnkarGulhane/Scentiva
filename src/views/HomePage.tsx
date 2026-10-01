import React from 'react';
import { HeroSection } from '../components/home/HeroSection';
import { BrandTicker } from '../components/home/BrandTicker';
import { CategoryCards } from '../components/home/CategoryCards';
import { BestSellers } from '../components/home/BestSellers';
import { ScentFinderTeaser } from '../components/home/ScentFinderTeaser';
import { EditorialSection } from '../components/home/EditorialSection';
import { TrustBadges } from '../components/home/TrustBadges';

export const HomePage: React.FC = () => {
  return (
    <div className="min-h-screen">
      <HeroSection />
      <BrandTicker />
      <CategoryCards />
      <BestSellers />
      <ScentFinderTeaser />
      <EditorialSection />
      <TrustBadges />
    </div>
  );
};

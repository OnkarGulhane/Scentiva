import React from 'react';
import { Link } from '../common/Link';
import { BRANDS } from '../../data/brands';
import { ArrowRight } from 'lucide-react';

export const BrandTicker: React.FC = () => {
  return (
    <section className="py-12 bg-white border-b border-neutral-200/60 overflow-hidden">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mb-6 flex items-center justify-between">
        <div>
          <span className="text-[11px] font-semibold uppercase tracking-widest text-brand-rose-500 block">
            Iconic Houses
          </span>
          <h2 className="font-serif text-2xl font-semibold text-neutral-900">
            Shop by Fragrance House
          </h2>
        </div>
        <Link
          to="/brands"
          className="text-xs font-semibold text-brand-plum-900 hover:text-brand-rose-500 flex items-center gap-1 transition-colors"
        >
          <span>All Brands ({BRANDS.length})</span>
          <ArrowRight className="w-3.5 h-3.5" />
        </Link>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-3 sm:gap-4">
          {BRANDS.map(brand => (
            <Link
              key={brand.id}
              to={`/brands/${brand.slug}`}
              className="group p-4 rounded-xl border border-neutral-200/80 bg-neutral-50/50 hover:bg-white hover:border-brand-blush-300 hover:shadow-card transition-all text-center flex flex-col items-center justify-center min-h-[90px]"
            >
              <span className="font-serif text-lg font-bold tracking-wider text-neutral-800 group-hover:text-brand-plum-900 transition-colors">
                {brand.name.toUpperCase()}
              </span>
              <span className="text-[10px] text-neutral-400 mt-1 font-sans">
                {brand.origin.split(',')[0]} • {brand.featuredProductCount} Scents
              </span>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
};

import React from 'react';
import { Link } from '../common/Link';
import { CATEGORIES } from '../../data/categories';
import { ArrowRight, Sparkles } from 'lucide-react';

export const CategoryCards: React.FC = () => {
  return (
    <section className="py-16 bg-neutral-50 border-b border-neutral-200/60">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center max-w-2xl mx-auto mb-12 space-y-2">
          <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500">
            Curated Olfactory Collections
          </span>
          <h2 className="font-serif text-3xl sm:text-4xl font-bold text-brand-plum-950">
            Explore Fragrances by Character
          </h2>
          <p className="text-xs sm:text-sm text-neutral-600">
            From alluring feminine florals to audacious masculine woods and boundary-defying niche extracts.
          </p>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          {CATEGORIES.map(cat => (
            <Link
              key={cat.id}
              to={`/categories/${cat.slug}`}
              className="group relative rounded-2xl overflow-hidden aspect-[3/4] bg-neutral-900 shadow-card hover:shadow-card-hover transition-all duration-300 flex flex-col justify-end p-4 border border-neutral-200/60 hover:border-brand-blush-300"
            >
              {/* Background Image with Gradient Overlay */}
              <img
                src={cat.image}
                alt={cat.title}
                className="absolute inset-0 w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-700 ease-out opacity-80"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-brand-plum-950 via-brand-plum-950/40 to-transparent" />

              {/* Content */}
              <div className="relative z-10 space-y-1">
                <span className="text-[10px] uppercase font-semibold tracking-wider text-brand-blush-200 block">
                  {cat.tagline}
                </span>
                <h3 className="font-serif text-lg sm:text-xl font-bold text-white group-hover:text-brand-blush-100 transition-colors">
                  {cat.title}
                </h3>
                <div className="flex items-center justify-between text-[11px] text-neutral-300 pt-1">
                  <span>{cat.productCount} Fragrances</span>
                  <ArrowRight className="w-3.5 h-3.5 opacity-0 group-hover:opacity-100 group-hover:translate-x-1 transition-all" />
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
};

import React from 'react';
import { Link } from '@/components/common/Link';
import { Compass, Sparkles } from 'lucide-react';

export default function NotFound() {
  return (
    <div className="min-h-[70vh] w-full flex flex-col items-center justify-center p-8 text-center space-y-6">
      <div className="w-20 h-20 rounded-full bg-brand-blush-100/60 border border-brand-blush-300/40 flex items-center justify-center text-brand-plum-900 mx-auto">
        <Compass className="w-10 h-10 text-brand-rose-500 animate-spin-slow" />
      </div>

      <div className="space-y-2 max-w-md">
        <span className="text-xs font-semibold uppercase tracking-widest text-brand-gold-500 flex items-center justify-center gap-1.5">
          <Sparkles className="w-3.5 h-3.5" />
          404 • Scent Unknown
        </span>
        <h1 className="font-serif text-3xl sm:text-4xl font-bold text-neutral-900">
          Fragrance Not Found
        </h1>
        <p className="text-xs sm:text-sm text-neutral-500 leading-relaxed">
          The flacon or olfactory journey you are looking for has evaporated or moved to another vault section.
        </p>
      </div>

      <div className="flex flex-col sm:flex-row items-center gap-3">
        <Link
          to="/shop"
          className="px-6 py-3 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white font-semibold text-xs transition-all shadow-sm"
        >
          Explore All Perfumes
        </Link>
        <Link
          to="/"
          className="px-6 py-3 rounded-full border border-neutral-300 hover:border-brand-plum-900 text-neutral-700 font-semibold text-xs transition-all"
        >
          Return Home
        </Link>
      </div>
    </div>
  );
}

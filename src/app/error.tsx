'use client';

import React, { useEffect } from 'react';
import { Link } from '@/components/common/Link';
import { RotateCcw } from 'lucide-react';

export default function GlobalError({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  useEffect(() => {
    console.error('SCENTIVA Runtime Error:', error);
  }, [error]);

  return (
    <div className="min-h-[60vh] w-full flex flex-col items-center justify-center p-8 text-center space-y-6">
      <div className="w-16 h-16 rounded-full bg-semantic-error/10 border border-semantic-error/30 flex items-center justify-center text-semantic-error mx-auto">
        <RotateCcw className="w-8 h-8" />
      </div>

      <div className="space-y-2 max-w-md">
        <h2 className="font-serif text-2xl sm:text-3xl font-bold text-neutral-900">
          Something went wrong
        </h2>
        <p className="text-xs sm:text-sm text-neutral-500 leading-relaxed">
          An unexpected interruption occurred during the olfactory experience.
        </p>
      </div>

      <div className="flex items-center gap-3">
        <button
          onClick={() => reset()}
          className="px-6 py-3 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white font-semibold text-xs transition-all shadow-sm flex items-center gap-2"
        >
          <RotateCcw className="w-3.5 h-3.5" />
          <span>Retry</span>
        </button>
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

import React from 'react';

export default function Loading() {
  return (
    <div className="min-h-[60vh] w-full flex flex-col items-center justify-center p-8 space-y-4">
      <div className="w-12 h-12 rounded-full border-2 border-brand-plum-900 border-t-brand-gold-500 animate-spin" />
      <span className="font-serif text-sm tracking-widest text-brand-plum-950 uppercase animate-pulse">
        SCENTIVA • Loading Atmosphere
      </span>
    </div>
  );
}

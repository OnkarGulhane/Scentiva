import React from 'react';

/**
 * Lightweight Static Flacon Fallback Poster
 * Renders instantaneously (<5ms) during Stage 1 LCP, WebGL error states, or when 3D is disabled.
 */
export const StaticFlaconFallback: React.FC = () => (
  <div className="w-full h-full min-h-[380px] sm:min-h-[460px] lg:min-h-[540px] flex items-center justify-center p-6 bg-gradient-to-tr from-brand-blush-100/40 via-white to-brand-gold-100/40 rounded-3xl">
    <div className="text-center space-y-4 animate-in fade-in duration-500">
      <div className="w-36 h-52 mx-auto rounded-3xl bg-gradient-to-b from-brand-plum-950 via-brand-plum-900 to-brand-plum-950 border-2 border-brand-gold-500/70 p-4 shadow-modal flex flex-col items-center justify-between relative overflow-hidden">
        <div className="absolute inset-0 bg-radial-gradient from-brand-gold-300/15 via-transparent to-transparent pointer-events-none" />
        
        {/* Cap Representation */}
        <div className="w-16 h-8 rounded-t-lg bg-gradient-to-r from-brand-plum-950 via-brand-plum-800 to-brand-plum-950 border border-brand-gold-400/50 flex items-center justify-center">
          <div className="w-8 h-1 bg-brand-gold-400/80 rounded-full" />
        </div>

        {/* Crest */}
        <div className="w-16 h-16 rounded-full border border-brand-gold-400/60 flex items-center justify-center bg-black/20 my-auto">
          <span className="font-serif text-2xl font-bold text-brand-gold-400">S</span>
        </div>

        {/* Label */}
        <div className="text-center space-y-0.5 pb-1">
          <div className="font-serif text-[11px] font-bold tracking-widest text-brand-gold-300 uppercase">
            SCENTIVA
          </div>
          <div className="text-[8px] tracking-wider text-brand-blush-200/80 uppercase">
            Haute Parfumerie
          </div>
        </div>
      </div>
      
      <div className="text-xs text-neutral-500 font-medium tracking-wide">
        SCENTIVA Signature Flacon Model • Master Curation
      </div>
    </div>
  </div>
);

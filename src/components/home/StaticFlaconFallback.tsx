import React from 'react';

/**
 * Lightweight Static Flacon Fallback Poster (Grand Luxury Presence)
 * Renders instantaneously (<5ms) during Stage 1 LCP, WebGL error states, or when 3D is disabled.
 */
export const StaticFlaconFallback: React.FC = () => (
  <div className="w-full h-full min-h-[500px] sm:min-h-[560px] lg:min-h-[620px] flex items-center justify-center p-6 bg-gradient-to-tr from-brand-blush-100/50 via-white to-brand-gold-100/40 rounded-3xl">
    <div className="text-center space-y-5 animate-in fade-in duration-500">
      {/* Grand Luxury Flacon Graphic */}
      <div className="w-52 h-72 mx-auto rounded-3xl bg-gradient-to-b from-brand-plum-950 via-brand-plum-900 to-brand-plum-950 border-2 border-brand-gold-500/80 p-5 shadow-2xl flex flex-col items-center justify-between relative overflow-hidden ring-4 ring-brand-gold-400/20">
        <div className="absolute inset-0 bg-radial-gradient from-brand-gold-300/20 via-transparent to-transparent pointer-events-none" />
        
        {/* Cap Representation */}
        <div className="w-24 h-10 rounded-t-xl bg-gradient-to-r from-brand-plum-950 via-brand-plum-800 to-brand-plum-950 border-b-2 border-brand-gold-400/80 flex items-center justify-center shadow-md">
          <div className="w-12 h-1.5 bg-brand-gold-400/90 rounded-full" />
        </div>

        {/* Crest */}
        <div className="w-20 h-20 rounded-full border-2 border-brand-gold-400/80 flex items-center justify-center bg-black/30 my-auto shadow-inner">
          <span className="font-serif text-3xl font-bold text-brand-gold-300 drop-shadow">S</span>
        </div>

        {/* Label */}
        <div className="text-center space-y-1 pb-2">
          <div className="font-serif text-xs font-bold tracking-[0.25em] text-brand-gold-300 uppercase">
            SCENTIVA
          </div>
          <div className="text-[9px] tracking-widest text-brand-blush-200/90 uppercase font-medium">
            Haute Parfumerie • Pure Parfum
          </div>
        </div>
      </div>
      
      <div className="text-xs text-neutral-500 font-medium tracking-wide">
        SCENTIVA Signature Flacon • Grand Scale Master Curation
      </div>
    </div>
  </div>
);

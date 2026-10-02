import React from 'react';
import { Sparkles } from 'lucide-react';

/**
 * High-End Luxury Flacon Visual (Grand Scale Master Curation)
 * Renders instantaneously (<1ms) with zero layout shift and seamless visual continuity.
 */
export const StaticFlaconFallback: React.FC = () => (
  <div className="w-full h-full min-h-[480px] sm:min-h-[540px] lg:min-h-[600px] xl:min-h-[640px] flex flex-col items-center justify-center p-6 relative overflow-hidden select-none">
    {/* Ambient Glows */}
    <div className="absolute inset-0 bg-radial-gradient from-brand-gold-300/15 via-brand-blush-200/10 to-transparent pointer-events-none" />

    <div className="relative z-10 flex flex-col items-center justify-center text-center space-y-6 max-w-sm">
      {/* Grand Crystal Flacon Simulation */}
      <div className="relative group cursor-pointer">
        {/* Outer Flacon Aura */}
        <div className="absolute -inset-4 bg-gradient-to-r from-brand-gold-400/20 via-brand-rose-400/20 to-brand-plum-500/20 rounded-full blur-2xl opacity-75 group-hover:opacity-100 transition-opacity duration-700" />

        {/* Flacon Container */}
        <div className="relative w-48 sm:w-56 h-64 sm:h-72 rounded-3xl bg-gradient-to-b from-white/80 via-white/40 to-white/90 backdrop-blur-xl border-2 border-brand-gold-400/70 shadow-2xl p-4 flex flex-col items-center justify-between ring-1 ring-white/80">
          
          {/* Heavy Magnetic Crown Cap */}
          <div className="w-24 h-9 rounded-xl bg-gradient-to-b from-brand-plum-950 via-brand-plum-900 to-brand-plum-950 border-b-2 border-brand-gold-400 shadow-md flex items-center justify-center">
            <div className="w-10 h-1 bg-brand-gold-400/90 rounded-full" />
          </div>

          {/* Gold Atomizer Neck */}
          <div className="w-14 h-3 bg-gradient-to-r from-brand-gold-300 via-brand-gold-400 to-brand-gold-500 rounded-sm shadow-xs -mt-1" />

          {/* Luminous Liquid Elixir Core */}
          <div className="w-full flex-1 my-2 rounded-2xl bg-gradient-to-b from-amber-300/85 via-amber-400/90 to-amber-500/85 border border-amber-300/60 shadow-inner flex flex-col items-center justify-center p-3 relative overflow-hidden">
            {/* Shimmer Light Reflection */}
            <div className="absolute top-0 left-0 w-full h-full bg-gradient-to-tr from-transparent via-white/35 to-transparent pointer-events-none" />

            {/* Emblem Seal */}
            <div className="w-14 h-14 rounded-full bg-brand-plum-950/90 border-2 border-brand-gold-400 flex items-center justify-center shadow-lg relative z-10">
              <span className="font-serif text-2xl font-bold text-brand-gold-300">S</span>
            </div>

            <div className="mt-2 text-center relative z-10">
              <span className="font-serif text-[10px] sm:text-[11px] font-bold tracking-[0.25em] text-brand-plum-950 uppercase block">
                SCENTIVA
              </span>
              <span className="text-[8px] font-sans font-bold tracking-widest text-brand-plum-900/80 uppercase">
                Extrait de Parfum
              </span>
            </div>
          </div>

          {/* Heavy Crystal Glass Base */}
          <div className="w-full h-4 rounded-b-xl bg-white/70 border-t border-brand-gold-300/50 backdrop-blur-md" />
        </div>
      </div>

      {/* Caption & Interactivity Hint */}
      <div className="inline-flex items-center gap-1.5 px-3.5 py-1 rounded-full bg-white/90 border border-neutral-200 shadow-xs text-[11px] font-medium text-brand-plum-950">
        <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
        <span>SCENTIVA Master Atelier Flacon</span>
      </div>
    </div>
  </div>
);


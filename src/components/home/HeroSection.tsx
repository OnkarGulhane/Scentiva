'use client';

import React, { useRef, Suspense, lazy } from 'react';
import { Link } from '../common/Link';
import { Sparkles, ArrowRight, Star } from 'lucide-react';
import gsap from 'gsap';
import { useGsapContext } from '../../motion/gsapContext';
import { MOTION_DURATIONS, MOTION_EASINGS } from '../../motion/motionTokens';
import { StaticFlaconFallback } from './StaticFlaconFallback';
import { featureFlags } from '../../services/featureFlags';
import { analytics } from '../../services/analyticsService';

// Staged Lazy Load for 3D Hero to prioritize LCP and zero-delay interactivity
const Hero3DCanvasLazy = lazy(() =>
  import('./Hero3DCanvas').then(module => ({ default: module.Hero3DCanvas }))
);

export const HeroSection: React.FC = () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const headlineRef = useRef<HTMLHeadingElement>(null);
  const copyRef = useRef<HTMLParagraphElement>(null);
  const ctaRef = useRef<HTMLDivElement>(null);
  const statsRef = useRef<HTMLDivElement>(null);

  useGsapContext(() => {
    const tl = gsap.timeline({ defaults: { ease: MOTION_EASINGS.easeOut } });

    tl.from('.hero-eyebrow', { opacity: 0, y: -12, duration: MOTION_DURATIONS.standard })
      .from(headlineRef.current, { opacity: 0, y: 18, duration: MOTION_DURATIONS.emphasis }, '-=0.2')
      .from(copyRef.current, { opacity: 0, y: 14, duration: MOTION_DURATIONS.standard }, '-=0.2')
      .from(ctaRef.current, { opacity: 0, y: 12, duration: MOTION_DURATIONS.standard }, '-=0.15')
      .from(statsRef.current, { opacity: 0, y: 12, duration: MOTION_DURATIONS.standard }, '-=0.1');
  }, containerRef);

  return (
    <section
      ref={containerRef}
      className="relative overflow-hidden bg-gradient-to-b from-brand-blush-100/40 via-neutral-50 to-neutral-50 pt-8 pb-16 lg:py-20 border-b border-neutral-200/60"
    >
      {/* Subtle Background Ambient Glows */}
      <div className="absolute top-10 left-1/4 w-96 h-96 rounded-full bg-brand-blush-200/25 blur-3xl pointer-events-none" />
      <div className="absolute bottom-10 right-10 w-96 h-96 rounded-full bg-brand-gold-100/35 blur-3xl pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-center">
          {/* Left Column: Typography & CTAs (Immediate First Render) */}
          <div className="lg:col-span-6 space-y-6 text-center lg:text-left z-10">
            {/* Eyebrow */}
            <div className="hero-eyebrow inline-flex items-center gap-1.5 sm:gap-2 px-3 sm:px-3.5 py-1.5 rounded-full bg-brand-plum-900 text-brand-blush-200 text-[10px] sm:text-xs font-semibold tracking-wider sm:tracking-widest uppercase shadow-sm max-w-full">
              <Sparkles className="w-3.5 h-3.5 text-brand-gold-500 shrink-0" />
              <span className="truncate">Haute Parfumerie & Multi-Brand Vault • Since 2026</span>
            </div>

            {/* Headline (Section 11 Core Message) */}
            <h1
              ref={headlineRef}
              className="font-serif text-4xl sm:text-5xl lg:text-6xl xl:text-7xl font-bold tracking-tight text-brand-plum-950 leading-[1.06]"
            >
              Find the Scent <br className="hidden sm:inline" />
              <span className="gold-gradient-text italic font-serif">That Feels Like You.</span>
            </h1>

            {/* Subheading */}
            <p
              ref={copyRef}
              className="text-base sm:text-lg text-neutral-600 max-w-xl mx-auto lg:mx-0 font-normal leading-relaxed"
            >
              Explore authentic luxury, designer, and artisanal fragrances from the world’s most coveted perfume houses—curated under one prestigious address.
            </p>

            {/* CTAs (Section 11 Specification) */}
            <div
              ref={ctaRef}
              className="flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-4 pt-2"
            >
              <Link
                to="/shop"
                onClick={() => analytics.track('recommendation_clicked', { source: 'hero_primary_cta' })}
                className="w-full sm:w-auto px-8 py-4 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white font-semibold text-sm flex items-center justify-center gap-2 shadow-card hover:shadow-card-hover transition-all active:scale-98"
              >
                <span>Explore Fragrances</span>
                <ArrowRight className="w-4 h-4" />
              </Link>

              {featureFlags.isEnabled('ENABLE_SCENT_FINDER') && (
                <Link
                  to="/find-your-scent"
                  onClick={() => analytics.trackScentFinderStarted(1)}
                  className="w-full sm:w-auto px-7 py-4 rounded-full bg-white hover:bg-brand-blush-100/60 text-brand-plum-900 font-semibold text-sm border border-brand-plum-900/20 flex items-center justify-center gap-2 transition-all shadow-xs"
                >
                  <Sparkles className="w-4 h-4 text-brand-rose-500" />
                  <span>Find Your Scent</span>
                </Link>
              )}
            </div>

            {/* Micro Trust Stats */}
            <div
              ref={statsRef}
              className="grid grid-cols-3 gap-4 pt-6 border-t border-neutral-200/80 max-w-lg mx-auto lg:mx-0 text-left"
            >
              <div className="space-y-0.5">
                <div className="text-xl sm:text-2xl font-bold font-serif text-brand-plum-950">10+</div>
                <div className="text-[11px] text-neutral-500 font-medium">Prestige Houses</div>
              </div>
              <div className="space-y-0.5 border-l border-neutral-200 pl-4">
                <div className="text-xl sm:text-2xl font-bold font-serif text-brand-plum-950">100%</div>
                <div className="text-[11px] text-neutral-500 font-medium">Authentic Flacons</div>
              </div>
              <div className="space-y-0.5 border-l border-neutral-200 pl-4">
                <div className="flex items-center gap-1 text-xl sm:text-2xl font-bold font-serif text-brand-plum-950">
                  <span>4.9</span>
                  <Star className="w-4 h-4 fill-brand-gold-500 text-brand-gold-500 inline" />
                </div>
                <div className="text-[11px] text-neutral-500 font-medium">Connoisseur Score</div>
              </div>
            </div>
          </div>

          {/* Right Column: Signature 3D Interactive Perfume Flacon (Grand Scale) */}
          <div className="lg:col-span-6 relative flex items-center justify-center w-full">
            <div className="w-full relative rounded-3xl bg-gradient-to-tr from-brand-blush-100/70 via-white to-brand-gold-100/50 p-3 sm:p-4 shadow-modal border border-white/95 overflow-hidden">
              {featureFlags.isEnabled('ENABLE_3D_HERO') ? (
                <Suspense fallback={<StaticFlaconFallback />}>
                  <Hero3DCanvasLazy />
                </Suspense>
              ) : (
                <StaticFlaconFallback />
              )}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

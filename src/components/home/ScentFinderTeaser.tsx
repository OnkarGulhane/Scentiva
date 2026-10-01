import React from 'react';
import { Link } from '../common/Link';
import { Sparkles, ArrowRight, Compass, Heart, CheckCircle2 } from 'lucide-react';

export const ScentFinderTeaser: React.FC = () => {
  return (
    <section className="py-16 bg-gradient-to-r from-brand-plum-950 via-brand-plum-900 to-brand-plum-950 text-white relative overflow-hidden">
      {/* Glow Effects */}
      <div className="absolute -top-24 -left-24 w-80 h-80 rounded-full bg-brand-rose-500/20 blur-3xl pointer-events-none" />
      <div className="absolute -bottom-24 -right-24 w-80 h-80 rounded-full bg-brand-gold-500/20 blur-3xl pointer-events-none" />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
          {/* Left: Copy & CTA */}
          <div className="lg:col-span-6 space-y-5 text-center lg:text-left">
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-brand-plum-800 text-brand-gold-500 text-xs font-semibold uppercase tracking-widest border border-brand-gold-500/30">
              <Sparkles className="w-3.5 h-3.5" />
              <span>AI Fragrance Matchmaker</span>
            </span>

            <h2 className="font-serif text-3xl sm:text-4xl lg:text-5xl font-bold leading-tight">
              Unsure Where to Begin? <br />
              <span className="gold-gradient-text italic">Find Your Perfect Scent.</span>
            </h2>

            <p className="text-sm text-brand-blush-200/90 leading-relaxed max-w-lg mx-auto lg:mx-0">
              Answer 4 simple lifestyle questions about your favorite scent memories, preferred occasions, and intensity profile. Our algorithmic sommelier will recommend your ideal olfactory matches.
            </p>

            <div className="flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-4 pt-2">
              <Link
                to="/find-your-scent"
                className="px-8 py-4 rounded-full bg-brand-gold-500 hover:bg-brand-gold-500/90 text-brand-plum-950 font-semibold text-xs uppercase tracking-wider flex items-center gap-2 shadow-lg transition-all active:scale-98"
              >
                <span>Start 60-Second Quiz</span>
                <ArrowRight className="w-4 h-4" />
              </Link>
            </div>
          </div>

          {/* Right: Quiz Preview Interactive Step Cards */}
          <div className="lg:col-span-6 grid grid-cols-2 gap-3.5">
            <div className="p-4 rounded-2xl bg-white/10 backdrop-blur-md border border-brand-blush-300/20 space-y-2">
              <span className="text-[10px] uppercase tracking-wider text-brand-gold-500 font-bold">Step 01</span>
              <h4 className="font-serif text-base font-semibold">Fragrance Family</h4>
              <p className="text-[11px] text-neutral-300">Fresh Citrus, Woody Oud, Velvet Floral, or Spicy Amber.</p>
            </div>

            <div className="p-4 rounded-2xl bg-white/10 backdrop-blur-md border border-brand-blush-300/20 space-y-2">
              <span className="text-[10px] uppercase tracking-wider text-brand-gold-500 font-bold">Step 02</span>
              <h4 className="font-serif text-base font-semibold">Occasion & Vibe</h4>
              <p className="text-[11px] text-neutral-300">Everyday signature, Boardroom presence, or Romantic evening.</p>
            </div>

            <div className="p-4 rounded-2xl bg-white/10 backdrop-blur-md border border-brand-blush-300/20 space-y-2">
              <span className="text-[10px] uppercase tracking-wider text-brand-gold-500 font-bold">Step 03</span>
              <h4 className="font-serif text-base font-semibold">Sillage & Longevity</h4>
              <p className="text-[11px] text-neutral-300">Subtle intimate skin scent or room-filling magnetic trail.</p>
            </div>

            <div className="p-4 rounded-2xl bg-white/10 backdrop-blur-md border border-brand-blush-300/20 space-y-2">
              <span className="text-[10px] uppercase tracking-wider text-brand-gold-500 font-bold">Step 04</span>
              <h4 className="font-serif text-base font-semibold">Personalized Match</h4>
              <p className="text-[11px] text-neutral-300">Top 3 bottles tailored with notes breakdown and why it fits you.</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

'use client';

import React, { useState, useEffect } from 'react';
import { Sparkles, ArrowRight, X } from 'lucide-react';
import { Link } from '../common/Link';

const ANNOUNCEMENTS = [
  {
    text: 'Complimentary Luxury Discovery Coffret with all orders above ₹9,999',
    link: '/offers',
    badge: 'Limited Gift'
  },
  {
    text: 'Use code WELCOME10 for 10% OFF on your signature fragrance journey',
    link: '/offers',
    badge: 'Code: WELCOME10'
  },
  {
    text: '100% Authentic Guaranteed Direct From Parisian & Italian Perfume Ateliers',
    link: '/policies/authenticity',
    badge: 'Since 2026'
  }
];

export const AnnouncementBar: React.FC = () => {
  const [index, setIndex] = useState(0);
  const [visible, setVisible] = useState(true);

  useEffect(() => {
    const timer = setInterval(() => {
      setIndex(prev => (prev + 1) % ANNOUNCEMENTS.length);
    }, 4500);
    return () => clearInterval(timer);
  }, []);

  if (!visible) return null;

  const current = ANNOUNCEMENTS[index];

  return (
    <div className="bg-brand-plum-950 text-white text-xs py-2 px-4 transition-all duration-300 border-b border-brand-plum-800/60 relative z-40">
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        <div className="hidden md:flex items-center gap-2 text-brand-gold-500 font-medium tracking-widest text-[11px] uppercase">
          <Sparkles className="w-3.5 h-3.5" />
          <span>Haute Parfumerie</span>
        </div>

        <div className="flex-1 flex items-center justify-center text-center gap-2">
          <span className="bg-brand-plum-800 text-brand-blush-300 px-2 py-0.5 rounded text-[10px] font-semibold tracking-wider uppercase">
            {current.badge}
          </span>
          <Link
            to={current.link}
            className="hover:text-brand-blush-200 transition-colors inline-flex items-center gap-1.5 font-light"
          >
            <span>{current.text}</span>
            <ArrowRight className="w-3 h-3 opacity-70 hidden sm:inline" />
          </Link>
        </div>

        <div className="flex items-center gap-3 sm:gap-4">
          <Link
            to="/admin"
            className="hidden sm:inline-flex items-center gap-1 text-[10px] font-semibold text-brand-gold-300 hover:text-white uppercase tracking-wider bg-brand-plum-900/90 hover:bg-brand-plum-800 px-2 py-0.5 rounded-full border border-brand-gold-500/30 transition-colors"
          >
            <span>Admin Console</span>
          </Link>
          <Link
            to="/find-your-scent"
            className="hidden lg:inline text-[11px] font-medium text-brand-blush-300 hover:text-white uppercase tracking-wider"
          >
            AI Scent Matcher
          </Link>
          <button
            onClick={() => setVisible(false)}
            className="text-neutral-400 hover:text-white p-0.5"
            aria-label="Dismiss banner"
          >
            <X className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </div>
  );
};

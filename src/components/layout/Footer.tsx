'use client';

import React, { useState } from 'react';
import { Link } from '../common/Link';
import { useStore } from '../../context/StoreContext';
import { 
  Sparkles, 
  Send, 
  ShieldCheck, 
  Truck, 
  RotateCcw, 
  CreditCard,
  Instagram,
  Facebook,
  Twitter,
  Youtube,
  CheckCircle2,
  Gift
} from 'lucide-react';
import { BRANDS } from '../../data/brands';
import { CATEGORIES } from '../../data/categories';

export const Footer: React.FC = () => {
  const { showToast } = useStore();
  const [newsletterEmail, setNewsletterEmail] = useState('');
  const [subscribed, setSubscribed] = useState(false);

  const handleSubscribe = (e: React.FormEvent) => {
    e.preventDefault();
    if (newsletterEmail && newsletterEmail.includes('@')) {
      setSubscribed(true);
      showToast('Thank you for joining the SCENTIVA Connoisseurs Club!', 'success');
      setNewsletterEmail('');
    } else {
      showToast('Please enter a valid email address', 'warning');
    }
  };

  return (
    <footer className="bg-brand-plum-950 text-white border-t border-brand-plum-800/80 pt-16 pb-24 lg:pb-16 mt-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-12">
        {/* Newsletter & Club Invite */}
        <div className="rounded-2xl bg-gradient-to-r from-brand-plum-900 via-brand-plum-800 to-brand-plum-950 p-6 sm:p-10 border border-brand-blush-300/20 shadow-modal flex flex-col md:flex-row items-center justify-between gap-6">
          <div className="space-y-2 text-center md:text-left max-w-lg">
            <span className="inline-flex items-center gap-1.5 text-xs font-semibold uppercase tracking-widest text-brand-gold-500">
              <Sparkles className="w-3.5 h-3.5" />
              <span>Privé Connoisseurs Club</span>
            </span>
            <h3 className="font-serif text-2xl sm:text-3xl text-white font-medium">
              Receive 10% Off Your First Scent & Private Release Invites
            </h3>
            <p className="text-xs text-brand-blush-200/80">
              Join fragrance enthusiasts discovering rare botanical extractions and niche previews.
            </p>
          </div>

          <div className="w-full md:w-auto min-w-[320px]">
            {subscribed ? (
              <div className="flex items-center gap-2 text-semantic-success bg-white/10 px-4 py-3 rounded-xl border border-semantic-success/40 text-xs font-medium">
                <CheckCircle2 className="w-5 h-5 text-brand-blush-300 flex-shrink-0" />
                <span>You're subscribed! Use coupon code <strong>WELCOME10</strong> at checkout.</span>
              </div>
            ) : (
              <form onSubmit={handleSubscribe} className="flex gap-2">
                <input
                  type="email"
                  placeholder="Enter your email address..."
                  value={newsletterEmail}
                  onChange={e => setNewsletterEmail(e.target.value)}
                  className="flex-1 px-4 py-3 text-xs rounded-xl bg-white/10 border border-brand-blush-300/30 text-white placeholder:text-brand-blush-200/50 focus:outline-none focus:border-brand-blush-300 focus:ring-2 focus:ring-brand-blush-300/20"
                />
                <button
                  type="submit"
                  className="px-5 py-3 rounded-xl bg-brand-gold-500 hover:bg-brand-gold-500/90 text-brand-plum-950 font-semibold text-xs transition-all shadow-md flex items-center gap-1.5"
                >
                  <span>Join</span>
                  <Send className="w-3.5 h-3.5" />
                </button>
              </form>
            )}
          </div>
        </div>

        {/* Main Footer Links Grid */}
        <div className="grid grid-cols-2 md:grid-cols-5 gap-8 pt-4">
          {/* Brand Info */}
          <div className="col-span-2 space-y-4">
            <Link to="/" className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-brand-plum-900 border border-brand-blush-300/40 p-1 flex items-center justify-center">
                <img src="/assets/scentiva-emblem.svg" alt="SCENTIVA Emblem" className="w-full h-full" />
              </div>
              <div>
                <span className="font-serif text-2xl font-semibold tracking-wider text-white">SCENTIVA</span>
                <span className="block text-[9px] font-sans font-semibold tracking-[0.3em] text-brand-blush-300 uppercase">
                  SINCE 2026
                </span>
              </div>
            </Link>
            <p className="text-xs text-neutral-300 leading-relaxed max-w-sm">
              The premier destination for authentic luxury, designer, and niche fragrances. Curating olfactory masterpieces from world-renowned perfumeries with uncompromising elegance.
            </p>
            <div className="flex items-center gap-3 text-neutral-400">
              <a href="#instagram" className="p-2 rounded-full bg-white/5 hover:bg-white/10 hover:text-brand-blush-300 transition-colors" aria-label="Instagram">
                <Instagram className="w-4 h-4" />
              </a>
              <a href="#facebook" className="p-2 rounded-full bg-white/5 hover:bg-white/10 hover:text-brand-blush-300 transition-colors" aria-label="Facebook">
                <Facebook className="w-4 h-4" />
              </a>
              <a href="#twitter" className="p-2 rounded-full bg-white/5 hover:bg-white/10 hover:text-brand-blush-300 transition-colors" aria-label="Twitter">
                <Twitter className="w-4 h-4" />
              </a>
              <a href="#youtube" className="p-2 rounded-full bg-white/5 hover:bg-white/10 hover:text-brand-blush-300 transition-colors" aria-label="Youtube">
                <Youtube className="w-4 h-4" />
              </a>
            </div>
          </div>

          {/* Collections */}
          <div className="space-y-3">
            <h4 className="text-xs font-semibold uppercase tracking-widest text-brand-gold-500">
              Collections
            </h4>
            <ul className="space-y-2 text-xs text-neutral-300">
              {CATEGORIES.map(cat => (
                <li key={cat.id}>
                  <Link to={`/categories/${cat.slug}`} className="hover:text-brand-blush-200 transition-colors">
                    {cat.title}
                  </Link>
                </li>
              ))}
              <li>
                <Link to="/gifts" className="hover:text-brand-blush-200 transition-colors flex items-center gap-1">
                  <Gift className="w-3 h-3 text-brand-gold-500" />
                  <span>Gift Coffrets</span>
                </Link>
              </li>
              <li>
                <Link to="/find-your-scent" className="text-brand-blush-300 hover:text-white font-medium">
                  Scent Sommelier ✨
                </Link>
              </li>
            </ul>
          </div>

          {/* Luxury Houses */}
          <div className="space-y-3">
            <h4 className="text-xs font-semibold uppercase tracking-widest text-brand-gold-500">
              Featured Houses
            </h4>
            <ul className="space-y-2 text-xs text-neutral-300">
              {BRANDS.slice(0, 6).map(brand => (
                <li key={brand.id}>
                  <Link to={`/brands/${brand.slug}`} className="hover:text-brand-blush-200 transition-colors">
                    {brand.name}
                  </Link>
                </li>
              ))}
              <li>
                <Link to="/brands" className="text-brand-rose-500 hover:text-brand-blush-200 font-medium">
                  All Brands →
                </Link>
              </li>
            </ul>
          </div>

          {/* Customer Care & Policies */}
          <div className="space-y-3">
            <h4 className="text-xs font-semibold uppercase tracking-widest text-brand-gold-500">
              Customer Care
            </h4>
            <ul className="space-y-2 text-xs text-neutral-300">
              <li>
                <Link to="/account/orders" className="hover:text-brand-blush-200 transition-colors">
                  Track Your Order
                </Link>
              </li>
              <li>
                <Link to="/help" className="hover:text-brand-blush-200 transition-colors">
                  Help Center & FAQs
                </Link>
              </li>
              <li>
                <Link to="/policies/shipping" className="hover:text-brand-blush-200 transition-colors">
                  Shipping Policy
                </Link>
              </li>
              <li>
                <Link to="/policies/returns" className="hover:text-brand-blush-200 transition-colors">
                  Returns & Exchanges
                </Link>
              </li>
              <li>
                <Link to="/policies/authenticity" className="hover:text-brand-blush-200 transition-colors">
                  Authenticity Guarantee
                </Link>
              </li>
              <li>
                <Link to="/admin" className="text-brand-gold-500 hover:underline font-semibold">
                  Admin Dashboard Demo
                </Link>
              </li>
            </ul>
          </div>
        </div>

        {/* Trust Badges Strip */}
        <div className="border-t border-brand-plum-800/80 pt-8 pb-4 grid grid-cols-2 md:grid-cols-4 gap-4 text-neutral-300 text-xs">
          <div className="flex items-center gap-2.5">
            <ShieldCheck className="w-5 h-5 text-brand-gold-500 flex-shrink-0" />
            <div>
              <span className="font-semibold text-white block">Curated Flacons</span>
              <span className="text-[11px] text-neutral-400">Authentic luxury curation</span>
            </div>
          </div>
          <div className="flex items-center gap-2.5">
            <Truck className="w-5 h-5 text-brand-gold-500 flex-shrink-0" />
            <div>
              <span className="font-semibold text-white block">Free Express Shipping</span>
              <span className="text-[11px] text-neutral-400">On all orders above ₹999</span>
            </div>
          </div>
          <div className="flex items-center gap-2.5">
            <RotateCcw className="w-5 h-5 text-brand-gold-500 flex-shrink-0" />
            <div>
              <span className="font-semibold text-white block">7-Day Easy Returns</span>
              <span className="text-[11px] text-neutral-400">Hassle-free exchange</span>
            </div>
          </div>
          <div className="flex items-center gap-2.5">
            <CreditCard className="w-5 h-5 text-brand-gold-500 flex-shrink-0" />
            <div>
              <span className="font-semibold text-white block">Secure Payments</span>
              <span className="text-[11px] text-neutral-400">UPI, Cards, NetBanking</span>
            </div>
          </div>
        </div>

        {/* Copyright */}
        <div className="border-t border-brand-plum-900 pt-6 flex flex-col sm:flex-row items-center justify-between text-[11px] text-neutral-400 gap-3">
          <p>© 2026 SCENTIVA Marketplace Prototype. All rights reserved.</p>
          <div className="flex items-center gap-4">
            <Link to="/policies/privacy" className="hover:text-white">Privacy Policy</Link>
            <Link to="/policies/terms" className="hover:text-white">Terms of Service</Link>
            <Link to="/contact" className="hover:text-white">Contact Concierge</Link>
          </div>
        </div>
      </div>
    </footer>
  );
};

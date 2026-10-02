'use client';

import React from 'react';
import { Home, Compass, Sparkles, ShoppingBag, ShieldCheck, Heart, User } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import { Link } from '../common/Link';
import { useLocation } from '../../hooks/useNavigation';

export const MobileBottomNav: React.FC = () => {
  const { cartCount, setIsCartDrawerOpen, isHydrated } = useStore();
  const location = useLocation();

  // Hide on admin routes or full screen checkout to prevent clutter
  if (location.pathname.startsWith('/admin') || location.pathname === '/checkout') {
    return null;
  }

  const isRouteActive = (path: string) => {
    if (path === '/') return location.pathname === '/';
    return location.pathname.startsWith(path);
  };

  return (
    <div className="fixed bottom-0 left-0 right-0 z-40 bg-white/95 backdrop-blur-md border-t border-neutral-200/90 lg:hidden shadow-modal pb-safe">
      <div className="grid grid-cols-5 h-14 max-w-md mx-auto">
        <Link
          to="/"
          className={`flex flex-col items-center justify-center gap-0.5 text-[10px] font-medium transition-colors ${
            isRouteActive('/') ? 'text-brand-plum-900 font-semibold' : 'text-neutral-500 hover:text-neutral-800'
          }`}
        >
          <Home className="w-5 h-5" />
          <span>Home</span>
        </Link>

        <Link
          to="/shop"
          className={`flex flex-col items-center justify-center gap-0.5 text-[10px] font-medium transition-colors ${
            isRouteActive('/shop') ? 'text-brand-plum-900 font-semibold' : 'text-neutral-500 hover:text-neutral-800'
          }`}
        >
          <Compass className="w-5 h-5" />
          <span>Shop</span>
        </Link>

        {/* Highlighted Shopping Bag Trigger */}
        <button
          onClick={() => setIsCartDrawerOpen(true)}
          className="flex flex-col items-center justify-center gap-0.5 text-[10px] font-medium transition-colors relative text-brand-plum-900"
          aria-label="Open Shopping Bag"
        >
          <div className="relative p-1 rounded-full bg-brand-blush-100/90 border border-brand-blush-300/60 shadow-xs">
            <ShoppingBag className="w-4 h-4 text-brand-plum-900" />
            <span className="absolute -top-1 -right-1.5 w-4 h-4 bg-brand-plum-900 text-white rounded-full text-[9px] font-bold flex items-center justify-center shadow-xs">
              {isHydrated ? cartCount : 0}
            </span>
          </div>
          <span className="font-semibold text-brand-plum-950">Bag</span>
        </button>

        <Link
          to="/find-your-scent"
          className={`flex flex-col items-center justify-center gap-0.5 text-[10px] font-medium transition-colors relative ${
            isRouteActive('/find-your-scent') ? 'text-brand-plum-900 font-bold' : 'text-neutral-500 hover:text-brand-plum-900'
          }`}
        >
          <Sparkles className="w-5 h-5 text-brand-gold-500" />
          <span>Finder</span>
        </Link>

        <Link
          to="/admin"
          className={`flex flex-col items-center justify-center gap-0.5 text-[10px] font-medium transition-colors ${
            isRouteActive('/admin') ? 'text-brand-plum-900 font-bold' : 'text-neutral-500 hover:text-brand-plum-900'
          }`}
        >
          <ShieldCheck className="w-5 h-5 text-brand-rose-500" />
          <span>Admin</span>
        </Link>
      </div>
    </div>
  );
};

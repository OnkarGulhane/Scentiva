'use client';

import React from 'react';
import { Home, Compass, Sparkles, Heart, User } from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import { Link } from '../common/Link';
import { useLocation } from '../../hooks/useNavigation';

export const MobileBottomNav: React.FC = () => {
  const { wishlist } = useStore();
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

        <Link
          to="/find-your-scent"
          className={`flex flex-col items-center justify-center gap-0.5 text-[10px] font-medium transition-colors relative ${
            isRouteActive('/find-your-scent') ? 'text-brand-plum-900 font-bold' : 'text-brand-rose-500 hover:text-brand-plum-900'
          }`}
        >
          <div className="p-1 rounded-full bg-brand-blush-100">
            <Sparkles className="w-4 h-4 text-brand-plum-900" />
          </div>
          <span>Finder</span>
        </Link>

        <Link
          to="/wishlist"
          className={`flex flex-col items-center justify-center gap-0.5 text-[10px] font-medium transition-colors relative ${
            isRouteActive('/wishlist') ? 'text-brand-plum-900 font-semibold' : 'text-neutral-500 hover:text-neutral-800'
          }`}
        >
          <div className="relative">
            <Heart className="w-5 h-5" />
            {wishlist.length > 0 && (
              <span className="absolute -top-1 -right-2 w-3.5 h-3.5 bg-brand-rose-500 text-white rounded-full text-[9px] font-bold flex items-center justify-center">
                {wishlist.length}
              </span>
            )}
          </div>
          <span>Saved</span>
        </Link>

        <Link
          to="/account"
          className={`flex flex-col items-center justify-center gap-0.5 text-[10px] font-medium transition-colors ${
            isRouteActive('/account') ? 'text-brand-plum-900 font-semibold' : 'text-neutral-500 hover:text-neutral-800'
          }`}
        >
          <User className="w-5 h-5" />
          <span>Account</span>
        </Link>
      </div>
    </div>
  );
};

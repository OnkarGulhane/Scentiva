'use client';

import React, { useState, useRef, useEffect } from 'react';
import { Link } from '../common/Link';
import { useNavigate, useLocation } from '../../hooks/useNavigation';
import { useStore } from '../../context/StoreContext';
import { 
  Search, 
  Heart, 
  ShoppingBag, 
  User, 
  Menu, 
  X, 
  Sparkles, 
  ChevronDown, 
  ChevronRight,
  ArrowRight,
  ShieldCheck,
  Package,
  Layers,
  Flame,
  Gift,
  Compass,
  Award,
  LogOut,
  MapPin
} from 'lucide-react';
import { BRANDS } from '../../data/brands';
import { CATEGORIES } from '../../data/categories';
import { SearchService } from '../../services/searchService';

export const Navbar: React.FC = () => {
  const { cartCount, wishlist, setIsCartDrawerOpen, products, isLoggedIn, currentUser, isHydrated, signOut } = useStore();
  const navigate = useNavigate();
  const location = useLocation();

  const [isScrolled, setIsScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [mobileBrandsExpanded, setMobileBrandsExpanded] = useState(false);
  const [mobileCollectionsExpanded, setMobileCollectionsExpanded] = useState(false);
  
  const [searchQuery, setSearchQuery] = useState('');
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [activeDropdown, setActiveDropdown] = useState<'brands' | 'collections' | 'account' | null>(null);

  const searchInputRef = useRef<HTMLInputElement>(null);
  const searchContainerRef = useRef<HTMLDivElement>(null);
  const dropdownContainerRef = useRef<HTMLDivElement>(null);

  // Active route indicators
  const currentPath = location.pathname;
  const isBrandsActive = currentPath.startsWith('/brands');
  const isCollectionsActive = currentPath.startsWith('/collections') || currentPath.startsWith('/categories');
  const isShopActive = currentPath === '/shop';
  const isGiftsActive = currentPath.startsWith('/gifts');
  const isFinderActive = currentPath.startsWith('/find-your-scent');
  const isOffersActive = currentPath.startsWith('/offers');
  const isStoriesActive = currentPath.startsWith('/stories');

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 20);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  // Close menus on route change
  useEffect(() => {
    setMobileMenuOpen(false);
    setIsSearchOpen(false);
    setActiveDropdown(null);
  }, [currentPath]);

  // Click outside and keyboard Escape handler
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      const target = e.target as Node;
      if (searchContainerRef.current && !searchContainerRef.current.contains(target)) {
        setIsSearchOpen(false);
      }
      if (dropdownContainerRef.current && !dropdownContainerRef.current.contains(target)) {
        setActiveDropdown(null);
      }
    };

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        setIsSearchOpen(false);
        setMobileMenuOpen(false);
        setActiveDropdown(null);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, []);

  // Live Suggestions from SearchService
  const suggestions = searchQuery.trim().length > 0 
    ? SearchService.getSuggestions(searchQuery, 4)
    : { products: [], brands: [], notes: [] };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      setIsSearchOpen(false);
      SearchService.addRecentSearch(searchQuery.trim());
      navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
    }
  };

  return (
    <header
      className={`sticky top-0 z-30 transition-all duration-300 ${
        isScrolled
          ? 'bg-white/95 backdrop-blur-md shadow-card border-b border-neutral-200/80 py-3'
          : 'bg-white border-b border-neutral-200/60 py-4'
      }`}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between gap-4">
          {/* Mobile Menu Button */}
          <button
            onClick={() => setMobileMenuOpen(true)}
            className="lg:hidden p-2 text-neutral-800 hover:text-brand-plum-900 rounded-lg hover:bg-neutral-100 transition-colors"
            aria-label="Open Navigation Menu"
          >
            <Menu className="w-6 h-6" />
          </button>

          {/* Brand Logo */}
          <Link to="/" className="flex items-center gap-3 group shrink-0">
            <div className="relative w-10 h-10 sm:w-11 sm:h-11 rounded-full bg-brand-plum-900 p-1 flex items-center justify-center shadow-md group-hover:scale-105 transition-transform">
              <img
                src="/assets/scentiva-emblem.svg"
                alt="SCENTIVA Emblem"
                className="w-full h-full object-contain"
              />
            </div>
            <div className="flex flex-col">
              <span className="font-serif text-2xl sm:text-3xl font-semibold tracking-wider text-brand-plum-950 leading-none group-hover:text-brand-plum-800 transition-colors">
                SCENTIVA
              </span>
              <span className="text-[9px] sm:text-[10px] font-sans font-semibold tracking-[0.3em] text-brand-rose-500 uppercase mt-0.5">
                HAUTE PARFUMERIE
              </span>
            </div>
          </Link>

          {/* Desktop Navigation Links */}
          <nav className="hidden lg:flex items-center space-x-1 xl:space-x-1.5" ref={dropdownContainerRef}>
            <Link
              to="/shop"
              className={`px-3 py-2 text-sm font-medium rounded-lg transition-colors ${
                isShopActive
                  ? 'text-brand-plum-900 bg-brand-blush-100/70 font-semibold'
                  : 'text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70'
              }`}
            >
              All Perfumes
            </Link>

              {/* Brands Dropdown */}
              <div
                className="relative"
                onMouseEnter={() => setActiveDropdown('brands')}
                onMouseLeave={() => setActiveDropdown(null)}
              >
                <button
                  type="button"
                  onClick={() => setActiveDropdown(prev => prev === 'brands' ? null : 'brands')}
                  className={`flex items-center gap-1.5 px-3 py-2 text-sm font-medium rounded-lg transition-colors ${
                    isBrandsActive || activeDropdown === 'brands'
                      ? 'text-brand-plum-900 bg-brand-blush-100/70 font-semibold'
                      : 'text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70'
                  }`}
                  aria-expanded={activeDropdown === 'brands'}
                  aria-haspopup="true"
                >
                  <span>Brands</span>
                  <ChevronDown className={`w-3.5 h-3.5 transition-transform duration-200 ${activeDropdown === 'brands' ? 'rotate-180 text-brand-plum-900' : 'text-neutral-400'}`} />
                </button>

                {/* Hover bridge & Dropdown Body */}
                {activeDropdown === 'brands' && (
                  <div className="absolute top-full left-0 pt-2 z-50 animate-in fade-in slide-in-from-top-2 duration-150">
                    <div className="w-[520px] bg-white rounded-2xl shadow-modal border border-neutral-200/90 p-5 backdrop-blur-md">
                      <div className="flex items-center justify-between pb-3.5 border-b border-neutral-100">
                        <div className="flex items-center gap-2">
                          <Award className="w-4 h-4 text-brand-gold-500" />
                          <span className="text-xs font-bold text-brand-plum-950 uppercase tracking-wider">Luxury Maisons</span>
                        </div>
                        <Link 
                          to="/brands" 
                          onClick={() => setActiveDropdown(null)}
                          className="text-xs text-brand-rose-600 hover:text-brand-plum-900 hover:underline font-semibold flex items-center gap-1.5 transition-colors"
                        >
                          <span>View All ({BRANDS.length})</span>
                          <ArrowRight className="w-3.5 h-3.5" />
                        </Link>
                      </div>

                      <div className="grid grid-cols-2 gap-x-4 gap-y-2 pt-3.5">
                        {BRANDS.slice(0, 10).map(brand => (
                          <Link
                            key={brand.id}
                            to={`/brands/${brand.slug}`}
                            onClick={() => setActiveDropdown(null)}
                            className="p-2.5 rounded-xl hover:bg-brand-blush-100/60 transition-all flex flex-col group/brand"
                          >
                            <span className="text-xs font-bold text-neutral-900 group-hover/brand:text-brand-plum-900 transition-colors leading-snug">
                              {brand.name}
                            </span>
                            <span className="text-[10px] text-neutral-400 font-medium tracking-wide uppercase mt-0.5">
                              {brand.tier}
                            </span>
                          </Link>
                        ))}
                      </div>

                      <div className="pt-3.5 mt-3 border-t border-neutral-100">
                        <Link
                          to="/brands"
                          onClick={() => setActiveDropdown(null)}
                          className="w-full py-2.5 px-4 rounded-xl bg-brand-blush-100/60 hover:bg-brand-blush-200/80 text-brand-plum-950 font-semibold text-xs flex items-center justify-between transition-all group/cta"
                        >
                          <span className="group-hover/cta:translate-x-0.5 transition-transform">Explore Full Brand Directory</span>
                          <ArrowRight className="w-4 h-4 text-brand-rose-600 group-hover/cta:translate-x-0.5 transition-transform" />
                        </Link>
                      </div>
                    </div>
                  </div>
                )}
              </div>

            {/* Collections Dropdown */}
            <div
              className="relative"
              onMouseEnter={() => setActiveDropdown('collections')}
              onMouseLeave={() => setActiveDropdown(null)}
            >
              <button
                type="button"
                onClick={() => setActiveDropdown(prev => prev === 'collections' ? null : 'collections')}
                className={`flex items-center gap-1.5 px-3 py-2 text-sm font-medium rounded-lg transition-colors ${
                  isCollectionsActive || activeDropdown === 'collections'
                    ? 'text-brand-plum-900 bg-brand-blush-100/70 font-semibold'
                    : 'text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70'
                }`}
                aria-expanded={activeDropdown === 'collections'}
                aria-haspopup="true"
              >
                <span>Collections</span>
                <ChevronDown className={`w-3.5 h-3.5 transition-transform duration-200 ${activeDropdown === 'collections' ? 'rotate-180 text-brand-plum-900' : 'text-neutral-400'}`} />
              </button>

              {/* Hover bridge & Dropdown Body */}
              {activeDropdown === 'collections' && (
                <div className="absolute top-full left-0 pt-2 z-50 animate-in fade-in slide-in-from-top-2 duration-150">
                  <div className="w-[460px] bg-white rounded-2xl shadow-modal border border-neutral-200/90 p-5 backdrop-blur-md">
                    <div className="flex items-center justify-between pb-3.5 border-b border-neutral-100">
                      <div className="flex items-center gap-2">
                        <Layers className="w-4 h-4 text-brand-rose-500" />
                        <span className="text-xs font-bold text-brand-plum-950 uppercase tracking-wider">Olfactory Universes</span>
                      </div>
                      <Link 
                        to="/collections" 
                        onClick={() => setActiveDropdown(null)}
                        className="text-xs text-brand-rose-600 hover:text-brand-plum-900 hover:underline font-semibold flex items-center gap-1.5 transition-colors"
                      >
                        <span>View All ({CATEGORIES.length})</span>
                        <ArrowRight className="w-3.5 h-3.5" />
                      </Link>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 pt-3">
                      {CATEGORIES.map(cat => (
                        <Link
                          key={cat.id}
                          to={`/collections/${cat.slug}`}
                          onClick={() => setActiveDropdown(null)}
                          className="flex items-center gap-2.5 p-2 rounded-xl hover:bg-brand-blush-100/50 transition-all group/item"
                        >
                          <div className="w-9 h-9 rounded-lg overflow-hidden bg-neutral-100 shrink-0 border border-neutral-200/60">
                            <img 
                              src={cat.image} 
                              alt={cat.title} 
                              className="w-full h-full object-cover group-hover/item:scale-110 transition-transform duration-500" 
                            />
                          </div>
                          <div className="flex-1 min-w-0">
                            <div className="text-xs font-bold text-neutral-900 group-hover/item:text-brand-plum-900 truncate">
                              {cat.title}
                            </div>
                            <div className="text-[10px] text-neutral-500 truncate">
                              {cat.tagline}
                            </div>
                          </div>
                        </Link>
                      ))}
                    </div>

                    <div className="pt-3 mt-3 border-t border-neutral-100">
                      <Link
                        to="/collections"
                        onClick={() => setActiveDropdown(null)}
                        className="w-full py-2 px-3 rounded-xl bg-brand-blush-100/50 hover:bg-brand-blush-200/60 text-brand-plum-950 font-semibold text-xs flex items-center justify-between transition-colors"
                      >
                        <span>Browse All Fragrance Collections</span>
                        <ArrowRight className="w-3.5 h-3.5 text-brand-rose-500" />
                      </Link>
                    </div>
                  </div>
                </div>
              )}
            </div>

            <Link
              to="/gifts"
              className={`px-3 py-2 text-sm font-medium rounded-lg transition-colors flex items-center gap-1.5 ${
                isGiftsActive
                  ? 'text-brand-plum-900 bg-brand-blush-100/70 font-semibold'
                  : 'text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70'
              }`}
            >
              <Gift className="w-3.5 h-3.5 text-brand-gold-500" />
              <span>Gift Coffrets</span>
            </Link>

            <Link
              to="/find-your-scent"
              className={`px-3.5 py-2 text-sm font-semibold rounded-lg transition-all flex items-center gap-1.5 shadow-2xs ${
                isFinderActive
                  ? 'text-white bg-brand-plum-900 shadow-sm'
                  : 'text-brand-plum-900 bg-brand-blush-100/80 hover:bg-brand-blush-200/90'
              }`}
            >
              <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
              <span>Scent Matcher</span>
            </Link>

            <Link
              to="/offers"
              className={`px-3 py-2 text-sm font-medium rounded-lg transition-colors flex items-center gap-1 ${
                isOffersActive
                  ? 'text-brand-plum-900 bg-brand-blush-100/70 font-semibold'
                  : 'text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70'
              }`}
            >
              <Flame className="w-3.5 h-3.5 text-brand-rose-500" />
              <span>Offers</span>
            </Link>

            <Link
              to="/stories"
              className={`px-3 py-2 text-sm font-medium rounded-lg transition-colors ${
                isStoriesActive
                  ? 'text-brand-plum-900 bg-brand-blush-100/70 font-semibold'
                  : 'text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70'
              }`}
            >
              Stories
            </Link>
          </nav>

          {/* Search Bar & Actions */}
          <div className="flex items-center gap-2 sm:gap-3" ref={searchContainerRef}>
            {/* Search Input with Autocomplete */}
            <div className="relative hidden md:block">
              <form onSubmit={handleSearchSubmit} className="relative">
                <input
                  ref={searchInputRef}
                  type="text"
                  placeholder="Search perfumes, brands, notes..."
                  value={searchQuery}
                  onChange={e => {
                    setSearchQuery(e.target.value);
                    setIsSearchOpen(true);
                  }}
                  onFocus={() => setIsSearchOpen(true)}
                  className="w-56 lg:w-72 pl-9 pr-4 py-2 text-xs rounded-full bg-neutral-100/90 border border-neutral-200 focus:outline-none focus:border-brand-plum-700 focus:bg-white focus:ring-2 focus:ring-brand-blush-200 transition-all placeholder:text-neutral-400 text-neutral-800"
                />
                <Search className="w-4 h-4 text-neutral-400 absolute left-3 top-1/2 -translate-y-1/2" />
              </form>

              {/* Autocomplete Dropdown */}
              {isSearchOpen && searchQuery.trim().length > 0 && (
                <div className="absolute top-full right-0 mt-2 w-80 lg:w-96 bg-white rounded-2xl shadow-modal border border-neutral-200/90 p-3.5 z-50 animate-in fade-in duration-150">
                  <div className="text-[11px] font-semibold text-neutral-400 uppercase tracking-wider mb-2 px-1">
                    Matching Fragrances ({suggestions.products.length})
                  </div>
                  {suggestions.products.length > 0 ? (
                    <div className="space-y-1.5">
                      {suggestions.products.map(prod => (
                        <Link
                          key={prod.id}
                          to={`/product/${prod.slug}`}
                          onClick={() => setIsSearchOpen(false)}
                          className="flex items-center gap-3 p-2 rounded-xl hover:bg-neutral-50 transition-colors"
                        >
                          <img
                            src={prod.images[0]}
                            alt={prod.name}
                            className="w-10 h-10 object-cover rounded-lg bg-neutral-100 shrink-0"
                          />
                          <div className="flex-1 min-w-0">
                            <div className="text-xs font-semibold text-neutral-800 truncate">{prod.name}</div>
                            <div className="text-[11px] text-neutral-500">{prod.brandName} • {prod.category}</div>
                          </div>
                          <div className="text-xs font-medium text-brand-plum-900 tabular-nums">
                            ₹{prod.variants[0].price.toLocaleString('en-IN')}
                          </div>
                        </Link>
                      ))}

                      {suggestions.notes.length > 0 && (
                        <div className="pt-2 border-t border-neutral-100 px-1">
                          <span className="text-[10px] text-neutral-400 uppercase font-bold">Matching Notes:</span>
                          <div className="flex flex-wrap gap-1 mt-1">
                            {suggestions.notes.map(n => (
                              <button
                                key={n}
                                type="button"
                                onClick={() => {
                                  setIsSearchOpen(false);
                                  navigate(`/search?q=${encodeURIComponent(n)}`);
                                }}
                                className="text-[11px] bg-brand-blush-100 text-brand-plum-900 px-2 py-0.5 rounded-md hover:bg-brand-blush-200"
                              >
                                {n}
                              </button>
                            ))}
                          </div>
                        </div>
                      )}

                      <button
                        type="button"
                        onClick={handleSearchSubmit}
                        className="w-full mt-2 py-2 text-xs text-center font-semibold text-brand-plum-900 bg-brand-blush-100/60 hover:bg-brand-blush-200/70 rounded-xl transition-colors"
                      >
                        View all search results for "{searchQuery}"
                      </button>
                    </div>
                  ) : (
                    <div className="py-4 text-center text-xs text-neutral-500">
                      No matching fragrances found for "{searchQuery}"
                    </div>
                  )}
                </div>
              )}
            </div>

            {/* Mobile Search Button */}
            <Link
              to="/search"
              className="md:hidden p-2 text-neutral-700 hover:text-brand-plum-900 rounded-full hover:bg-neutral-100"
              aria-label="Search Fragrances"
            >
              <Search className="w-5 h-5" />
            </Link>

            {/* Wishlist Button */}
            <Link
              to="/wishlist"
              className="relative p-2 text-neutral-700 hover:text-brand-plum-900 rounded-full hover:bg-neutral-100 transition-colors"
              aria-label="View Wishlist"
              title="View Wishlist"
            >
              <Heart className="w-5 h-5" />
              {isHydrated && wishlist.length > 0 && (
                <span className="absolute top-1 right-1 w-4 h-4 bg-brand-rose-500 text-white rounded-full text-[10px] font-bold flex items-center justify-center">
                  {wishlist.length}
                </span>
              )}
            </Link>

            {/* Shopping Bag / Cart Drawer Trigger */}
            <button
              type="button"
              onClick={() => setIsCartDrawerOpen(true)}
              className="relative flex items-center gap-1.5 px-3 py-1.5 rounded-full text-brand-plum-950 bg-brand-blush-100/80 hover:bg-brand-blush-200/90 border border-brand-blush-300/70 transition-all shadow-sm hover:shadow group cursor-pointer"
              aria-label="Open Shopping Bag"
              title="Open Shopping Bag"
            >
              <ShoppingBag className="w-4 h-4 sm:w-4.5 sm:h-4.5 text-brand-plum-900 group-hover:scale-110 transition-transform" />
              <span className="hidden sm:inline text-xs font-semibold text-brand-plum-900">Bag</span>
              <span className="bg-brand-plum-900 text-white rounded-full text-[10px] font-bold px-1.5 py-0.2 min-w-[18px] h-[18px] flex items-center justify-center shadow-xs">
                {isHydrated ? cartCount : 0}
              </span>
            </button>

            {/* Admin Console Direct Link */}
            <Link
              to="/admin"
              className="flex items-center gap-1 sm:gap-1.5 px-2.5 sm:px-3 py-1.5 rounded-full bg-brand-plum-950 hover:bg-brand-plum-900 text-brand-gold-300 hover:text-white text-xs font-semibold shadow-sm transition-all border border-brand-gold-500/30 group"
              aria-label="Open Admin Operations Console"
              title="Open Admin Operations Console"
            >
              <ShieldCheck className="w-3.5 h-3.5 sm:w-4 sm:h-4 text-brand-gold-400 group-hover:rotate-12 transition-transform" />
              <span className="text-[11px] sm:text-xs font-medium tracking-wide">Admin</span>
            </Link>

            {/* Account Dropdown Area (Guest & Authenticated States) */}
            <div
              className="relative hidden md:block"
              onMouseEnter={() => setActiveDropdown('account')}
              onMouseLeave={() => setActiveDropdown(null)}
            >
              <Link
                to={isHydrated && isLoggedIn ? '/account' : '/account/sign-in'}
                className="flex items-center gap-1.5 p-2 text-neutral-700 hover:text-brand-plum-900 rounded-full hover:bg-neutral-100 transition-colors"
                aria-label={isHydrated && isLoggedIn ? `Account (${currentUser?.name})` : 'Sign In / Create Account'}
                title={isHydrated && isLoggedIn ? `Signed in as ${currentUser?.name}` : 'Sign In / Create Account'}
              >
                <div className="flex items-center gap-1.5">
                  <User className="w-5 h-5 text-neutral-700" />
                  {isHydrated && isLoggedIn && currentUser && (
                    <span className="hidden xl:inline text-xs font-semibold text-brand-plum-950 max-w-[110px] truncate">
                      {currentUser.name.split(' ')[0]}
                    </span>
                  )}
                </div>
              </Link>

              {/* Luxury Dropdown Menu */}
              {activeDropdown === 'account' && (
                <div className="absolute top-full right-0 pt-2 z-50 animate-in fade-in slide-in-from-top-2 duration-150">
                  <div className="w-64 bg-white rounded-2xl shadow-modal border border-neutral-200/90 p-4 backdrop-blur-md">
                    {isHydrated && isLoggedIn && currentUser ? (
                      <div className="space-y-3">
                        <div className="pb-3 border-b border-neutral-100">
                          <div className="text-xs font-bold text-brand-plum-950 truncate">{currentUser.name}</div>
                          <div className="text-[11px] text-neutral-500 truncate">{currentUser.email}</div>
                          <div className="mt-1.5 inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-brand-blush-100/70 border border-brand-blush-300/40 text-[10px] font-bold text-brand-plum-900">
                            <Sparkles className="w-3 h-3 text-brand-gold-500" />
                            <span>{currentUser.tier || 'Privé Connoisseur'}</span>
                          </div>
                        </div>

                        <div className="space-y-1 text-xs">
                          <Link
                            to="/account"
                            onClick={() => setActiveDropdown(null)}
                            className="flex items-center gap-2.5 p-2 rounded-xl text-neutral-700 hover:text-brand-plum-900 hover:bg-neutral-50 font-medium transition-colors"
                          >
                            <User className="w-4 h-4 text-neutral-400" />
                            <span>My Account</span>
                          </Link>
                          <Link
                            to="/account/orders"
                            onClick={() => setActiveDropdown(null)}
                            className="flex items-center gap-2.5 p-2 rounded-xl text-neutral-700 hover:text-brand-plum-900 hover:bg-neutral-50 font-medium transition-colors"
                          >
                            <Package className="w-4 h-4 text-neutral-400" />
                            <span>Orders & Shipments</span>
                          </Link>
                          <Link
                            to="/wishlist"
                            onClick={() => setActiveDropdown(null)}
                            className="flex items-center gap-2.5 p-2 rounded-xl text-neutral-700 hover:text-brand-plum-900 hover:bg-neutral-50 font-medium transition-colors"
                          >
                            <Heart className="w-4 h-4 text-neutral-400" />
                            <span>Saved Vault ({wishlist.length})</span>
                          </Link>
                          <Link
                            to="/account/addresses"
                            onClick={() => setActiveDropdown(null)}
                            className="flex items-center gap-2.5 p-2 rounded-xl text-neutral-700 hover:text-brand-plum-900 hover:bg-neutral-50 font-medium transition-colors"
                          >
                            <MapPin className="w-4 h-4 text-neutral-400" />
                            <span>Addresses</span>
                          </Link>
                        </div>

                        <div className="pt-2 border-t border-neutral-100">
                          <button
                            type="button"
                            onClick={() => {
                              setActiveDropdown(null);
                              signOut();
                            }}
                            className="w-full flex items-center gap-2 p-2 rounded-xl text-xs font-semibold text-semantic-error hover:bg-neutral-50 transition-colors"
                          >
                            <LogOut className="w-4 h-4" />
                            <span>Sign Out</span>
                          </button>
                        </div>
                      </div>
                    ) : (
                      <div className="space-y-3 text-xs">
                        <div className="pb-2.5 border-b border-neutral-100">
                          <span className="font-serif text-sm font-bold text-brand-plum-950 block">SCENTIVA Privé</span>
                          <span className="text-[11px] text-neutral-500">Sign in to track orders, save favorites, and enjoy connoisseur rewards.</span>
                        </div>
                        <div className="space-y-2">
                          <Link
                            to="/account/sign-in"
                            onClick={() => setActiveDropdown(null)}
                            className="w-full py-2.5 px-4 rounded-xl bg-brand-plum-900 hover:bg-brand-plum-800 text-white font-semibold text-center block transition-colors shadow-xs"
                          >
                            Sign In
                          </Link>
                          <Link
                            to="/account/sign-up"
                            onClick={() => setActiveDropdown(null)}
                            className="w-full py-2 px-4 rounded-xl border border-neutral-200 hover:bg-neutral-50 text-neutral-800 font-semibold text-center block transition-colors"
                          >
                            Create Account
                          </Link>
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Mobile Drawer Menu */}
      {mobileMenuOpen && (
        <div className="fixed inset-0 z-50 lg:hidden flex">
          <div
            className="fixed inset-0 bg-neutral-950/60 backdrop-blur-sm transition-opacity"
            onClick={() => setMobileMenuOpen(false)}
          />

          <div className="relative ml-0 mr-auto w-full max-w-sm bg-white h-full shadow-modal flex flex-col z-10 animate-in slide-in-from-left duration-300">
            {/* Header */}
            <div className="p-4 border-b border-neutral-100 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-full bg-brand-plum-900 p-0.5 flex items-center justify-center">
                  <img src="/assets/scentiva-emblem.svg" alt="SCENTIVA" className="w-full h-full" />
                </div>
                <span className="font-serif text-lg font-bold text-brand-plum-950">SCENTIVA</span>
              </div>
              <button
                type="button"
                onClick={() => setMobileMenuOpen(false)}
                className="p-1.5 text-neutral-400 hover:text-neutral-700 rounded-full hover:bg-neutral-100"
                aria-label="Close menu"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Scrollable Links Area */}
            <div className="flex-1 overflow-y-auto p-4 space-y-2 text-sm">
              <Link
                to="/shop"
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center justify-between py-2.5 px-3 rounded-xl font-semibold text-neutral-900 hover:bg-neutral-100 transition-colors"
              >
                <span>All Perfumes</span>
                <ChevronRight className="w-4 h-4 text-neutral-400" />
              </Link>

              {/* Brands Mobile Accordion */}
              <div className="border border-neutral-200/80 rounded-2xl overflow-hidden">
                <button
                  type="button"
                  onClick={() => setMobileBrandsExpanded(prev => !prev)}
                  className="w-full flex items-center justify-between p-3 text-left font-semibold text-neutral-900 bg-neutral-50/70 hover:bg-neutral-100 transition-colors"
                  aria-expanded={mobileBrandsExpanded}
                >
                  <div className="flex items-center gap-2">
                    <Award className="w-4 h-4 text-brand-gold-500" />
                    <span>Luxury Brand Houses</span>
                  </div>
                  <ChevronDown className={`w-4 h-4 text-neutral-500 transition-transform duration-200 ${mobileBrandsExpanded ? 'rotate-180' : ''}`} />
                </button>

                {mobileBrandsExpanded && (
                  <div className="p-3 bg-white space-y-1 border-t border-neutral-100 animate-in fade-in duration-150">
                    <div className="grid grid-cols-2 gap-2">
                      {BRANDS.map(brand => (
                        <Link
                          key={brand.id}
                          to={`/brands/${brand.slug}`}
                          onClick={() => setMobileMenuOpen(false)}
                          className="p-2.5 rounded-xl hover:bg-brand-blush-100/60 transition-colors flex flex-col"
                        >
                          <span className="text-xs font-semibold text-neutral-900 leading-snug">{brand.name}</span>
                          <span className="text-[10px] text-neutral-400 font-medium tracking-wide uppercase mt-0.5">{brand.tier}</span>
                        </Link>
                      ))}
                    </div>
                    <Link
                      to="/brands"
                      onClick={() => setMobileMenuOpen(false)}
                      className="block pt-2 text-xs font-semibold text-brand-rose-600 hover:underline text-center"
                    >
                      View All Brand Houses ({BRANDS.length}) →
                    </Link>
                  </div>
                )}
              </div>

              {/* Collections Mobile Accordion */}
              <div className="border border-neutral-200/80 rounded-2xl overflow-hidden">
                <button
                  type="button"
                  onClick={() => setMobileCollectionsExpanded(prev => !prev)}
                  className="w-full flex items-center justify-between p-3 text-left font-semibold text-neutral-900 bg-neutral-50/70 hover:bg-neutral-100 transition-colors"
                  aria-expanded={mobileCollectionsExpanded}
                >
                  <div className="flex items-center gap-2">
                    <Layers className="w-4 h-4 text-brand-rose-500" />
                    <span>Fragrance Collections</span>
                  </div>
                  <ChevronDown className={`w-4 h-4 text-neutral-500 transition-transform duration-200 ${mobileCollectionsExpanded ? 'rotate-180' : ''}`} />
                </button>

                {mobileCollectionsExpanded && (
                  <div className="p-3 bg-white space-y-1.5 border-t border-neutral-100 animate-in fade-in duration-150">
                    {CATEGORIES.map(cat => (
                      <Link
                        key={cat.id}
                        to={`/collections/${cat.slug}`}
                        onClick={() => setMobileMenuOpen(false)}
                        className="flex items-center gap-2.5 p-2 rounded-xl hover:bg-brand-blush-100/50 transition-colors"
                      >
                        <img 
                          src={cat.image} 
                          alt={cat.title} 
                          className="w-8 h-8 rounded-lg object-cover bg-neutral-100 shrink-0" 
                        />
                        <div>
                          <div className="text-xs font-semibold text-neutral-900">{cat.title}</div>
                          <div className="text-[10px] text-neutral-500">{cat.tagline}</div>
                        </div>
                      </Link>
                    ))}
                    <Link
                      to="/collections"
                      onClick={() => setMobileMenuOpen(false)}
                      className="block pt-2 text-xs font-semibold text-brand-rose-600 hover:underline text-center"
                    >
                      Browse All Collections Directory →
                    </Link>
                  </div>
                )}
              </div>

              <Link
                to="/gifts"
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center justify-between py-2.5 px-3 rounded-xl font-semibold text-neutral-900 hover:bg-neutral-100 transition-colors"
              >
                <div className="flex items-center gap-2">
                  <Gift className="w-4 h-4 text-brand-gold-500" />
                  <span>Gift Sets & Coffrets</span>
                </div>
                <ChevronRight className="w-4 h-4 text-neutral-400" />
              </Link>

              <Link
                to="/find-your-scent"
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center justify-between py-2.5 px-3 rounded-xl font-semibold text-brand-plum-950 bg-brand-blush-100/80 hover:bg-brand-blush-200 transition-colors"
              >
                <div className="flex items-center gap-2">
                  <Sparkles className="w-4 h-4 text-brand-gold-500" />
                  <span>Scent Sommelier Quiz</span>
                </div>
                <ChevronRight className="w-4 h-4 text-brand-plum-900" />
              </Link>

              <Link
                to="/offers"
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center justify-between py-2.5 px-3 rounded-xl font-semibold text-neutral-900 hover:bg-neutral-100 transition-colors"
              >
                <div className="flex items-center gap-2">
                  <Flame className="w-4 h-4 text-brand-rose-500" />
                  <span>Coupons & Offers</span>
                </div>
                <ChevronRight className="w-4 h-4 text-neutral-400" />
              </Link>

              <Link
                to="/stories"
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center justify-between py-2.5 px-3 rounded-xl font-semibold text-neutral-900 hover:bg-neutral-100 transition-colors"
              >
                <span>Editorial Masterclasses</span>
                <ChevronRight className="w-4 h-4 text-neutral-400" />
              </Link>

              <Link
                to="/search"
                onClick={() => setMobileMenuOpen(false)}
                className="flex items-center justify-between py-2.5 px-3 rounded-xl font-semibold text-neutral-900 hover:bg-neutral-100 transition-colors"
              >
                <div className="flex items-center gap-2">
                  <Search className="w-4 h-4 text-neutral-500" />
                  <span>Search Catalog Vault</span>
                </div>
                <ChevronRight className="w-4 h-4 text-neutral-400" />
              </Link>

              <div className="pt-4 mt-2 border-t border-neutral-100 space-y-2">
                <Link
                  to={isLoggedIn ? '/account' : '/account/sign-in'}
                  onClick={() => setMobileMenuOpen(false)}
                  className="flex items-center justify-between py-2.5 px-3 rounded-xl font-semibold text-neutral-900 hover:bg-neutral-100 transition-colors"
                >
                  <div className="flex items-center gap-2">
                    <User className="w-4 h-4 text-neutral-600" />
                    <span>{isLoggedIn ? `Account (${currentUser?.name})` : 'Sign In / Create Account'}</span>
                  </div>
                  <ChevronRight className="w-4 h-4 text-neutral-400" />
                </Link>

                {isLoggedIn && (
                  <button
                    type="button"
                    onClick={() => {
                      setMobileMenuOpen(false);
                      signOut();
                    }}
                    className="w-full flex items-center justify-between py-2.5 px-3 rounded-xl font-semibold text-semantic-error hover:bg-neutral-100 transition-colors"
                  >
                    <div className="flex items-center gap-2">
                      <LogOut className="w-4 h-4" />
                      <span>Sign Out</span>
                    </div>
                  </button>
                )}

                <Link
                  to="/admin"
                  onClick={() => setMobileMenuOpen(false)}
                  className="flex items-center gap-2 py-2.5 px-3 rounded-xl text-xs font-mono text-brand-plum-900 bg-brand-blush-100/50 hover:bg-brand-blush-100 transition-colors"
                >
                  <ShieldCheck className="w-4 h-4 text-brand-gold-600" />
                  <span>Admin Operations Console</span>
                </Link>
              </div>
            </div>
          </div>
        </div>
      )}
    </header>
  );
};

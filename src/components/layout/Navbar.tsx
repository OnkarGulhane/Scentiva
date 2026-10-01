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
  ArrowRight,
  ShieldCheck,
  Package,
  Layers,
  Flame,
  Gift
} from 'lucide-react';
import { BRANDS } from '../../data/brands';
import { CATEGORIES } from '../../data/categories';
import { SearchService, POPULAR_SEARCHES } from '../../services/searchService';

export const Navbar: React.FC = () => {
  const { cartCount, wishlist, setIsCartDrawerOpen, products, isLoggedIn, currentUser } = useStore();
  const navigate = useNavigate();
  const location = useLocation();

  const [isScrolled, setIsScrolled] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [activeDropdown, setActiveDropdown] = useState<string | null>(null);

  const searchInputRef = useRef<HTMLInputElement>(null);
  const searchContainerRef = useRef<HTMLDivElement>(null);

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
  }, [location]);

  // Click outside and keyboard Escape handler
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (searchContainerRef.current && !searchContainerRef.current.contains(e.target as Node)) {
        setIsSearchOpen(false);
      }
    };
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        setIsSearchOpen(false);
        setMobileMenuOpen(false);
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
          <Link to="/" className="flex items-center gap-3 group">
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
                SINCE 2026
              </span>
            </div>
          </Link>

          {/* Desktop Navigation Links */}
          <nav className="hidden lg:flex items-center space-x-1 xl:space-x-2">
            <Link
              to="/shop"
              className="px-3.5 py-2 text-sm font-medium text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70 rounded-lg transition-colors"
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
                className="flex items-center gap-1 px-3.5 py-2 text-sm font-medium text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70 rounded-lg transition-colors"
                aria-expanded={activeDropdown === 'brands'}
              >
                <span>Brands</span>
                <ChevronDown className="w-4 h-4 text-neutral-400" />
              </button>

              {activeDropdown === 'brands' && (
                <div className="absolute top-full left-0 w-80 bg-white rounded-xl shadow-modal border border-neutral-200/80 p-3 animate-in fade-in slide-in-from-top-2 duration-200 z-50">
                  <div className="p-2 border-b border-neutral-100 flex items-center justify-between">
                    <span className="text-xs font-semibold text-neutral-400 uppercase tracking-wider">Luxury Houses</span>
                    <Link to="/brands" className="text-xs text-brand-rose-500 hover:underline font-medium">View All (10+)</Link>
                  </div>
                  <div className="grid grid-cols-2 gap-1.5 pt-2">
                    {BRANDS.slice(0, 8).map(brand => (
                      <Link
                        key={brand.id}
                        to={`/brands/${brand.slug}`}
                        className="px-3 py-2 text-xs font-medium text-neutral-700 hover:bg-brand-blush-100/60 hover:text-brand-plum-900 rounded-lg transition-colors flex items-center justify-between"
                      >
                        <span>{brand.name}</span>
                        <span className="text-[10px] text-neutral-400">{brand.featuredProductCount}</span>
                      </Link>
                    ))}
                  </div>
                </div>
              )}
            </div>

            {/* Categories Dropdown */}
            <div
              className="relative"
              onMouseEnter={() => setActiveDropdown('categories')}
              onMouseLeave={() => setActiveDropdown(null)}
            >
              <button
                className="flex items-center gap-1 px-3.5 py-2 text-sm font-medium text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70 rounded-lg transition-colors"
                aria-expanded={activeDropdown === 'categories'}
              >
                <span>Collections</span>
                <ChevronDown className="w-4 h-4 text-neutral-400" />
              </button>

              {activeDropdown === 'categories' && (
                <div className="absolute top-full left-0 w-80 bg-white rounded-xl shadow-modal border border-neutral-200/80 p-3 animate-in fade-in slide-in-from-top-2 duration-200 z-50">
                  <div className="space-y-1">
                    {CATEGORIES.map(cat => (
                      <Link
                        key={cat.id}
                        to={`/categories/${cat.slug}`}
                        className="flex items-center gap-3 p-2 rounded-lg hover:bg-brand-blush-100/50 transition-colors group/item"
                      >
                        <div className="w-10 h-10 rounded-md overflow-hidden bg-neutral-100 flex-shrink-0">
                          <img src={cat.image} alt={cat.title} className="w-full h-full object-cover group-hover/item:scale-105 transition-transform" />
                        </div>
                        <div className="flex-1">
                          <div className="text-xs font-semibold text-neutral-800 group-hover/item:text-brand-plum-900">{cat.title}</div>
                          <div className="text-[11px] text-neutral-500">{cat.tagline}</div>
                        </div>
                      </Link>
                    ))}
                  </div>
                </div>
              )}
            </div>

            <Link
              to="/gifts"
              className="px-3.5 py-2 text-sm font-medium text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70 rounded-lg transition-colors flex items-center gap-1.5"
            >
              <Gift className="w-3.5 h-3.5 text-brand-gold-500" />
              <span>Gift Coffrets</span>
            </Link>

            <Link
              to="/find-your-scent"
              className="px-3.5 py-2 text-sm font-medium text-brand-plum-900 bg-brand-blush-100/70 hover:bg-brand-blush-200/70 rounded-lg transition-colors flex items-center gap-1.5"
            >
              <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
              <span>Scent Matcher</span>
            </Link>

            <Link
              to="/offers"
              className="px-3.5 py-2 text-sm font-medium text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70 rounded-lg transition-colors flex items-center gap-1"
            >
              <Flame className="w-3.5 h-3.5 text-brand-rose-500" />
              <span>Offers</span>
            </Link>

            <Link
              to="/stories"
              className="px-3.5 py-2 text-sm font-medium text-neutral-800 hover:text-brand-plum-900 hover:bg-neutral-100/70 rounded-lg transition-colors"
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
                            className="w-10 h-10 object-cover rounded-lg bg-neutral-100 flex-shrink-0"
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
            >
              <Heart className="w-5 h-5" />
              {wishlist.length > 0 && (
                <span className="absolute top-1 right-1 w-4 h-4 bg-brand-rose-500 text-white rounded-full text-[10px] font-bold flex items-center justify-center">
                  {wishlist.length}
                </span>
              )}
            </Link>

            {/* Cart Drawer Trigger */}
            <button
              onClick={() => setIsCartDrawerOpen(true)}
              className="relative p-2 text-neutral-700 hover:text-brand-plum-900 rounded-full hover:bg-neutral-100 transition-colors"
              aria-label="Open Shopping Bag"
            >
              <ShoppingBag className="w-5 h-5" />
              {cartCount > 0 && (
                <span className="absolute top-1 right-1 w-4 h-4 bg-brand-plum-900 text-white rounded-full text-[10px] font-bold flex items-center justify-center animate-pulse-subtle">
                  {cartCount}
                </span>
              )}
            </button>

            {/* Account / Admin Portal */}
            <Link
              to={isLoggedIn ? '/account' : '/account/sign-in'}
              className="hidden sm:flex items-center gap-1.5 p-2 text-neutral-700 hover:text-brand-plum-900 rounded-full hover:bg-neutral-100 transition-colors"
              aria-label={isLoggedIn ? `Account (${currentUser?.name})` : 'Sign In'}
              title={isLoggedIn ? `Signed in as ${currentUser?.name}` : 'Sign In / Account'}
            >
              <User className="w-5 h-5" />
            </Link>
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

          <div className="relative ml-0 mr-auto w-full max-w-xs bg-white h-full shadow-modal flex flex-col z-10 animate-in slide-in-from-left duration-300">
            {/* Header */}
            <div className="p-4 border-b border-neutral-100 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-full bg-brand-plum-900 p-0.5 flex items-center justify-center">
                  <img src="/assets/scentiva-emblem.svg" alt="SCENTIVA" className="w-full h-full" />
                </div>
                <span className="font-serif text-lg font-bold text-brand-plum-950">SCENTIVA</span>
              </div>
              <button
                onClick={() => setMobileMenuOpen(false)}
                className="p-1 text-neutral-400 hover:text-neutral-700 rounded-full hover:bg-neutral-100"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Links */}
            <div className="flex-1 overflow-y-auto p-4 space-y-3 text-sm">
              <Link
                to="/shop"
                onClick={() => setMobileMenuOpen(false)}
                className="block py-2 font-medium text-neutral-800 hover:text-brand-plum-900"
              >
                All Perfumes
              </Link>
              <Link
                to="/search"
                onClick={() => setMobileMenuOpen(false)}
                className="block py-2 font-medium text-neutral-800 hover:text-brand-plum-900"
              >
                Search Vault
              </Link>
              <Link
                to="/brands"
                onClick={() => setMobileMenuOpen(false)}
                className="block py-2 font-medium text-neutral-800 hover:text-brand-plum-900"
              >
                Brand Houses
              </Link>
              <Link
                to="/gifts"
                onClick={() => setMobileMenuOpen(false)}
                className="block py-2 font-medium text-neutral-800 hover:text-brand-plum-900"
              >
                Gift Sets & Coffrets
              </Link>
              <Link
                to="/find-your-scent"
                onClick={() => setMobileMenuOpen(false)}
                className="block py-2 font-medium text-brand-plum-900 font-semibold"
              >
                ✨ Scent Sommelier Quiz
              </Link>
              <Link
                to="/offers"
                onClick={() => setMobileMenuOpen(false)}
                className="block py-2 font-medium text-neutral-800 hover:text-brand-plum-900"
              >
                Coupons & Offers
              </Link>
              <Link
                to="/stories"
                onClick={() => setMobileMenuOpen(false)}
                className="block py-2 font-medium text-neutral-800 hover:text-brand-plum-900"
              >
                Editorial Masterclasses
              </Link>

              <div className="pt-4 border-t border-neutral-100 space-y-2">
                <Link
                  to={isLoggedIn ? '/account' : '/account/sign-in'}
                  onClick={() => setMobileMenuOpen(false)}
                  className="block py-2 font-medium text-neutral-800 hover:text-brand-plum-900"
                >
                  {isLoggedIn ? 'My Privé Account' : 'Sign In / Register'}
                </Link>
                <Link
                  to="/admin"
                  onClick={() => setMobileMenuOpen(false)}
                  className="block py-2 text-xs font-mono text-brand-plum-700 hover:underline"
                >
                  ⚙️ Admin Operations Console
                </Link>
              </div>
            </div>
          </div>
        </div>
      )}
    </header>
  );
};

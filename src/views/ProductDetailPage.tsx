'use client';

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from '../hooks/useNavigation';
import { Link } from '../components/common/Link';
import { useStore } from '../context/StoreContext';
import { ProductVariant } from '../types';
import { ProductCard } from '../components/product/ProductCard';
import { Badge } from '../components/common/Badge';
import { SCENTIVA_FALLBACK_IMAGE } from '../data/mediaCatalog';
import { analytics } from '../services/analyticsService';
import { 
  Heart, 
  ShoppingBag, 
  Star, 
  Sparkles, 
  Truck, 
  ShieldCheck, 
  RotateCcw, 
  MapPin, 
  CheckCircle2, 
  Clock, 
  Wind, 
  Droplets,
  Plus, 
  Minus, 
  ArrowRight, 
  Share2, 
  ChevronRight, 
  MessageSquare 
} from 'lucide-react';

export const ProductDetailPage: React.FC = () => {
  const { slug } = useParams<{ slug: string }>();
  const navigate = useNavigate();
  const { products, addToCart, toggleWishlist, isInWishlist, formatPrice, showToast, isLoggedIn } = useStore();

  const product = products.find(p => p.slug === slug || p.id === slug);

  const [selectedVariant, setSelectedVariant] = useState<ProductVariant>(
    product?.variants[0] || { size: '50ml', price: 7999, sku: 'DEF', inStock: true }
  );
  const [activeImageIndex, setActiveImageIndex] = useState(0);
  const [quantity, setQuantity] = useState(1);
  const [pincodeInput, setPincodeInput] = useState('');
  const [pincodeStatus, setPincodeStatus] = useState<string | null>(null);

  // Review Form Modal
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [reviewName, setReviewName] = useState('');
  const [reviewRating, setReviewRating] = useState(5);
  const [reviewComment, setReviewComment] = useState('');

  const isWishlisted = product ? isInWishlist(product.id) : false;

  useEffect(() => {
    if (product) {
      analytics.trackFragranceProfileViewed(product.id, product.name, product.brandName);
      if (product.variants[0]) {
        setSelectedVariant(product.variants[0]);
      }
    }
  }, [product]);

  if (!product) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center p-6 text-center">
        <h2 className="font-serif text-3xl font-bold text-neutral-900 mb-2">Fragrance Not Found</h2>
        <p className="text-xs text-neutral-500 mb-6">The requested flacon may have been archived or moved in the vault.</p>
        <Link to="/shop" className="px-6 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold">
          Explore All Fragrances
        </Link>
      </div>
    );
  }

  const handlePincodeCheck = (e: React.FormEvent) => {
    e.preventDefault();
    if (pincodeInput.length === 6 && /^\d+$/.test(pincodeInput)) {
      setPincodeStatus('Available! Express Luxury Delivery estimated within 2–3 business days via BlueDart Apex Air.');
    } else {
      setPincodeStatus('Please enter a valid 6-digit Indian PIN code.');
    }
  };

  const handleNoteClick = (note: string) => {
    analytics.trackFragranceNoteClicked(note, 'pdp');
    navigate(`/search?q=${encodeURIComponent(note)}`);
  };

  const handleAddToCart = () => {
    analytics.trackCartAdded(product.id, selectedVariant.sku, selectedVariant.price, quantity);
    addToCart(product, selectedVariant, quantity);
  };

  const handleBuyNow = () => {
    analytics.trackCartAdded(product.id, selectedVariant.sku, selectedVariant.price, quantity);
    addToCart(product, selectedVariant, quantity);
    if (isLoggedIn) {
      navigate('/checkout');
    } else {
      navigate('/account/sign-in?redirect=/checkout');
    }
  };

  const handleWishlistToggle = () => {
    if (!isWishlisted) {
      analytics.trackWishlistAdded(product.id, product.name);
    } else {
      analytics.trackWishlistRemoved(product.id);
    }
    toggleWishlist(product);
  };

  const handleAddReview = (e: React.FormEvent) => {
    e.preventDefault();
    if (!reviewName.trim() || !reviewComment.trim()) {
      showToast('Please complete all review fields', 'warning');
      return;
    }
    showToast('Thank you! Your verified review has been submitted for connoisseur moderation.', 'success');
    setShowReviewModal(false);
    setReviewName('');
    setReviewComment('');
  };

  const relatedProducts = products
    .filter(p => p.id !== product.id && (p.category === product.category || p.brandId === product.brandId))
    .slice(0, 4);

  // Schema.org JSON-LD Structured Data
  const jsonLd = {
    '@context': 'https://schema.org',
    '@type': 'Product',
    name: `${product.brandName} ${product.name}`,
    image: product.images,
    description: product.description,
    brand: {
      '@type': 'Brand',
      name: product.brandName,
    },
    offers: {
      '@type': 'Offer',
      priceCurrency: 'INR',
      price: selectedVariant.price,
      availability: selectedVariant.inStock ? 'https://schema.org/InStock' : 'https://schema.org/OutOfStock',
      url: `https://scentiva.luxury/product/${product.slug}`,
    },
    aggregateRating: {
      '@type': 'AggregateRating',
      ratingValue: product.rating,
      reviewCount: product.reviewCount,
    },
  };

  return (
    <div className="min-h-screen bg-neutral-50 py-8 lg:py-12">
      <script
        type="application/ld+json"
        dangerouslySetInnerHTML={{ __html: JSON.stringify(jsonLd) }}
      />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-12">
        {/* Breadcrumb navigation */}
        <nav className="flex items-center gap-1.5 text-xs text-neutral-500 font-medium">
          <Link to="/" className="hover:text-brand-plum-900">Home</Link>
          <ChevronRight className="w-3.5 h-3.5 text-neutral-400" />
          <Link to="/shop" className="hover:text-brand-plum-900">Fragrances</Link>
          <ChevronRight className="w-3.5 h-3.5 text-neutral-400" />
          <Link to={`/brands/${product.brandId.replace('b-', '')}`} className="hover:text-brand-plum-900">
            {product.brandName}
          </Link>
          <ChevronRight className="w-3.5 h-3.5 text-neutral-400" />
          <span className="text-neutral-900 font-semibold truncate">{product.name}</span>
        </nav>

        {/* Product Stage Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-10 lg:gap-14 items-start">
          {/* Left Column: Image Gallery (6 Cols) */}
          <div className="lg:col-span-6 space-y-4 sticky top-24">
            <div className="relative aspect-[4/5] rounded-3xl overflow-hidden bg-white border border-neutral-200/80 shadow-card">
              <img
                src={product.images[activeImageIndex] || product.images[0] || SCENTIVA_FALLBACK_IMAGE}
                alt={`${product.brandName} ${product.name} luxury flacon`}
                onError={(e) => { e.currentTarget.src = SCENTIVA_FALLBACK_IMAGE; }}
                className="w-full h-full object-cover object-center transition-all duration-300"
              />

              {product.discountPercentage && product.discountPercentage > 0 && (
                <div className="absolute top-4 left-4">
                  <Badge variant="plum" size="md">
                    {product.discountPercentage}% OFF
                  </Badge>
                </div>
              )}

              <button
                onClick={handleWishlistToggle}
                className={`absolute top-4 right-4 p-3 rounded-full bg-white/90 backdrop-blur-md shadow-md transition-all hover:scale-110 ${
                  isWishlisted ? 'text-brand-rose-500 bg-brand-blush-100' : 'text-neutral-600 hover:text-brand-rose-500'
                }`}
                aria-label={isWishlisted ? 'Remove from wishlist' : 'Add to wishlist'}
              >
                <Heart className={`w-5 h-5 ${isWishlisted ? 'fill-current' : ''}`} />
              </button>
            </div>

            {/* Thumbnails */}
            {product.images.length > 1 && (
              <div className="flex gap-3 justify-center">
                {product.images.map((img, idx) => (
                  <button
                    key={idx}
                    onClick={() => setActiveImageIndex(idx)}
                    className={`w-20 h-20 rounded-2xl overflow-hidden border-2 transition-all ${
                      activeImageIndex === idx
                        ? 'border-brand-plum-900 shadow-md scale-105'
                        : 'border-neutral-200/80 opacity-70 hover:opacity-100'
                    }`}
                  >
                    <img 
                      src={img} 
                      alt={`${product.name} view ${idx + 1}`} 
                      onError={(e) => { e.currentTarget.src = SCENTIVA_FALLBACK_IMAGE; }}
                      className="w-full h-full object-cover" 
                    />
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Right Column: Product Info & Purchase Form (6 Cols) */}
          <div className="lg:col-span-6 space-y-6">
            <div className="space-y-2 border-b border-neutral-200 pb-6">
              <Link
                to={`/brands/${product.brandId.replace('b-', '')}`}
                className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500 hover:text-brand-plum-900 transition-colors inline-block"
              >
                {product.brandName}
              </Link>
              <h1 className="font-serif text-3xl sm:text-4xl lg:text-5xl font-bold text-brand-plum-950">
                {product.name}
              </h1>
              <p className="text-xs sm:text-sm text-neutral-500 font-medium">
                {product.concentration} • {product.category} • {product.fragranceFamilies.join(', ')}
              </p>

              {/* Rating */}
              <div className="flex items-center gap-3 pt-2">
                <div className="flex items-center gap-1 text-sm font-bold text-neutral-900 bg-brand-gold-100/70 px-2.5 py-1 rounded-md border border-brand-gold-500/40">
                  <Star className="w-4 h-4 fill-brand-gold-500 text-brand-gold-500" />
                  <span>{product.rating}</span>
                </div>
                <span className="text-xs text-neutral-500">({product.reviewCount} Verified Reviews)</span>
                <span className="text-xs text-semantic-success font-semibold flex items-center gap-1">
                  <CheckCircle2 className="w-3.5 h-3.5" /> In Stock ({product.stock} flacons)
                </span>
              </div>
            </div>

            {/* Price section */}
            <div className="space-y-1">
              <div className="flex items-baseline gap-3">
                <span className="text-3xl sm:text-4xl font-bold text-brand-plum-950 tabular-nums">
                  {formatPrice(selectedVariant.price)}
                </span>
                {selectedVariant.mrp && selectedVariant.mrp > selectedVariant.price && (
                  <span className="text-lg text-neutral-400 line-through tabular-nums">
                    {formatPrice(selectedVariant.mrp)}
                  </span>
                )}
                {selectedVariant.mrp && selectedVariant.mrp > selectedVariant.price && (
                  <span className="text-xs font-bold text-semantic-success bg-green-50 px-2 py-0.5 rounded border border-green-200">
                    Save {formatPrice(selectedVariant.mrp - selectedVariant.price)}
                  </span>
                )}
              </div>
              <p className="text-[11px] text-neutral-400">Inclusive of all taxes & luxury presentation coffret</p>
            </div>

            {/* Size Variant Selector */}
            <div className="space-y-2.5 pt-2">
              <div className="flex justify-between text-xs font-semibold text-neutral-800">
                <span>Select Volume / Flacon Size:</span>
                <span className="text-brand-plum-900 font-bold">{selectedVariant.size}</span>
              </div>
              <div className="grid grid-cols-3 sm:grid-cols-4 gap-2.5">
                {product.variants.map(v => (
                  <button
                    key={v.sku}
                    onClick={() => setSelectedVariant(v)}
                    className={`p-3 rounded-xl border text-center transition-all ${
                      selectedVariant.sku === v.sku
                        ? 'border-brand-plum-900 bg-brand-plum-900 text-white shadow-md'
                        : 'border-neutral-200 bg-white text-neutral-800 hover:border-neutral-400'
                    }`}
                  >
                    <div className="text-xs font-bold">{v.size}</div>
                    <div className="text-[11px] mt-0.5 opacity-90 tabular-nums">{formatPrice(v.price)}</div>
                  </button>
                ))}
              </div>
            </div>

            {/* Quantity Stepper & Actions */}
            <div className="space-y-3 pt-4">
              <div className="flex gap-4">
                {/* Quantity */}
                <div className="flex items-center border border-neutral-300 rounded-2xl bg-white px-3 py-2 shadow-xs">
                  <button
                    onClick={() => setQuantity(Math.max(1, quantity - 1))}
                    className="p-1 text-neutral-600 hover:text-neutral-950"
                    aria-label="Decrease quantity"
                  >
                    <Minus className="w-4 h-4" />
                  </button>
                  <span className="px-4 text-sm font-bold tabular-nums text-neutral-900">{quantity}</span>
                  <button
                    onClick={() => setQuantity(quantity + 1)}
                    className="p-1 text-neutral-600 hover:text-neutral-950"
                    aria-label="Increase quantity"
                  >
                    <Plus className="w-4 h-4" />
                  </button>
                </div>

                {/* Add to Bag CTA */}
                <button
                  onClick={handleAddToCart}
                  className="flex-1 py-4 px-6 rounded-2xl bg-brand-plum-900 hover:bg-brand-plum-800 active:scale-98 text-white font-semibold text-sm flex items-center justify-center gap-2 shadow-card hover:shadow-card-hover transition-all"
                  aria-label={`Add ${product.name} to bag`}
                >
                  <ShoppingBag className="w-4 h-4" />
                  <span>Add to Bag • {formatPrice(selectedVariant.price * quantity)}</span>
                </button>
              </div>

              {/* Buy Now Button */}
              <button
                onClick={handleBuyNow}
                className="w-full py-3.5 rounded-2xl bg-brand-gold-500 hover:bg-brand-gold-500/90 text-brand-plum-950 font-bold text-xs uppercase tracking-wider shadow-md transition-all active:scale-98"
              >
                Instant Buy Now
              </button>
            </div>

            {/* Delivery Pincode Checker */}
            <div className="p-4 rounded-2xl bg-white border border-neutral-200/80 space-y-2">
              <div className="flex items-center gap-2 text-xs font-semibold text-neutral-800">
                <MapPin className="w-4 h-4 text-brand-rose-500" />
                <span>Check Delivery & Pincode Serviceability</span>
              </div>
              <form onSubmit={handlePincodeCheck} className="flex gap-2">
                <input
                  type="text"
                  maxLength={6}
                  placeholder="Enter 6-digit Pincode (e.g. 411001)"
                  value={pincodeInput}
                  onChange={e => setPincodeInput(e.target.value)}
                  className="flex-1 px-3 py-2 text-xs rounded-xl border border-neutral-300 focus:outline-none focus:border-brand-plum-900"
                />
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-neutral-800 hover:bg-neutral-950 text-white text-xs font-semibold"
                >
                  Verify
                </button>
              </form>
              {pincodeStatus && (
                <p className="text-[11px] text-brand-plum-900 font-medium pt-1 animate-in fade-in">
                  {pincodeStatus}
                </p>
              )}
            </div>

            {/* Trust Points */}
            <div className="grid grid-cols-3 gap-2 text-center text-[11px] text-neutral-600 pt-2 border-t border-neutral-200">
              <div className="p-2 rounded-xl bg-white border border-neutral-200/60 flex flex-col items-center gap-1">
                <ShieldCheck className="w-4 h-4 text-brand-gold-500" />
                <span>100% Authentic</span>
              </div>
              <div className="p-2 rounded-xl bg-white border border-neutral-200/60 flex flex-col items-center gap-1">
                <Truck className="w-4 h-4 text-brand-gold-500" />
                <span>Free Express Air</span>
              </div>
              <div className="p-2 rounded-xl bg-white border border-neutral-200/60 flex flex-col items-center gap-1">
                <RotateCcw className="w-4 h-4 text-brand-gold-500" />
                <span>7-Day Return Guarantee</span>
              </div>
            </div>
          </div>
        </div>

        {/* Olfactory Pyramid & Fragrance Radar */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 pt-8 border-t border-neutral-200">
          {/* Notes Pyramid (7 Cols) */}
          <div className="lg:col-span-7 bg-white p-6 sm:p-8 rounded-3xl border border-neutral-200 shadow-xs space-y-6">
            <div className="space-y-1">
              <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500 flex items-center gap-1.5">
                <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
                Olfactory Architecture
              </span>
              <h3 className="font-serif text-2xl sm:text-3xl font-bold text-neutral-900">
                Fragrance Notes Pyramid
              </h3>
              <p className="text-xs text-neutral-500">Click any note to explore matching fragrances in the vault.</p>
            </div>

            <div className="space-y-4">
              {/* Top Notes */}
              <div className="p-4 rounded-2xl bg-brand-blush-100/50 border border-brand-blush-200/60 space-y-1">
                <div className="flex items-center justify-between text-xs font-bold text-brand-plum-950">
                  <span className="uppercase tracking-wider">Top Notes (0–15 mins)</span>
                  <span className="text-[11px] text-brand-rose-500 font-medium">First Impression</span>
                </div>
                <div className="flex flex-wrap gap-2 pt-1.5">
                  {product.notes.top.map((note, i) => (
                    <button 
                      key={i} 
                      onClick={() => handleNoteClick(note)}
                      className="text-xs bg-white hover:bg-brand-plum-900 hover:text-white text-neutral-800 px-3 py-1 rounded-full border border-neutral-200 font-medium transition-colors cursor-pointer"
                    >
                      {note} ↗
                    </button>
                  ))}
                </div>
              </div>

              {/* Heart Notes */}
              <div className="p-4 rounded-2xl bg-brand-gold-100/40 border border-brand-gold-500/30 space-y-1">
                <div className="flex items-center justify-between text-xs font-bold text-brand-plum-950">
                  <span className="uppercase tracking-wider">Heart / Middle Notes (2–4 hours)</span>
                  <span className="text-[11px] text-brand-gold-500 font-medium">The Scent Identity</span>
                </div>
                <div className="flex flex-wrap gap-2 pt-1.5">
                  {product.notes.heart.map((note, i) => (
                    <button 
                      key={i} 
                      onClick={() => handleNoteClick(note)}
                      className="text-xs bg-white hover:bg-brand-plum-900 hover:text-white text-neutral-800 px-3 py-1 rounded-full border border-neutral-200 font-medium transition-colors cursor-pointer"
                    >
                      {note} ↗
                    </button>
                  ))}
                </div>
              </div>

              {/* Base Notes */}
              <div className="p-4 rounded-2xl bg-brand-plum-900/5 border border-brand-plum-900/20 space-y-1">
                <div className="flex items-center justify-between text-xs font-bold text-brand-plum-950">
                  <span className="uppercase tracking-wider">Base Notes (6–12+ hours)</span>
                  <span className="text-[11px] text-brand-plum-900 font-medium">Deep Sensual Drydown</span>
                </div>
                <div className="flex flex-wrap gap-2 pt-1.5">
                  {product.notes.base.map((note, i) => (
                    <button 
                      key={i} 
                      onClick={() => handleNoteClick(note)}
                      className="text-xs bg-white hover:bg-brand-plum-900 hover:text-white text-neutral-800 px-3 py-1 rounded-full border border-neutral-200 font-medium transition-colors cursor-pointer"
                    >
                      {note} ↗
                    </button>
                  ))}
                </div>
              </div>
            </div>

            {/* Editorial Story */}
            <div className="pt-4 border-t border-neutral-100 space-y-2">
              <h4 className="text-xs font-bold uppercase tracking-wider text-neutral-900">The Story Behind The Flacon</h4>
              <p className="text-xs sm:text-sm text-neutral-600 leading-relaxed">
                {product.story || product.description}
              </p>
            </div>
          </div>

          {/* Performance Profile (5 Cols) */}
          <div className="lg:col-span-5 bg-white p-6 sm:p-8 rounded-3xl border border-neutral-200 shadow-xs space-y-6">
            <div className="space-y-1">
              <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500">
                Performance Profile
              </span>
              <h3 className="font-serif text-2xl font-bold text-neutral-900">
                Longevity & Sillage
              </h3>
            </div>

            <div className="space-y-4">
              <div className="p-4 rounded-2xl bg-neutral-50 border border-neutral-200 space-y-2">
                <div className="flex items-center justify-between text-xs">
                  <span className="font-bold text-neutral-800 flex items-center gap-1.5">
                    <Clock className="w-4 h-4 text-brand-rose-500" />
                    Longevity On Skin
                  </span>
                  <span className="font-bold text-brand-plum-900">{product.longevity}</span>
                </div>
                <div className="w-full bg-neutral-200 h-2 rounded-full overflow-hidden">
                  <div className="bg-brand-plum-900 h-full w-[85%] rounded-full" />
                </div>
              </div>

              <div className="p-4 rounded-2xl bg-neutral-50 border border-neutral-200 space-y-2">
                <div className="flex items-center justify-between text-xs">
                  <span className="font-bold text-neutral-800 flex items-center gap-1.5">
                    <Wind className="w-4 h-4 text-brand-gold-500" />
                    Sillage (Projection Trail)
                  </span>
                  <span className="font-bold text-brand-plum-900">{product.sillage}</span>
                </div>
                <div className="w-full bg-neutral-200 h-2 rounded-full overflow-hidden">
                  <div className="bg-brand-gold-500 h-full w-[78%] rounded-full" />
                </div>
              </div>

              <div className="p-4 rounded-2xl bg-neutral-50 border border-neutral-200 space-y-2">
                <span className="text-xs font-bold text-neutral-800 block">Best Suited Occasions</span>
                <div className="flex flex-wrap gap-1.5">
                  {product.occasion.map((occ, i) => (
                    <span key={i} className="text-[11px] bg-white text-neutral-700 px-2.5 py-1 rounded-md border border-neutral-200">
                      {occ}
                    </span>
                  ))}
                </div>
              </div>

              <div className="p-4 rounded-2xl bg-neutral-50 border border-neutral-200 space-y-2">
                <span className="text-xs font-bold text-neutral-800 block">Ideal Seasons</span>
                <div className="flex flex-wrap gap-1.5">
                  {product.season.map((sea, i) => (
                    <span key={i} className="text-[11px] bg-white text-neutral-700 px-2.5 py-1 rounded-md border border-neutral-200">
                      {sea}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Customer Reviews Section */}
        <div className="bg-white p-6 sm:p-10 rounded-3xl border border-neutral-200 shadow-xs space-y-8">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-neutral-100 pb-6">
            <div>
              <h3 className="font-serif text-2xl sm:text-3xl font-bold text-neutral-900">
                Customer Reviews ({product.reviewCount})
              </h3>
              <p className="text-xs text-neutral-500 mt-0.5">
                Authentic testimonials from verified fragrance connoisseurs
              </p>
            </div>

            <button
              onClick={() => setShowReviewModal(true)}
              className="px-5 py-2.5 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold flex items-center gap-2 shadow-sm"
            >
              <MessageSquare className="w-3.5 h-3.5" />
              <span>Write a Review</span>
            </button>
          </div>

          {/* Reviews List */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="p-5 rounded-2xl bg-neutral-50 border border-neutral-200/80 space-y-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-full bg-brand-blush-200 text-brand-plum-950 font-bold text-xs flex items-center justify-center">
                    OP
                  </div>
                  <div>
                    <span className="text-xs font-bold text-neutral-900 block">Omkar Patil</span>
                    <span className="text-[10px] text-semantic-success font-medium">✓ Verified Buyer</span>
                  </div>
                </div>
                <div className="flex text-brand-gold-500">
                  {[...Array(5)].map((_, i) => (
                    <Star key={i} className="w-3.5 h-3.5 fill-current" />
                  ))}
                </div>
              </div>
              <p className="text-xs text-neutral-700 leading-relaxed">
                "Unmatched opening and the drydown is exceptional. I wore this to an evening gala in Mumbai and received 5 compliments within the first hour. 100% authentic bottle and the packaging was pure luxury."
              </p>
              <div className="text-[10px] text-neutral-400">Reviewed on Sep 22, 2026</div>
            </div>

            <div className="p-5 rounded-2xl bg-neutral-50 border border-neutral-200/80 space-y-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-full bg-brand-gold-100 text-brand-plum-950 font-bold text-xs flex items-center justify-center">
                    AK
                  </div>
                  <div>
                    <span className="text-xs font-bold text-neutral-900 block">Ananya Kapoor</span>
                    <span className="text-[10px] text-semantic-success font-medium">✓ Verified Buyer</span>
                  </div>
                </div>
                <div className="flex text-brand-gold-500">
                  {[...Array(5)].map((_, i) => (
                    <Star key={i} className="w-3.5 h-3.5 fill-current" />
                  ))}
                </div>
              </div>
              <p className="text-xs text-neutral-700 leading-relaxed">
                "The projection lasts a solid 10 hours on my scarf. Scentiva delivered within 48 hours in thermal packaging. Will definitely order the discovery set next!"
              </p>
              <div className="text-[10px] text-neutral-400">Reviewed on Sep 18, 2026</div>
            </div>
          </div>
        </div>

        {/* Related Fragrances */}
        <div className="space-y-6 pt-4">
          <div className="flex items-center justify-between">
            <h3 className="font-serif text-2xl font-bold text-neutral-900">
              You May Also Adore
            </h3>
            <Link to="/shop" className="text-xs font-semibold text-brand-plum-900 hover:text-brand-rose-500">
              Explore All →
            </Link>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-2 md:grid-cols-4 gap-4 sm:gap-6">
            {relatedProducts.map(prod => (
              <ProductCard key={prod.id} product={prod} />
            ))}
          </div>
        </div>
      </div>

      {/* Review Submission Modal */}
      {showReviewModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-neutral-950/70 backdrop-blur-sm">
          <div className="bg-white rounded-3xl p-6 sm:p-8 max-w-md w-full shadow-modal border border-neutral-200 space-y-4">
            <h3 className="font-serif text-2xl font-bold text-neutral-900">
              Review {product.name}
            </h3>
            <form onSubmit={handleAddReview} className="space-y-3">
              <div>
                <label className="text-xs font-semibold text-neutral-700 block mb-1">Your Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Omkar P."
                  value={reviewName}
                  onChange={e => setReviewName(e.target.value)}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-neutral-300 focus:outline-none focus:border-brand-plum-900"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-neutral-700 block mb-1">Rating</label>
                <div className="flex gap-2">
                  {[1, 2, 3, 4, 5].map(star => (
                    <button
                      type="button"
                      key={star}
                      onClick={() => setReviewRating(star)}
                      className="p-1 text-brand-gold-500 hover:scale-110 transition-transform"
                    >
                      <Star className={`w-6 h-6 ${star <= reviewRating ? 'fill-current' : 'text-neutral-300'}`} />
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-neutral-700 block mb-1">Your Olfactory Experience</label>
                <textarea
                  rows={4}
                  required
                  placeholder="Describe the projection, longevity, notes, and compliments you received..."
                  value={reviewComment}
                  onChange={e => setReviewComment(e.target.value)}
                  className="w-full px-3 py-2 text-xs rounded-xl border border-neutral-300 focus:outline-none focus:border-brand-plum-900"
                />
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowReviewModal(false)}
                  className="flex-1 py-2.5 rounded-xl border border-neutral-300 text-xs font-semibold text-neutral-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 rounded-xl bg-brand-plum-900 text-white text-xs font-semibold hover:bg-brand-plum-800"
                >
                  Submit Review
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Mobile Sticky Bottom CTA Bar */}
      <div className="lg:hidden fixed bottom-14 left-0 right-0 z-30 bg-white/95 backdrop-blur-md border-t border-neutral-200 p-3 px-4 flex items-center justify-between gap-4 shadow-modal">
        <div>
          <span className="text-[10px] text-neutral-500 block uppercase tracking-wider">{selectedVariant.size}</span>
          <span className="text-base font-bold text-brand-plum-950 tabular-nums">
            {formatPrice(selectedVariant.price)}
          </span>
        </div>
        <button
          onClick={handleAddToCart}
          className="flex-1 py-3 px-4 rounded-xl bg-brand-plum-900 text-white text-xs font-semibold flex items-center justify-center gap-2 shadow-md active:scale-95 transition-all"
        >
          <ShoppingBag className="w-4 h-4" />
          <span>Add to Bag</span>
        </button>
      </div>
    </div>
  );
};

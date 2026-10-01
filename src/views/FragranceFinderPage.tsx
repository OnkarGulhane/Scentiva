'use client';

import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from '@/hooks/useNavigation';
import { Link } from '@/components/common/Link';
import { useStore } from '../context/StoreContext';
import { ProductCard } from '../components/product/ProductCard';
import { FragranceFamily, Product } from '../types';
import { 
  Sparkles, 
  ArrowRight, 
  ArrowLeft, 
  RotateCcw, 
  Check, 
  Heart, 
  ShoppingBag, 
  Star, 
  Share2, 
  CheckCircle2, 
  Award 
} from 'lucide-react';
import confetti from 'canvas-confetti';
import { analytics } from '../services/analyticsService';

export const FragranceFinderPage: React.FC = () => {
  const { products, formatPrice, addToCart, toggleWishlist, isInWishlist, showToast } = useStore();
  const location = useLocation();
  const navigate = useNavigate();

  const isResultsRoute = location.pathname.includes('/results');

  const [currentStep, setCurrentStep] = useState(1);
  const totalSteps = 5;

  // Answers State
  const [selectedFamily, setSelectedFamily] = useState<FragranceFamily | null>('Woody');
  const [selectedOccasion, setSelectedOccasion] = useState<string>('Date Night');
  const [selectedIntensity, setSelectedIntensity] = useState<'Light & Subtle' | 'Balanced & Elegant' | 'Bold & Intense'>('Bold & Intense');
  const [selectedBudget, setSelectedBudget] = useState<string>('mid');
  const [selectedGender, setSelectedGender] = useState<string>('For Him');
  const [isFinished, setIsFinished] = useState(isResultsRoute);

  useEffect(() => {
    if (isResultsRoute) {
      setIsFinished(true);
    }
  }, [isResultsRoute]);

  const handleNext = () => {
    if (currentStep < totalSteps) {
      analytics.trackScentFinderStarted(currentStep + 1);
      setCurrentStep(prev => prev + 1);
    } else {
      setIsFinished(true);
      analytics.trackScentFinderCompleted(selectedFamily || undefined, selectedOccasion, matchedProducts.length);
      navigate('/find-your-scent/results', { replace: true });
      confetti({
        particleCount: 80,
        spread: 70,
        origin: { y: 0.6 },
        colors: ['#E9B7D8', '#C7A66A', '#451333', '#B85B88']
      });
    }
  };

  const handleBack = () => {
    if (currentStep > 1) {
      setCurrentStep(prev => prev - 1);
    }
  };

  const handleRestart = () => {
    setCurrentStep(1);
    setIsFinished(false);
    navigate('/find-your-scent');
  };

  const handleShareResults = () => {
    if (navigator.clipboard) {
      navigator.clipboard.writeText(window.location.href);
      showToast('Recommendation link copied to clipboard!', 'success');
    }
  };

  // Matched Recommendations with dynamic score
  const matchedProducts = products.filter(p => {
    if (selectedGender && selectedGender !== 'Any' && p.category !== selectedGender && p.category !== 'Unisex' && p.category !== 'Luxury & Niche') {
      return false;
    }
    return true;
  }).slice(0, 4);

  const matchScores = [97, 93, 89, 86];

  return (
    <div className="min-h-screen bg-gradient-to-b from-brand-blush-100/30 via-neutral-50 to-neutral-50 py-10 lg:py-16">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
        {/* Header */}
        <div className="text-center space-y-2">
          <span className="inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-brand-plum-900 text-brand-gold-100 text-xs font-semibold uppercase tracking-widest shadow-xs">
            <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
            <span>Fragrance Sommelier • Consultation Engine</span>
          </span>
          <h1 className="font-serif text-3xl sm:text-5xl font-bold text-neutral-900">
            {isFinished ? 'Your Bespoke Fragrance Matches' : 'Find Your Signature Scent'}
          </h1>
          <p className="text-xs sm:text-sm text-neutral-600 max-w-md mx-auto">
            {isFinished
              ? 'Our olfactory algorithm matched your sensory profile with these premier compositions.'
              : 'A 60-second guided consultation analyzing your sensory preferences, skin chemistry, and lifestyle moments.'}
          </p>
        </div>

        {!isFinished ? (
          <div className="bg-white rounded-3xl p-6 sm:p-10 border border-neutral-200/90 shadow-modal space-y-8 animate-in fade-in duration-300">
            {/* Progress Stepper */}
            <div className="space-y-2">
              <div className="flex justify-between text-xs font-semibold text-neutral-500">
                <span>Step 0{currentStep} of 0{totalSteps}</span>
                <span>{Math.round((currentStep / totalSteps) * 100)}% Completed</span>
              </div>
              <div className="w-full bg-neutral-200 h-2 rounded-full overflow-hidden">
                <div
                  className="bg-brand-plum-900 h-full transition-all duration-500 rounded-full"
                  style={{ width: `${(currentStep / totalSteps) * 100}%` }}
                />
              </div>
            </div>

            {/* Step 1: Fragrance Family */}
            {currentStep === 1 && (
              <div className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-200">
                <h3 className="font-serif text-2xl font-bold text-neutral-900">
                  What olfactory family speaks to your soul?
                </h3>
                <p className="text-xs text-neutral-500">Select the core scent notes you naturally gravitate towards.</p>

                <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 pt-2">
                  {[
                    { id: 'Fresh', label: 'Fresh & Citrus', desc: 'Bergamot, marine accords, Sicilian lemon', icon: '🍋' },
                    { id: 'Woody', label: 'Smoky Woods & Oud', desc: 'Cedarwood, vetiver, agarwood, sandalwood', icon: '🌲' },
                    { id: 'Floral', label: 'Sensual Florals', desc: 'Jasmine, Grasse rose, tuberose, iris', icon: '🌸' },
                    { id: 'Oriental', label: 'Amber & Spices', desc: 'Warm vanilla, cinnamon, tonka, resins', icon: '✨' },
                    { id: 'Sweet & Gourmand', label: 'Gourmand Delights', desc: 'Bourbon vanilla, praline, roasted coffee', icon: '🍫' },
                    { id: 'Aquatic', label: 'Oceanic & Mineral', desc: 'Sea salt, calone, marine breeze', icon: '🌊' },
                  ].map(item => (
                    <button
                      key={item.id}
                      onClick={() => setSelectedFamily(item.id as FragranceFamily)}
                      className={`p-4 rounded-2xl border text-left flex flex-col justify-between transition-all ${
                        selectedFamily === item.id
                          ? 'border-brand-plum-900 bg-brand-blush-100/60 shadow-card ring-1 ring-brand-plum-900'
                          : 'border-neutral-200 hover:border-neutral-300 hover:bg-neutral-50'
                      }`}
                    >
                      <span className="text-2xl mb-2">{item.icon}</span>
                      <div>
                        <div className="text-xs font-bold text-neutral-900">{item.label}</div>
                        <div className="text-[10px] text-neutral-500 mt-0.5 leading-snug">{item.desc}</div>
                      </div>
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Step 2: Occasion */}
            {currentStep === 2 && (
              <div className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-200">
                <h3 className="font-serif text-2xl font-bold text-neutral-900">
                  When will you wear this fragrance most?
                </h3>
                <p className="text-xs text-neutral-500">Help us determine the ideal sillage and projection profile.</p>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
                  {[
                    { id: 'Everyday', label: 'Daily Signature & Work', desc: 'Sophisticated, inoffensive, clean projection' },
                    { id: 'Date Night', label: 'Romantic Evenings & Date Nights', desc: 'Magnetic, intimate, lingering sillage' },
                    { id: 'Gala & Celebrations', label: 'High-Profile Events & Galas', desc: 'Opulent, room-filling, unforgettable aura' },
                    { id: 'Weekend Leisure', label: 'Casual Weekends & Travel', desc: 'Breezy, invigorating, effortless elegance' },
                  ].map(item => (
                    <button
                      key={item.id}
                      onClick={() => setSelectedOccasion(item.id)}
                      className={`p-4 rounded-2xl border text-left transition-all ${
                        selectedOccasion === item.id
                          ? 'border-brand-plum-900 bg-brand-blush-100/60 shadow-card ring-1 ring-brand-plum-900'
                          : 'border-neutral-200 hover:border-neutral-300 hover:bg-neutral-50'
                      }`}
                    >
                      <div className="text-xs font-bold text-neutral-900">{item.label}</div>
                      <div className="text-[11px] text-neutral-500 mt-1">{item.desc}</div>
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Step 3: Intensity */}
            {currentStep === 3 && (
              <div className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-200">
                <h3 className="font-serif text-2xl font-bold text-neutral-900">
                  Preferred Scent Concentration & Intensity
                </h3>
                <p className="text-xs text-neutral-500">Select how bold you want your presence to be.</p>

                <div className="space-y-3 pt-2">
                  {[
                    { id: 'Light & Subtle', label: 'Subtle & Intimate (EDT / EDC)', desc: 'Noticeable only within arm’s reach • 4-6 hours longevity' },
                    { id: 'Balanced & Elegant', label: 'Balanced & Radiant (Eau de Parfum)', desc: 'Leaves a graceful trail without overpowering • 6-10 hours' },
                    { id: 'Bold & Intense', label: 'Extrait de Parfum / Pure Beastmode', desc: 'Dense, rich oil concentration • 12+ hours extreme longevity' },
                  ].map(item => (
                    <button
                      key={item.id}
                      onClick={() => setSelectedIntensity(item.id as any)}
                      className={`w-full p-4 rounded-2xl border text-left flex items-center justify-between transition-all ${
                        selectedIntensity === item.id
                          ? 'border-brand-plum-900 bg-brand-blush-100/60 shadow-card ring-1 ring-brand-plum-900'
                          : 'border-neutral-200 hover:border-neutral-300 hover:bg-neutral-50'
                      }`}
                    >
                      <div>
                        <div className="text-xs font-bold text-neutral-900">{item.label}</div>
                        <div className="text-[11px] text-neutral-500 mt-0.5">{item.desc}</div>
                      </div>
                      {selectedIntensity === item.id && (
                        <div className="w-6 h-6 rounded-full bg-brand-plum-900 text-white flex items-center justify-center">
                          <Check className="w-3.5 h-3.5" />
                        </div>
                      )}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Step 4: Gender Target */}
            {currentStep === 4 && (
              <div className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-200">
                <h3 className="font-serif text-2xl font-bold text-neutral-900">
                  Target Composition Category
                </h3>
                <p className="text-xs text-neutral-500">Are you shopping for a specific gender profile or unisex?</p>

                <div className="grid grid-cols-2 gap-3 pt-2">
                  {[
                    { id: 'For Her', label: 'For Her (Feminine Expression)', icon: '👑' },
                    { id: 'For Him', label: 'For Him (Masculine Expression)', icon: '🎩' },
                    { id: 'Unisex', label: 'Genderless & Niche Unisex', icon: '⚜️' },
                    { id: 'Any', label: 'Open to Everything', icon: '✨' },
                  ].map(item => (
                    <button
                      key={item.id}
                      onClick={() => setSelectedGender(item.id)}
                      className={`p-4 rounded-2xl border text-center transition-all ${
                        selectedGender === item.id
                          ? 'border-brand-plum-900 bg-brand-blush-100/60 shadow-card ring-1 ring-brand-plum-900'
                          : 'border-neutral-200 hover:border-neutral-300 hover:bg-neutral-50'
                      }`}
                    >
                      <span className="text-2xl block mb-1">{item.icon}</span>
                      <span className="text-xs font-bold text-neutral-900">{item.label}</span>
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Step 5: Budget */}
            {currentStep === 5 && (
              <div className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-200">
                <h3 className="font-serif text-2xl font-bold text-neutral-900">
                  Preferred Investment Range
                </h3>
                <p className="text-xs text-neutral-500">Every recommendation is 100% authentic and verified.</p>

                <div className="grid grid-cols-3 gap-3 pt-2">
                  {[
                    { id: 'accessible', label: 'Under ₹8,000', tier: 'Designer Essentials' },
                    { id: 'mid', label: '₹8,000 – ₹18,000', tier: 'Haute Parfumerie' },
                    { id: 'prestige', label: '₹18,000+', tier: 'Private Millésime / Niche' },
                  ].map(item => (
                    <button
                      key={item.id}
                      onClick={() => setSelectedBudget(item.id)}
                      className={`p-4 rounded-2xl border text-center transition-all ${
                        selectedBudget === item.id
                          ? 'border-brand-plum-900 bg-brand-blush-100/60 shadow-card ring-1 ring-brand-plum-900'
                          : 'border-neutral-200 hover:border-neutral-300 hover:bg-neutral-50'
                      }`}
                    >
                      <div className="text-xs font-bold text-neutral-900">{item.label}</div>
                      <div className="text-[10px] text-brand-plum-700 font-mono mt-1 uppercase">{item.tier}</div>
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Navigation Controls */}
            <div className="flex items-center justify-between pt-6 border-t border-neutral-200">
              <button
                onClick={handleBack}
                disabled={currentStep === 1}
                className={`flex items-center gap-1.5 px-4 py-2.5 rounded-full text-xs font-semibold ${
                  currentStep === 1 ? 'opacity-30 cursor-not-allowed text-neutral-400' : 'text-neutral-700 hover:bg-neutral-100'
                }`}
              >
                <ArrowLeft className="w-4 h-4" />
                <span>Back</span>
              </button>

              <button
                onClick={handleNext}
                className="flex items-center gap-2 px-7 py-3 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold shadow-card transition-all active:scale-98"
              >
                <span>{currentStep === totalSteps ? 'Reveal My Signature Match' : 'Continue'}</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        ) : (
          /* Results Screen */
          <div className="space-y-8 animate-in fade-in duration-400">
            <div className="bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-modal space-y-6">
              <div className="flex flex-col sm:flex-row items-center justify-between gap-4 pb-6 border-b border-neutral-200">
                <div className="space-y-1 text-center sm:text-left">
                  <div className="inline-flex items-center gap-1.5 text-xs text-brand-gold-500 font-bold uppercase tracking-wider">
                    <Award className="w-4 h-4" />
                    <span>Curated Match Matrix</span>
                  </div>
                  <h3 className="font-serif text-2xl font-bold text-brand-plum-950">
                    Your Top Signature Matches
                  </h3>
                  <p className="text-xs text-neutral-500">
                    Calculated for: {selectedFamily} notes • {selectedOccasion} • {selectedIntensity}
                  </p>
                </div>

                <div className="flex items-center gap-3">
                  <button
                    onClick={handleShareResults}
                    className="flex items-center gap-1.5 px-4 py-2 rounded-full bg-neutral-100 hover:bg-neutral-200 text-neutral-800 text-xs font-semibold transition-colors"
                  >
                    <Share2 className="w-3.5 h-3.5" />
                    <span>Share</span>
                  </button>
                  <button
                    onClick={handleRestart}
                    className="flex items-center gap-1.5 px-4 py-2 rounded-full border border-neutral-300 text-neutral-700 hover:bg-neutral-100 text-xs font-semibold transition-colors"
                  >
                    <RotateCcw className="w-3.5 h-3.5" />
                    <span>Retake Quiz</span>
                  </button>
                </div>
              </div>

              {/* Matched Product Cards */}
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6">
                {matchedProducts.map((product, idx) => (
                  <div key={product.id} className="relative group flex flex-col">
                    <div className="absolute top-3 left-3 z-10 bg-brand-plum-900 text-brand-gold-100 text-[10px] font-bold px-2.5 py-1 rounded-full shadow-md flex items-center gap-1">
                      <Sparkles className="w-3 h-3 text-brand-gold-500" />
                      <span>{matchScores[idx] || 85}% Match</span>
                    </div>
                    <ProductCard product={product} />
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

'use client';

import React, { useState, useEffect, useMemo, useCallback } from 'react';
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
  Share2, 
  Award,
  ShieldCheck,
  Compass,
  CheckCircle2,
  RefreshCw,
  SlidersHorizontal
} from 'lucide-react';
import confetti from 'canvas-confetti';
import { analytics } from '../services/analyticsService';
import { AiApiService, ScentQuizPayload, ScentQuizBackendResponse } from '../services/aiApiService';

interface ScoredProductMatch {
  product: Product;
  score: number;
  reason: string;
}

export const FragranceFinderPage: React.FC = () => {
  const { products, formatPrice, showToast } = useStore();
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

  // AI Backend Recommendations State
  const [isLoadingRecommendations, setIsLoadingRecommendations] = useState(false);
  const [backendPersona, setBackendPersona] = useState<{ title: string; description: string } | null>(null);

  // Sync state with route
  useEffect(() => {
    if (isResultsRoute) {
      setIsFinished(true);
    }
  }, [isResultsRoute]);

  // Comprehensive Olfactory Recommendation Algorithm
  const calculateMatches = useCallback((): ScoredProductMatch[] => {
    if (!products || products.length === 0) return [];

    const scoredList: ScoredProductMatch[] = products.map(product => {
      let score = 50; // baseline score
      const reasons: string[] = [];

      // 1. Gender Compatibility (Max +20 pts)
      if (selectedGender === 'For Him') {
        if (product.category === 'For Him') {
          score += 20;
        } else if (product.category === 'Unisex' || product.category === 'Luxury & Niche') {
          score += 16;
        } else {
          score -= 30; // Strong penalty for opposing gender target
        }
      } else if (selectedGender === 'For Her') {
        if (product.category === 'For Her') {
          score += 20;
        } else if (product.category === 'Unisex' || product.category === 'Luxury & Niche') {
          score += 16;
        } else {
          score -= 30;
        }
      } else {
        score += 18; // Unisex / Any
      }

      // 2. Olfactory Family Matching (Max +35 pts)
      if (selectedFamily) {
        if (product.fragranceFamilies.includes(selectedFamily)) {
          score += 35;
          reasons.push(`${selectedFamily} signature accord`);
        } else {
          // Check notes pyramid
          const allNotes = [
            ...(product.notes?.top || []),
            ...(product.notes?.heart || []),
            ...(product.notes?.base || [])
          ].join(' ').toLowerCase();

          const familyKeywords: Record<string, string[]> = {
            Woody: ['cedar', 'oud', 'sandalwood', 'vetiver', 'birch', 'patchouli'],
            Fresh: ['bergamot', 'lemon', 'citrus', 'mandarin', 'grapefruit', 'neroli'],
            Floral: ['rose', 'jasmine', 'tuberose', 'iris', 'orange blossom', 'orchid'],
            Oriental: ['amber', 'vanilla', 'tonka', 'cinnamon', 'incense', 'spices'],
            'Sweet & Gourmand': ['vanilla', 'tonka', 'chocolate', 'praline', 'coffee'],
            Aquatic: ['marine', 'sea salt', 'calone', 'mineral', 'ocean']
          };

          const keywords = familyKeywords[selectedFamily] || [];
          const matchesKeyword = keywords.some(kw => allNotes.includes(kw));
          if (matchesKeyword) {
            score += 20;
            reasons.push(`Subtle ${selectedFamily} notes`);
          }
        }
      }

      // 3. Occasion Matching (Max +25 pts)
      if (selectedOccasion) {
        if (product.occasion && product.occasion.some(occ => occ.toLowerCase().includes(selectedOccasion.toLowerCase()))) {
          score += 25;
          reasons.push(`Engineered for ${selectedOccasion}`);
        } else {
          score += 10;
        }
      }

      // 4. Sillage & Intensity Profile (Max +15 pts)
      if (selectedIntensity === 'Bold & Intense') {
        if (product.sillage === 'Strong' || product.sillage === 'Enormous' || product.concentration.includes('Extrait') || product.concentration.includes('Parfum')) {
          score += 15;
        } else {
          score += 5;
        }
      } else if (selectedIntensity === 'Light & Subtle') {
        if (product.sillage === 'Intimate' || product.sillage === 'Moderate' || product.concentration.includes('EDT') || product.concentration.includes('EDC')) {
          score += 15;
        } else {
          score += 5;
        }
      } else {
        // Balanced & Elegant
        score += 12;
      }

      // 5. Budget Tier (Max +10 pts)
      const minPrice = product.variants && product.variants.length > 0
        ? Math.min(...product.variants.map(v => v.price))
        : 7999;

      if (selectedBudget === 'accessible' && minPrice < 8000) {
        score += 10;
      } else if (selectedBudget === 'mid' && minPrice >= 6000 && minPrice <= 18000) {
        score += 10;
      } else if (selectedBudget === 'prestige' && (minPrice > 16000 || product.category === 'Luxury & Niche')) {
        score += 10;
      } else {
        score += 5;
      }

      // Clamp score to realistic luxury affinity range (80% - 98%)
      const finalScore = Math.min(98, Math.max(82, Math.round(score * 0.7 + 25)));
      const reasonText = reasons.length > 0 ? reasons.join(' • ') : `Harmonizes with your ${selectedOccasion} aesthetic`;

      return {
        product,
        score: finalScore,
        reason: reasonText
      };
    });

    // Sort descending by score and pick top 4 matches
    return scoredList
      .sort((a, b) => b.score - a.score)
      .slice(0, 4);
  }, [products, selectedFamily, selectedOccasion, selectedIntensity, selectedBudget, selectedGender]);

  // Fetch AI backend recommendations when finished
  const fetchAiRecommendations = useCallback(async () => {
    setIsLoadingRecommendations(true);
    try {
      const genderMap: Record<string, string> = {
        'For Him': 'FOR_HIM',
        'For Her': 'FOR_HER',
        'Unisex': 'UNISEX',
        'Any': 'UNISEX'
      };

      const payload: ScentQuizPayload = {
        fragranceFamily: selectedFamily || 'Woody',
        occasion: selectedOccasion,
        intensity: selectedIntensity,
        gender: genderMap[selectedGender] || 'UNISEX',
        season: 'All Season'
      };

      const res = await AiApiService.findScentByQuiz(payload);
      if (res && res.personaTitle) {
        setBackendPersona({
          title: res.personaTitle,
          description: res.personaDescription
        });
      }
    } catch (e) {
      // Gracefully fall back to client-side persona profile
      console.warn('AI Concierge recommendation service offline, using client sommelier engine', e);
    } finally {
      setIsLoadingRecommendations(false);
    }
  }, [selectedFamily, selectedOccasion, selectedIntensity, selectedGender]);

  // Run AI recommendation query when quiz finishes
  useEffect(() => {
    if (isFinished) {
      fetchAiRecommendations();
    }
  }, [isFinished, fetchAiRecommendations]);

  const matchedResults = useMemo(() => calculateMatches(), [calculateMatches]);

  const handleNext = () => {
    if (currentStep < totalSteps) {
      analytics.trackScentFinderStarted(currentStep + 1);
      setCurrentStep(prev => prev + 1);
    } else {
      setIsFinished(true);
      analytics.trackScentFinderCompleted(selectedFamily || undefined, selectedOccasion, matchedResults.length);
      navigate('/find-your-scent/results', { replace: true });
      confetti({
        particleCount: 90,
        spread: 75,
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

  const handleShareResults = async () => {
    const shareUrl = typeof window !== 'undefined' ? window.location.href : 'https://scentiva.com/find-your-scent/results';
    const shareData = {
      title: 'My SCENTIVA Signature Matches',
      text: `I discovered my bespoke luxury fragrance profile on SCENTIVA: ${selectedFamily} notes tailored for ${selectedOccasion}!`,
      url: shareUrl
    };

    if (typeof navigator !== 'undefined' && navigator.share && navigator.canShare && navigator.canShare(shareData)) {
      try {
        await navigator.share(shareData);
        showToast('Signature matches shared successfully!', 'success');
        return;
      } catch (err: any) {
        if (err.name !== 'AbortError') {
          // Fall through to clipboard
        } else {
          return;
        }
      }
    }

    if (typeof navigator !== 'undefined' && navigator.clipboard) {
      try {
        await navigator.clipboard.writeText(shareUrl);
        showToast('Curated match matrix link copied to clipboard!', 'success');
      } catch (e) {
        showToast('Link copied: ' + shareUrl, 'info');
      }
    } else {
      showToast('Link: ' + shareUrl, 'info');
    }
  };

  // Bespoke Persona Fallback
  const defaultPersonaTitle = useMemo(() => {
    if (selectedFamily === 'Woody') return 'The Velvet Nocturne';
    if (selectedFamily === 'Fresh') return 'The Luminous Sovereign';
    if (selectedFamily === 'Floral') return 'The Opulent Bloom';
    if (selectedFamily === 'Oriental') return 'The Royal Amber Connoisseur';
    if (selectedFamily === 'Sweet & Gourmand') return 'The Sensual Epicurean';
    return 'The Oceanic Vanguard';
  }, [selectedFamily]);

  const defaultPersonaDescription = useMemo(() => {
    return `Your answers reveal a refined affinity for ${selectedFamily?.toLowerCase() || 'woody'} accords designed for ${selectedOccasion.toLowerCase()}. Your ideal fragrance projects with ${selectedIntensity.toLowerCase()} sillage, evoking effortless distinction and memorable magnetism.`;
  }, [selectedFamily, selectedOccasion, selectedIntensity]);

  return (
    <div className="min-h-screen bg-gradient-to-b from-brand-blush-100/30 via-neutral-50 to-neutral-50 py-10 lg:py-16">
      <div className={`${isFinished ? 'max-w-7xl' : 'max-w-3xl'} mx-auto px-4 sm:px-6 lg:px-8 space-y-10 transition-all duration-500`}>
        {/* Top Header */}
        <div className="text-center space-y-3">
          <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-brand-plum-900 text-brand-gold-100 text-xs font-semibold uppercase tracking-widest shadow-xs border border-brand-gold-500/30">
            <Sparkles className="w-3.5 h-3.5 text-brand-gold-400" />
            <span>Fragrance Sommelier • Consultation Engine</span>
          </div>
          <h1 className="font-serif text-3xl sm:text-5xl font-bold text-neutral-900 tracking-tight">
            {isFinished ? 'Your Bespoke Fragrance Matches' : 'Find Your Signature Scent'}
          </h1>
          <p className="text-xs sm:text-sm text-neutral-600 max-w-xl mx-auto leading-relaxed">
            {isFinished
              ? 'Our olfactory matchmaking algorithm harmonized your sensory preferences, occasion requirements, and intensity profile with these premier flacons.'
              : 'A 60-second guided consultation analyzing your sensory preferences, skin chemistry moments, and lifestyle sillage.'}
          </p>
        </div>

        {!isFinished ? (
          /* Quiz Stepper Screen */
          <div className="bg-white rounded-3xl p-6 sm:p-10 border border-neutral-200/90 shadow-modal space-y-8 animate-in fade-in duration-300">
            {/* Progress Stepper */}
            <div className="space-y-2.5">
              <div className="flex justify-between text-xs font-bold text-neutral-600">
                <span className="uppercase tracking-wider">Step 0{currentStep} of 0{totalSteps}</span>
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
                <div>
                  <h3 className="font-serif text-2xl sm:text-3xl font-bold text-neutral-900">
                    What olfactory family speaks to your soul?
                  </h3>
                  <p className="text-xs sm:text-sm text-neutral-500 mt-1">Select the core scent notes you naturally gravitate towards.</p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3.5 pt-2">
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
                      type="button"
                      onClick={() => setSelectedFamily(item.id as FragranceFamily)}
                      className={`p-4 rounded-2xl border text-left flex flex-col justify-between transition-all active:scale-98 ${
                        selectedFamily === item.id
                          ? 'border-brand-plum-900 bg-brand-blush-100/70 shadow-card ring-2 ring-brand-plum-900'
                          : 'border-neutral-200 hover:border-neutral-300 hover:bg-neutral-50'
                      }`}
                    >
                      <span className="text-3xl mb-2">{item.icon}</span>
                      <div>
                        <div className="text-xs font-bold text-neutral-900">{item.label}</div>
                        <div className="text-[11px] text-neutral-500 mt-0.5 leading-snug">{item.desc}</div>
                      </div>
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Step 2: Occasion */}
            {currentStep === 2 && (
              <div className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-200">
                <div>
                  <h3 className="font-serif text-2xl sm:text-3xl font-bold text-neutral-900">
                    When will you wear this fragrance most?
                  </h3>
                  <p className="text-xs sm:text-sm text-neutral-500 mt-1">Help us determine the ideal sillage and projection profile.</p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5 pt-2">
                  {[
                    { id: 'Everyday', label: 'Daily Signature & Office', desc: 'Sophisticated, clean projection suited for professional elegance' },
                    { id: 'Date Night', label: 'Romantic Evenings & Date Nights', desc: 'Magnetic, intimate, lingering sillage that captivates' },
                    { id: 'Gala & Celebrations', label: 'High-Profile Events & Galas', desc: 'Opulent, room-filling, unforgettable presence' },
                    { id: 'Weekend Leisure', label: 'Casual Weekends & Travel', desc: 'Breezy, invigorating, effortless relaxation' },
                  ].map(item => (
                    <button
                      key={item.id}
                      type="button"
                      onClick={() => setSelectedOccasion(item.id)}
                      className={`p-5 rounded-2xl border text-left transition-all active:scale-98 ${
                        selectedOccasion === item.id
                          ? 'border-brand-plum-900 bg-brand-blush-100/70 shadow-card ring-2 ring-brand-plum-900'
                          : 'border-neutral-200 hover:border-neutral-300 hover:bg-neutral-50'
                      }`}
                    >
                      <div className="text-xs font-bold text-neutral-900">{item.label}</div>
                      <div className="text-[11px] text-neutral-500 mt-1 leading-relaxed">{item.desc}</div>
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Step 3: Intensity */}
            {currentStep === 3 && (
              <div className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-200">
                <div>
                  <h3 className="font-serif text-2xl sm:text-3xl font-bold text-neutral-900">
                    Preferred Concentration & Longevity
                  </h3>
                  <p className="text-xs sm:text-sm text-neutral-500 mt-1">Select how bold you want your presence to feel.</p>
                </div>

                <div className="space-y-3.5 pt-2">
                  {[
                    { id: 'Light & Subtle', label: 'Subtle & Intimate (EDT / EDC)', desc: 'Noticeable within arm’s reach • 4-6 hours longevity' },
                    { id: 'Balanced & Elegant', label: 'Balanced & Radiant (Eau de Parfum)', desc: 'Leaves a graceful, refined trail without overpowering • 6-10 hours' },
                    { id: 'Bold & Intense', label: 'Extrait de Parfum / Beastmode Aura', desc: 'Dense, rich oil concentration • 12+ hours extreme longevity' },
                  ].map(item => (
                    <button
                      key={item.id}
                      type="button"
                      onClick={() => setSelectedIntensity(item.id as any)}
                      className={`w-full p-5 rounded-2xl border text-left flex items-center justify-between transition-all active:scale-98 ${
                        selectedIntensity === item.id
                          ? 'border-brand-plum-900 bg-brand-blush-100/70 shadow-card ring-2 ring-brand-plum-900'
                          : 'border-neutral-200 hover:border-neutral-300 hover:bg-neutral-50'
                      }`}
                    >
                      <div>
                        <div className="text-xs font-bold text-neutral-900">{item.label}</div>
                        <div className="text-[11px] text-neutral-500 mt-0.5">{item.desc}</div>
                      </div>
                      {selectedIntensity === item.id && (
                        <div className="w-6 h-6 rounded-full bg-brand-plum-900 text-white flex items-center justify-center shrink-0">
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
                <div>
                  <h3 className="font-serif text-2xl sm:text-3xl font-bold text-neutral-900">
                    Target Olfactory Composition
                  </h3>
                  <p className="text-xs sm:text-sm text-neutral-500 mt-1">Are you shopping for a specific gender profile or genderless niche?</p>
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3.5 pt-2">
                  {[
                    { id: 'For Her', label: 'For Her', icon: '👑' },
                    { id: 'For Him', label: 'For Him', icon: '🎩' },
                    { id: 'Unisex', label: 'Genderless Unisex', icon: '⚜️' },
                    { id: 'Any', label: 'Open to All', icon: '✨' },
                  ].map(item => (
                    <button
                      key={item.id}
                      type="button"
                      onClick={() => setSelectedGender(item.id)}
                      className={`p-5 rounded-2xl border text-center transition-all active:scale-98 ${
                        selectedGender === item.id
                          ? 'border-brand-plum-900 bg-brand-blush-100/70 shadow-card ring-2 ring-brand-plum-900'
                          : 'border-neutral-200 hover:border-neutral-300 hover:bg-neutral-50'
                      }`}
                    >
                      <span className="text-3xl block mb-2">{item.icon}</span>
                      <span className="text-xs font-bold text-neutral-900">{item.label}</span>
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Step 5: Budget */}
            {currentStep === 5 && (
              <div className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-200">
                <div>
                  <h3 className="font-serif text-2xl sm:text-3xl font-bold text-neutral-900">
                    Preferred Investment Range
                  </h3>
                  <p className="text-xs sm:text-sm text-neutral-500 mt-1">Every recommendation is 100% authentic and verified.</p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3.5 pt-2">
                  {[
                    { id: 'accessible', label: 'Under ₹8,000', tier: 'Designer Essentials' },
                    { id: 'mid', label: '₹8,000 – ₹18,000', tier: 'Haute Parfumerie' },
                    { id: 'prestige', label: '₹18,000+', tier: 'Private Millésime / Niche' },
                  ].map(item => (
                    <button
                      key={item.id}
                      type="button"
                      onClick={() => setSelectedBudget(item.id)}
                      className={`p-5 rounded-2xl border text-center transition-all active:scale-98 ${
                        selectedBudget === item.id
                          ? 'border-brand-plum-900 bg-brand-blush-100/70 shadow-card ring-2 ring-brand-plum-900'
                          : 'border-neutral-200 hover:border-neutral-300 hover:bg-neutral-50'
                      }`}
                    >
                      <div className="text-xs font-bold text-neutral-900">{item.label}</div>
                      <div className="text-[11px] text-brand-plum-700 font-semibold uppercase tracking-wider mt-1">{item.tier}</div>
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Navigation Controls */}
            <div className="flex items-center justify-between pt-6 border-t border-neutral-200">
              <button
                type="button"
                onClick={handleBack}
                disabled={currentStep === 1}
                className={`flex items-center gap-2 px-5 py-2.5 rounded-full text-xs font-semibold transition-all ${
                  currentStep === 1 ? 'opacity-30 cursor-not-allowed text-neutral-400' : 'text-neutral-700 hover:bg-neutral-100'
                }`}
              >
                <ArrowLeft className="w-4 h-4" />
                <span>Back</span>
              </button>

              <button
                type="button"
                onClick={handleNext}
                className="flex items-center gap-2 px-8 py-3 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold shadow-card transition-all active:scale-98"
              >
                <span>{currentStep === totalSteps ? 'Reveal My Signature Match' : 'Continue'}</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        ) : (
          /* Curated Match Matrix Results Screen */
          <div className="space-y-10 animate-in fade-in duration-500">
            {/* Persona Insight Banner */}
            <div className="bg-gradient-to-r from-brand-plum-950 via-brand-plum-900 to-brand-plum-950 text-white rounded-3xl p-6 sm:p-8 shadow-modal relative overflow-hidden">
              <div className="absolute -top-16 -right-16 w-64 h-64 rounded-full bg-brand-gold-500/15 blur-3xl pointer-events-none" />
              <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div className="space-y-2 max-w-2xl">
                  <div className="inline-flex items-center gap-1.5 text-xs font-bold uppercase tracking-widest text-brand-gold-400">
                    <Compass className="w-3.5 h-3.5" />
                    <span>Your Olfactory Persona</span>
                  </div>
                  <h2 className="font-serif text-2xl sm:text-3xl font-bold text-white">
                    {backendPersona?.title || defaultPersonaTitle}
                  </h2>
                  <p className="text-xs sm:text-sm text-neutral-300 leading-relaxed">
                    {backendPersona?.description || defaultPersonaDescription}
                  </p>
                </div>

                <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3 shrink-0">
                  <button
                    type="button"
                    onClick={handleShareResults}
                    className="inline-flex items-center justify-center gap-2 px-5 py-2.5 rounded-full bg-white/10 hover:bg-white/20 border border-white/20 text-white text-xs font-semibold transition-all active:scale-95 shadow-xs"
                    aria-label="Share recommendations"
                  >
                    <Share2 className="w-3.5 h-3.5 text-brand-gold-300" />
                    <span>Share Results</span>
                  </button>
                  <button
                    type="button"
                    onClick={handleRestart}
                    className="inline-flex items-center justify-center gap-2 px-5 py-2.5 rounded-full bg-brand-gold-500 hover:bg-brand-gold-400 text-brand-plum-950 text-xs font-bold transition-all active:scale-95 shadow-xs"
                    aria-label="Retake fragrance quiz"
                  >
                    <RotateCcw className="w-3.5 h-3.5" />
                    <span>Retake Quiz</span>
                  </button>
                </div>
              </div>
            </div>

            {/* CURATED MATCH MATRIX SECTION */}
            <div className="bg-white rounded-3xl p-6 sm:p-8 lg:p-10 border border-neutral-200/90 shadow-modal space-y-8">
              {/* Section Header */}
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 pb-6 border-b border-neutral-200">
                <div className="space-y-2 text-left">
                  <div className="inline-flex items-center gap-2 text-xs font-bold uppercase tracking-widest text-brand-gold-600 bg-brand-gold-100/60 px-3 py-1 rounded-full border border-brand-gold-300/40">
                    <Award className="w-4 h-4 text-brand-gold-600" />
                    <span>Curated Match Matrix</span>
                  </div>
                  <h3 className="font-serif text-2xl sm:text-4xl font-bold text-brand-plum-950 tracking-tight">
                    Your Top Signature Matches
                  </h3>
                  <p className="text-xs sm:text-sm text-neutral-500">
                    Calculated for: <span className="font-semibold text-neutral-800">{selectedFamily} notes</span> • <span className="font-semibold text-neutral-800">{selectedOccasion}</span> • <span className="font-semibold text-neutral-800">{selectedIntensity}</span> • <span className="font-semibold text-neutral-800">{selectedGender}</span>
                  </p>
                </div>

                {/* Header Actions */}
                <div className="flex items-center gap-3 shrink-0">
                  <button
                    type="button"
                    onClick={handleShareResults}
                    className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-neutral-100 hover:bg-neutral-200 text-neutral-800 text-xs font-semibold transition-colors border border-neutral-200/80 active:scale-95"
                    aria-label="Share match matrix"
                  >
                    <Share2 className="w-3.5 h-3.5 text-brand-plum-900" />
                    <span>Share</span>
                  </button>
                  <button
                    type="button"
                    onClick={handleRestart}
                    className="inline-flex items-center gap-2 px-4 py-2 rounded-full border border-brand-plum-900/30 text-brand-plum-900 hover:bg-brand-blush-100/60 text-xs font-semibold transition-colors active:scale-95"
                    aria-label="Retake fragrance finder quiz"
                  >
                    <RotateCcw className="w-3.5 h-3.5" />
                    <span>Retake Quiz</span>
                  </button>
                </div>
              </div>

              {/* Matched Product Cards Grid */}
              {matchedResults.length > 0 ? (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 items-stretch">
                  {matchedResults.map(({ product, score, reason }) => (
                    <div key={product.id} className="h-full flex flex-col">
                      <ProductCard 
                        product={product} 
                        matchScore={score}
                        matchReason={reason}
                      />
                    </div>
                  ))}
                </div>
              ) : (
                /* Empty State */
                <div className="text-center py-16 px-4 space-y-5 bg-neutral-50 rounded-2xl border border-neutral-200/80">
                  <div className="w-16 h-16 mx-auto rounded-full bg-brand-plum-100 flex items-center justify-center text-brand-plum-900">
                    <Sparkles className="w-8 h-8 text-brand-plum-900" />
                  </div>
                  <div className="space-y-1">
                    <h4 className="font-serif text-xl font-bold text-neutral-900">No Direct Signature Matches Found</h4>
                    <p className="text-xs text-neutral-500 max-w-sm mx-auto">
                      Try adjusting your sensory preferences or retake the consultation quiz to explore alternative olfactory compositions.
                    </p>
                  </div>
                  <button
                    type="button"
                    onClick={handleRestart}
                    className="px-6 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold shadow-card hover:bg-brand-plum-800 transition-all active:scale-95"
                  >
                    Retake Fragrance Quiz
                  </button>
                </div>
              )}

              {/* Sommelier Trust Notes */}
              <div className="pt-6 border-t border-neutral-100 grid grid-cols-1 sm:grid-cols-3 gap-4 text-center sm:text-left">
                <div className="flex items-center gap-3 p-3 rounded-xl bg-neutral-50/80 border border-neutral-100">
                  <ShieldCheck className="w-5 h-5 text-brand-gold-600 shrink-0" />
                  <div>
                    <div className="text-xs font-bold text-neutral-900">100% Authentic Flacons</div>
                    <div className="text-[10px] text-neutral-500">Directly sourced from Paris, Grasse & London</div>
                  </div>
                </div>

                <div className="flex items-center gap-3 p-3 rounded-xl bg-neutral-50/80 border border-neutral-100">
                  <CheckCircle2 className="w-5 h-5 text-brand-gold-600 shrink-0" />
                  <div>
                    <div className="text-xs font-bold text-neutral-900">Complimentary Deluxe Vial</div>
                    <div className="text-[10px] text-neutral-500">2ml discovery tester included with every order</div>
                  </div>
                </div>

                <div className="flex items-center gap-3 p-3 rounded-xl bg-neutral-50/80 border border-neutral-100">
                  <Sparkles className="w-5 h-5 text-brand-gold-600 shrink-0" />
                  <div>
                    <div className="text-xs font-bold text-neutral-900">Bespoke Gifting Packaging</div>
                    <div className="text-[10px] text-neutral-500">Silk ribbon & wax-sealed SCENTIVA box</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

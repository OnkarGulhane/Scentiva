'use client';

import React, { useState, useEffect, useRef } from 'react';
import { 
  Sparkles, 
  X, 
  Send, 
  ShoppingBag, 
  ExternalLink, 
  HelpCircle, 
  ShieldCheck, 
  ChevronRight, 
  Loader2,
  Globe
} from 'lucide-react';
import { useStore } from '../../context/StoreContext';
import { AiApiService, AiAssistantProductItem } from '../../services/aiApiService';
import { useNavigate } from '../../hooks/useNavigation';

export type ScentivaLanguage = 'en' | 'mr' | 'hi';

interface Message {
  id: string;
  sender: 'ai' | 'user';
  text: string;
  category?: string;
  products?: AiAssistantProductItem[];
  actions?: Array<{ label: string; action: string; target: string }>;
  suggestedFollowups?: string[];
  timestamp: string;
}

interface ScentivaAiConciergeDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  initialTab?: 'assistant' | 'support';
}

const LANGUAGE_CONFIG: Record<ScentivaLanguage, {
  name: string;
  title: string;
  online: string;
  subtitle: string;
  tabAssistant: string;
  tabSupport: string;
  welcomeMessage: string;
  placeholderAssistant: string;
  placeholderSupport: string;
  assistantPrompts: string[];
  supportPrompts: string[];
  switchNotice: string;
  authoritativeMatches: string;
  addToBag: string;
  view: string;
  searching: string;
  supportTag: string;
}> = {
  mr: {
    name: 'मराठी',
    title: 'Scentiva AI Assistant',
    online: 'सक्रिय (Online)',
    subtitle: 'लक्झरी परफ्युम व ऑर्डर असिस्टंट',
    tabAssistant: 'शॉपिंग असिस्टंट',
    tabSupport: 'कस्टमर केअर व ऑर्डर्स',
    welcomeMessage: 'Scentiva AI Assistant मध्ये आपले स्वागत आहे! मी आपल्याला मराठी किंवा इंग्रजीमध्ये सर्वोत्तम परफ्युम निवडण्यात, सुगंधी नोट्स समजून घेण्यात आणि ऑर्डर ट्रॅकिंगमध्ये मदत करू शकतो. मी आपली काय सेवा करू?',
    placeholderAssistant: 'उदा. ऑफिससाठी फ्रेश आणि लॉन्ग-लास्टिंग परफ्युम ₹२५०० च्या आत...',
    placeholderSupport: 'उदा. माझी ऑर्डर कुठे आहे / रिटर्न पॉलिसी काय आहे...',
    assistantPrompts: [
      'Office sathi fresh perfume pahije, long-lasting under ₹3000',
      'Summer madhe daily use sathi fresh fragrance',
      'Date night sathi sweet woody perfume',
      'Extrait ani EDP madhe kay pharak aahe?'
    ],
    supportPrompts: [
      'What is your return policy?',
      'How does temperature-controlled shipping work?',
      'Are all perfumes 100% authentic?',
      'Where is my order?'
    ],
    switchNotice: 'भाषा बदलली: मराठी. मी आपल्याला कोणती मदत करू?',
    authoritativeMatches: 'निवडक परफ्युम पर्याय',
    addToBag: 'बॅगमध्ये ॲड करा',
    view: 'पहा',
    searching: 'ऑथेंटिक परफ्युम कॅटलॉग आणि स्टॉक तपासत आहे...',
    supportTag: 'इंग्लिश, मराठी व हिंदी सपोर्ट'
  },
  en: {
    name: 'English',
    title: 'Scentiva AI Assistant',
    online: 'Online',
    subtitle: 'Luxury Perfume & Order Assistant',
    tabAssistant: 'Shopping Assistant',
    tabSupport: 'Customer Care & Orders',
    welcomeMessage: 'Welcome to Scentiva AI Assistant. I can assist you with bespoke luxury fragrance recommendations, notes, or order care in English. How may I assist you today?',
    placeholderAssistant: 'e.g. Fresh long-lasting perfume for office under ₹2500...',
    placeholderSupport: 'e.g. Where is my order / What is the return policy?',
    assistantPrompts: [
      'Office perfume, fresh & long-lasting under ₹3000',
      'Summer daily fresh citrus fragrance',
      'Romantic date night woody vanilla perfume',
      'Difference between Extrait and EDP?'
    ],
    supportPrompts: [
      'What is your return policy?',
      'How does temperature-controlled shipping work?',
      'Are all perfumes 100% authentic?',
      'Where is my order?'
    ],
    switchNotice: 'Language switched to English. How may I help you today?',
    authoritativeMatches: 'Authoritative Matches',
    addToBag: 'Add to Bag',
    view: 'View',
    searching: 'Consulting olfactory archive & verifying stock...',
    supportTag: 'Supports English, Marathi & Hindi'
  },
  hi: {
    name: 'हिंदी',
    title: 'Scentiva AI Assistant',
    online: 'सक्रिय (Online)',
    subtitle: 'लक्जरी परफ्यूम व ऑर्डर असिस्टेंट',
    tabAssistant: 'शॉपिंग असिस्टेंट',
    tabSupport: 'कस्टमर केयर व ऑर्डर्स',
    welcomeMessage: 'Scentiva AI Assistant में आपका स्वागत है! मैं आपको हिंदी या इंग्लिश में बेहतरीन लग्जरी परफ्यूम चुनने, सुगंधित नोट्स समझने और ऑर्डर ट्रैकिंग में मदद कर सकता हूँ। मैं आपकी क्या सहायता करूँ?',
    placeholderAssistant: 'उदा. ऑफिस के लिए फ्रेश और लॉन्ग-लास्टिंग परफ्यूम ₹2500 के अंदर...',
    placeholderSupport: 'उदा. मेरा ऑर्डर कहाँ है / रिटर्न पॉलिसी क्या है...',
    assistantPrompts: [
      'Office ke liye fresh perfume chahiye, long-lasting under ₹3000',
      'Garmiyon ke liye daily fresh citrus fragrance',
      'Date night ke liye sweet vanilla woody perfume',
      'Extrait aur EDP mein kya difference hai?'
    ],
    supportPrompts: [
      'What is your return policy?',
      'Packaging aur delivery kaise hoti hai?',
      'Kya sabhi perfumes 100% original hain?',
      'Where is my order?'
    ],
    switchNotice: 'भाषा बदली गई: हिंदी। मैं आपकी क्या सहायता कर सकता हूँ?',
    authoritativeMatches: 'अनुशंसित परफ्यूम विकल्प',
    addToBag: 'बैग में जोड़ें',
    view: 'देखें',
    searching: 'प्रामाणिक परफ्यूम कैटलॉग और स्टॉक जांच रहे हैं...',
    supportTag: 'इंग्लिश, मराठी और हिंदी सपोर्ट'
  }
};

export const ScentivaAiConciergeDrawer: React.FC<ScentivaAiConciergeDrawerProps> = ({
  isOpen,
  onClose,
  initialTab = 'assistant'
}) => {
  const { addToCart, showToast, formatPrice, isLoggedIn, currentUser, cart, wishlist, products } = useStore();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<'assistant' | 'support'>(initialTab);
  const [language, setLanguage] = useState<ScentivaLanguage>('mr');
  const [inputQuery, setInputQuery] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const currentLangConfig = LANGUAGE_CONFIG[language];

  const [messages, setMessages] = useState<Message[]>([
    {
      id: 'welcome-1',
      sender: 'ai',
      text: LANGUAGE_CONFIG.mr.welcomeMessage,
      suggestedFollowups: LANGUAGE_CONFIG.mr.assistantPrompts,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    }
  ]);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (initialTab) {
      setActiveTab(initialTab);
    }
  }, [initialTab]);

  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
      scrollToBottom();
    } else {
      document.body.style.overflow = '';
    }
    return () => {
      document.body.style.overflow = '';
    };
  }, [isOpen, messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const handleLanguageChange = (newLang: ScentivaLanguage) => {
    if (newLang === language) return;
    setLanguage(newLang);
    const cfg = LANGUAGE_CONFIG[newLang];
    const switchMsg: Message = {
      id: `lang-switch-${Date.now()}`,
      sender: 'ai',
      text: cfg.switchNotice,
      suggestedFollowups: activeTab === 'assistant' ? cfg.assistantPrompts : cfg.supportPrompts,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };
    setMessages(prev => [...prev, switchMsg]);
  };

  const handleSendMessage = async (queryText?: string) => {
    const textToSend = queryText || inputQuery;
    if (!textToSend.trim() || isLoading) return;

    const userMessage: Message = {
      id: `user-${Date.now()}`,
      sender: 'user',
      text: textToSend.trim(),
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages(prev => [...prev, userMessage]);
    setInputQuery('');
    setIsLoading(true);

    try {
      if (activeTab === 'assistant') {
        const response = await AiApiService.shoppingAssistantChat({
          query: textToSend.trim(),
          userId: currentUser?.id ? String(currentUser.id) : undefined,
          userContext: {
            isLoggedIn,
            language,
            cartItemCount: cart.length,
            wishlistItemCount: wishlist.length
          }
        });

        // Map recommended products with catalog fallbacks if needed
        let recProducts: AiAssistantProductItem[] = response.recommendedProducts || [];
        if (recProducts.length === 0 && products.length > 0) {
          // Find matching products from client store
          const q = textToSend.toLowerCase();
          recProducts = products.filter(p => {
            const matchName = p.name.toLowerCase().includes(q);
            const matchFamily = p.fragranceFamilies?.some(f => q.includes(f.toLowerCase()));
            const matchBrand = p.brandName?.toLowerCase().includes(q);
            return matchName || matchFamily || matchBrand;
          }).slice(0, 3).map(p => ({
            id: p.id,
            productId: p.id,
            name: p.name,
            brandName: p.brandName || 'Scentiva Maison',
            price: p.variants?.[0]?.price || 2500,
            inStock: (p.stock ?? 1) > 0,
            reason: `Exquisite ${p.fragranceFamilies?.join(', ') || 'niche'} composition with authentic artisan craft.`,
            primaryImageUrl: p.images?.[0] || '',
            slug: p.slug
          }));
        }

        const aiMessage: Message = {
          id: `ai-${Date.now()}`,
          sender: 'ai',
          text: response.message || (
            language === 'mr' 
              ? 'आपल्या आवडीनुसार आम्ही खालील निवडक परफ्युम पर्याय आणले आहेत:' 
              : language === 'hi' 
              ? 'आपकी पसंद के अनुसार हमने निम्नलिखित चुनिंदा परफ्यूम विकल्प तैयार किए हैं:' 
              : 'I have curated these exquisite compositions tailored to your olfactory request:'
          ),
          products: recProducts,
          suggestedFollowups: response.suggestedFollowups || currentLangConfig.assistantPrompts.slice(0, 3),
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        };
        setMessages(prev => [...prev, aiMessage]);
      } else {
        // Customer Support Mode
        const supportRes = await AiApiService.customerSupportChat({
          message: textToSend.trim(),
          userId: currentUser?.id ? String(currentUser.id) : undefined,
          authToken: typeof window !== 'undefined' ? localStorage.getItem('scentiva_access_token') || undefined : undefined
        });

        const aiMessage: Message = {
          id: `ai-${Date.now()}`,
          sender: 'ai',
          text: supportRes.message,
          category: supportRes.category,
          actions: supportRes.suggestedActions,
          suggestedFollowups: currentLangConfig.supportPrompts.slice(0, 3),
          timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        };
        setMessages(prev => [...prev, aiMessage]);
      }
    } catch (err) {
      const errorMessage: Message = {
        id: `ai-err-${Date.now()}`,
        sender: 'ai',
        text: language === 'mr'
          ? "माफ करा, असिस्टंट सर्व्हिस सध्या व्यस्त आहे. कृपया पुन्हा प्रयत्न करा."
          : language === 'hi'
          ? "क्षमा करें, असिस्टेंट सेवा अभी व्यस्त है। कृपया पुनः प्रयास करें।"
          : "I apologize, our luxury assistant is momentarily experiencing high demand. Please try again.",
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleAddToCart = (productItem: AiAssistantProductItem) => {
    const fullProduct = products.find(p => String(p.id) === String(productItem.productId) || p.slug === productItem.slug);
    if (fullProduct) {
      addToCart(fullProduct, undefined, 1);
      showToast(`${fullProduct.name} ${language === 'mr' ? 'बॅगमध्ये ॲड केले' : language === 'hi' ? 'बैग में जोड़ा गया' : 'added to your bag'}`, 'success');
    } else {
      showToast(`Navigating to ${productItem.name}`, 'info');
      onClose();
      navigate(`/product/${productItem.slug || productItem.productId}`);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-hidden" role="dialog" aria-modal="true">
      {/* Backdrop */}
      <div 
        className="fixed inset-0 bg-neutral-950/70 backdrop-blur-sm transition-opacity animate-fade-in"
        onClick={onClose}
      />

      <div className="fixed inset-y-0 right-0 max-w-full flex pl-10">
        <div className="w-screen max-w-md md:max-w-lg bg-neutral-900 border-l border-neutral-800 text-neutral-100 flex flex-col shadow-2xl relative">
          
          {/* Header */}
          <div className="p-4 md:p-5 border-b border-neutral-800 bg-neutral-950/90 flex flex-col gap-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-linear-to-br from-brand-gold-500/20 to-brand-plum-900/40 border border-brand-gold-500/40 flex items-center justify-center text-brand-gold-400">
                  <Sparkles className="w-5 h-5 animate-pulse" />
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <h2 className="text-sm font-semibold tracking-wider uppercase text-brand-gold-400">
                      {currentLangConfig.title}
                    </h2>
                    <span className="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] font-medium bg-brand-gold-500/10 text-brand-gold-300 border border-brand-gold-500/20">
                      {currentLangConfig.online}
                    </span>
                  </div>
                  <p className="text-xs text-neutral-400">{currentLangConfig.subtitle}</p>
                </div>
              </div>

              <button
                onClick={onClose}
                className="p-2 rounded-full hover:bg-neutral-800 text-neutral-400 hover:text-white transition-colors"
                aria-label="Close Assistant"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Language Selector Bar */}
            <div className="flex items-center justify-between pt-1 border-t border-neutral-800/80">
              <div className="flex items-center gap-1.5 text-xs text-neutral-400">
                <Globe className="w-3.5 h-3.5 text-brand-gold-400" />
                <span className="text-[11px] font-medium">Language / भाषा:</span>
              </div>
              <div className="flex items-center gap-1 bg-neutral-950 p-0.5 rounded-lg border border-neutral-800">
                {(['en', 'mr', 'hi'] as const).map((lang) => (
                  <button
                    key={lang}
                    onClick={() => handleLanguageChange(lang)}
                    className={`px-2.5 py-1 rounded-md text-[11px] font-semibold transition-all ${
                      language === lang
                        ? 'bg-brand-gold-500 text-neutral-950 shadow-xs'
                        : 'text-neutral-400 hover:text-neutral-200'
                    }`}
                  >
                    {lang === 'en' ? 'English' : lang === 'mr' ? 'मराठी' : 'हिंदी'}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Mode Switcher Tabs */}
          <div className="grid grid-cols-2 p-1.5 bg-neutral-950/60 border-b border-neutral-800/80 text-xs font-medium">
            <button
              onClick={() => setActiveTab('assistant')}
              className={`py-2 px-3 rounded-md flex items-center justify-center gap-2 transition-all ${
                activeTab === 'assistant'
                  ? 'bg-neutral-800 text-brand-gold-300 shadow-sm border border-brand-gold-500/20'
                  : 'text-neutral-400 hover:text-neutral-200'
              }`}
            >
              <Sparkles className="w-3.5 h-3.5" />
              <span>{currentLangConfig.tabAssistant}</span>
            </button>

            <button
              onClick={() => setActiveTab('support')}
              className={`py-2 px-3 rounded-md flex items-center justify-center gap-2 transition-all ${
                activeTab === 'support'
                  ? 'bg-neutral-800 text-brand-gold-300 shadow-sm border border-brand-gold-500/20'
                  : 'text-neutral-400 hover:text-neutral-200'
              }`}
            >
              <HelpCircle className="w-3.5 h-3.5" />
              <span>{currentLangConfig.tabSupport}</span>
            </button>
          </div>

          {/* Chat Messages Body */}
          <div className="flex-1 overflow-y-auto p-4 md:p-5 space-y-4">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex flex-col ${msg.sender === 'user' ? 'items-end' : 'items-start'}`}
              >
                <div
                  className={`max-w-[88%] rounded-2xl p-3.5 text-sm leading-relaxed ${
                    msg.sender === 'user'
                      ? 'bg-brand-plum-900/90 text-white rounded-tr-xs border border-brand-plum-700/50'
                      : 'bg-neutral-800/90 text-neutral-200 rounded-tl-xs border border-neutral-700/50'
                  }`}
                >
                  <p className="whitespace-pre-line">{msg.text}</p>

                  {/* Product Recommendation Cards */}
                  {msg.products && msg.products.length > 0 && (
                    <div className="mt-3.5 space-y-2.5">
                      <p className="text-[11px] font-semibold uppercase tracking-wider text-brand-gold-400">
                        {currentLangConfig.authoritativeMatches} ({msg.products.length})
                      </p>
                      {msg.products.map((p, idx) => (
                        <div
                          key={idx}
                          className="bg-neutral-900/90 border border-neutral-700/80 rounded-xl p-3 flex flex-col gap-2 hover:border-brand-gold-500/50 transition-colors"
                        >
                          <div className="flex items-start justify-between gap-2">
                            <div>
                              <span className="text-[10px] uppercase font-bold tracking-widest text-brand-gold-400">
                                {p.brandName}
                              </span>
                              <h4 className="text-sm font-serif font-medium text-white">{p.name}</h4>
                            </div>
                            <span className="text-xs font-semibold text-brand-gold-300">
                              {formatPrice(p.price || p.effectivePrice || 2500)}
                            </span>
                          </div>

                          {p.reason && (
                            <p className="text-xs text-neutral-300 italic bg-neutral-950/50 p-2 rounded-lg border border-neutral-800/60">
                              "{p.reason}"
                            </p>
                          )}

                          <div className="flex items-center gap-2 pt-1">
                            <button
                              onClick={() => handleAddToCart(p)}
                              className="flex-1 py-1.5 px-3 bg-brand-gold-500/20 hover:bg-brand-gold-500/30 text-brand-gold-300 border border-brand-gold-500/40 rounded-lg text-xs font-medium flex items-center justify-center gap-1.5 transition-colors"
                            >
                              <ShoppingBag className="w-3.5 h-3.5" />
                              <span>{currentLangConfig.addToBag}</span>
                            </button>
                            <button
                              onClick={() => {
                                onClose();
                                navigate(`/product/${p.slug || p.productId}`);
                              }}
                              className="py-1.5 px-3 bg-neutral-800 hover:bg-neutral-700 text-neutral-300 rounded-lg text-xs font-medium flex items-center justify-center gap-1 transition-colors"
                            >
                              <span>{currentLangConfig.view}</span>
                              <ExternalLink className="w-3 h-3" />
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}

                  {/* Actions / Policy Links */}
                  {msg.actions && msg.actions.length > 0 && (
                    <div className="mt-3 flex flex-wrap gap-2">
                      {msg.actions.map((act, i) => (
                        <button
                          key={i}
                          onClick={() => {
                            onClose();
                            navigate(act.target);
                          }}
                          className="py-1 px-2.5 bg-brand-gold-500/10 hover:bg-brand-gold-500/20 border border-brand-gold-500/30 text-brand-gold-300 rounded-md text-xs flex items-center gap-1.5 transition-colors"
                        >
                          <span>{act.label}</span>
                          <ChevronRight className="w-3 h-3" />
                        </button>
                      ))}
                    </div>
                  )}
                </div>
                <span className="text-[10px] text-neutral-500 mt-1 px-1">{msg.timestamp}</span>
              </div>
            ))}

            {isLoading && (
              <div className="flex items-center gap-2 text-xs text-neutral-400 italic p-3 bg-neutral-800/40 rounded-xl max-w-[75%] border border-neutral-700/30 animate-pulse">
                <Loader2 className="w-3.5 h-3.5 animate-spin text-brand-gold-400" />
                <span>{currentLangConfig.searching}</span>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Quick Suggestion Chips */}
          <div className="px-4 py-2 bg-neutral-950/40 border-t border-neutral-800/60 overflow-x-auto no-scrollbar flex items-center gap-2">
            {(activeTab === 'assistant' ? currentLangConfig.assistantPrompts : currentLangConfig.supportPrompts).map((prompt, idx) => (
              <button
                key={idx}
                onClick={() => handleSendMessage(prompt)}
                disabled={isLoading}
                className="whitespace-nowrap px-3 py-1 bg-neutral-800/70 hover:bg-neutral-700/80 text-neutral-300 hover:text-brand-gold-300 text-xs rounded-full border border-neutral-700/60 transition-colors shrink-0"
              >
                {prompt}
              </button>
            ))}
          </div>

          {/* Input Footer */}
          <div className="p-3.5 md:p-4 bg-neutral-950 border-t border-neutral-800 pb-safe">
            <form
              onSubmit={(e) => {
                e.preventDefault();
                handleSendMessage();
              }}
              className="flex items-center gap-2"
            >
              <input
                type="text"
                value={inputQuery}
                onChange={(e) => setInputQuery(e.target.value)}
                placeholder={
                  activeTab === 'assistant'
                    ? currentLangConfig.placeholderAssistant
                    : currentLangConfig.placeholderSupport
                }
                className="flex-1 bg-neutral-800/90 border border-neutral-700 rounded-xl px-4 py-2.5 text-xs md:text-sm text-white placeholder-neutral-500 focus:outline-none focus:border-brand-gold-500 focus:ring-1 focus:ring-brand-gold-500/50"
              />
              <button
                type="submit"
                disabled={!inputQuery.trim() || isLoading}
                className="p-2.5 bg-brand-gold-500 hover:bg-brand-gold-400 disabled:opacity-40 text-neutral-950 rounded-xl font-medium transition-all shadow-md flex items-center justify-center"
                aria-label="Send query"
              >
                <Send className="w-4 h-4" />
              </button>
            </form>
            <div className="mt-2 flex items-center justify-between text-[10px] text-neutral-500 px-1">
              <span>{currentLangConfig.supportTag}</span>
              <span className="flex items-center gap-1 text-brand-gold-500/70">
                <ShieldCheck className="w-3 h-3" />
                Verified Scentiva Catalog
              </span>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

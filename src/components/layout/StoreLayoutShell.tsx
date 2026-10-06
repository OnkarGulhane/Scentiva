'use client';

import React, { useState, useEffect } from 'react';
import { usePathname } from 'next/navigation';
import { AnnouncementBar } from './AnnouncementBar';
import { Navbar } from './Navbar';
import { Footer } from './Footer';
import { MobileBottomNav } from './MobileBottomNav';
import { ScentivaAiConciergeDrawer } from '../ai/ScentivaAiConciergeDrawer';
import { Sparkles } from 'lucide-react';

interface StoreLayoutShellProps {
  children: React.ReactNode;
}

export const StoreLayoutShell: React.FC<StoreLayoutShellProps> = ({ children }) => {
  const pathname = usePathname() || '/';
  const isAdminRoute = pathname.startsWith('/admin');
  const [isAiOpen, setIsAiOpen] = useState(false);
  const [aiTab, setAiTab] = useState<'assistant' | 'support'>('assistant');

  useEffect(() => {
    const handleOpenAi = (e: any) => {
      if (e.detail?.tab) {
        setAiTab(e.detail.tab);
      }
      setIsAiOpen(true);
    };

    window.addEventListener('open-scentiva-ai', handleOpenAi);
    return () => {
      window.removeEventListener('open-scentiva-ai', handleOpenAi);
    };
  }, []);

  if (isAdminRoute) {
    return <>{children}</>;
  }

  return (
    <div className="flex flex-col min-h-screen w-full relative">
      <AnnouncementBar />
      <Navbar />
      <main className="flex-1 w-full">
        {children}
      </main>
      <Footer />
      <MobileBottomNav />

      {/* Floating Scentiva Luxury AI Assistant Trigger */}
      <div className="fixed bottom-20 right-4 lg:bottom-8 lg:right-8 z-40">
        <button
          onClick={() => {
            setAiTab('assistant');
            setIsAiOpen(true);
          }}
          className="group relative flex items-center gap-2.5 px-4 py-3 bg-neutral-900/95 hover:bg-neutral-800 text-white rounded-full border border-brand-gold-500/40 shadow-2xl backdrop-blur-md transition-all duration-300 hover:scale-105 hover:border-brand-gold-400 focus:outline-none focus:ring-2 focus:ring-brand-gold-500/50"
          aria-label="Open Scentiva AI Assistant"
        >
          <div className="w-6 h-6 rounded-full bg-linear-to-br from-brand-gold-500 to-brand-gold-400 flex items-center justify-center text-neutral-950 font-bold shadow-xs">
            <Sparkles className="w-3.5 h-3.5 animate-pulse" />
          </div>
          <div className="flex flex-col items-start pr-1">
            <span className="text-[11px] font-semibold tracking-wider uppercase text-brand-gold-300">
              AI Assistant
            </span>
            <span className="text-[9px] text-neutral-400 hidden sm:inline">Perfume & Order Help</span>
          </div>
        </button>
      </div>

      {/* AI Concierge Drawer Modal */}
      <ScentivaAiConciergeDrawer
        isOpen={isAiOpen}
        onClose={() => setIsAiOpen(false)}
        initialTab={aiTab}
      />
    </div>
  );
};



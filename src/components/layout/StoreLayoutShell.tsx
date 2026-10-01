'use client';

import React, { Suspense } from 'react';
import { usePathname } from 'next/navigation';
import { AnnouncementBar } from './AnnouncementBar';
import { Navbar } from './Navbar';
import { Footer } from './Footer';
import { MobileBottomNav } from './MobileBottomNav';

interface StoreLayoutShellProps {
  children: React.ReactNode;
}

export const StoreLayoutShell: React.FC<StoreLayoutShellProps> = ({ children }) => {
  const pathname = usePathname() || '/';
  const isAdminRoute = pathname.startsWith('/admin');

  if (isAdminRoute) {
    return (
      <Suspense fallback={<div className="min-h-screen bg-neutral-100 animate-pulse" />}>
        {children}
      </Suspense>
    );
  }

  return (
    <div className="flex flex-col min-h-screen w-full">
      <AnnouncementBar />
      <Suspense fallback={<header className="h-20 bg-white" />}>
        <Navbar />
      </Suspense>
      <main className="flex-1 w-full">
        <Suspense fallback={<div className="min-h-[50vh] flex items-center justify-center p-8"><div className="w-8 h-8 rounded-full border-2 border-brand-plum-900 border-t-transparent animate-spin" /></div>}>
          {children}
        </Suspense>
      </main>
      <Footer />
      <Suspense fallback={null}>
        <MobileBottomNav />
      </Suspense>
    </div>
  );
};


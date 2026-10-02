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
    return <>{children}</>;
  }

  return (
    <div className="flex flex-col min-h-screen w-full">
      <AnnouncementBar />
      <Navbar />
      <main className="flex-1 w-full">
        {children}
      </main>
      <Footer />
      <MobileBottomNav />
    </div>
  );
};


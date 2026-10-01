'use client';

import React, { useEffect } from 'react';
import { StoreProvider } from '../../context/StoreContext';
import { CartDrawer } from '../layout/CartDrawer';
import { QuickViewModal } from '../common/QuickViewModal';
import { ToastContainer } from '../common/Toast';
import { initSmoothScroll, destroySmoothScroll } from '../../motion/smoothScroll';

interface ClientProvidersProps {
  children: React.ReactNode;
}

export const ClientProviders: React.FC<ClientProvidersProps> = ({ children }) => {
  useEffect(() => {
    const lenis = initSmoothScroll();
    return () => {
      destroySmoothScroll();
    };
  }, []);

  return (
    <StoreProvider>
      {children}
      <CartDrawer />
      <QuickViewModal />
      <ToastContainer />
    </StoreProvider>
  );
};

'use client';

import React from 'react';
import { usePathname } from 'next/navigation';
import { AdminLayout } from '@/views/admin/AdminLayout';

export default function AdminAppLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const pathname = usePathname();

  // If on login page, render full-screen without admin navigation sidebar
  if (pathname === '/admin/login') {
    return <>{children}</>;
  }

  return <AdminLayout>{children}</AdminLayout>;
}

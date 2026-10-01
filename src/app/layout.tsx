import type { Metadata, Viewport } from 'next';
import '../styles/tokens.css';
import '../styles/index.css';
import { ClientProviders } from '@/components/providers/ClientProviders';
import { StoreLayoutShell } from '@/components/layout/StoreLayoutShell';

export const metadata: Metadata = {
  title: 'SCENTIVA — Luxury Multi-Brand Fragrance Marketplace',
  description: 'Discover luxury, niche, and designer fragrances from world-renowned perfume houses. Find your signature scent with SCENTIVA — Since 2026.',
  keywords: ['luxury perfume', 'niche fragrance', 'designer cologne', 'parfum', 'haute parfumerie', 'SCENTIVA'],
  authors: [{ name: 'SCENTIVA Haute Parfumerie' }],
  icons: {
    icon: '/assets/scentiva-emblem.svg',
    shortcut: '/assets/scentiva-emblem.svg',
    apple: '/assets/scentiva-emblem.svg',
  },
  openGraph: {
    title: 'SCENTIVA — Luxury Multi-Brand Fragrance Marketplace',
    description: 'Explore authentic luxury, designer, and artisanal fragrances curated under one prestigious address.',
    url: 'https://scentiva.luxury',
    siteName: 'SCENTIVA',
    type: 'website',
  },
};

export const viewport: Viewport = {
  width: 'device-width',
  initialScale: 1,
  maximumScale: 5,
  viewportFit: 'cover',
  themeColor: '#321027',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="w-full">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="anonymous" />
        <link
          href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,400;0,500;0,600;0,700;1,400;1,600&family=Inter:wght@300;400;500;600;700&display=swap"
          rel="stylesheet"
        />
      </head>
      <body className="bg-neutral-50 text-neutral-800 font-sans antialiased selection:bg-brand-plum-700 selection:text-brand-blush-100 min-h-screen w-full overflow-x-hidden">
        <ClientProviders>
          <StoreLayoutShell>{children}</StoreLayoutShell>
        </ClientProviders>
      </body>
    </html>
  );
}

# SCENTIVA — Luxury Multi-Brand Fragrance Marketplace
> **SINCE 2026** • Haute Parfumerie & Niche Olfactory Vault  
> Production-Grade Next.js 14 App Router + TypeScript + Tailwind CSS Frontend

SCENTIVA is a production-grade luxury multi-brand fragrance marketplace frontend engineered with Next.js 14 (App Router), TypeScript, Tailwind CSS, Three.js / React Three Fiber, GSAP ScrollTrigger, and Lenis smooth scrolling. Built for seamless future integration with a Spring Boot + PostgreSQL microservices backend.

---

## 🌟 Visual & Design Tokens

- **Brand Color Palette:**
  - `Plum 950`: `#321027` (Deepest brand surface, footer, dark hero)
  - `Plum 900`: `#451333` (Primary brand identity)
  - `Plum 800`: `#5B1B43` (Hover & dark active surfaces)
  - `Plum 700`: `#742653` (Secondary brand surface)
  - `Blush 300`: `#E9B7D8` (Logo accent on dark surfaces)
  - `Blush 200`: `#F2D2E7` (Soft accent surface)
  - `Blush 100`: `#FAEAF4` (Subtle blush background)
  - `Rose 500`: `#B85B88` (Accent actions & badge highlights)
  - `Gold 500`: `#C7A66A` (Prestige highlights & luxury badges)
  - `Gold 100`: `#F5EBD7` (Luxury accent background)
  - `Canvas Background`: `#FAF8F7`
- **Typography:**
  - Editorial & Headings: `Cormorant Garamond` (Google Serif)
  - UI & Controls: `Inter` (Sans-serif with tabular numerals)
- **3D Flacon & Motion:**
  - Real-time 3D WebGL Perfume Flacon with cursor parallax tilt, golden botanical scent sparks, glass transmission, and liquid depth.
  - Graceful static CSS/SVG fallback for low-power and mobile devices.
  - Lenis smooth scrolling with GSAP ScrollTrigger orchestrations.

---

## 🏗️ Architecture & Project Structure

```text
src/
  app/                          # Next.js 14 App Router (45 routes + metadata + sitemap + robots)
    layout.tsx                  # Root Shell with fonts, ClientProviders, StoreLayoutShell
    page.tsx                    # Luxury Homepage
    shop/                       # Catalog & Dynamic Filters
    search/                     # Autocomplete & Faceted Search
    brands/                     # Brand Directory
      [slug]/                   # Dynamic Brand Detail
    categories/
      [slug]/                   # Dynamic Category Collection
    product/
      [slug]/                   # Dynamic Product Detail (JSON-LD + OpenGraph)
    find-your-scent/            # Interactive Scent Matchmaker
      results/                  # Profiler Results
    wishlist/                   # Saved Fragrances Vault
    cart/                       # Bag & Discount Calculation
    checkout/                   # Multi-Step Demo Checkout
      payment/                  # Payment Selection (UPI, Card, Net Banking, COD)
    order/
      success/                  # Order Confirmation & Confetti
    account/                    # Connoisseur Membership & Loyalty
      sign-in/                  # Authentication Gateway
      sign-up/                  # SCENTIVA Society Registration
      orders/                   # Order History
        [id]/                   # Dynamic Order Tracking
      addresses/                # Saved Addresses Book
    offers/                     # Privileges & Coupon Codes
    gifts/                      # Curated Discovery Coffrets
    stories/                    # Editorial Journal & Masterclasses
      [slug]/                   # Dynamic Article
    help/                       # Concierge & FAQ
    contact/                    # Concierge Inquiries
    policies/                   # Shipping, Returns, Privacy, Terms
    admin/                      # Complete Operational Admin Console
      login/
      products/
        new/
      brands/
      categories/
      inventory/
      orders/
      customers/
      promotions/
      content/
      reports/
      settings/
    api/                        # Next.js Route Handlers
      health/
      products/
    sitemap.ts                  # Dynamic SEO Sitemap
    robots.ts                   # Crawler Directives

  views/                        # Reusable Storefront & Admin View Components
  components/                   # UI, Home, Product, Layout, Common & Motion Components
  context/                      # StoreContext (Cart, Wishlist, Orders, Auth, Toasts)
  data/                         # Curated Media Catalog, Verified Fragrance Data & Stories
  hooks/                        # useNavigation, useDebounce, useMediaQuery
  lib/api/                      # Typed ApiClient gateway for Spring Boot microservices
  motion/                       # GSAP ScrollTrigger, Lenis smooth scrolling
  services/                     # Domain Services (Product, Brand, Category, Order, Recommendation)
  styles/                       # Design Tokens & Tailwind CSS
  types/                        # Strict TypeScript Domain Interfaces
```

---

## ⚡ Quick Start & Development

### Prerequisites
- Node.js >= 18.17.0
- npm >= 9.0.0

### Run Development Server
```bash
npm run dev
```
Open [http://localhost:3000](http://localhost:3000) in your browser.

### Build for Production
```bash
npm run build
```
Generates optimized static pages, server bundles, dynamic metadata, and build traces.

### Start Production Server
```bash
npm start
```

---

## 🔌 Spring Boot & PostgreSQL Microservices Readiness

The frontend architecture includes clean service abstraction layers (`src/services/`, `src/lib/api/`):
- `ProductService`, `BrandService`, `CategoryService`, `OrderService`, `CustomerService`, `PromotionService`, `ContentService`.
- `RecommendationService`: Features `IFragranceRecommendationService` with `DeterministicRecommendationService` engine, architected for drop-in replacement by Spring Boot AI / Vector embedding recommendation endpoints.
- `ApiClient`: Configured with `NEXT_PUBLIC_API_URL` environment variable support and error handling.

---

## 📄 License & Ownership
Copyright © 2026 SCENTIVA Haute Parfumerie. All rights reserved.

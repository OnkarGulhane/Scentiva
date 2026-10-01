# SCENTIVA — Repository Memory & Architecture Blueprint
> **Brand:** SCENTIVA — "SINCE 2026"  
> **Type:** Haute Parfumerie & Multi-Brand Fragrance Marketplace  
> **Target Form Factor:** Mobile-first customer storefront + Desktop web experience + Operational admin console  
> **Architecture:** Next.js 14 App Router (SSG/SSR) + TypeScript 5.5 + Tailwind CSS + Three.js / R3F + GSAP Motion  
> **Last Updated:** 2026-10-01  

---

## 1. System Architecture & Tech Stack

- **Framework:** Next.js 14 (`next` v14.2.24, React 18.3.1, React DOM)
- **Routing Engine:** Next.js 14 App Router (`src/app/` with 45 static/dynamic routes)
- **Type System:** TypeScript 5.5 (`strict: true`, `noEmit: true`, path alias `@/* -> ./src/*`)
- **Styling:** TailwindCSS v3.4 + Custom CSS Design Tokens ([src/styles/tokens.css](file:///e:/Scentiva/src/styles/tokens.css), [src/styles/index.css](file:///e:/Scentiva/src/styles/index.css))
- **Typography:** Google Fonts `Cormorant Garamond` (display/editorial serif) + `Inter` (UI/controls/pricing)
- **3D & WebGL Engine:** Three.js + `@react-three/fiber` + `@react-three/drei` ([src/components/home/Hero3DCanvas.tsx](file:///e:/Scentiva/src/components/home/Hero3DCanvas.tsx)) with 360° `OrbitControls` drag rotation, `Float` levitation, golden mist `Sparkles`, and WebGL detection with luxury CSS fallback.
- **Motion Architecture:** Lenis smooth scrolling singleton ([src/motion/smoothScroll.ts](file:///e:/Scentiva/src/motion/smoothScroll.ts)) + GSAP v3 + ScrollTrigger + Scoped lifecycle context hooks ([src/motion/gsapContext.ts](file:///e:/Scentiva/src/motion/gsapContext.ts), [src/motion/scrollReveal.ts](file:///e:/Scentiva/src/motion/scrollReveal.ts), [src/motion/motionTokens.ts](file:///e:/Scentiva/src/motion/motionTokens.ts)) + `canvas-confetti`.
- **Backend Readiness & API Abstraction:**
  - Typed ApiClient Gateway ([src/lib/api/apiClient.ts](file:///e:/Scentiva/src/lib/api/apiClient.ts)) with `NEXT_PUBLIC_API_URL` environment configuration, abort timeouts, and retry handlers.
  - Dedicated Domain Services in `src/services/` (`ProductService`, `BrandService`, `CategoryService`, `SearchService`, `OrderService`, `InventoryService`, `PromotionService`, `CustomerService`, `ContentService`, `RecommendationService`, `AnalyticsService`, `FeatureFlags`).
  - Standardized AI Recommendation Interface (`IFragranceRecommendationService` with `DeterministicRecommendationService` in [src/services/recommendationService.ts](file:///e:/Scentiva/src/services/recommendationService.ts)).
- **State & Storage:** React Context API ([src/context/StoreContext.tsx](file:///e:/Scentiva/src/context/StoreContext.tsx)) with two-phase SSR-safe hydration, demo authentication, order pipeline, and LocalStorage persistence.

---

## 2. Design System & Token Specifications

### Color Palette (Nocturne Gallery Identity)
| Token Name | HEX | Usage |
|---|---|---|
| `color.brand.plum.950` | `#321027` | Deepest brand surface, dark hero background, footer, typography |
| `color.brand.plum.900` | `#451333` | Primary brand background, primary CTA buttons, emblem base |
| `color.brand.plum.800` | `#5B1B43` | Active/hover dark brand surfaces |
| `color.brand.plum.700` | `#742653` | Accent panels, selection indicators |
| `color.brand.blush.300` | `#E9B7D8` | Logo accent, badge highlights on dark surfaces |
| `color.brand.blush.200` | `#F2D2E7` | Soft accent background |
| `color.brand.blush.100` | `#FAEAF4` | Subtle blush pill and surface fill |
| `color.brand.rose.500` | `#B85B88` | Accent actions, category eyebrows, wishlist active state |
| `color.brand.gold.500` | `#C7A66A` | Prestige badges, ratings, highlights, secondary buy CTAs |
| `color.brand.gold.100` | `#F5EBD7` | Soft gold badge background |
| `color.neutral.50` | `#FAF8F7` | Main canvas light page background |
| `color.neutral.100` | `#F2EEEC` | Secondary subtle container fill |
| `color.neutral.200` | `#E4DCDA` | Card borders, dividers |
| `color.neutral.800` | `#342C30` | Primary readable text |
| `color.neutral.950` | `#1D171B` | Strong headings, dark modals |

---

## 3. Route & Screen Inventory (Next.js App Router — 45 Routes)

### Customer Storefront (30 Routes)
1. `/` — [src/app/page.tsx](file:///e:/Scentiva/src/app/page.tsx): 3D flacon hero, brand ticker, category cards, best sellers, fragrance quiz teaser, journal, trust badges.
2. `/shop` — [src/app/shop/page.tsx](file:///e:/Scentiva/src/app/shop/page.tsx): Full fragrance catalog with faceted filters, concentration switcher, price slider.
3. `/search` — [src/app/search/page.tsx](file:///e:/Scentiva/src/app/search/page.tsx): Autocomplete search route with NLP query parsing and zero-result recovery.
4. `/brands` — [src/app/brands/page.tsx](file:///e:/Scentiva/src/app/brands/page.tsx): Brand directory with tier filters.
5. `/brands/[slug]` — [src/app/brands/[slug]/page.tsx](file:///e:/Scentiva/src/app/brands/[slug]/page.tsx): Brand detail with origin, bio, and dedicated flacons.
6. `/categories/[slug]` — [src/app/categories/[slug]/page.tsx](file:///e:/Scentiva/src/app/categories/[slug]/page.tsx): Category collections (For Her, For Him, Unisex, Luxury & Niche, Everyday Fresh, Gift Sets).
7. `/product/[slug]` — [src/app/product/[slug]/page.tsx](file:///e:/Scentiva/src/app/product/[slug]/page.tsx): Product detail with dynamic OpenGraph, Schema.org JSON-LD, size/concentration selector, olfactory pyramid, and reviews.
8. `/find-your-scent` — [src/app/find-your-scent/page.tsx](file:///e:/Scentiva/src/app/find-your-scent/page.tsx): 5-step interactive scent quiz.
9. `/find-your-scent/results` — [src/app/find-your-scent/results/page.tsx](file:///e:/Scentiva/src/app/find-your-scent/results/page.tsx): Scent recommendation results with match score % and rationale.
10. `/wishlist` — [src/app/wishlist/page.tsx](file:///e:/Scentiva/src/app/wishlist/page.tsx): Saved items with quick buy.
11. `/cart` — [src/app/cart/page.tsx](file:///e:/Scentiva/src/app/cart/page.tsx): Shopping bag with coupons and threshold delivery calculator.
12. `/checkout` — [src/app/checkout/page.tsx](file:///e:/Scentiva/src/app/checkout/page.tsx): 4-step demo checkout.
13. `/checkout/payment` — [src/app/checkout/payment/page.tsx](file:///e:/Scentiva/src/app/checkout/payment/page.tsx): Payment method selector (UPI, Card, Net Banking, COD).
14. `/order/success` — [src/app/order/success/page.tsx](file:///e:/Scentiva/src/app/order/success/page.tsx): Confetti animation & order confirmation.
15. `/account` — [src/app/account/page.tsx](file:///e:/Scentiva/src/app/account/page.tsx): Connoisseur dashboard & VIP tier.
16. `/account/sign-in` — [src/app/account/sign-in/page.tsx](file:///e:/Scentiva/src/app/account/sign-in/page.tsx): Demo sign-in with 1-click prefill.
17. `/account/sign-up` — [src/app/account/sign-up/page.tsx](file:///e:/Scentiva/src/app/account/sign-up/page.tsx): Registration with welcome perks.
18. `/account/orders` — [src/app/account/orders/page.tsx](file:///e:/Scentiva/src/app/account/orders/page.tsx): Order history with status pills.
19. `/account/orders/[id]` — [src/app/account/orders/[id]/page.tsx](file:///e:/Scentiva/src/app/account/orders/[id]/page.tsx): Dynamic order tracking timeline.
20. `/account/addresses` — [src/app/account/addresses/page.tsx](file:///e:/Scentiva/src/app/account/addresses/page.tsx): Address book CRUD.
21. `/offers` — [src/app/offers/page.tsx](file:///e:/Scentiva/src/app/offers/page.tsx): Promo coupons with 1-click copy.
22. `/gifts` — [src/app/gifts/page.tsx](file:///e:/Scentiva/src/app/gifts/page.tsx): Curated Gift sets & discovery coffrets.
23. `/stories` — [src/app/stories/page.tsx](file:///e:/Scentiva/src/app/stories/page.tsx): Editorial journal & masterclasses.
24. `/stories/[slug]` — [src/app/stories/[slug]/page.tsx](file:///e:/Scentiva/src/app/stories/[slug]/page.tsx): Dynamic masterclass article.
25. `/help` — [src/app/help/page.tsx](file:///e:/Scentiva/src/app/help/page.tsx): FAQ & authenticity info.
26. `/contact` — [src/app/contact/page.tsx](file:///e:/Scentiva/src/app/contact/page.tsx): Concierge contact form.
27. `/policies/shipping` — [src/app/policies/shipping/page.tsx](file:///e:/Scentiva/src/app/policies/shipping/page.tsx): Shipping policy.
28: `/policies/returns` — [src/app/policies/returns/page.tsx](file:///e:/Scentiva/src/app/policies/returns/page.tsx): Return policy.
29. `/policies/privacy` — [src/app/policies/privacy/page.tsx](file:///e:/Scentiva/src/app/policies/privacy/page.tsx): Privacy policy.
30. `/policies/terms` — [src/app/policies/terms/page.tsx](file:///e:/Scentiva/src/app/policies/terms/page.tsx): Terms of service.

### Admin Operations Console (13 Routes)
31. `/admin` — [src/app/admin/page.tsx](file:///e:/Scentiva/src/app/admin/page.tsx): Sales metrics, orders trend chart, conversion rate.
32. `/admin/login` — [src/app/admin/login/page.tsx](file:///e:/Scentiva/src/app/admin/login/page.tsx): Admin login.
33. `/admin/products` — [src/app/admin/products/page.tsx](file:///e:/Scentiva/src/app/admin/products/page.tsx): Product management table.
34. `/admin/products/new` — [src/app/admin/products/new/page.tsx](file:///e:/Scentiva/src/app/admin/products/new/page.tsx): Add new fragrance form.
35. `/admin/brands` — [src/app/admin/brands/page.tsx](file:///e:/Scentiva/src/app/admin/brands/page.tsx): Brand manager.
36. `/admin/categories` — [src/app/admin/categories/page.tsx](file:///e:/Scentiva/src/app/admin/categories/page.tsx): Category manager.
37. `/admin/inventory` — [src/app/admin/inventory/page.tsx](file:///e:/Scentiva/src/app/admin/inventory/page.tsx): Stock adjustment controls.
38. `/admin/orders` — [src/app/admin/orders/page.tsx](file:///e:/Scentiva/src/app/admin/orders/page.tsx): Order fulfillment pipeline.
39. `/admin/customers` — [src/app/admin/customers/page.tsx](file:///e:/Scentiva/src/app/admin/customers/page.tsx): Customer registry.
40. `/admin/promotions` — [src/app/admin/promotions/page.tsx](file:///e:/Scentiva/src/app/admin/promotions/page.tsx): Active coupons.
41. `/admin/content` — [src/app/admin/content/page.tsx](file:///e:/Scentiva/src/app/admin/content/page.tsx): Editorial CMS.
42. `/admin/reports` — [src/app/admin/reports/page.tsx](file:///e:/Scentiva/src/app/admin/reports/page.tsx): Analytics & reports.
43. `/admin/settings` — [src/app/admin/settings/page.tsx](file:///e:/Scentiva/src/app/admin/settings/page.tsx): Store settings.

### SEO & API Handlers (2 Routes)
44. `/sitemap.xml` — [src/app/sitemap.ts](file:///e:/Scentiva/src/app/sitemap.ts)
45. `/robots.txt` — [src/app/robots.ts](file:///e:/Scentiva/src/app/robots.ts)
46. `/api/health` — [src/app/api/health/route.ts](file:///e:/Scentiva/src/app/api/health/route.ts)
47. `/api/products` — [src/app/api/products/route.ts](file:///e:/Scentiva/src/app/api/products/route.ts)

---

## 4. Key Verification & Performance Baseline
- **Build Status:** Passing cleanly with Exit Code 0 (`next build` generates 45 static pages).
- **First Load JS:** 87.3 kB shared baseline bundle.
- **TypeScript:** 0 compiler errors.
- **GitHub Repository:** [https://github.com/OnkarGulhane/Scentiva](https://github.com/OnkarGulhane/Scentiva)

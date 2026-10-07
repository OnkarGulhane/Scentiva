# SCENTIVA — Repository Memory & Architecture Blueprint
> **Brand:** SCENTIVA — "SINCE 2026"  
> **Type:** Haute Parfumerie & Luxury Multi-Brand Fragrance Marketplace  
> **Target Form Factors:** Mobile-first customer storefront + Desktop immersive experience + Operational admin console  
> **Frontend Architecture:** Next.js 14 App Router (SSG/SSR) + TypeScript 5.5 + Tailwind CSS + Three.js / R3F + GSAP Motion  
> **Backend Architecture:** Spring Boot 3.3.4 (Java 21 LTS) Modular Monolith + PostgreSQL 17 + Flyway + Spring Security 6 (JWT)  
> **AI Architecture:** Python FastAPI Microservice (`ai-service/`) + LangChain + PostgreSQL pgvector RAG + Multi-Lingual NLP Engine  
> **Last Updated:** 2026-10-07  
> **Repository Remote:** [https://github.com/OnkarGulhane/Scentiva](https://github.com/OnkarGulhane/Scentiva)  
> **Current Status:** Phases 0–24 100% Complete & Verified (236/236 Java JUnit tests passing, 21/21 Python AI pytest tests passing, 86/86 Live API assertions passing, 13/13 Live Render Production Assertions passing on `https://scentiva-backend-zmvo.onrender.com`, 50/50 Next.js static pages prerendered without hydration mismatch, Multi-Lingual AI Assistant with English/Marathi/Hindi switcher, Scentiva pgvector RAG & catalog tools, Production Order Invoice OpenPDF engine, Google Identity Services OAuth 2.0, Supabase PostgreSQL 17 & Storage, Razorpay Secure Gateway live test mode, latest commit `3499a3b` on `main`).

---

## 1. System Topology & Full-Stack Architecture

```
                                  SCENTIVA ARCHITECTURE
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                FRONTEND LAYER (Next.js 14)                             │
│  - App Router (50 Static/Dynamic Pages)               - 3D Hero WebGL Flacon (R3F)     │
│  - Custom Design Tokens (Plum/Gold/Blush)             - GSAP / ScrollTrigger / Lenis   │
│  - StoreContext (SSR Hydration Resilience)            - Dual-Mode API Gateway Layer    │
│  - Multi-Lingual AI Assistant (EN / MR / HI)          - Razorpay Standard Checkout SDK │
│  - Google Identity Services SSO                       - Zero-Blocker Instant UX        │
└─────────────────────────────────────────▲──────────────────────────────────────────────┘
                                          │  REST HTTP (JSON Envelopes)
                                          │  X-Correlation-ID / Bearer JWT
┌─────────────────────────────────────────▼──────────────────────────────────────────────┐
│                            BACKEND LAYER (Spring Boot 3.3.4)                           │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐  │
│  │                              API GATEWAY & CONTROLLERS                           │  │
│  │  AuthController   BrandController      ProductController   InventoryController   │  │
│  │  CartController   CheckoutController   OrderController     ShippingController    │  │
│  │  ReviewController ReturnController     NotificationCtrl    AdminDashboardCtrl    │  │
│  │  AiConciergeCtrl  StoryController      MediaUploadCtrl     InvoiceController     │  │
│  └──────────────────────────────────────▲───────────────────────────────────────────┘  │
│                                         │                                              │
│  ┌──────────────────────────────────────▼───────────────────────────────────────────┐  │
│  │                             DOMAIN SERVICES & ENGINES                            │  │
│  │  AuthService       ProductService      InventoryService (Pessimistic Lock / TTL) │  │
│  │  CartService       CheckoutService     RazorpayPaymentProvider (HMAC SHA-256)    │  │
│  │  OrderService      ShippingService     SupabaseStorageService (scentiva-media)   │  │
│  │  InvoiceService    ScentivaEmailService (Resend + Gmail SMTP SPI)                │  │
│  │  ReviewService     ReturnService       CmsStoryService / SeoService / AuditLog   │  │
│  └──────────────────────────────────────▲───────────────────────────────────────────┘  │
│                                         │                                              │
│  ┌──────────────────────────────────────▼───────────────────────────────────────────┐  │
│  │                    AI SERVICE CLIENT & HYBRID FALLBACK                           │  │
│  │  PythonAiServiceClient (WebClient REST) ──► Failover: DefaultAiConciergeProvider  │  │
│  └──────────────────────────────────────┬───────────────────────────────────────────┘  │
│                                         │                                              │
│  ┌──────────────────────────────────────▼───────────────────────────────────────────┐  │
│  │                            DATA ACCESS LAYER (Spring Data JPA)                   │  │
│  │  UserRepository    ProductRepository   InventoryRecordRepository (@Lock PESSIM.) │  │
│  │  CartRepository    OrderRepository     PaymentRepository   ReviewRepository      │  │
│  └──────────────────────────────────────▲───────────────────────────────────────────┘  │
└─────────────────────────────────────────┼──────────────────────────────────────────────┘
                                          │  REST (Port 8000)
┌─────────────────────────────────────────▼──────────────────────────────────────────────┐
│                          PYTHON AI SERVICE (FastAPI + LangChain)                       │
│  - Multi-Lingual Sensory NLP (English, Marathi, Hindi) - Catalog & Policy RAG Tools    │
│  - Guardrails & Input Sanitization                    - Deterministic & OpenAI LLMs    │
│  - pgvector Similarity Search Pipeline                - Live Order Tracking Integration│
└─────────────────────────────────────────┬──────────────────────────────────────────────┘
                                          │  Flyway SQL Migrations (V1, V2, V3, V4, V5)
┌─────────────────────────────────────────▼──────────────────────────────────────────────┐
│                      PERSISTENCE & STORAGE (Supabase Cloud Infrastructure)             │
│  - Supabase PostgreSQL 17 (35 Tables + pgvector RAG)  - NUMERIC(12,2) Exact Decimal    │
│  - Supabase Storage Public Bucket ('scentiva-media')  - Razorpay Payment Gateway Live  │
└────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Frontend Specifications (Next.js 14 App Router)

- **Framework & Runtime:** Next.js 14 (`next` v14.2.24), React 18.3.1, React DOM, TypeScript 5.5 (`strict: true`, path alias `@/* -> ./src/*`).
- **Styling & Design Tokens:** TailwindCSS v3.4 + Custom CSS Tokens ([src/styles/tokens.css](file:///e:/Scentiva/src/styles/tokens.css), [src/styles/index.css](file:///e:/Scentiva/src/styles/index.css)).
  - Deep Plum Surfaces: `#321027`, `#451333`, `#5B1B43`
  - Prestige Gold Accents: `#C7A66A`, `#F5EBD7`
  - Blush Highlights: `#E9B7D8`, `#F2D2E7`, `#FAEAF4`
  - Canvas Neutrals: `#FAF8F7`, `#F2EEEC`, `#E4DCDA`, `#342C30`, `#1D171B`
- **Typography:** Google Fonts `Cormorant Garamond` (Editorial serif for luxury headers) + `Inter` (Precise UI/pricing/body).
- **3D & WebGL Engine:** Three.js + `@react-three/fiber` + `@react-three/drei` ([src/components/home/Hero3DCanvas.tsx](file:///e:/Scentiva/src/components/home/Hero3DCanvas.tsx)):
  - Centering offset: `group position={[0, -0.42, 0]}` across `3.86` vertical units.
  - Camera Framing: `position: [0, 0, 5.2]`, `FOV 46°` (87.5% bottle occupancy, 12.5% visual breathing room, 0% cropping across all viewports).
  - Offscreen GPU throttle: `IntersectionObserver` switches `frameloop` to `never` when scrolled out of view.
- **SSR Hydration Architecture & Resilience ([src/context/StoreContext.tsx](file:///e:/Scentiva/src/context/StoreContext.tsx)):**
  - Deterministic initial SSR state (`isHydrated: false`, `currentUser: null`, `cart: []`, `wishlist: INITIAL_WISHLIST`).
  - Post-mount client hydration in `useEffect` reading `localStorage` safely and background token sync via `AuthApiService.getMe()`.
  - Zero React hydration mismatch errors across all 50 statically generated pages.
- **Multi-Lingual AI Assistant ([src/components/ai/ScentivaAiConciergeDrawer.tsx](file:///e:/Scentiva/src/components/ai/ScentivaAiConciergeDrawer.tsx)):**
  - Instant 3-Language Selector (**English** | **मराठी** | **हिंदी**) in drawer header.
  - Dynamic localized greetings, quick suggestion prompts, and placeholder text per selected language.
  - Rich interactive recommendation cards with authentic catalog data, stock validation, and direct **"Add to Bag"** and **"View Fragrance"** actions.
  - Floating trigger button integrated in luxury layout shell ([src/components/layout/StoreLayoutShell.tsx](file:///e:/Scentiva/src/components/layout/StoreLayoutShell.tsx)).
- **Dual Authentication Experience ([src/views/SignInPage.tsx](file:///e:/Scentiva/src/views/SignInPage.tsx)):**
  - Google Identity Services (OAuth 2.0) with luxury "Continue with Google" SSO pill button.
  - Seamless tab toggle between Sign In and Create Account with email/password.
  - Guest cart merging upon authentication and automatic redirect return to `/checkout`.
- **Frontend Dual-Mode API Gateway Layer:**
  - [src/lib/api/apiClient.ts](file:///e:/Scentiva/src/lib/api/apiClient.ts): Standardized HTTP client with automatic JWT token attachment, unique `X-Correlation-ID` header propagation, timeout management, and retry handling.
  - Domain Services:
    - [src/services/authApiService.ts](file:///e:/Scentiva/src/services/authApiService.ts)
    - [src/services/catalogApiService.ts](file:///e:/Scentiva/src/services/catalogApiService.ts)
    - [src/services/cartApiService.ts](file:///e:/Scentiva/src/services/cartApiService.ts)
    - [src/services/checkoutApiService.ts](file:///e:/Scentiva/src/services/checkoutApiService.ts)
    - [src/services/orderApiService.ts](file:///e:/Scentiva/src/services/orderApiService.ts)
    - [src/services/aiApiService.ts](file:///e:/Scentiva/src/services/aiApiService.ts)
    - [src/services/cmsApiService.ts](file:///e:/Scentiva/src/services/cmsApiService.ts)
    - [src/services/adminApiService.ts](file:///e:/Scentiva/src/services/adminApiService.ts)

---

## 3. Backend Architecture & Domain Modules (Spring Boot 3.3.4)

### Module Hierarchy (`com.scentiva.modules.*`)
1. **`common`:** Base entity (`id`, `createdAt`, `updatedAt`, `isDeleted`, `version`), response envelopes (`ApiResponse<T>`, `ApiPaginatedResponse<T>`, `ApiErrorResponse`), global exception handling (`GlobalExceptionHandler`), and Swagger/OpenAPI 3.0 docs.
2. **`auth` & `security`:** JWT Provider (HMAC SHA-512), Google Identity Token Verifier, `JwtAuthenticationFilter`, `CustomUserDetailsService`, `UserPrincipal`, BCrypt password encoder (work factor 12), and role-based access control (`ROLE_CUSTOMER`, `ROLE_ADMIN`, `ROLE_SUPER_ADMIN`).
3. **`customer`:** Customer profiles, loyalty tiers (`BRONZE`, `SILVER`, `GOLD`, `PLATINUM`, `VIP`), cascading multi-address book (`SHIPPING`, `BILLING`) with default address switching.
4. **`catalog`:** Brands (with luxury tiers), categories, products, olfactory pyramids (top, heart, base notes, sillage, longevity), product variants (exact `BigDecimal` pricing, SKU uniqueness, concentration, volume), and multi-image galleries.
5. **`inventory`:** Multi-location fulfillment centers (Pune, Mumbai, Delhi), `InventoryRecord` with pessimistic write locks (`@Lock(LockModeType.PESSIMISTIC_WRITE)`), 15-minute checkout stock hold TTL, dynamic release, deduction on order dispatch, and immutable `InventoryMovement` ledgers.
6. **`cart`:** Persistent shopping bag, item addition/updates with server-authoritative stock checks, unit price freezing upon addition, cart merging upon login, dynamic free shipping qualification (Threshold: ₹2,000 / Standard Fee: ₹150).
7. **`promotion`:** Active marketing campaigns, coupon engine (`PERCENTAGE`, `FIXED_AMOUNT`, `FREE_SHIPPING`) with maximum discount caps, minimum order value thresholds, expiry, usage quotas, and redemption tracking.
8. **`payment`:** Payment SPI abstraction (`PaymentProvider`), `RazorpayPaymentProvider` (live order creation & HMAC SHA-256 signature verification), `DemoPaymentProvider` (simulating instant capture, 3D-Secure challenge, card decline, and refunds), transactions log, and idempotency protection.
9. **`order`:** Full lifecycle state machine (`PLACED` → `CONFIRMED` → `PROCESSING` → `SHIPPED` → `DELIVERED` / `CANCELLED` / `REFUNDED`), immutable JSON snapshots (`OrderSnapshot`), and automated restock + refund triggers upon cancellation.
10. **`invoice`:** OpenPDF 2.0.3 deterministic A4 vector PDF engine (`PdfInvoiceGenerator.java`), `INV-YYYY-XXXXXX` numbering format, and endpoints for both JSON metadata and inline PDF streaming (`/api/v1/orders/{orderNumber}/invoice/pdf`).
11. **`email`:** Pluggable `EmailProvider` SPI with `ResendEmailProvider`, `GmailSmtpEmailProvider`, and `MockEmailProvider` utilizing luxury Deep Plum/Gold HTML Thymeleaf templates.
12. **`shipping`:** Shipping SPI abstraction (`ShippingProvider`), `ManualShippingProvider` (generating `SC-TRK-*` courier tracking numbers), carrier status transitions (`DISPATCHED` → `IN_TRANSIT` → `OUT_FOR_DELIVERY` → `DELIVERED`), and timeline events.
13. **`review`:** Product reviews with verified-purchase enforcement against delivered orders, 1-5 star ratings, automated sentiment heuristics, and backoffice moderation (`PENDING`, `APPROVED`, `REJECTED`).
14. **`returns`:** Customer return workflow with item-level reasons, return status transitions (`REQUESTED` → `APPROVED` → `PICKED_UP` → `RECEIVED` → `REFUNDED`), stock return adjustments, and payment refund processing.
15. **`notification`:** Multi-channel notifications (`IN_APP`, `EMAIL`, `SMS`, `WHATSAPP`), Spring `@EventListener` triggers for order lifecycle events, unread badge counters, and mark-as-read endpoints.
16. **`admin`:** Backoffice executive analytics (GMV revenue, time-series sales trends, top product performance, order state breakdowns), customer management, user governance, and store settings.
17. **`ai`:** AI Gateway (`AiConciergeController.java`), `PythonAiServiceClient.java` communicating with FastAPI microservice, and failover `DefaultAiConciergeProvider`.
18. **`cms` & `seo`:** Olfactory journal articles, scheduled promotional banners, dynamic XML sitemaps, OpenGraph metadata, and Schema.org `Product`/`Brand` JSON-LD microdata.
19. **`observability`:** Distributed request tracing with `X-Correlation-ID` filter, MDC logging, immutable administrative `AuditLog` records, and real-time JVM system metrics.

---

## 4. Scentiva AI Platform Architecture (`ai-service/`)

- **Runtime & Framework:** Python 3.10+ / FastAPI / Uvicorn (Port `8000`).
- **5 Core AI Modules:**
  1. **AI Shopping Assistant (P1):** Conversational shopping agent parsing English, Marathi-English, and Hindi-English natural language queries (e.g., *"Mala office sathi fresh perfume pahije, long-lasting under ₹2500"*), querying live catalog tools, verifying real prices and stock, and returning interactive "Add to Bag" recommendation cards.
  2. **AI Semantic Search (P1):** Sensory and olfactory intent detection (notes, season, longevity, gender) with pgvector similarity fallback.
  3. **AI Scent Finder (P1):** Multi-step sensory assessment evaluating olfactory preferences and generating distinct Archetypes ("The Velvet Nocturne", "The Parisian Blossom", "The Amber Alchemist") with 4 verified recommendations.
  4. **Personalized Recommendations (P2):** User session & history aware olfactory rails on homepage (`AiPersonalizedRail.tsx`) and cart drawer.
  5. **AI Customer Support & Live Tracking (P2):** RAG search over Scentiva store policies (7-day returns, 2ml discovery spray, thermal packaging) + live authenticated order tracking tools (`SC-TRK-*`).
- **Security & Reliability:**
  - Strict input sanitization and prompt injection defenses (`validators/guardrails.py`).
  - Secret redaction in structured log output (`app_logging/logger.py`).
  - Strict catalog price verification with graceful luxury budget fallbacks (`tools/catalog_tools.py`).
  - Deterministic and OpenAI LLM factories (`llm/factory.py`).

---

## 5. Authoritative Financial & Arithmetic Invariants

- **Zero Float Invariant:** All money values in Java are strictly `BigDecimal` with `RoundingMode.HALF_UP` and stored in PostgreSQL as `NUMERIC(12,2)`.
- **Order Total Equation:**
  $$\text{subtotal} = \sum (\text{item.unitPrice} \times \text{item.quantity})$$
  $$\text{discount} = \min(\text{calculatedDiscount}, \text{subtotal})$$
  $$\text{deliveryFee} = \begin{cases} 0.00 & \text{if } (\text{subtotal} - \text{discount}) \ge \text{freeDeliveryThreshold} \\ \text{standardFee} & \text{otherwise} \end{cases}$$
  $$\text{totalAmount} = \max(0.00, \text{subtotal} - \text{discount} + \text{taxAmount} + \text{deliveryFee})$$
- **Stock Integrity:** Available Stock = $\text{quantityOnHand} - \text{quantityReserved}$. Available stock can never be negative. Reservations must use pessimistic write locks to prevent race conditions.

---

## 6. Automated Verification & Stress Testing Suite

- **Total Backend Tests:** **236 / 236 Java tests passing (100% BUILD SUCCESS, 0 failures, 0 errors)**
- **Python AI Service Tests:** **21 / 21 Pytest tests passing (100% PASS, 0 failures)**
- **Live HTTP Test Assertions:** **86 / 86 tests passing (100% PASS, 0 failures)**
- **Stress & Concurrency Proofs:**
  1. `InventoryConcurrencyStressTest.java`: 100 concurrent threads competing for 5 scarce units. Exactly 5 succeed, 95 rejected gracefully with `InsufficientStockException`, resulting in 0 available stock and 0 negative balance.
  2. `CheckoutIdempotencyStressTest.java`: 20 concurrent threads submitting the exact same `idempotencyKey`. Exactly 1 order created in DB, 0 duplicate charges, all threads receive identical order confirmation.
  3. `FinancialMathPrecisionTest.java`: Verified exact ₹0.01 precision across ₹28.55 Cr GMV orders, cap validations, and non-negative boundaries.
  4. `SecurityRbacMatrixStressTest.java`: Validated access matrix across `ANONYMOUS`, `CUSTOMER`, and `ADMIN` roles for all secured endpoints.
- **Frontend Verification:**
  - `npm run build` cleanly prerenders 50 / 50 static pages with 0 TypeScript compiler errors (Exit Code 0).

---

## 7. Project Roadmap & Completion Status

| Phase | Description | Status |
|---|---|---|
| **Phase 0** | Baseline Audit, Master SRS & Toolchain Setup | ✅ COMPLETED (`a5e67ae`) |
| **Phase 1** | Spring Boot 3.3.4 Foundation & Flyway DB Migrations | ✅ COMPLETED (`fedea5c`) |
| **Phase 2** | Database & Domain Entities & Repositories | ✅ COMPLETED (`3b2b2b7`) |
| **Phase 3** | Authentication, JWT Token Provider & Security Filter | ✅ COMPLETED (`189cf2c`) |
| **Phase 4** | Catalog & Perfume Domain Services & Controllers | ✅ COMPLETED (`5bfa79d`) |
| **Phase 5** | Multi-Location Inventory & Stock Reservation Engine | ✅ COMPLETED (`a15b16e`) |
| **Phase 6** | Customer Profile, Address Book, Bag & Wishlist | ✅ COMPLETED (`d758d16`) |
| **Phase 7** | Checkout Orchestration, Coupon Engine & Payment Provider | ✅ COMPLETED (`8b60956`) |
| **Phase 8** | Orders, Payment Lifecycle & Shipping Abstraction | ✅ COMPLETED (`07b35da`) |
| **Phase 9** | Promotions, Reviews, Returns & Notifications | ✅ COMPLETED (`36334bf`) |
| **Phase 10** | Backoffice Admin Console & Analytics APIs | ✅ COMPLETED (`4ad526f`) |
| **Phase 11** | AI Concierge, Scent Finder Quiz & Semantic Search | ✅ COMPLETED (`5dab4f9`) |
| **Phase 12** | Editorial CMS, Banners, SEO & Observability | ✅ COMPLETED (`37f2570`) |
| **Phase 13** | Frontend REST API Integration & Gateway Layer | ✅ COMPLETED (`e9e27cf`) |
| **Phase 14** | Concurrency, Idempotency, Precision & Security Stress Testing | ✅ COMPLETED (`cea7211`) |
| **Phase 15** | Production Hardening, Docker Containerization & Multi-Env Config | ✅ COMPLETED (`58f62fa`) |
| **Phase 16** | Authentication UX, Hydration Resilience & SRS v1.1.0 Baseline | ✅ COMPLETED (`48fcf8d`) |
| **Phase 17** | Authentication + Checkout Complete Fix & Real Customer Address Engine | ✅ COMPLETED (`6485538`) |
| **Phase 18** | Complete API Testing, Bug Audit, DTO Contracts & Verification Documentation | ✅ COMPLETED (`ce5e514`) |
| **Phase 19** | **Supabase Database (PostgreSQL 17) & Cloud Storage Integration** | ✅ **COMPLETED** (`0ed07b2`) |
| **Phase 20** | **Razorpay Secure Payment Gateway & Zero-Latency Resilient Checkout UX** | ✅ **COMPLETED** (`0ed07b2`) |
| **Phase 21** | **Google Identity Services (OAuth 2.0) & Cross-Platform SSO Flow** | ✅ **COMPLETED** (`47c8941`) |
| **Phase 22** | **Hybrid Email Service (Resend API & Gmail SMTP SPI) + Luxury HTML Templates** | ✅ **COMPLETED** |
| **Phase 23** | **Production Order Invoice & Receipt System (OpenPDF A4 Engine)** | ✅ **COMPLETED** |
| **Phase 24** | **Scentiva AI Platform (FastAPI + pgvector + Multi-Lingual AI Assistant)** | ✅ **COMPLETED** (`660f402`) |

---

## 8. Complete API Testing & Bug Audit Documentation

All API audits, live assertions, test cases, and sign-offs are documented in:
1. [docs/api-testing/API-INVENTORY.md](file:///e:/Scentiva/docs/api-testing/API-INVENTORY.md): Complete list of 90+ REST API endpoints.
2. [docs/api-testing/API-TEST-REPORT.md](file:///e:/Scentiva/docs/api-testing/API-TEST-REPORT.md): 236/236 JUnit test execution results and 86/86 live integration assertions.
3. [docs/api-testing/API-BUG-REPORT.md](file:///e:/Scentiva/docs/api-testing/API-BUG-REPORT.md): Bug audit report detailing root causes and verified fixes.
4. [docs/api-testing/API-TEST-CASES.md](file:///e:/Scentiva/docs/api-testing/API-TEST-CASES.md): Structured test case matrix.
5. [docs/api-testing/API-FINAL-STATUS.md](file:///e:/Scentiva/docs/api-testing/API-FINAL-STATUS.md): Production sign-off summary.

---

## 9. Production Deployment & Cloud Architecture

- **Frontend (Vercel Edge):** Next.js 14 App Router, auto-building on Git push, optimized via [vercel.json](file:///e:/Scentiva/vercel.json) with HTTP security headers and aggressive asset caching.
- **Backend (Render Web Service):** Spring Boot 3.3.4 containerized with [backend/Dockerfile](file:///e:/Scentiva/backend/Dockerfile) (Eclipse Temurin 21 JRE Alpine + `-XX:+UseContainerSupport`), bound dynamically to `$PORT`, with `/api/v1/health` actuator checks.
- **AI Microservice (Render Web Service / Container):** Python 3.10 FastAPI service with Uvicorn ASGI server.
- **Persistence (Render / Supabase PostgreSQL):** Managed PostgreSQL database with Flyway auto-migration (`V1` to `V5` + `pgvector`).
- **Local Orchestration:** [docker-compose.yml](file:///e:/Scentiva/docker-compose.yml) providing single-command startup for PostgreSQL 17 + Spring Boot 3.3.4 + Next.js 14 + Python AI Service.
- **Operational Guide:** Full deployment walkthrough in [docs/DEPLOYMENT_GUIDE.md](file:///e:/Scentiva/docs/DEPLOYMENT_GUIDE.md).

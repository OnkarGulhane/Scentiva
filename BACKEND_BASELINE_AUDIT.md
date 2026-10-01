# SCENTIVA — Backend Baseline Audit & Architecture Verification (Phase 0)
> **Audit Date:** October 1, 2026  
> **Status:** APPROVED & BASELINE ESTABLISHED  
> **Target Framework:** Spring Boot 3.3.x / Java 21 LTS / Maven 3.9.x / PostgreSQL 17 / Next.js 14 App Router  
> **Document Authority:** [SCENTIVA_SRS.md](file:///e:/Scentiva/SCENTIVA_SRS.md) & [SCENTIVA_MASTER_BACKEND_IMPLEMENTATION_ANTIGRAVITY_PROMPT.md](file:///e:/Scentiva/prompts/SCENTIVA_MASTER_BACKEND_IMPLEMENTATION_ANTIGRAVITY_PROMPT.md)  
> **Repository:** [https://github.com/OnkarGulhane/Scentiva](https://github.com/OnkarGulhane/Scentiva)  

---

## 1. Executive Baseline Summary

In accordance with Phase 0 of the Master Backend Implementation directives, a comprehensive audit was executed across the existing Scentiva codebase, the approved [SCENTIVA_SRS.md](file:///e:/Scentiva/SCENTIVA_SRS.md), and the local system toolchain. 

### Key Findings
1. **Frontend Integrity:** The Next.js 14 App Router storefront is completely hardened with 45 prerendered static routes, WebGL 3D flacon camera calibration (`0% clipping`), two-phase SSR hydration, and zero TypeScript compiler errors.
2. **Backend Green-Field Status:** No conflicting or legacy backend code exists. The backend will be engineered as an enterprise-grade **Java Spring Boot Modular Monolith** located in `e:/Scentiva/backend/`.
3. **Environment Readiness:**
   - **Java:** JDK 21.0.7 LTS (`C:\Program Files\Java\jdk-21`) — **Verified Active**
   - **Maven:** Apache Maven 3.9.16 — **Verified Active**
   - **Database:** PostgreSQL 17.11 (`psql` available) — **Verified Active**
   - **Node.js / Next.js:** Node.js v20+ with Next.js 14.2.24 — **Verified Active**

---

## 2. Repository & Workspace Topology Analysis

To prevent namespace collisions between the Next.js `src/` directory and the Java Maven directory hierarchy, the project structure is partitioned as follows:

```text
E:\Scentiva\
├── backend/                       # Java Spring Boot 3.3.x Modular Monolith
│   ├── pom.xml                   # Maven Build Descriptor (Spring Boot 3.3.x, Java 21)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/scentiva/
│   │   │   │   ├── ScentivaApplication.java
│   │   │   │   ├── config/       # Security, Web, CORS, Swagger, Async
│   │   │   │   ├── common/       # Global ApiResponse<T>, BaseEntity, Exceptions
│   │   │   │   ├── modules/      # Domain Modules (Auth, Catalog, Cart, Order, etc.)
│   │   │   │   └── providers/    # Provider Abstractions (Payment, Shipping, AI)
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-local.yml
│   │   │       ├── application-prod.yml
│   │   │       └── db/migration/ # Flyway V1__... SQL Migrations
│   │   └── test/                 # JUnit 5, Mockito, Testcontainers JPA tests
├── src/                          # Next.js 14 App Router Frontend (Client Storefront & Admin UI)
├── public/                       # Static Assets & 3D Textures
├── docs/                         # Architecture, QA & Audit Reports
├── prompts/                      # Master Prompts & Implementation Directives
├── SCENTIVA_SRS.md               # Authoritative Implementation Contract
├── SCENTIVA_SRS_QA_REPORT.md     # Requirements QA Audit
├── progress.md                   # Daily Progress & Quality Gate Tracker
└── package.json                  # Next.js Package Dependencies
```

---

## 3. Confirmed Architecture & Engineering Principles

### 3.1 Modular Monolith vs Microservices
- **Confirmed Decision (ADR-001):** Single Spring Boot modular monolith. Eliminates distributed transaction failures (2PC / Sagas), network serialization latency, and operational overhead while maintaining strict internal package domain encapsulation.

### 3.2 Backend as the Sole Source of Truth
- **Rule 1 Enforced:** The frontend is strictly a presentation client. All prices, stock availability, discount calculations, tax computations, payment signatures, and order state transitions are authoritatively calculated and validated by the backend.

### 3.3 DTO Boundary Encapsulation
- **Rule 2 Enforced:** JPA Entities are strictly internal to the service/repository layers and are never serialized or exposed directly through REST controllers. All API inputs and outputs use typed Request/Response DTOs.

### 3.4 Decimal-Exact Accounting
- **Rule 3 Enforced (ADR-003):** Floating-point numbers (`float`, `double`) are strictly prohibited for monetary values. All currency fields are modeled with `BigDecimal` in Java and `NUMERIC(12,2)` in PostgreSQL.

---

## 4. Provider Abstraction Matrix

To prevent tight vendor coupling, external integrations use standard Java interfaces with local development implementations and production drop-ins:

| Domain | Interface | Local / Sandbox Implementation | Production Target |
|---|---|---|---|
| **Payments** | `PaymentProvider` | `DemoPaymentProvider` (Simulates instant success/failure/refunds) | `RazorpayPaymentProvider` (HMAC SHA-256 signatures, webhooks) |
| **Logistics** | `ShippingProvider` | `ManualShippingProvider` (Admin tracking update & notes) | `ShiprocketProvider` (Automated AWB & live webhook tracking) |
| **Storage** | `StorageProvider` | `LocalStorageProvider` (Local disk media directory) | `ObjectStorageProvider` (AWS S3 / Cloudflare R2 / MinIO) |
| **AI Concierge** | `AIProvider` | `MockAIProvider` (Deterministic scent quiz & note matching) | `OpenAIProvider` / `AnthropicProvider` (RAG & embeddings) |
| **Notifications** | `NotificationProvider` | `LogNotificationProvider` (Console/file audit logs) | `SendGridMailProvider` / `TwilioSmsProvider` |

---

## 5. Domain Module Architecture (`com.scentiva.modules.*`)

```text
com.scentiva.modules
├── auth/            # Security, JWT, BCrypt, AuthController, User, Role, Permission
├── customer/        # CustomerProfile, CustomerAddress, AddressSnapshots
├── catalog/         # Brand, Category, Product, OlfactoryPyramid, Variant, SKU, ProductImage
├── inventory/       # Warehouse, InventoryRecord, InventoryMovement, Pessimistic Lock Engine
├── cart/            # Cart, CartItem, Authoritative Price Calculation Engine
├── wishlist/        # Wishlist, WishlistItem, Back-in-Stock Trigger
├── checkout/        # CheckoutOrchestrator, IdempotencyEngine, ValidationPipeline
├── order/           # Order, OrderItem, OrderSnapshot, OrderStateMachine
├── payment/         # Payment, PaymentTransaction, PaymentStateMachine, Providers
├── shipping/        # Shipment, ShipmentEvent, ShippingStateMachine, Providers
├── promotion/       # Coupon, CouponRedemption, CampaignRule, DiscountEngine
├── review/          # Review, Rating, ModerationWorkflow, VerifiedBuyerCheck
├── content/         # Story, Banner, FAQ, CMSController
├── ai/              # ScentConciergeService, SemanticSearchEngine, PromptGuardrails
└── admin/           # DashboardMetrics, InventoryControls, OrderFulfillment, StaffRegistry
```

---

## 6. Database & Migration Strategy (PostgreSQL 17)

- **Migration Tool:** Flyway (`src/main/resources/db/migration/`) with incremental versioned scripts (`V1__init_schema.sql`, `V2__seed_catalog.sql`, etc.).
- **Concurrency & Locks:** Row-level pessimistic locking (`SELECT ... FOR UPDATE`) during checkout stock reservation to prevent race conditions and overselling.
- **Indexes:** B-Tree indexing on foreign keys, unique slug indexes, and composite indexes on `(category_id, is_active, base_price)`.
- **Audit Trails:** Immutable `inventory_movements` and `audit_logs` tracking all administrative and inventory modifications.

---

## 7. Contradictions & Ambiguities Resolved

| Item | Potential Ambiguity | Final Verified Resolution |
|---|---|---|
| **Inventory Multi-Location** | Single store demo vs enterprise warehouse network. | Designed `warehouses` table with `1..N` `inventory_records` per variant. Local environment defaults to `PUNE_MAIN_WH` while code fully supports multi-location routing. |
| **Historical Address Changes** | Customer updates address after order dispatch. | Implemented `order_snapshots` storing immutable JSON of shipping address at checkout time. Subsequent profile edits do not alter past orders. |
| **Coupon Discount Authority** | Frontend computes coupon discount. | Backend recalculates and validates coupon validity, subtotal thresholds, and user usage limits on every cart/checkout submission. |
| **Payment Status Flow** | Single boolean `isPaid`. | Modeled distinct `Payment` and `PaymentTransaction` entities with a formal state machine (`INITIATED` $\rightarrow$ `PENDING` $\rightarrow$ `SUCCESS` / `FAILED` $\rightarrow$ `REFUNDED`). |

---

## 8. Implementation Phasing Roadmap

```text
[ Phase 0: Baseline Audit & Architecture Verification ] ──► (COMPLETE)
       │
       ▼
[ Phase 1: Backend Foundation ] (Spring Boot 3.3.x, Maven, Flyway, PostgreSQL Schema, Global DTOs, Health Endpoint)
       │
       ▼
[ Phase 2: Database & Domain Foundation ] (JPA Entities, Repositories, Constraints, Testcontainers Tests)
       │
       ▼
[ Phase 3: Authentication, Security & RBAC ] (Spring Security 6, JWT, BCrypt Work Factor 12, Role Guards)
       │
       ▼
[ Phase 4: Catalog & Perfume Domain ] (Brands, Categories, Products, Olfactory Pyramids, Variants, SKUs)
       │
       ▼
[ Phase 5: Multi-Location Inventory & Stock Reservation ] (Row-Level Locking, 15-Min Hold, Movements Ledger)
       │
       ▼
[ Phase 6: Customer Profile, Address Book, Bag & Wishlist ] (Authoritative Calculation Engine, Snapshot Strategy)
       │
       ▼
[ Phase 7: Checkout Orchestration & Demo Payment ] (Idempotency Engine, Validation Pipeline, DemoPaymentProvider)
       │
       ▼
[ Phase 8: Order Lifecycle & Shipping Abstraction ] (Order State Machine, Historical Snapshots, ManualShippingProvider)
       │
       ▼
[ Phase 9: Promotions, Reviews, Returns & Notifications ] (Coupon Engine, Verified Purchase, 7-Day Return Policy)
       │
       ▼
[ Phase 10: Backoffice Admin Console APIs ] (Dashboard Analytics, Inventory Controls, Order Dispatch Pipeline)
       │
       ▼
[ Phase 11: AI Concierge & Semantic Search ] (Scent Quiz Engine, Note Vector Matching, Guardrails)
       │
       ▼
[ Phase 12: CMS, SEO, Media & Observability ] (Stories CMS, Schema.org Data, Structured MDC Logging)
       │
       ▼
[ Phase 13: Frontend-Backend REST API Integration ] (Next.js ApiClient Connection, Live Storefront Flow)
       │
       ▼
[ Phase 14: Comprehensive QA, Concurrency & Security Tests ] (100 Simultaneous Checkouts Test, Edge Case Verification)
       │
       ▼
[ Phase 15: Production Hardening & Docker Containerization ] (Docker Compose, Multi-Env Profiles, Production Sign-off)
```

---

## 9. Quality Gate Sign-Off (Phase 0)

- [x] Master prompt and [SCENTIVA_SRS.md](file:///e:/Scentiva/SCENTIVA_SRS.md) completely reviewed and analyzed.
- [x] Existing Next.js frontend verified passing clean build (45/45 static pages prerendered).
- [x] Local toolchain (JDK 21, Maven 3.9, PostgreSQL 17) validated.
- [x] Zero conflicting legacy backend files identified.
- [x] Phase 0 deliverables produced: [BACKEND_BASELINE_AUDIT.md](file:///e:/Scentiva/BACKEND_BASELINE_AUDIT.md).
- [x] Stop condition satisfied: Halting execution to await explicit user approval before initiating Phase 1.

**Phase 0 Status:** **`COMPLETE & VERIFIED`**

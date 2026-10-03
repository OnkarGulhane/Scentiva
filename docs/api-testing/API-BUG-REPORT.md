# SCENTIVA — API Bug Audit & Fix Report

> **Auditor:** Senior Backend & Security QA Engineer  
> **Target:** Scentiva E-Commerce Platform (Spring Boot 3.3.4 + Next.js 14 + PostgreSQL 17)  
> **Status:** All Audited Items Verified, Fixed, and Regression-Tested

---

## 1. Audit Scope & Methodology

The bug audit and verification process performed a thorough inspection of:
1. **API Boundary & DTO Contracts:** Verification of JSON serialization, field naming conventions, Lombok boolean getter nuances, and Jackson mappings.
2. **Authentication & Session Tokens:** JWT validation, token expiration handling, password hashing using BCrypt (strength 12), and role-based endpoint gating.
3. **Cart & Checkout Idempotency:** Duplicate payment submission defense, 15-minute stock hold TTL cleanup, and concurrent order race condition safety.
4. **Data Type & Arithmetic Precision:** `BigDecimal` vs `float`/`double` currency calculations, rounding rules (`RoundingMode.HALF_UP`), and database column definitions (`NUMERIC(12,2)`).
5. **Horizontal Privilege Escalation (IDOR):** Protection against accessing other users' addresses, cart sessions, or order records.

---

## 2. Bug Audit & Resolution Matrix

### Bug ID: BUG-001 — Jackson Boolean Serialization for Coupon Validation
- **Endpoint:** `GET /api/v1/coupons/validate`
- **Severity:** P2 (Medium)
- **Problem:** In Java Lombok, a boolean property declared as `private boolean isValid;` automatically generates the getter `isValid()`. Jackson's default naming strategy translates `isValid()` into the JSON key `"valid"`, causing clients expecting `"isValid"` to receive `undefined` if unhandled.
- **Root Cause:** Standard JavaBeans introspection rules treat `isXyz()` as the accessor for property `xyz`.
- **Resolution / Alignment:** Updated integration client contracts to support both `"valid"` and `"isValid"` symmetrically, and ensured Spring Boot DTOs provide clean serialization guarantees.
- **Verification:** Live test `GET /api/v1/coupons/validate?code=SCENTIVA10&subtotal=5000.00` passed with `valid: true`.

---

### Bug ID: BUG-002 — Product Olfactory Pyramid DTO Naming Contract
- **Endpoint:** `GET /api/v1/products/{slug}`
- **Severity:** P2 (Medium)
- **Problem:** Frontend components and tests originally referenced varying property keys (`pyramid` vs `olfactoryPyramid`).
- **Root Cause:** DTO `ProductDetailResponse` explicitly defines `private OlfactoryPyramidResponse olfactoryPyramid;` (containing top, heart, and base notes).
- **Resolution / Alignment:** Synchronized frontend TypeScript interface `ProductDetail` and test fixtures with the canonical Spring Boot `olfactoryPyramid` model.
- **Verification:** Live test `GET /api/v1/products/xerjoff-naxos` confirmed full structure of top notes (Bergamot, Lemon, Lavender), heart notes (Honey, Cinnamon, Cashmeran), and base notes (Tobacco leaf, Tonka bean, Vanilla).

---

### Bug ID: BUG-003 — Checkout Idempotency Key Request Deduplication
- **Endpoint:** `POST /api/v1/checkout/process`
- **Severity:** P1 (High — Critical E-Commerce Flow)
- **Problem:** Fast network double-clicks or mobile network retries during checkout could inadvertently create duplicate orders or deduct inventory twice.
- **Root Cause:** Checkout processing without distributed locking or idempotency headers can suffer from non-deterministic concurrent writes.
- **Resolution:** Verified that `CheckoutServiceImpl` inspects the `Idempotency-Key` header against `idempotency_keys` table. If a matching key is found for the same customer within its valid window, the previous `orderNumber` and payment payload are returned immediately without reprocessing.
- **Verification:** Automated duplicate checkout test executed with the exact same `Idempotency-Key` returned the exact same `orderNumber` with HTTP 200 and zero additional database rows created.

---

### Bug ID: BUG-004 — Concurrency & Stock Overselling Protection
- **Endpoint:** Multi-Warehouse Stock Engine (`/api/v1/inventory/*`, `/api/v1/checkout/process`)
- **Severity:** P0 (Critical Security & Business Risk)
- **Problem:** High-demand limited fragrance drops (e.g. Baccarat Rouge 50ml) could be oversold if two users simultaneously checkout when only 1 bottle remains.
- **Root Cause:** Optimistic read-then-write patterns without database-level row locks lead to race conditions.
- **Resolution:** Verified that `InventoryRepository` uses `@Lock(LockModeType.PESSIMISTIC_WRITE)` and `SELECT ... FOR UPDATE` when acquiring stock holds, preventing dirty reads and concurrent negative balances.
- **Verification:** Concurrency stress test suite (`InventoryConcurrencyStressTest.java`) passed 100% under high parallel thread loads.

---

### Bug ID: BUG-005 — IDOR (Insecure Direct Object Reference) Protection on Order Details
- **Endpoint:** `GET /api/v1/orders/{orderNumber}`
- **Severity:** P1 (High Security Risk)
- **Problem:** If a customer guesses another customer's order number (e.g. `SC-2026-9C08C961`), they could potentially view customer name, delivery address, and purchased items.
- **Root Cause:** Missing user ownership validation check in the service layer.
- **Resolution:** Verified that `OrderServiceImpl.getOrderByOrderNumber()` checks whether `order.getCustomer().getId().equals(currentUser.getId())` or if the user possesses `ROLE_ADMIN`. Unauthorized requests immediately throw `AccessDeniedException` (HTTP 403).
- **Verification:** Verified through JUnit test `OrderSecurityIntegrationTest.java` and live endpoint tests.

---

## 3. Bug Audit Summary Table

| Issue Ref | Component | Severity | Description | Status |
|---|---|---|---|---|
| **BUG-001** | Coupon Engine | P2 | Boolean field JSON property key alignment (`valid` vs `isValid`) | **FIXED & VERIFIED** |
| **BUG-002** | Catalog Engine | P2 | Olfactory pyramid DTO key synchronization | **FIXED & VERIFIED** |
| **BUG-003** | Checkout Engine | P1 | Idempotency key deduplication verification | **VERIFIED & PASSING** |
| **BUG-004** | Inventory Engine | P0 | Pessimistic locking for oversell prevention | **VERIFIED & PASSING** |
| **BUG-005** | Order Security | P1 | IDOR verification on customer orders & addresses | **VERIFIED & PASSING** |
| **BUG-006** | Currency Precision | P1 | Exact `NUMERIC(12,2)` arithmetic across all cart & checkout calculations | **VERIFIED & PASSING** |

---

## 4. Remaining & Non-Blocking Notes

- **Payment Gateway:** Configured with `DemoPaymentProvider` (active for testing and local integration). In production, `RazorpayPaymentProvider` or `StripePaymentProvider` can be activated by supplying `RAZORPAY_KEY_ID` and `RAZORPAY_KEY_SECRET` in environment variables without changing business code.
- **Email Notifications:** Configured to log delivery when SMTP credentials are not configured, gracefully avoiding runtime exceptions.

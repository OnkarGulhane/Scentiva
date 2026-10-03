# SCENTIVA — Complete API Test Case Specifications

> **Scope:** Core End-to-End Test Matrix for Scentiva Haute Parfumerie API

---

## 1. Authentication & Security Test Cases

| Case ID | Endpoint | Description | Input Payload / Params | Expected HTTP Status | Expected Assertion |
|---|---|---|---|---|---|
| **TC-AUTH-001** | `POST /api/v1/auth/register` | Register new user with valid data | Valid name, unique email, strong password | **201 Created** | Response contains `accessToken`, `refreshToken`, customer ID |
| **TC-AUTH-002** | `POST /api/v1/auth/register` | Duplicate email rejection | Already registered email | **409 Conflict** | Error message indicates email is already taken |
| **TC-AUTH-003** | `POST /api/v1/auth/login` | Valid customer login | Correct email and password | **200 OK** | Returns valid JWT with 24h expiration |
| **TC-AUTH-004** | `POST /api/v1/auth/login` | Invalid password attempt | Correct email, wrong password | **401 / 400** | Authentication rejected |
| **TC-AUTH-005** | `GET /api/v1/auth/me` | Fetch authenticated identity | Header `Authorization: Bearer <token>` | **200 OK** | Matches customer ID and email in token |
| **TC-AUTH-006** | `GET /api/v1/auth/me` | Unauthenticated profile access | No Authorization header | **401 Unauthorized** | Request denied by `JwtAuthenticationEntryPoint` |
| **TC-AUTH-007** | `POST /api/v1/auth/change-password` | Rotate customer password | `currentPassword`, `newPassword` | **200 OK** | Password updated, old password no longer works |

---

## 2. Catalog & Discovery Test Cases

| Case ID | Endpoint | Description | Input Payload / Params | Expected HTTP Status | Expected Assertion |
|---|---|---|---|---|---|
| **TC-CAT-001** | `GET /api/v1/products` | Retrieve paginated products | `page=0&size=10` | **200 OK** | Returns array of products, totalElements $\ge 1$ |
| **TC-CAT-002** | `GET /api/v1/products/{slug}` | Retrieve product by slug (PDP) | `xerjoff-naxos` | **200 OK** | Returns `olfactoryPyramid` with top/heart/base notes and bottle variants |
| **TC-CAT-003** | `GET /api/v1/products/search` | Search fragrances by keyword | `q=Tobacco` | **200 OK** | Returns matching fragrance list |
| **TC-CAT-004** | `GET /api/v1/brands` | List luxury fragrance houses | None | **200 OK** | Returns Tom Ford, Creed, Xerjoff, Maison Francis Kurkdjian |
| **TC-CAT-005** | `GET /api/v1/categories` | List fragrance concentrations | None | **200 OK** | Returns Extrait de Parfum, Eau de Parfum, Oud & Attar |

---

## 3. Shopping Bag & Wishlist Test Cases

| Case ID | Endpoint | Description | Input Payload / Params | Expected HTTP Status | Expected Assertion |
|---|---|---|---|---|---|
| **TC-CART-001** | `POST /api/v1/cart/items` | Add product variant to cart | `variantId: 1, quantity: 2` | **200 OK** | Cart items array length $\ge 1$, total amount calculated |
| **TC-CART-002** | `GET /api/v1/cart` | Fetch current cart | Bearer token or `X-Session-Id` | **200 OK** | Returns active cart with exact item lines and prices |
| **TC-CART-003** | `PUT /api/v1/cart/items/{id}` | Update quantity of cart line | `quantity: 1` | **200 OK** | Cart line quantity updated, recalculates subtotal |
| **TC-CART-004** | `POST /api/v1/wishlist/{id}` | Save variant to wishlist | Path variable `variantId` | **200 OK** | Variant saved to user wishlist |
| **TC-CART-005** | `POST /api/v1/wishlist/{id}/move-to-cart` | Move wishlist item to cart | Path variable `variantId` | **200 OK** | Item moved from wishlist into active bag |

---

## 4. Promotion & Checkout Test Cases

| Case ID | Endpoint | Description | Input Payload / Params | Expected HTTP Status | Expected Assertion |
|---|---|---|---|---|---|
| **TC-PROMO-001** | `GET /api/v1/coupons/validate` | Validate coupon above threshold | `code=SCENTIVA10&subtotal=5000.00` | **200 OK** | Returns `valid: true` and 10% calculated discount |
| **TC-PROMO-002** | `GET /api/v1/coupons/validate` | Reject coupon below threshold | `code=SCENTIVA10&subtotal=1000.00` | **200 OK** | Returns `valid: false` with minimum spend error message |
| **TC-CHECK-001** | `GET /api/v1/checkout/review` | Compute checkout breakdown | `shippingAddressId=1&couponCode=SCENTIVA10` | **200 OK** | Computes subtotal, discount, delivery fee, tax, totalAmount |
| **TC-CHECK-002** | `POST /api/v1/checkout/process` | Place order with Idempotency Key | `Idempotency-Key: idem-12345` | **200 OK** | Creates order number `SC-2026-XXXXXXXX` and holds stock |
| **TC-CHECK-003** | `POST /api/v1/checkout/process` | Duplicate request with same Key | Same `Idempotency-Key: idem-12345` | **200 OK** | Returns identical order number, no duplicate database entry |
| **TC-CHECK-004** | `POST /api/v1/checkout/verify` | Verify payment and commit order | Order number, paymentId, signature | **200 OK** | Transitions order to `CONFIRMED`, commits inventory hold |

---

## 5. Orders & Fulfillment Test Cases

| Case ID | Endpoint | Description | Input Payload / Params | Expected HTTP Status | Expected Assertion |
|---|---|---|---|---|---|
| **TC-ORD-001** | `GET /api/v1/orders` | Customer order history | Bearer token | **200 OK** | Paginated list of user's past orders |
| **TC-ORD-002** | `GET /api/v1/orders/{orderNumber}` | Order detail snapshot | `orderNumber: SC-2026-XXXXXXXX` | **200 OK** | Returns full order snapshot, purchased items, and shipping address |
| **TC-ORD-003** | `POST /api/v1/orders/{orderNumber}/cancel` | Cancel order prior to shipment | Valid order number | **200 OK** | Order status becomes `CANCELLED`, inventory hold released |

---

## 6. Admin & RBAC Security Test Cases

| Case ID | Endpoint | Description | Input Payload / Params | Expected HTTP Status | Expected Assertion |
|---|---|---|---|---|---|
| **TC-ADMIN-001** | `GET /api/v1/admin/dashboard/summary` | Customer access to Admin API | Customer JWT | **403 Forbidden** | Access denied by Spring Security |
| **TC-ADMIN-002** | `GET /api/v1/admin/dashboard/summary` | Admin access to Admin API | Admin JWT (`admin@scentiva.com`) | **200 OK** | Returns executive KPIs (`totalRevenue`, `totalOrders`, etc.) |
| **TC-ADMIN-003** | `GET /api/v1/admin/users` | List administrative staff | Admin JWT | **200 OK** | Returns staff list with roles |
| **TC-ADMIN-004** | `GET /api/v1/observability/metrics` | Fetch JVM & DB telemetry | Admin JWT | **200 OK** | Returns memory, CPU, and connection pool metrics |
| **TC-ADMIN-005** | `POST /api/v1/shipping` | Create courier dispatch | `orderId: 1, carrierName: 'Bluedart'` | **200 OK** | Creates tracking number and sets order to `SHIPPED` |

---

## 7. AI Concierge Test Cases

| Case ID | Endpoint | Description | Input Payload / Params | Expected HTTP Status | Expected Assertion |
|---|---|---|---|---|---|
| **TC-AI-001** | `POST /api/v1/ai/scent-finder` | Scent Quiz recommendations | Personality, occasion, season, notes | **200 OK** | Returns array of matching fragrance flacons |
| **TC-AI-002** | `GET /api/v1/ai/semantic-search` | Natural language search | `q=warm spicy winter vanilla` | **200 OK** | Returns semantically matched fragrances |
| **TC-AI-003** | `POST /api/v1/ai/concierge/chat` | AI Fragrance Advisor chat | `message: "Recommend a date night perfume"` | **200 OK** | Returns conversational sommelier guidance |


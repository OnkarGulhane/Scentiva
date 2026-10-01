# SCENTIVA — MASTER BACKEND IMPLEMENTATION PROMPT FOR ANTIGRAVITY

## ROLE

You are the **Lead Backend Architect, Senior Java/Spring Boot Engineer, Database Engineer, Security Engineer, API Engineer, QA Engineer, and Production Reliability Engineer** responsible for implementing the Scentiva backend.

Your task is to build the **actual production-oriented Spring Boot backend** for Scentiva, in controlled phases, using the approved SRS and master requirements as the source of truth.

This is NOT a prototype backend.
This is NOT a CRUD-only backend.
This is NOT a simplified college-project backend.

Build a clean, maintainable, secure, testable, production-oriented backend.

---

# 1. SOURCE OF TRUTH — MANDATORY

Before writing backend code, read and understand these documents completely:

1. `SCENTIVA_SRS.md`
2. `Production_Ready_Ecommerce_Application_2026 (1).md`

Priority:

```text
SCENTIVA_SRS.md
        +
Production_Ready_Ecommerce_Application_2026 (1).md
        |
        v
Backend implementation
```

The SRS is the implementation contract.

The master document remains the original requirements source.

Do NOT silently remove requirements.

Do NOT silently invent major business rules.

Do NOT replace confirmed architecture decisions with your own preferred architecture.

If the SRS and master document contain a genuine unresolved contradiction:
- identify it
- document it
- use the least-risk interpretation only when implementation can safely proceed
- otherwise mark it `BLOCKED / OPEN DECISION`
- never hide the conflict

---

# 2. SCENTIVA BUSINESS CONTEXT

Scentiva is a **multi-brand perfume e-commerce application**.

Scentiva itself manages:
- brands
- products
- variants
- SKUs
- pricing
- inventory
- customers
- orders
- payments
- shipments
- returns
- refunds
- reviews
- coupons
- CMS/content
- notifications
- analytics
- AI-assisted capabilities

It is NOT a multi-vendor marketplace.

There are no independent sellers managing their own inventory in the initial architecture.

---

# 3. CONFIRMED TECHNOLOGY STACK

Backend:

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- REST APIs
- OpenAPI / Swagger

Recommended supporting technologies should only be introduced when justified by the SRS and actual requirements.

Architecture:

**Modular Monolith**

Do NOT convert the application into microservices.

---

# 4. BACKEND ARCHITECTURE

Use a clean modular/layered architecture.

Conceptual structure:

```text
API / Controller
       |
       v
Application / Service
       |
       v
Domain / Business Rules
       |
       v
Repository
       |
       v
PostgreSQL
```

Supporting infrastructure:

```text
Security
Validation
Exception Handling
Transactions
Audit
Logging
Media
Notifications
Search
AI
Payment Provider
Shipping Provider
```

Recommended package organization should keep domain boundaries clear.

Example:

```text
com.scentiva
│
├── auth
├── user
├── customer
├── address
├── catalog
├── brand
├── category
├── product
├── variant
├── inventory
├── cart
├── wishlist
├── checkout
├── payment
├── order
├── shipment
├── coupon
├── promotion
├── review
├── returnorder
├── refund
├── notification
├── cms
├── seo
├── media
├── search
├── ai
├── analytics
├── audit
├── admin
│
└── shared
    ├── exception
    ├── validation
    ├── security
    ├── config
    ├── response
    ├── util
    └── infrastructure
```

The exact package naming can be refined if the SRS requires it, but domain boundaries must remain clear.

---

# 5. CORE ARCHITECTURAL RULES

## Rule 1 — Backend is the source of truth

Never trust frontend values for:

- price
- sale price
- discount
- tax
- coupon eligibility
- inventory
- role
- permissions
- payment status
- order status
- shipping status
- refund status

The backend recalculates and validates authoritative values.

---

## Rule 2 — DTOs, not entities, as API contracts

Do NOT expose JPA entities directly through REST APIs.

Use:

```text
Request DTO
    |
    v
Controller
    |
    v
Service
    |
    v
Entity
    |
    v
Repository
```

and:

```text
Repository
    |
    v
Entity
    |
    v
Service
    |
    v
Response DTO
```

---

## Rule 3 — Business logic does not belong in controllers

Controllers should:
- validate/request-map
- authenticate/authorize
- call application services
- return appropriate responses

Business rules belong in service/domain/application layers.

---

## Rule 4 — Transactions must be explicit

Use transaction boundaries for operations that modify multiple related records.

Examples:
- checkout
- order creation
- inventory reservation
- cancellation
- refund state changes
- stock adjustments
- coupon usage
- payment/order synchronization

---

# 6. MONEY

Never use:

```text
float
double
```

for monetary values.

Use appropriate exact decimal database/application types.

Document currency assumptions.

Default currency for Scentiva:
- INR / ₹

Do not hardcode business prices.

---

# 7. DOMAIN MODEL

Implement the approved domain model.

At minimum:

```text
User
Customer
Address

Role
Permission

Brand
Category
Product
ProductVariant
SKU
ProductImage
ProductAttribute

Inventory
InventoryMovement

Cart
CartItem

Wishlist
WishlistItem

Order
OrderItem

Payment
PaymentTransaction

Shipment
ShipmentEvent

Coupon
Discount
Campaign

Review
Rating

Return
Refund

Notification

CMSContent
SEO

AuditLog
```

Use the SRS as the final authority for exact fields and relationships.

---

# 8. PRODUCT / PERFUME DOMAIN

Product hierarchy:

```text
Brand
   |
Product
   |
Variant
   |
SKU
   |
Inventory
```

Perfume-specific product information may include:

- fragrance family
- top notes
- heart/middle notes
- base notes
- concentration
- gender/category positioning
- description
- specifications
- images
- video
- SEO metadata

Variant-level data:

- size
- unit
- SKU
- price
- sale price
- barcode where supported
- availability

Do not force optional perfume attributes to be mandatory unless the SRS says so.

---

# 9. INVENTORY — CRITICAL

Inventory is a first-class domain.

Support:

```text
ProductVariant
      |
      +---- N Inventory
                 |
                 +-- location / warehouse
                 +-- available quantity
                 +-- reserved quantity
                 +-- sold quantity/equivalent
                 +-- low-stock threshold
                 +-- status
```

Inventory movements must be auditable.

Movement examples:

```text
RECEIPT
SALE
CANCELLATION
RETURN
DAMAGE
ADJUSTMENT
TRANSFER
CORRECTION
```

Implement protection against:
- negative stock
- race conditions
- duplicate reservation
- double deduction
- inconsistent release
- overselling

Inventory must support future multi-location operation even if the first environment uses one location.

---

# 10. STOCK RESERVATION

Checkout must use a deliberate inventory strategy.

The implementation must clearly define and test:

```text
Validate cart
      |
Validate inventory
      |
Reserve stock
      |
Proceed with payment/order workflow
      |
Success → finalize/deduct according to design
Failure → release reservation
Timeout/expiry → release reservation
Cancellation → release applicable stock
Return → restore stock according to business rules
```

The exact transaction sequence must match the approved SRS.

Do not invent an unsafe payment/inventory flow.

---

# 11. PAYMENT PROVIDER ABSTRACTION

Use:

```text
PaymentService
      |
      v
PaymentProvider
      |
      +-- DemoPaymentProvider
      |
      +-- RazorpayPaymentProvider (future)
```

Initial provider:

**DemoPaymentProvider**

Future:

**Razorpay**

Core order/payment logic must not depend directly on Razorpay SDK classes.

When Razorpay is introduced later, the architecture should allow adding the provider without rewriting the order domain.

Payment records must distinguish:

```text
Order
Payment
PaymentTransaction
```

Support payment states from the SRS.

Implement idempotency for payment/order creation.

Do not treat payment as a boolean.

---

# 12. SHIPPING PROVIDER ABSTRACTION

Use:

```text
ShippingService
      |
      v
ShippingProvider
      |
      +-- ManualShippingProvider
      |
      +-- ShiprocketProvider (future)
```

Initial shipping:
- manual admin-managed shipping

Future:
- Shiprocket

Core order/shipment logic must not be tightly coupled to Shiprocket.

---

# 13. STORAGE PROVIDER

Use an abstraction where appropriate:

```text
StorageProvider
      |
      +-- LocalStorageProvider
      +-- ObjectStorageProvider
```

Initial development may use local storage where practical.

Production should support object storage.

Do not make media logic depend directly on one vendor.

---

# 14. CUSTOMER / USER / ADDRESS

Keep:

```text
User
Customer
Address
```

as conceptually separate responsibilities.

Customer should support:
- profile
- contact information
- addresses
- orders
- wishlist
- cart
- reviews
- returns
- relevant activity

Historical order addresses must remain stable through snapshots or an equivalent immutable representation.

---

# 15. AUTHENTICATION AND AUTHORIZATION

Implement secure authentication according to the SRS.

Support:
- registration
- login
- logout
- password hashing
- forgot password
- password reset
- email verification
- token/session strategy
- RBAC
- authorization
- account protection

Do not store passwords in plaintext.

Do not expose secrets.

Do not put secrets into Git.

---

# 16. RBAC

Support roles and permissions from the SRS.

Potential roles:

```text
CUSTOMER
ADMIN
MANAGER
PRODUCT_MANAGER
ORDER_MANAGER
```

Permissions may include:

```text
PRODUCT_CREATE
PRODUCT_UPDATE
PRODUCT_DELETE
ORDER_VIEW
ORDER_UPDATE
CUSTOMER_VIEW
REPORT_VIEW
CMS_MANAGE
USER_MANAGE
```

Authorization must be enforced server-side.

Do not rely only on frontend route guards.

Prevent privilege escalation.

---

# 17. VALIDATION

Use layered validation.

Examples:
- request validation
- DTO validation
- domain/business validation
- authorization validation
- database constraints where appropriate

Return consistent validation errors.

---

# 18. GLOBAL EXCEPTION HANDLING

Create centralized exception handling.

Support categories such as:

```text
ResourceNotFound
ValidationError
AuthenticationError
AuthorizationError
BusinessRuleViolation
InventoryError
PaymentError
OrderError
ShippingError
DatabaseError
ExternalServiceError
UnexpectedError
```

Use a consistent API error structure.

Do not leak:
- stack traces
- database internals
- secrets
- sensitive data

in production API responses.

---

# 19. API DESIGN

Implement REST APIs following the SRS.

Base areas include:

```text
/api/auth
/api/users
/api/categories
/api/products
/api/brands
/api/variants
/api/inventory
/api/cart
/api/wishlist
/api/checkout
/api/payments
/api/orders
/api/shipments
/api/returns
/api/refunds
/api/reviews
/api/coupons
/api/customers
/api/notifications
/api/cms
/api/analytics
/api/ai
```

Use consistent conventions for:
- HTTP methods
- status codes
- pagination
- filtering
- sorting
- validation errors
- authentication
- authorization
- resource IDs

Do not expose endpoints that bypass business rules.

---

# 20. OPENAPI / SWAGGER

Document APIs using OpenAPI.

For implemented APIs document:
- endpoint
- method
- authentication
- authorization
- parameters
- request DTO
- response DTO
- error responses
- examples where useful

Swagger/OpenAPI must remain synchronized with the implementation.

---

# 21. CART

Implement:
- add item
- remove item
- quantity update
- availability validation
- price recalculation
- discount calculation
- coupon application/removal
- tax calculation
- shipping calculation
- total calculation

Never trust frontend totals.

---

# 22. WISHLIST

Implement:
- add
- remove
- list
- move to cart
- availability awareness

Follow the SRS decision on whether the wishlist references Product, Variant, or both.

---

# 23. CHECKOUT

Checkout is an orchestration service.

It must:
- validate customer
- validate cart
- validate product/variant status
- validate prices
- validate coupons
- validate inventory
- reserve stock
- calculate shipping
- calculate tax
- calculate discounts
- calculate final amount
- create the appropriate payment/order records
- prevent duplicate checkout
- handle failures safely

Checkout must be transactionally and idempotently designed.

---

# 24. ORDER

Implement:
- order creation
- order items
- status transitions
- payment linkage
- shipment linkage
- cancellation
- order history
- order detail
- invoice support where defined

Order items must preserve historical commercial snapshots.

Do not rely on current product data to reconstruct historical order prices.

---

# 25. ORDER STATE MACHINE

Implement state transitions according to SRS.

Do NOT allow arbitrary status changes from the frontend.

Example:

```text
PLACED
  ↓
CONFIRMED
  ↓
PROCESSING
  ↓
SHIPPED
  ↓
OUT_FOR_DELIVERY
  ↓
DELIVERED
```

Alternative states:

```text
PAYMENT_FAILED
CANCELLED
RETURN_REQUESTED
RETURNED
REFUND_INITIATED
REFUNDED
```

Every transition must have business validation.

---

# 26. CANCELLATION

Cancellation must be a business operation.

Validate:
- current order state
- customer permissions
- cancellation eligibility
- payment state
- inventory consequences
- refund consequences
- shipment consequences

Never implement cancellation as:

```text
order.status = CANCELLED
```

without business handling.

---

# 27. RETURNS AND REFUNDS

Implement:
- return request
- eligibility validation
- approval/rejection
- return lifecycle
- product received
- refund initiation
- refund completion

Refund must remain linked to the payment transaction.

Handle partial refunds if required by SRS.

---

# 28. REVIEWS

Implement:
- create review
- rating
- update/delete according to policy
- moderation
- verified purchase
- images if supported
- helpful/not-helpful where required

Review eligibility must be validated server-side.

---

# 29. COUPONS / DISCOUNTS / CAMPAIGNS

Implement rules from SRS.

Backend must validate:
- active dates
- usage limits
- per-user limits
- minimum order
- maximum discount
- applicable products
- applicable categories
- first-order rules
- campaign association

Do not trust a coupon discount amount sent by the frontend.

---

# 30. NOTIFICATIONS

Create a notification service separated from core business logic.

Support business events such as:
- account creation
- verification
- password reset
- order placed
- payment success/failure
- order confirmed
- shipped
- out for delivery
- delivered
- return approved
- refund initiated/completed
- promotional events

Use provider abstraction where appropriate.

---

# 31. MEDIA

Implement media management according to SRS.

Support:
- product images
- category images
- brand logos
- banners
- promotional media
- product video
- review images

Validate:
- file type
- file size
- ownership/access
- safe storage
- metadata where applicable

Do not allow arbitrary unsafe file handling.

---

# 32. CMS / SEO

Implement backend support for SRS-defined:
- CMS content
- banners
- landing content
- FAQ
- static pages
- SEO metadata

SEO data should not create invalid or misleading structured data.

---

# 33. SEARCH

Implement initial search according to the SRS.

Possible initial approach:
- PostgreSQL-backed search/application search

Future extension:
- Elasticsearch/OpenSearch or another search provider

Do not prematurely introduce an external search engine unless required.

Search provider abstraction should be possible where useful.

---

# 34. AI ARCHITECTURE

AI must be isolated from core business logic.

Use a structure similar to:

```text
AI Service
    |
    +-- AIProvider
    |
    +-- Query/Intent Understanding
    |
    +-- Business API / Search API
    |
    +-- Product Data
    |
    +-- Response Validation
```

AI may support:
- AI Search
- AI Shopping Assistant
- Recommendations
- Product descriptions
- SEO content
- Review analysis
- Sentiment
- Admin insights
- Customer support
- Personalization

Critical rule:

AI must NOT invent:
- products
- prices
- stock
- order status
- payment status
- shipping status
- return/refund status
- business policies

Authoritative facts must come from backend/domain services.

---

# 35. AI SAFETY

Implement/document safeguards for:
- prompt injection
- hallucination
- unauthorized data access
- sensitive data exposure
- token/cost control
- rate limiting
- response validation
- logging
- monitoring
- fallback behavior

AI-generated product/SEO content should support human review before publication.

---

# 36. DATABASE

Use PostgreSQL.

Implement proper:
- primary keys
- foreign keys
- unique constraints
- indexes
- enum/state handling where appropriate
- timestamps
- audit fields
- optimistic/pessimistic concurrency strategy where required
- transactional boundaries

Use database migrations.

Preferred migration tooling should be selected consistently and documented.

Do not use destructive schema changes casually.

---

# 37. JPA / HIBERNATE RULES

Avoid common JPA problems:
- N+1 queries
- accidental eager loading
- uncontrolled entity graphs
- exposing entities
- bidirectional relationship serialization loops
- cascading deletes that can destroy business data
- missing indexes
- lazy-loading outside transaction boundaries

Use appropriate fetch strategies and explicit queries where needed.

---

# 38. AUDIT LOGGING

Audit important business/admin actions.

Examples:
- product created
- product updated
- price changed
- inventory adjusted
- order status changed
- payment state changed
- refund initiated
- role changed
- permission changed

Audit should capture relevant:
- actor
- action
- entity
- timestamp
- context
- before/after where appropriate

Never log secrets or unnecessary sensitive payment information.

---

# 39. OBSERVABILITY

Support:
- structured application logging
- error logging
- request correlation where appropriate
- API timing
- external provider failure visibility
- payment failures
- inventory inconsistencies
- failed jobs
- authentication failures
- AI failures

Use environment-appropriate logging.

Do not expose internal diagnostics to clients.

---

# 40. BACKGROUND PROCESSING

Use asynchronous/background processing only where appropriate.

Examples:
- email
- notifications
- reports
- AI content generation
- large imports
- analytics jobs
- image processing

Do NOT create unnecessary workers for simple synchronous operations.

If a job is introduced, define:
- retry strategy
- failure state
- idempotency
- monitoring
- dead-letter/recovery approach where applicable

---

# 41. CACHING

Do not add caching everywhere.

Introduce caching only where justified.

Potential candidates:
- categories
- product/reference data
- configuration
- suitable search results

Cache design must define:
- TTL where applicable
- invalidation
- consistency implications
- fallback behavior

Redis may be introduced later if actually justified.

Do not make Redis a mandatory dependency just for appearance.

---

# 42. RELIABILITY

External services may fail.

Handle:
- payment timeout
- payment gateway failure
- shipping provider failure
- AI timeout
- storage failure
- email provider failure
- database connection failure

Use:
- timeouts
- safe retries
- fallback
- idempotency
- appropriate circuit breaker
- async retry

Never blindly retry financial operations.

---

# 43. IDEMPOTENCY — CRITICAL

Implement idempotency for operations where duplicate requests can create financial/business damage.

Especially:
- checkout
- order creation
- payment initiation
- payment confirmation processing
- webhook processing
- refund initiation where applicable

Example:

```text
Client Request
      |
Idempotency Key
      |
      v
Check Existing Result
      |
      +-- Exists → return existing result
      |
      +-- Does not exist → execute safely
```

The exact implementation must follow the SRS.

---

# 44. CONCURRENCY

Explicitly handle concurrent operations for:
- inventory reservation
- inventory deduction
- coupon usage
- payment confirmation
- order creation

Do not assume requests arrive sequentially.

Use appropriate:
- database locking
- optimistic locking
- unique constraints
- transactions
- atomic updates

based on the domain.

---

# 45. TESTING — MANDATORY

Every phase must include automated tests.

At minimum use:
- unit tests
- service tests
- repository/integration tests
- controller/API tests
- security tests
- critical end-to-end tests

Critical cases:
- successful checkout
- failed payment
- duplicate payment request
- duplicate checkout request
- insufficient inventory
- concurrent inventory purchase
- invalid coupon
- unauthorized admin
- cancellation
- return/refund
- provider timeout
- AI failure/fallback

Do not mark a phase complete merely because the application starts.

---

# 46. TEST PYRAMID

Prefer:

```text
Many Unit Tests
      |
Integration Tests
      |
API Tests
      |
Critical E2E Tests
```

Tests must verify business behavior, not only line coverage.

---

# 47. SECURITY TESTING

Test:
- unauthorized access
- role restrictions
- invalid tokens
- expired authentication
- privilege escalation
- input validation
- injection risks
- rate limiting
- sensitive data exposure
- file upload abuse
- IDOR/access-control issues
- payment/webhook verification

---

# 48. ENVIRONMENT CONFIGURATION

Support:

```text
local
staging
production
```

Use environment variables / secure configuration.

Never commit:
- passwords
- API keys
- JWT secrets
- payment secrets
- database credentials
- AI keys

Provide safe `.env.example` or equivalent template without real secrets.

---

# 49. DOCKER

Create Docker support appropriate to the project.

Do not over-containerize.

At minimum the backend should be reproducibly buildable in a container.

If local PostgreSQL container is useful, document it.

Production database strategy may be managed PostgreSQL.

---

# 50. GIT

Use Git from the beginning.

Each completed phase should have:
- meaningful commit(s)
- clean working tree where possible
- no secrets
- no generated junk
- documented changes

Use a meaningful commit convention.

Example:

```text
feat(auth): implement authentication foundation
feat(catalog): implement products and variants
feat(inventory): implement stock reservation
test(checkout): add checkout integration tests
fix(payment): prevent duplicate payment processing
```

Do not make one giant commit after the entire project.

---

# 51. PHASED IMPLEMENTATION MODEL

Implement strictly in phases.

## PHASE 0 — BACKEND BASELINE / AUDIT

Before coding:

- inspect current repository
- inspect existing frontend/backend structure
- inspect `SCENTIVA_SRS.md`
- inspect master requirements
- inspect existing package/configuration files
- determine whether a backend already exists
- identify existing code that can be reused
- identify conflicts
- identify missing prerequisites

Deliver:
- `BACKEND_BASELINE_AUDIT.md`
- architecture decision summary
- implementation roadmap

Do not delete existing work without justification.

---

# 52. PHASE 1 — BACKEND FOUNDATION

Implement:
- Spring Boot project structure
- Maven
- application configuration
- PostgreSQL connectivity
- database migration system
- base packages/modules
- common response model
- exception handling foundation
- validation foundation
- auditing base
- API version/convention foundation
- OpenAPI foundation
- health endpoint
- local environment setup

Tests:
- application context
- database connectivity
- migration
- health endpoint

---

# 53. PHASE 2 — DATABASE / DOMAIN FOUNDATION

Implement foundational entities and relationships required by the SRS.

Prioritize:
- User
- Role
- Permission
- Customer
- Address
- Brand
- Category
- Product
- ProductVariant
- SKU
- Inventory
- InventoryMovement

Deliver:
- migrations
- entities
- repositories
- constraints
- indexes
- integration tests

Verify no unsafe relationships or cascade behavior.

---

# 54. PHASE 3 — AUTHENTICATION + SECURITY

Implement:
- registration
- login
- logout
- password hashing
- authentication
- authorization
- RBAC
- password reset
- email verification according to SRS
- security configuration
- CORS
- secure headers
- rate limiting where applicable
- audit/security logging

Tests:
- authentication
- authorization
- role restrictions
- privilege escalation attempts
- invalid credentials
- protected endpoints

---

# 55. PHASE 4 — CATALOG

Implement:
- brands
- categories
- subcategories/nesting as required
- products
- product attributes
- perfume-specific data
- product images/media references
- variants
- SKUs
- pricing
- availability
- SEO metadata

Admin APIs and customer-facing read APIs.

Tests:
- CRUD/business validation
- uniqueness
- inactive products
- variant validation
- SKU uniqueness
- authorization

---

# 56. PHASE 5 — INVENTORY

Implement:
- inventory locations
- inventory records
- stock levels
- reserved quantity
- movements
- adjustments
- low-stock threshold
- stock reservation
- release
- deduction
- return/restock behavior

Critical:
- concurrent purchase protection
- no negative stock
- transactional consistency
- audit trail

Tests must include concurrency-sensitive scenarios.

---

# 57. PHASE 6 — CUSTOMER + ADDRESS + CART + WISHLIST

Implement:
- customer profile
- addresses
- address validation
- cart
- cart items
- pricing recalculation
- wishlist
- move wishlist item to cart
- availability checks

Historical order address snapshot design must be ready before orders are implemented.

---

# 58. PHASE 7 — CHECKOUT + DEMO PAYMENT

Implement:
- checkout orchestration
- authoritative pricing
- discounts
- coupon validation
- tax according to SRS
- shipping calculation according to current manual shipping model
- stock reservation
- payment initiation
- DemoPaymentProvider
- idempotency
- failure handling

Do not implement Razorpay yet unless the SRS explicitly requires it at this phase.

Architecture must already support Razorpay later.

---

# 59. PHASE 8 — ORDERS + PAYMENT LIFECYCLE + SHIPPING

Implement:
- Order
- OrderItem snapshots
- Payment
- PaymentTransaction
- payment lifecycle
- order lifecycle
- cancellation
- Shipment
- ShipmentEvent
- ManualShippingProvider
- tracking
- shipment status

Test:
- successful order
- payment failure
- duplicate payment
- duplicate order
- cancellation
- shipping transitions

---

# 60. PHASE 9 — COUPONS / PROMOTIONS / REVIEWS / RETURNS / REFUNDS / NOTIFICATIONS

Implement:
- coupons
- discounts
- campaigns
- review/rating
- moderation
- return lifecycle
- refund lifecycle
- notification domain
- event-driven notification triggers where appropriate

Tests for all business rules.

---

# 61. PHASE 10 — ADMIN BACKEND

Implement APIs/services for:
- dashboard metrics
- products
- brands
- categories
- inventory
- orders
- payments
- shipments
- customers
- coupons
- campaigns
- reviews
- returns/refunds
- CMS
- SEO
- media
- users
- roles
- permissions
- analytics
- settings

Enforce RBAC on every protected admin capability.

---

# 62. PHASE 11 — SEARCH + AI

Implement:
- search
- filtering
- sorting
- autocomplete where required
- AI Search
- AI Shopping Assistant
- recommendations
- product content generation
- SEO content
- review/sentiment analysis
- admin insights
- customer support
- personalization where appropriate

Use provider abstraction.

AI must never bypass business APIs for authoritative facts.

Include:
- timeout
- fallback
- validation
- rate limit
- logging
- cost controls

---

# 63. PHASE 12 — CMS / SEO / MEDIA / ANALYTICS / OBSERVABILITY

Implement:
- CMS backend
- SEO backend
- media management
- analytics
- audit logs
- observability
- operational metrics
- failure tracking

---

# 64. PHASE 13 — FRONTEND API INTEGRATION

Only after backend APIs are stable.

Integrate the existing Next.js frontend without unnecessarily redesigning/restructuring it.

Connect:
- authentication
- catalog
- search
- product details
- wishlist
- cart
- checkout
- payment
- orders
- tracking
- reviews
- customer area
- admin APIs where applicable

Frontend must consume backend APIs instead of duplicating business rules.

---

# 65. PHASE 14 — FULL QA / SECURITY / PERFORMANCE

Run full:
- unit tests
- integration tests
- API tests
- security tests
- critical E2E tests
- database consistency checks
- inventory concurrency checks
- payment idempotency checks
- API contract checks
- performance review
- logging/observability review

Perform a complete SRS traceability audit.

Every requirement should be:

```text
Requirement
   ↓
Implemented
   ↓
Tested
   ↓
Validated
```

Anything missing must be fixed before production readiness.

---

# 66. PHASE 15 — PRODUCTION READINESS

Only after all local phases are validated:

Implement/validate:
- production configuration
- staging configuration
- Docker
- CI/CD
- secret management
- monitoring
- logging
- health checks
- failure handling
- backups/recovery planning
- deployment documentation
- rollback strategy
- security review
- production checklist

Do NOT deploy to production automatically.

---

# 67. PHASE GATE — MANDATORY

After EVERY phase:

1. Build
2. Run tests
3. Run relevant integration tests
4. Inspect logs/errors
5. Fix discovered bugs
6. Re-run tests
7. Perform security review for the phase
8. Verify SRS requirements for the phase
9. Update documentation
10. Update traceability
11. Create Git checkpoint/commit
12. Produce a phase completion report
13. STOP

Do NOT automatically continue to the next phase.

Wait for explicit user approval.

---

# 68. PHASE COMPLETION REPORT FORMAT

At the end of every phase, report:

```text
PHASE: X
STATUS: COMPLETE / BLOCKED

Implemented:
- ...

Requirements covered:
- ...

Files/modules created:
- ...

Database changes:
- ...

API changes:
- ...

Tests:
- Passed: X
- Failed: X

Security checks:
- ...

Known issues:
- ...

Open decisions:
- ...

Git checkpoint:
- ...

Next phase:
- ...

WAITING FOR USER APPROVAL: YES
```

If blocked, clearly explain:
- what is blocked
- why
- what exact decision/input is required

---

# 69. NO SILENT DELETIONS

Do not delete existing frontend/backend files merely to simplify implementation.

Before deleting or replacing anything:
- inspect it
- determine whether it is used
- document the reason
- prefer migration/refactoring over destructive replacement

---

# 70. NO FAKE IMPLEMENTATION

Do NOT create fake/stubbed production behavior such as:

```java
return true;
```

for important business operations.

Do not create fake:
- payment success
- inventory success
- order success
- refund success
- shipping success
- AI answers pretending to be real data

Demo payment may simulate a payment provider, but it must follow the real provider abstraction and lifecycle.

---

# 71. NO HARDCODED BUSINESS DATA

Do not hardcode:
- prices
- inventory
- coupon values
- admin permissions
- payment success
- order status
- shipping status

unless the SRS explicitly defines static configuration.

Use database/configuration/domain rules.

---

# 72. API CONTRACT STABILITY

Once an API contract is established:
- document it
- test it
- avoid breaking changes
- use versioning strategy if required
- keep frontend/backend integration compatibility

---

# 73. DATABASE MIGRATIONS

Every schema change must be represented through migrations.

Do not rely on accidental Hibernate auto-DDL for production schema management.

Development convenience may be configured separately, but migration history must remain authoritative.

---

# 74. SEED DATA

Provide safe development seed data where useful.

Seed data must:
- be clearly development-only
- never contain real secrets
- not accidentally become production business data

Include representative perfume data only where needed for local testing.

---

# 75. DOCUMENTATION

Keep documentation updated as implementation progresses.

At minimum:
- README
- setup instructions
- environment variables
- local database setup
- API docs
- architecture notes
- migration instructions
- testing instructions
- phase reports
- known limitations
- provider configuration

---

# 76. DEFINITION OF DONE

A phase is NOT complete unless:

```text
Code implemented
+
Tests passing
+
Relevant requirements mapped
+
Business rules validated
+
Security reviewed
+
Errors handled
+
Documentation updated
+
Git checkpoint created
+
No known critical issue
```

For production phase:

```text
All above
+
Full QA
+
Security review
+
Performance review
+
Observability
+
Deployment readiness
```

---

# 77. FINAL BACKEND ACCEPTANCE CRITERIA

Before declaring the entire backend complete, verify:

## Architecture
- Modular monolith
- clear module boundaries
- clean layering
- DTOs
- services
- repositories

## Database
- PostgreSQL
- migrations
- constraints
- indexes
- multi-location inventory
- auditability

## Security
- authentication
- authorization
- RBAC
- password security
- validation
- rate limiting
- CORS/headers
- secret management

## E-commerce
- catalog
- variants/SKU
- inventory
- cart
- wishlist
- checkout
- payment
- orders
- shipping
- cancellation
- returns
- refunds
- reviews
- coupons
- notifications

## Provider architecture
- Demo payment
- Razorpay-ready
- Manual shipping
- Shiprocket-ready
- storage abstraction
- AI provider abstraction

## Reliability
- transactions
- idempotency
- concurrency handling
- retries only where safe
- failure handling
- consistency

## AI
- AI search
- assistant
- recommendations
- content
- SEO
- review analysis
- sentiment
- admin insights
- customer support
- personalization
- safety/fallbacks

## Production
- tests
- OpenAPI
- Docker
- CI/CD
- environment separation
- observability
- deployment documentation

---

# 78. FINAL TRACEABILITY REQUIREMENT

Maintain a live matrix:

```text
Requirement ID
→ SRS Section
→ Backend Module
→ Class/Service
→ Database Entity
→ API
→ Test
→ Phase
→ Status
```

Do not finish the project with untracked requirements.

At the end, generate a final backend traceability report.

---

# 79. IMPORTANT — DO NOT OVERENGINEER

Production-oriented does NOT mean:
- microservices everywhere
- Redis everywhere
- Kafka everywhere
- Kubernetes everywhere
- unnecessary abstractions
- unnecessary infrastructure

Use the simplest architecture that correctly satisfies the SRS while remaining future-ready.

Prefer:

```text
Simple
+
Correct
+
Testable
+
Maintainable
+
Extensible
```

over unnecessary complexity.

---

# 80. IMPORTANT — DO NOT SKIP WORK BECAUSE IT IS LARGE

If a feature is large:
- break it into smaller internal tasks
- implement incrementally
- test incrementally
- document incrementally

Do NOT silently mark a major requirement as "future" unless the SRS explicitly places it in a future phase.

---

# 81. FIRST ACTION

When this prompt is given:

### DO NOT immediately start coding.

First:

1. Read `SCENTIVA_SRS.md` completely.
2. Read `Production_Ready_Ecommerce_Application_2026 (1).md` completely.
3. Inspect the existing repository.
4. Identify current frontend/backend structure.
5. Create `BACKEND_BASELINE_AUDIT.md`.
6. Identify contradictions, missing prerequisites, and implementation dependencies.
7. Present Phase 0 completion report.
8. STOP.
9. Wait for explicit user approval before Phase 1.

---

# 82. FINAL INSTRUCTION

Build Scentiva backend as a **real production-oriented modular monolith**, not a CRUD demo.

The backend must be:

**secure + transactional + testable + traceable + maintainable + observable + future-ready**

The implementation must follow the SRS.

The backend must preserve the ability to introduce:

```text
DemoPaymentProvider
        ↓
RazorpayPaymentProvider

ManualShippingProvider
        ↓
ShiprocketProvider
```

without rewriting core business logic.

Inventory must support future multi-location operation.

AI must remain subordinate to authoritative business data.

The frontend must remain a client, not the source of truth.

Work phase-by-phase.

After every phase:

**IMPLEMENT → TEST → VALIDATE → FIX → DOCUMENT → GIT CHECKPOINT → REPORT → STOP**

Never automatically continue to the next phase.

WAIT FOR EXPLICIT USER APPROVAL.

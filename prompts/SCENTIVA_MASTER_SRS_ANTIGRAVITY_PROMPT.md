# SCENTIVA — MASTER SRS GENERATION PROMPT FOR ANTIGRAVITY

## ROLE

You are the lead Product Architect, Software Architect, Business Analyst, SRS Writer, and Requirements QA reviewer for **Scentiva**.

Your job in this task is ONLY to create a **complete, production-oriented Software Requirements Specification (SRS)** for the Scentiva e-commerce application.

Do NOT start coding the application.
Do NOT create the Spring Boot backend.
Do NOT modify the existing Next.js frontend.
Do NOT generate implementation code unless it is required as a tiny illustrative example inside the SRS.
Do NOT skip requirements because implementation will happen later.

The deliverable of this task is the SRS only.

---

# 1. SOURCE-OF-TRUTH RULE

You will be given a master requirements document:

**`Production_Ready_Ecommerce_Application_2026 (1).md`**

Read the ENTIRE document from beginning to end before writing the SRS.

Treat that document as the primary requirements source.

You MUST preserve and cover its:
- terminology
- business capabilities
- functional requirements
- technical requirements
- architecture principles
- security requirements
- AI requirements
- testing requirements
- DevOps/CI/CD requirements
- production requirements
- failure-handling requirements
- deployment requirements
- phase structure
- customer flow
- admin flow
- final architecture

Do not silently remove, simplify, or replace a requirement.

If something is ambiguous in the master document, explicitly mark it as:
- `OPEN DECISION`
- `ASSUMPTION`
- `REQUIRES CONFIRMATION`

Do NOT silently invent business rules.

---

# 2. SCENTIVA PROJECT CONTEXT

This is a **multi-brand perfume e-commerce application**.

Scentiva is NOT a perfume manufacturer and NOT a marketplace where independent sellers manage their own inventory.

Scentiva itself manages:
- products
- brands
- variants
- SKUs
- pricing
- inventory
- orders
- customers
- promotions
- shipping
- returns/refunds
- content
- AI-assisted capabilities

The product catalog contains perfumes from multiple brands.

The system must feel like a serious production-oriented e-commerce application.

IMPORTANT:
Do NOT frame the project as a simplified "college project" or reduce requirements because of educational context.

---

# 3. CONFIRMED TECHNOLOGY DIRECTION

Document the following as the current architecture direction:

## Frontend
- Existing Next.js application
- React/Next.js architecture
- Responsive UI
- Mobile-first design
- Existing frontend should be preserved during backend integration unless a future implementation decision explicitly requires a change

## Backend
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- REST APIs
- Maven
- OpenAPI / Swagger

## Database
- PostgreSQL

## Architecture style
- **Modular Monolith** initially
- Clear domain/module boundaries
- Production-oriented layered architecture

Conceptual backend:

```text
Next.js Frontend
       |
       | REST API
       v
Spring Boot Modular Monolith
       |
       +-- Auth / Users
       +-- Customer
       +-- Catalog
       +-- Brands
       +-- Categories
       +-- Products
       +-- Variants / SKU
       +-- Inventory
       +-- Cart
       +-- Wishlist
       +-- Checkout
       +-- Payments
       +-- Orders
       +-- Shipping
       +-- Coupons / Promotions
       +-- Reviews
       +-- Returns
       +-- Refunds
       +-- Notifications
       +-- CMS
       +-- SEO
       +-- Search
       +-- AI
       +-- Analytics
       +-- Media
       +-- Audit
       +-- Admin
       |
       v
PostgreSQL
```

Do NOT introduce microservices merely because they sound "more production-ready".
The initial architecture is a modular monolith unless a requirement explicitly proves otherwise.

---

# 4. FUTURE-PROOF PROVIDER ABSTRACTIONS

The SRS MUST explicitly define provider abstraction where external providers may change.

## Payment

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

Initial implementation:
- Demo payment

Future:
- Razorpay

The core order/payment business logic must NOT be tightly coupled to Razorpay-specific code.

Payment confirmation must support trusted server-side verification/webhook handling when a real gateway is introduced.

## Shipping

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

Initial implementation:
- Manual admin-managed shipping/delivery

Future:
- Shiprocket

The core order/shipment logic must NOT require rewriting when Shiprocket is introduced.

## Storage

```text
StorageProvider
      |
      +-- LocalStorageProvider
      +-- ObjectStorageProvider
```

## AI

```text
AIProvider
      |
      +-- Configured/Development Provider
      +-- Production AI Provider(s)
```

## Notifications

Conceptually support:
- Email
- SMS
- WhatsApp
- Push

## Search

Conceptually support:
- PostgreSQL/application-level search initially where appropriate
- Future external search engine such as Elasticsearch/OpenSearch if scale requires it

Do not force an external search engine into the first implementation without a requirement.

---

# 5. PERFUME DOMAIN MODEL

The SRS MUST explicitly adapt the generic e-commerce model to perfume commerce.

Primary hierarchy:

```text
Brand
   |
   v
Category
   |
   v
Product
   |
   v
Variant
   |
   v
SKU
   |
   v
Inventory
```

A perfume example:

```text
Brand: Dior

Product:
Sauvage Eau de Parfum

Variants:
- 30ml
- 60ml
- 100ml

Each sellable variant has:
- SKU
- price
- sale price where applicable
- inventory
- availability
- product/variant attributes
```

Product-level perfume attributes may include, where applicable:
- fragrance family
- top notes
- middle/heart notes
- base notes
- gender/category positioning
- concentration
- description
- ingredients/specifications where applicable
- brand
- category
- images
- videos
- SEO metadata

Variant-level data may include:
- size
- unit
- SKU
- price
- sale price
- barcode/future identifier
- availability
- inventory reference

Do not assume every perfume has every attribute; the SRS should define optional vs required attributes appropriately.

---

# 6. INVENTORY ARCHITECTURE

Inventory is a first-class domain.

The SRS MUST support future multi-location inventory.

Use this conceptual relationship:

```text
ProductVariant
      |
      +---- 1..N Inventory
                    |
                    +-- Pune Warehouse
                    +-- Mumbai Warehouse
                    +-- Delhi Warehouse
```

Even if the first deployment has only one location, the database/domain model must not block multiple inventory locations later.

Inventory should cover:
- available quantity
- reserved quantity
- sold quantity or equivalent accounting
- low-stock threshold
- warehouse/location
- stock status
- inventory movements
- adjustments

Inventory movement reasons may include:
- supplier receipt
- sale
- cancellation
- return
- damaged stock
- manual adjustment
- warehouse transfer
- stock correction

The SRS MUST explicitly cover:
- stock reservation
- reservation release
- stock deduction
- concurrent purchase protection
- no incorrect negative inventory
- inventory consistency with orders/payments

Do not reduce inventory to a simple `stock` integer.

---

# 7. CUSTOMER / USER / ADDRESS MODEL

Clearly distinguish:

## User
Identity/authentication/security account.

## Customer
Commerce-domain profile and customer relationship.

## Address
Separate customer-owned entity.

The SRS should support:
- customer profile
- multiple addresses
- address selection during checkout
- billing address where required
- shipping/delivery address

Historical orders MUST preserve an address snapshot or equivalent immutable historical representation so that editing a customer's address later does not change old orders.

Do NOT silently add guest checkout unless explicitly marked as an open decision because the master requirements do not clearly require guest checkout.

---

# 8. CART

Cart should be a business domain, not merely frontend state.

Support:
- add item
- remove item
- update quantity
- availability validation
- pricing recalculation
- discount calculation
- coupon application/removal
- tax calculation
- shipping calculation
- final total

Backend is authoritative for:
- price
- discount
- tax
- inventory
- permissions
- eligibility

Frontend values must never be trusted as final financial/business truth.

---

# 9. WISHLIST

Support:
- add product/variant
- remove
- view wishlist
- move to cart
- availability awareness
- optional price/availability notification capability

The SRS must define whether wishlist references Product, Variant, or both, and explain the chosen rule.

---

# 10. CHECKOUT

Checkout is an orchestration/business process.

Conceptual flow:

```text
Cart
  |
  v
Customer
  |
  v
Address
  |
  v
Shipping Method
  |
  v
Coupon / Discount
  |
  v
Tax
  |
  v
Payment Method
  |
  v
Payment
  |
  v
Order
```

Checkout must define:
- cart validation
- product/variant availability
- price validation
- discount validation
- coupon validation
- shipping selection
- tax calculation
- final amount calculation
- payment method
- order creation
- inventory reservation/consistency
- idempotency
- error/failure behavior

Do not invent a complex external tax provider unless required.
If tax provider architecture is useful, document it as an extensibility point or open decision.

---

# 11. PAYMENT DOMAIN

Keep these separate:

```text
Order
Payment
PaymentTransaction
```

Payment states should support the master requirements, including states such as:
- INITIATED
- PENDING
- SUCCESS
- FAILED
- CANCELLED
- REFUNDED
- PARTIALLY_REFUNDED

The SRS MUST explain:
- payment lifecycle
- payment/order relationship
- transaction history
- server-side verification
- webhook handling for real gateways
- duplicate payment protection
- idempotency
- failure recovery
- refund relationship

Initial payment:
- Demo payment provider

Future:
- Razorpay provider

Do not hardwire business logic to Razorpay.

---

# 12. ORDER DOMAIN

Order is a first-class domain.

Support:
- order creation
- order items
- order status lifecycle
- payment status
- shipment status
- cancellation
- order history
- invoice where applicable
- returns
- refunds

Order items MUST preserve historical snapshots such as:
- product name
- variant
- SKU
- quantity
- unit price
- discount
- final price
- other commercially relevant information needed for historical accuracy

Do not depend only on current Product records for historical order display.

Order status transitions must be business-rule controlled.

---

# 13. SHIPPING

Initial shipping model:
- Manual admin-managed shipping/delivery

Future provider:
- Shiprocket

Support conceptual:
- shipment
- shipment status
- tracking number where applicable
- shipment events
- delivery milestones
- provider reference
- shipping failure handling

The SRS should allow either:
- internal shipment events
- provider-backed shipment tracking

without coupling the core Order domain to one provider.

---

# 14. RETURNS AND REFUNDS

Support:
- return request
- approval/rejection
- pickup/return shipment
- product received
- refund initiation
- refund completion

States may include:
- RETURN_REQUESTED
- RETURN_APPROVED
- RETURN_REJECTED
- RETURN_PICKUP
- RETURN_RECEIVED
- REFUND_INITIATED
- REFUNDED

Refunds must be associated with payment transactions.

Business rules for return eligibility must be documented as configurable where appropriate rather than hardcoded assumptions.

---

# 15. REVIEWS AND RATINGS

Support:
- star rating
- written review
- review images
- verified purchase indicator
- review date
- helpful/not-helpful
- moderation status

The SRS must address:
- eligibility
- moderation
- abuse/spam prevention
- review lifecycle

---

# 16. COUPONS / DISCOUNTS / CAMPAIGNS

Support:
- percentage discount
- fixed discount
- product-specific
- category-specific
- minimum order value
- maximum discount
- usage limits
- per-user limits
- validity period
- first-order discount
- campaign association

Backend validates all pricing rules.

---

# 17. CMS / SEO / MEDIA

CMS should manage appropriate content such as:
- banners
- hero content
- promotional sections
- landing pages
- blogs
- FAQs
- offers
- static pages
- navigation menus
- footer content
- SEO metadata

Do NOT turn the whole frontend into an uncontrolled page builder.

SEO should include:
- slugs
- meta title
- meta description
- canonical URLs
- structured data where valid
- product schema
- organization schema
- breadcrumb schema
- review/rating structured data where valid
- sitemap
- robots configuration
- Open Graph/social metadata

Media should support:
- product images
- category images
- brand logos
- banners
- promotional images
- product videos
- review images

Include:
- file type validation
- file size validation
- access control
- optimization
- object storage/CDN as appropriate

---

# 18. ADMIN / RBAC

Define roles and permissions clearly.

Potential roles from the master requirements include:
- CUSTOMER
- ADMIN
- MANAGER
- PRODUCT_MANAGER
- ORDER_MANAGER

Permissions may include:
- PRODUCT_CREATE
- PRODUCT_UPDATE
- PRODUCT_DELETE
- ORDER_VIEW
- ORDER_UPDATE
- CUSTOMER_VIEW
- REPORT_VIEW
- CMS_MANAGE
- USER_MANAGE

The SRS must define authorization boundaries and prevent privilege escalation.

---

# 19. AUTHENTICATION AND SECURITY

Support/document:
- registration
- login
- logout
- password hashing
- forgot password
- password reset
- email verification
- OAuth/Google login where required/future
- session/token approach
- RBAC
- account protection

Security requirements:
- input validation
- secure password hashing
- CSRF protection where applicable
- CORS
- rate limiting
- secure headers
- secrets management
- SQL injection protection
- authentication
- authorization
- audit logging
- HTTPS in deployed environments

Never trust frontend-provided:
- price
- discount
- tax
- coupon eligibility
- inventory
- user role
- permissions
- order status
- payment status

---

# 20. AI REQUIREMENTS

AI is a meaningful business capability, not just a chatbot.

Document:
- AI Search
- AI Shopping Assistant
- AI Recommendations
- AI Product Description Generation
- AI SEO Content
- AI Review Analysis
- AI Sentiment Analysis
- AI Admin Insights
- AI Customer Support
- AI Personalization where appropriate

AI architecture:

```text
Spring Boot Application
        |
        +-- Business APIs
        |
        +-- AI Service
              |
              +-- LLM Provider
              +-- Embeddings
              +-- Vector Store
              +-- RAG where useful
              +-- Prompt Templates
              +-- Tool/Function Calling
              +-- Structured Outputs
              +-- Response Validation
```

Critical rule:

AI MUST NOT be the source of truth for:
- product existence
- product price
- inventory
- order status
- payment status
- shipping status
- return/refund status
- business policies

AI must retrieve authoritative application data.

AI safety must cover:
- prompt injection
- hallucination
- unauthorized data access
- sensitive data exposure
- excessive token usage
- cost control
- rate limiting
- response validation
- logging
- monitoring
- fallbacks

AI-generated product/SEO content should support:
```text
Generate
  |
  v
Human Review
  |
  v
Approve
  |
  v
Publish
```

High-impact AI recommendations should remain decision support unless explicitly approved for automation.

---

# 21. BACKEND ENGINEERING REQUIREMENTS

Document:
- REST API
- Controller layer
- Service layer
- Repository layer
- PostgreSQL
- DTOs
- validation
- global exception handling
- transactions
- logging
- audit
- media
- caching where justified
- background processing where justified
- OpenAPI/Swagger

DTOs should separate API contracts from persistence entities.

API error responses must be consistent.

Do not put all business logic in controllers.

---

# 22. DATABASE REQUIREMENTS

Document and define the conceptual data model for at least:

- User
- Customer
- Address
- Category
- Product
- ProductVariant
- SKU
- Brand
- ProductImage
- ProductAttribute
- Inventory
- InventoryMovement
- Cart
- CartItem
- Wishlist
- WishlistItem
- Order
- OrderItem
- Payment
- PaymentTransaction
- Shipment
- ShipmentEvent where appropriate
- Coupon
- Discount
- Campaign
- Review
- Rating
- Return
- Refund
- Notification
- CMSContent
- SEO
- Role
- Permission
- AuditLog

The SRS must include:
- entity purpose
- key fields
- important relationships
- cardinality
- constraints
- ownership
- lifecycle
- important indexes where relevant

Use exact numeric/decimal semantics for monetary values.
Do NOT recommend float/double for money.

---

# 23. API REQUIREMENTS

Define API groups such as:

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

For important endpoints document:
- purpose
- method
- authentication
- authorization
- request
- response
- validation
- error cases
- idempotency where applicable

Do not over-invent endpoint details where the master document only provides conceptual APIs. Mark implementation-level details that still require decision.

---

# 24. OBSERVABILITY / RELIABILITY / PRODUCTION

Document:
- application logs
- error tracking
- API latency
- DB performance
- payment failures
- failed jobs
- authentication failures
- inventory inconsistencies
- AI failures
- external service failures

Failure handling:
- timeouts
- safe retries
- fallbacks
- idempotency
- circuit breaker where appropriate
- asynchronous retry where appropriate

Financial operations must NOT be blindly retried.

Consistency must be maintained across:
- Order
- Payment
- Inventory
- Shipment
- Refund

---

# 25. PERFORMANCE

Document:
- DB indexes
- efficient queries
- pagination
- caching
- loading strategies
- connection pooling
- image optimization
- response size
- search optimization
- CDN where appropriate
- background jobs where genuinely useful

Do NOT recommend premature optimization.

Caching must consider invalidation and consistency.

---

# 26. TESTING

The SRS must define:
- unit tests
- service/business tests
- repository/integration tests
- API/controller tests
- security tests
- end-to-end scenarios

Important scenarios:
- successful checkout
- failed payment
- out-of-stock
- invalid coupon
- unauthorized admin access
- duplicate checkout/payment request
- cancellation
- return/refund
- inventory changes
- concurrent stock purchase
- provider timeout/failure
- AI unavailable/fallback

---

# 27. GIT / CI/CD / DOCKER / ENVIRONMENTS

Document:
- Git
- meaningful commits
- branching
- pull requests
- code review
- `.gitignore`
- secret protection
- releases/tags

CI/CD:
```text
Git Push
   |
   v
CI
   +-- Build
   +-- Test
   +-- Security Checks
   |
   v
Package / Docker Image
   |
   v
Deployment
```

Environments:
- Development
- Testing/Staging
- Production

Secrets/configuration must be externalized.

Examples:
- DATABASE_URL
- DATABASE_USERNAME
- DATABASE_PASSWORD
- JWT_SECRET
- PAYMENT_API_KEY
- PAYMENT_SECRET
- AI_API_KEY
- EMAIL_API_KEY
- STORAGE_KEY

Never hardcode secrets.

---

# 28. LOCAL → STAGING → PRODUCTION

The SRS must explicitly support this deployment progression:

```text
LOCAL DEVELOPMENT
       |
       v
LOCAL VALIDATION
       |
       v
STAGING
       |
       v
STAGING VALIDATION
       |
       v
PRODUCTION
```

Do not assume production deployment before local correctness is established.

---

# 29. REQUIREMENT TRACEABILITY — MANDATORY

This is one of the most important sections.

Create a complete traceability matrix:

```text
Requirement ID
Master Document Section
Requirement
Scentiva Interpretation
Domain / Module
Database Entity
API Area
Frontend Area
Admin Area
Validation
Security Consideration
Test Scenario
Implementation Phase
Status
Open Decision / Assumption
```

Every major requirement from the master document MUST map into this matrix.

No major feature should exist only in prose without traceability.

---

# 30. REQUIREMENT IDs

Create stable IDs such as:

```text
AUTH-001
CAT-001
PROD-001
VAR-001
INV-001
CART-001
WISH-001
CHK-001
PAY-001
ORD-001
SHIP-001
RET-001
REF-001
REV-001
COUP-001
CMS-001
SEO-001
NOTIF-001
ADMIN-001
AI-001
SEC-001
API-001
DB-001
TEST-001
DEVOPS-001
OBS-001
```

Use a consistent numbering convention.

---

# 31. ACCEPTANCE CRITERIA

Every major functional requirement should have testable acceptance criteria.

Avoid vague statements such as:
- "system should be fast"
- "AI should be good"
- "payment should work"

Instead define observable behavior.

Example:

```text
Given a valid customer with sufficient inventory,
when checkout is submitted,
then the backend validates the authoritative cart state,
creates the appropriate order/payment records,
and prevents duplicate order creation for the same idempotency key.
```

Keep acceptance criteria implementation-neutral where appropriate.

---

# 32. NON-FUNCTIONAL REQUIREMENTS

Create explicit NFR sections for:
- security
- performance
- scalability
- availability
- reliability
- maintainability
- observability
- auditability
- accessibility
- responsive behavior
- privacy/data protection
- usability
- compatibility
- deployment
- recoverability

Do not invent numeric SLAs unless they are explicitly defined as targets/open decisions.

---

# 33. BUSINESS RULES

Create a dedicated business-rules section.

Cover rules for:
- pricing
- discounts
- coupons
- inventory
- reservation
- checkout
- payment
- order state transitions
- cancellation
- returns
- refunds
- review eligibility
- permissions
- AI data authority

Each rule should have an ID.

---

# 34. STATE MACHINES

Where useful, define state transitions for:

### Payment
```text
INITIATED
PENDING
SUCCESS
FAILED
CANCELLED
REFUNDED
PARTIALLY_REFUNDED
```

### Order
```text
PLACED
CONFIRMED
PROCESSING
SHIPPED
OUT_FOR_DELIVERY
DELIVERED
CANCELLED
RETURN_REQUESTED
RETURNED
REFUND_INITIATED
REFUNDED
PAYMENT_FAILED
```

Do not permit arbitrary frontend-driven transitions.

If a state transition is not fully defined by the master document, mark the exact rule as an OPEN DECISION rather than inventing it.

---

# 35. END-TO-END FLOWS

Document complete flows for:

## Customer
```text
Home
→ Search/Category
→ Listing
→ Product Details
→ Wishlist / Cart
→ Checkout
→ Address
→ Coupon
→ Shipping
→ Tax
→ Payment
→ Order Confirmation
→ Tracking
→ Delivery
→ Review / Return
→ Refund
```

## Admin
```text
Login
→ Dashboard
→ Products
→ Categories
→ Brands
→ Inventory
→ Orders
→ Payments
→ Shipments
→ Returns/Refunds
→ Customers
→ Coupons/Campaigns
→ Reviews
→ CMS
→ Analytics
→ AI Insights
→ Users/Roles/Permissions
→ Settings
```

---

# 36. ARCHITECTURAL DECISION RECORDS

Create a concise ADR/decision section documenting confirmed decisions:

1. Modular monolith initially
2. Next.js existing frontend preserved
3. Spring Boot backend
4. PostgreSQL
5. Demo payment initially
6. Razorpay as future payment provider
7. Manual shipping initially
8. Shiprocket as future shipping provider
9. Multi-location inventory capable
10. Provider abstraction for external systems
11. AI is assistive and must use authoritative business data
12. Local → staging → production
13. No college-project simplification
14. No unnecessary microservices
15. Backend is source of truth
16. Production-oriented engineering standards

Clearly distinguish:
- confirmed decision
- assumption
- open decision

---

# 37. PHASE MAPPING

Map requirements to implementation phases.

Use the master phases as the baseline:

### Phase 1 — Foundation
Requirements, architecture, Git/GitHub, project setup, DB design, ER diagram, UI architecture, API conventions

### Phase 2 — Authentication and Security

### Phase 3 — Catalog

### Phase 4 — Discovery and Shopping

### Phase 5 — Checkout

### Phase 6 — Payments and Orders

### Phase 7 — Post-Purchase

### Phase 8 — Administration

### Phase 9 — AI

### Phase 10 — Production Engineering

If implementation needs finer sub-phases, create them beneath these master phases without deleting the master phases.

---

# 38. SRS DOCUMENT STRUCTURE

Create the final file:

**`SCENTIVA_SRS.md`**

Recommended structure:

1. Document Control
2. Executive Summary
3. Purpose
4. Scope
5. Product Vision
6. Scentiva Business Context
7. Goals and Success Criteria
8. Stakeholders
9. User Roles
10. Assumptions
11. Constraints
12. Open Decisions
13. Functional Requirements
14. Customer Experience
15. Catalog
16. Brands
17. Categories
18. Products
19. Variants/SKU
20. Inventory
21. Search
22. AI Search
23. Wishlist
24. Cart
25. Customer
26. Address
27. Checkout
28. Pricing/Discounts/Coupons
29. Payment
30. Orders
31. Shipping
32. Returns
33. Refunds
34. Reviews/Ratings
35. Notifications
36. CMS
37. SEO
38. Media
39. Admin
40. Analytics
41. AI Capabilities
42. Security
43. Backend Architecture
44. Frontend Architecture
45. Database Architecture
46. API Architecture
47. Provider Abstraction
48. Transactions/Consistency
49. Idempotency
50. Failure Handling
51. Observability
52. Performance
53. Background Processing
54. Caching
55. Testing Strategy
56. Git/CI/CD
57. Docker
58. Environments
59. Deployment
60. Privacy/Data Protection
61. End-to-End Flows
62. State Machines
63. Business Rules
64. Non-Functional Requirements
65. Acceptance Criteria
66. Requirement Traceability Matrix
67. Phase Mapping
68. Architecture Decision Records
69. Risks
70. Dependencies
71. Open Questions
72. SRS Completion Checklist

You may reorganize sections for readability, but MUST NOT omit coverage.

---

# 39. QUALITY GATE — MANDATORY BEFORE FINISHING

Before declaring the SRS complete, perform an internal requirements QA audit.

Check all of the following:

### Coverage
- Did every major section of the master document get represented?
- Did every major feature get an ID?
- Did every major feature get acceptance criteria?
- Did every major feature get phase mapping?
- Did every major feature get traceability?

### Architecture
- Modular monolith?
- Spring Boot?
- PostgreSQL?
- Next.js preserved?
- Provider abstraction?
- Demo payment → Razorpay future?
- Manual shipping → Shiprocket future?
- Multi-location inventory?
- Order/Payment/PaymentTransaction separated?
- Shipment separate?
- Address snapshots?
- Inventory reservation?
- Idempotency?
- AI authority boundaries?

### Security
- Auth?
- RBAC?
- Validation?
- CORS?
- CSRF where applicable?
- Rate limiting?
- Secrets?
- Audit?
- Authorization?

### Production
- Testing?
- CI/CD?
- Docker?
- Environment separation?
- Monitoring?
- Failure handling?
- Privacy?
- Deployment?

### AI
- AI Search?
- Assistant?
- Recommendations?
- Content?
- SEO?
- Review analysis?
- Sentiment?
- Admin insights?
- Customer support?
- Personalization?
- Hallucination protection?
- Prompt injection?
- Provider abstraction?
- Human review?

### E-commerce integrity
- Backend-authoritative pricing?
- Inventory concurrency?
- Payment verification?
- Duplicate request protection?
- Order snapshots?
- Refund relationship?
- State transitions?

If anything is missing:
1. identify it,
2. add it,
3. re-run the audit.

Do NOT finish with known missing requirements.

---

# 40. CONTRADICTION / AMBIGUITY AUDIT

Before finalizing, identify contradictions between:
- master document
- Scentiva-specific decisions
- database relationships
- API design
- business flows
- state transitions

Do not silently resolve a meaningful contradiction.

Instead:
```text
Conflict
→ Evidence
→ Recommended interpretation
→ Status
```

Use `OPEN DECISION` if user confirmation is genuinely required.

Do NOT create fake certainty.

---

# 41. SCOPE CONTROL

The SRS must be complete but must not silently expand scope.

Do not add unrelated features merely because they are common in e-commerce.

If a useful feature is not explicitly required by the master document:
- place it under `Optional / Future Extension`, or
- place it under `Open Decision`

Do not make optional features mandatory without justification.

---

# 42. DOCUMENT QUALITY

The SRS must be:
- professional
- structured
- implementation-ready
- internally consistent
- readable
- detailed
- testable
- traceable
- production-oriented

Avoid:
- filler
- repeated generic explanations
- vague requirements
- contradictory statements
- unexplained architecture changes
- invented business rules

Use tables where useful.

Use diagrams in Mermaid where helpful.

Use stable terminology throughout the document.

---

# 43. IMPORTANT: DO NOT CODE

This task ends when the SRS is complete and QA-validated.

Do NOT:
- scaffold Spring Boot
- create Java files
- create entities
- create controllers
- create repositories
- modify Next.js
- install dependencies
- configure Docker
- configure GitHub Actions
- create database migrations

Those are later implementation phases.

---

# 44. REQUIRED OUTPUT FILES

Create:

### Primary
`SCENTIVA_SRS.md`

### Optional QA report
`SCENTIVA_SRS_QA_REPORT.md`

The QA report should contain:
- source document reviewed
- coverage summary
- architecture validation
- contradiction/ambiguity findings
- open decisions
- assumptions
- missing requirements found and fixed during audit
- final completion status

If you create the QA report, it must not contradict the SRS.

---

# 45. FINAL COMPLETION REPORT

At the end of the task, provide a concise report containing:

```text
SRS STATUS: COMPLETE / BLOCKED

Master document fully reviewed: YES/NO
Major requirements covered: YES/NO
Traceability completed: YES/NO
Acceptance criteria completed: YES/NO
Architecture validated: YES/NO
Security requirements covered: YES/NO
AI requirements covered: YES/NO
Production requirements covered: YES/NO
Contradiction audit completed: YES/NO
Open decisions identified: YES/NO
Implementation started: NO
```

If BLOCKED:
- explain exactly what information is missing
- do not invent it

---

# 46. STOP CONDITION — VERY IMPORTANT

After creating and QA-validating the SRS:

**STOP.**

Do not continue into backend implementation.

Do not proceed to the next development phase automatically.

The next phase will start only after explicit user approval.

---

# 47. FINAL INSTRUCTION

Your objective is not to make the SRS merely long.

Your objective is to make it:

**complete + consistent + traceable + testable + production-oriented + Scentiva-specific + future-ready**

The master requirements document remains the primary source of truth.

Scentiva-specific confirmed architecture decisions must be incorporated explicitly.

Where the master document is silent or ambiguous, clearly label the decision instead of inventing certainty.

Before completion, perform the full QA audit described above.

Then create `SCENTIVA_SRS.md`, optionally create `SCENTIVA_SRS_QA_REPORT.md`, provide the completion report, and STOP.

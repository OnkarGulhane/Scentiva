# SCENTIVA — SRS GENERATION PROMPT

## Role

You are the **Lead Software Architect + Senior Business Analyst + SRS Author** for the Scentiva e-commerce application.

Your job in this task is **ONLY to create the complete Software Requirements Specification (SRS)** for Scentiva.

Do NOT start coding.
Do NOT modify the application.
Do NOT redesign the existing frontend.
Do NOT skip requirements.
Do NOT invent unsupported business requirements.

The SRS must become the approved implementation contract for the next development stages.

---

# 1. AUTHORITATIVE SOURCE OF TRUTH

Use the following document as the primary and authoritative requirements source:

`Production_Ready_Ecommerce_Application_2026(1).md`

This document contains the production-oriented e-commerce requirements that must be covered.

Before writing the SRS:

1. Read the entire master document.
2. Understand all 77 major numbered sections and their supporting subsections.
3. Extract all functional, technical, security, testing, AI, admin, customer, payment, inventory, operational and deployment requirements.
4. Do not silently omit a requirement because this is currently a college-level project.
5. Do not silently replace requirements with simpler alternatives.
6. If a requirement needs a college-level/local implementation, preserve the requirement and explicitly define the initial implementation boundary plus the future-ready extension.
7. Clearly distinguish:
   - Required now
   - Required as an architecture/extension point
   - Future production integration
8. If the master document does not define a detail, mark it as:
   `TBD / Decision Required`
   rather than inventing a business rule.

---

# 2. SCENTIVA BUSINESS CONTEXT

Scentiva is a **multi-brand perfume e-commerce application**.

Important business clarification:

- Scentiva sells/manages the products and inventory itself.
- It is NOT a marketplace where external sellers independently manage their own stores.
- Multiple perfume brands are sold through Scentiva.
- The existing frontend is a Next.js application.
- Backend will be Spring Boot.
- Database will be PostgreSQL.
- Development starts locally.
- Production deployment happens only after local implementation, testing and validation are complete.
- Payment is Demo Payment initially, with a Razorpay-ready architecture.
- Shipping/delivery is initially managed manually by Admin.
- The architecture must remain extensible for future production integrations.
- The project is currently college-level, but engineering practices should remain production-oriented.

---

# 3. PERFUME-SPECIFIC DOMAIN MAPPING

The generic e-commerce requirements from the master document must be mapped correctly to Scentiva.

Use this domain relationship:

Brand
→ Product
→ Variant
→ SKU
→ Inventory

A perfume product may contain:

- Brand
- Product name
- Description
- Gender / target audience
- Concentration
- Fragrance family
- Top notes
- Heart notes
- Base notes
- Longevity
- Sillage
- Occasion
- Season
- Country of origin
- Ingredients
- Images/media
- Variants
- Sizes
- SKU
- Price
- Discount
- Stock
- Product status
- SEO metadata
- Reviews
- Ratings

Do not force irrelevant generic e-commerce concepts into the perfume domain.

---

# 4. EXISTING FRONTEND CONTEXT

The existing Scentiva frontend is substantially implemented using:

- Next.js
- App Router
- TypeScript
- Tailwind CSS
- Three.js / React Three Fiber
- GSAP
- Lenis
- Typed API abstraction
- Domain service structure
- Store/context state
- Customer pages
- Admin pages
- Local/demo data

The SRS must define how the future Spring Boot backend integrates with this existing frontend.

Do NOT propose unnecessary frontend restructuring.

---

# 5. REQUIRED SRS STRUCTURE

Create a complete SRS with at least the following sections.

## 1. Document Control
- Document title
- Version
- Status
- Source of truth
- Scope
- Approval model

## 2. Product Vision

## 3. Business Objectives

## 4. Product Scope

Clearly define:
- Customer scope
- Admin scope
- Backend scope
- Database scope
- AI scope
- Security scope
- Testing scope
- Deployment scope

## 5. Out of Scope / Deferred Production Integrations

Only defer things that are genuinely implementation-stage decisions.

Examples may include:
- Razorpay production credentials
- Real courier provider
- SMS provider
- Cloud object storage
- Advanced external search engine
- Production AI provider

Do NOT use "future" as an excuse to remove master requirements.

## 6. User Roles

Cover all relevant roles from the master requirements.

At minimum evaluate:
- Customer
- Super Admin
- Admin
- Catalog Manager
- Order Manager
- Marketing Manager
- Content Manager
- Support Agent
- Analyst

Define permissions at capability level.

## 7. Functional Requirements

Create unique IDs such as:

FR-CAT-001
FR-PROD-001
FR-CART-001
FR-ORD-001

Every major requirement must have:
- ID
- Requirement
- Description
- Actor
- Preconditions
- Main flow
- Alternate flow
- Validation
- Error conditions
- Dependencies
- Acceptance criteria
- Implementation phase

## 8. Customer Requirements

Cover all applicable areas from the master:

- Home
- Categories
- Product listing
- Product details
- Variants
- SKU
- Search
- Advanced filtering
- AI Search
- Wishlist
- Cart
- Checkout
- Address management
- Payments
- Orders
- Order tracking
- Cancellation
- Returns
- Refunds
- Reviews
- Ratings
- Customer support
- Notifications
- Recommendations
- Personalization

## 9. Product & Catalog Requirements

Cover:
- Brands
- Categories
- Products
- Variants
- SKU
- Pricing
- Discounts
- Product status
- Media
- SEO
- Product metadata
- Perfume-specific attributes

## 10. Inventory Requirements

Define:
- Available stock
- Reserved stock
- Sold stock
- Stock adjustments
- Inventory movements
- Low-stock handling
- Stock validation
- Overselling prevention
- Order reservation/release
- Admin inventory operations
- Transactional consistency

## 11. Cart & Wishlist

Define:
- Add/remove
- Quantity
- Variant/SKU selection
- Price refresh
- Stock validation
- Guest/customer behaviour
- Wishlist operations
- Cart persistence

## 12. Checkout

Define:
- Address
- Shipping calculation
- Tax/GST
- Discounts
- Final totals
- Payment
- Order creation
- Failure recovery
- Idempotency

## 13. Payment Requirements

Use this architecture:

PaymentService
→ PaymentGateway
→ DemoGateway
→ RazorpayGateway (future)

Define:
- Payment lifecycle
- Payment status
- Transaction records
- Demo payment
- Razorpay readiness
- Server-side validation
- Signature verification
- Webhooks
- Duplicate payment protection
- Idempotency
- Refund support
- Failure/abandoned payment handling
- Security of credentials and logs

Never allow the frontend to be the source of truth for successful payment.

## 14. Order Management

Define the order lifecycle.

Recommended lifecycle:

PENDING
→ CONFIRMED
→ PROCESSING
→ PACKED
→ SHIPPED
→ OUT_FOR_DELIVERY
→ DELIVERED

Also define applicable states:

CANCELLED
PAYMENT_FAILED
RETURN_REQUESTED
RETURN_APPROVED
RETURNED
REFUND_INITIATED
REFUNDED

Define valid state transitions and invalid transitions.

## 15. Shipping & Delivery

Initial implementation:
- Admin-managed/manual delivery workflow.

Define an abstraction that can later support:
- Courier integration
- Tracking provider
- Shipping APIs

## 16. Coupons / Discounts / Promotions

Cover:
- Coupon creation
- Validation
- Expiry
- Usage limits
- Per-user limits
- Minimum order value
- Product/category applicability
- Discount calculation
- Admin management
- Invalid coupon handling

## 17. Reviews & Ratings

Cover:
- Review submission
- Rating
- Eligibility
- Moderation
- Editing/removal rules
- Abuse prevention
- Review analysis

## 18. Customer Management

Cover:
- Customer profile
- Addresses
- Orders
- Account data
- Support
- Activity where appropriate

## 19. Admin / Business Management

Cover:
- Dashboard
- Products
- Brands
- Categories
- Inventory
- Orders
- Payments
- Shipments
- Returns/refunds
- Customers
- Coupons
- Campaigns
- Reviews
- CMS
- SEO
- Media
- Notifications
- Analytics
- Users
- Roles
- Permissions
- Settings

## 20. CMS

Define manageable content such as:
- Banners
- Homepage sections
- Promotional content
- Static content
- SEO content

## 21. Notifications

Define:
- In-app notifications
- Email architecture
- Event triggers
- Notification templates
- Provider abstraction
- Future SMS/Push integration
- Failure handling

## 22. Search

Define PostgreSQL-based initial search.

Cover:
- Keyword search
- Brand
- Category
- Fragrance family
- Concentration
- Gender
- Price
- Size
- Rating
- Occasion
- Season
- Availability
- Sorting
- Pagination

Architecture should allow future external search infrastructure without rewriting domain logic.

## 23. AI Requirements

Do NOT remove the AI scope.

Cover:
- AI Search
- AI Shopping Assistant
- Recommendations
- Product description generation
- SEO content generation
- Review/sentiment analysis
- Admin insights
- Customer support
- Personalization
- AI safety/reliability
- Provider abstraction
- Fallback behaviour
- Cost/control considerations
- Human/admin override

Clearly distinguish:
- V1 local implementation
- Future production AI integration

## 24. Analytics

Define:
- Product views
- Search activity
- Cart events
- Checkout events
- Orders
- Revenue
- Inventory indicators
- Customer activity
- Admin dashboard metrics
- AI/admin insights

## 25. Authentication & Authorization

Cover:
- Registration
- Login
- Password handling
- Session/token strategy
- Role-based access control
- Permission checks
- Account security
- Admin authorization
- Future OTP readiness

## 26. Database Requirements

Define the logical data model.

At minimum evaluate entities such as:

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
- ProductMedia
- Inventory
- InventoryMovement
- Cart
- CartItem
- Wishlist
- WishlistItem
- Coupon
- Promotion
- Order
- OrderItem
- Payment
- PaymentTransaction
- Shipment
- Return
- Refund
- Review
- Notification
- CMSContent
- AuditLog
- Analytics/Event records
- AI-related records where necessary

For each entity define:
- Purpose
- Important fields
- Relationships
- Constraints
- Indexing considerations
- Audit requirements

Money must use exact decimal/numeric representation, never floating point.

Order items must preserve historical snapshot information such as:
- Product name
- SKU
- Variant
- Quantity
- Unit price
- Discount
- Final price

## 27. API Requirements

Define REST API requirements.

For each major module include:
- Endpoint
- HTTP method
- Authentication
- Authorization
- Request
- Response
- Validation
- Error response
- Pagination/filtering
- Idempotency where required

Maintain clean separation between:
- Controller
- DTO
- Service
- Domain/business logic
- Repository/data layer

## 28. Backend Architecture

Use:

Next.js
↓ REST API
Spring Boot
↓
PostgreSQL

Backend should be a modular monolith initially.

Avoid unnecessary microservices.

Define module boundaries and dependencies.

## 29. Security Requirements

Cover:
- Password security
- Authentication
- Authorization
- Input validation
- SQL injection protection
- XSS considerations
- CSRF where applicable
- CORS
- Secure headers
- Secrets management
- Environment variables
- Sensitive logging prevention
- File upload validation
- Payment security
- Admin protection
- Rate limiting where appropriate
- Audit logging
- Privacy/data handling

## 30. Error Handling & Failure Scenarios

Explicitly define behaviour for:
- Product unavailable
- Stock changed during checkout
- Payment failure
- Duplicate payment request
- Duplicate order request
- Coupon expiry
- Invalid coupon
- Database failure
- External integration failure
- Notification failure
- AI provider failure
- Timeout
- Partial failure
- Invalid state transition

## 31. Non-Functional Requirements

Cover:
- Performance
- Scalability
- Availability
- Maintainability
- Accessibility
- Responsiveness
- SEO
- Internationalization readiness
- Observability
- Logging
- Auditability
- Security
- Reliability

## 32. Testing Requirements

Define:
- Unit tests
- Integration tests
- Repository tests
- Controller/API tests
- Security tests
- Business rule tests
- Payment tests
- Inventory tests
- Order lifecycle tests
- Failure tests
- Frontend/backend integration tests
- Regression tests
- Performance checks
- Manual QA

Do not only test happy paths.

## 33. Local Development Requirements

Define the local environment:

Next.js → localhost:3000
Spring Boot → localhost:8080
PostgreSQL → localhost:5432

Define:
- Environment variables
- Database setup
- Seed/demo data
- Local file/media storage
- Demo payment
- API documentation
- Logging

## 34. Staging & Production Readiness

Define the transition:

LOCAL
→ STAGING
→ PRODUCTION

Production must not begin before local completion and validation.

## 35. CI/CD

Cover:
- Git
- GitHub
- Branching
- Pull/merge validation
- Automated tests
- Build
- Environment configuration
- Deployment pipeline
- Rollback considerations

## 36. Observability

Cover:
- Application logs
- Error logs
- Audit logs
- Health checks
- Metrics
- Traceability
- Important business events

## 37. Git / Development Governance

Every implementation phase must end with:

1. Tests
2. Validation
3. Bug fixing
4. Documentation update
5. Git checkpoint
6. Commit
7. Completion report
8. STOP

The next phase may start only after explicit user approval.

## 38. Implementation Roadmap

Create a complete phased roadmap that maps every requirement to an implementation phase.

Recommended architecture:

PHASE 0 — SRS + Requirements + Scope Freeze
PHASE 1 — Architecture + Database + API Contract
PHASE 2 — Spring Boot Foundation + PostgreSQL
PHASE 3 — Authentication + RBAC + Security
PHASE 4 — Brands + Categories + Products + Variants
PHASE 5 — Inventory + SKU
PHASE 6 — Customer + Address + Cart + Wishlist
PHASE 7 — Checkout + Demo Payment
PHASE 8 — Orders + Order Lifecycle
PHASE 9 — Coupons + Discounts + Promotions
PHASE 10 — Reviews + Returns + Refunds + Notifications
PHASE 11 — Admin Backend + Dashboard APIs
PHASE 12 — Next.js ↔ Spring Boot Integration
PHASE 13 — Complete QA + Security + Performance
PHASE 14 — Production Readiness + Razorpay Test Mode

You may refine phase boundaries only if the master document requires it, but do not reduce scope.

## 39. Traceability Matrix

Create a master matrix:

Requirement ID
→ Master Document Section
→ Scentiva Feature
→ Database
→ API
→ Frontend
→ Admin
→ Validation
→ Test
→ Phase
→ Status

The matrix must make it possible to prove that no major master requirement was silently skipped.

## 40. Acceptance Criteria

Every major module must have measurable acceptance criteria.

Examples:
- API returns correct validation errors.
- Unauthorized user cannot access admin endpoint.
- Stock cannot become negative.
- Duplicate checkout cannot create duplicate order.
- Payment success is verified by backend.
- Invalid order state transition is rejected.
- Coupon rules are enforced server-side.

## 41. Decision Log

Include confirmed decisions and unresolved decisions separately.

Confirmed decisions must include:
- Scentiva-owned inventory
- Spring Boot backend
- PostgreSQL
- Demo payment initially
- Razorpay-ready architecture
- Manual admin shipping initially
- Local-first development
- Staging before production
- Existing Next.js frontend remains the customer-facing application
- Phase-by-phase implementation
- Explicit approval required before moving to the next phase

Do not mark unresolved decisions as confirmed.

---

# 6. IMPORTANT RULES

### Rule 1 — No Requirement Skipping

If a master requirement exists, it must appear in the SRS or be explicitly mapped to another requirement.

### Rule 2 — No Silent Simplification

Do not remove production-oriented requirements merely because this is a college project.

Instead write:

`Initial Local Implementation`
+
`Future Production Extension`

### Rule 3 — No Unnecessary Complexity

Do not introduce microservices, Kubernetes, Elasticsearch, complex distributed infrastructure, or other infrastructure unless the master requirements actually require it.

### Rule 4 — Backend Owns Business Truth

The SRS must make clear that critical business rules belong to the backend.

### Rule 5 — Frontend Must Not Be Trusted

Never treat client-side:
- price
- stock
- discount
- payment success
- authorization
- order total

as authoritative.

### Rule 6 — Failure Scenarios Are Requirements

Failure handling is part of the feature, not an optional extra.

### Rule 7 — Future-Ready Does Not Mean Future-Only

An architecture may be extensible, but required local functionality must actually be defined.

### Rule 8 — Do Not Code

This task ends when the SRS is complete and internally cross-checked.

---

# 7. FINAL SRS QUALITY CHECK

Before finishing, perform an internal audit:

- [ ] Entire master document reviewed
- [ ] All major sections represented
- [ ] Customer scope covered
- [ ] Admin scope covered
- [ ] Product/catalog scope covered
- [ ] Inventory covered
- [ ] Cart/wishlist covered
- [ ] Checkout covered
- [ ] Payments covered
- [ ] Orders covered
- [ ] Shipping covered
- [ ] Returns/refunds covered
- [ ] Reviews covered
- [ ] Marketing covered
- [ ] CMS covered
- [ ] Notifications covered
- [ ] Analytics covered
- [ ] AI covered
- [ ] Security covered
- [ ] Database covered
- [ ] APIs covered
- [ ] Testing covered
- [ ] Failure scenarios covered
- [ ] CI/CD covered
- [ ] Observability covered
- [ ] Local/staging/production covered
- [ ] Git governance covered
- [ ] Perfume-specific domain mapping covered
- [ ] Traceability matrix created
- [ ] Acceptance criteria created
- [ ] No unsupported assumptions presented as confirmed decisions

## OUTPUT

Create:

`SCENTIVA_SRS.md`

This document will become the implementation contract for the backend and frontend integration phases.

STOP after producing and validating the SRS.
Do not start implementation until the user explicitly approves the SRS.

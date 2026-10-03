# SCENTIVA — Complete Backend API Inventory

> **Architecture:** Spring Boot 3.3.4 (Java 21 LTS) Modular Monolith  
> **Database:** PostgreSQL 17 (`scentiva_db` with 35 relational tables)  
> **Security Protocol:** Stateless JWT (HMAC SHA-512) Filter Chain with Role-Based Access Control (RBAC)  
> **Base URL:** `http://localhost:8080/api/v1`

---

## 1. Authentication & Identity Management (`/api/v1/auth`)

| Method | Endpoint | Description | Auth | Role | Request Body / Query Params | Response Format |
|---|---|---|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Register new customer account | Public | Anonymous | `RegisterRequest` (firstName, lastName, email, password, phone) | `ApiResponse<AuthResponse>` (accessToken, refreshToken, user) |
| `POST` | `/api/v1/auth/login` | Authenticate customer or administrator | Public | Anonymous | `LoginRequest` (email, password) | `ApiResponse<AuthResponse>` |
| `POST` | `/api/v1/auth/refresh` | Refresh expired access token | Public | Anonymous | `RefreshTokenRequest` (refreshToken) | `ApiResponse<AuthResponse>` |
| `GET` | `/api/v1/auth/me` | Retrieve authenticated identity profile | Required | Any | Header `Authorization: Bearer <JWT>` | `ApiResponse<CustomerProfileResponse>` |
| `POST` | `/api/v1/auth/change-password` | Update user password | Required | Any | `ChangePasswordRequest` (currentPassword, newPassword) | `ApiResponse<Void>` |
| `POST` | `/api/v1/auth/forgot-password` | Request password reset token | Public | Anonymous | `ForgotPasswordRequest` (email) | `ApiResponse<Void>` |
| `POST` | `/api/v1/auth/reset-password` | Execute password reset via token | Public | Anonymous | `ResetPasswordRequest` (token, newPassword) | `ApiResponse<Void>` |

---

## 2. Customer Profile & Address Book (`/api/v1/customer`)

| Method | Endpoint | Description | Auth | Role | Request Body / Query Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/customer/profile` | Get customer profile details | Required | `ROLE_CUSTOMER` | None | `ApiResponse<CustomerProfileResponse>` |
| `PUT` | `/api/v1/customer/profile` | Update profile information | Required | `ROLE_CUSTOMER` | `UpdateProfileRequest` | `ApiResponse<CustomerProfileResponse>` |
| `GET` | `/api/v1/customer/addresses` | List customer shipping/billing addresses | Required | `ROLE_CUSTOMER` | None | `ApiResponse<List<AddressResponse>>` |
| `POST` | `/api/v1/customer/addresses` | Add new delivery address | Required | `ROLE_CUSTOMER` | `AddressRequest` (fullName, phone, line1, line2, city, state, postalCode, isDefault) | `ApiResponse<AddressResponse>` |
| `PUT` | `/api/v1/customer/addresses/{id}` | Update existing address | Required | `ROLE_CUSTOMER` | `AddressRequest` | `ApiResponse<AddressResponse>` |
| `DELETE` | `/api/v1/customer/addresses/{id}` | Soft-delete customer address | Required | `ROLE_CUSTOMER` | Path variable `id` | `ApiResponse<Void>` |
| `PATCH` | `/api/v1/customer/addresses/{id}/default`| Mark address as default | Required | `ROLE_CUSTOMER` | Path variable `id` | `ApiResponse<AddressResponse>` |

---

## 3. Product Catalog & Fragrance Discovery (`/api/v1/products`, `/brands`, `/categories`)

| Method | Endpoint | Description | Auth | Role | Request Body / Query Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/products` | Paginated catalog with multi-faceted filtering | Public | Anonymous | `page, size, brandSlug, categorySlug, minPrice, maxPrice, scentFamily, sort` | `ApiResponse<ApiPaginatedResponse<ProductSummaryResponse>>` |
| `GET` | `/api/v1/products/{slug}` | Product detail with Olfactory Pyramid & Variants | Public | Anonymous | Path variable `slug` | `ApiResponse<ProductDetailResponse>` |
| `GET` | `/api/v1/products/featured` | Curated editorial & best-seller showcase | Public | Anonymous | Query `limit` (default 8) | `ApiResponse<List<ProductSummaryResponse>>` |
| `GET` | `/api/v1/products/search` | Full-text catalog keyword search | Public | Anonymous | Query `q` | `ApiResponse<List<ProductSummaryResponse>>` |
| `POST` | `/api/v1/products` | Create new product entity | Required | `ROLE_ADMIN` | `ProductCreateRequest` | `ApiResponse<ProductDetailResponse>` |
| `PUT` | `/api/v1/products/{id}` | Update product & olfactory metadata | Required | `ROLE_ADMIN` | `ProductUpdateRequest` | `ApiResponse<ProductDetailResponse>` |
| `DELETE` | `/api/v1/products/{id}` | Soft-delete product catalog entity | Required | `ROLE_ADMIN` | Path variable `id` | `ApiResponse<Void>` |
| `GET` | `/api/v1/brands` | List luxury fragrance houses | Public | Anonymous | None | `ApiResponse<List<BrandResponse>>` |
| `GET` | `/api/v1/brands/{slug}` | Get brand details & heritage story | Public | Anonymous | Path variable `slug` | `ApiResponse<BrandResponse>` |
| `GET` | `/api/v1/categories` | List fragrance categories & concentrations | Public | Anonymous | None | `ApiResponse<List<CategoryResponse>>` |
| `GET` | `/api/v1/categories/{slug}` | Get category details | Public | Anonymous | Path variable `slug` | `ApiResponse<CategoryResponse>` |

---

## 4. Multi-Location Inventory & Stock Engine (`/api/v1/inventory`, `/warehouses`)

| Method | Endpoint | Description | Auth | Role | Request Body / Query Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/inventory/variant/{id}/available` | Get net available stock (Physical - Holds) | Public | Anonymous | Path variable `id` | `ApiResponse<Integer>` |
| `GET` | `/api/v1/inventory/variant/{id}` | Multi-warehouse stock breakdown | Required | `ROLE_ADMIN` | Path variable `id` | `ApiResponse<List<InventoryResponse>>` |
| `POST` | `/api/v1/inventory/adjust` | Manual stock audit adjustment | Required | `ROLE_ADMIN` | `InventoryAdjustRequest` | `ApiResponse<InventoryResponse>` |
| `POST` | `/api/v1/inventory/transfer` | Inter-warehouse stock rebalancing | Required | `ROLE_ADMIN` | `StockTransferRequest` | `ApiResponse<Void>` |
| `GET` | `/api/v1/inventory/low-stock` | Low-stock threshold warning report | Required | `ROLE_ADMIN` | Query `threshold` | `ApiResponse<List<LowStockResponse>>` |
| `GET` | `/api/v1/inventory/movements/variant/{id}` | Audit log of all inventory delta transactions | Required | `ROLE_ADMIN` | Path variable `id` | `ApiResponse<List<InventoryMovementResponse>>` |
| `GET` | `/api/v1/warehouses` | List all regional fulfillment hubs | Public | Anonymous | None | `ApiResponse<List<WarehouseResponse>>` |

---

## 5. Shopping Bag Engine (`/api/v1/cart`)

| Method | Endpoint | Description | Auth | Role | Headers / Request Body | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/cart` | Retrieve user/guest active shopping bag | Optional | Any | Header `X-Session-Id` (guest) or Bearer | `ApiResponse<CartResponse>` |
| `POST` | `/api/v1/cart/items` | Add product variant to bag | Optional | Any | `CartItemAddRequest` (variantId, quantity) | `ApiResponse<CartResponse>` |
| `PUT` | `/api/v1/cart/items/{itemId}` | Update item quantity in bag | Optional | Any | `CartItemUpdateRequest` (quantity) | `ApiResponse<CartResponse>` |
| `DELETE` | `/api/v1/cart/items/{itemId}`| Remove item line from bag | Optional | Any | Path variable `itemId` | `ApiResponse<CartResponse>` |
| `DELETE` | `/api/v1/cart` | Clear entire bag | Optional | Any | None | `ApiResponse<Void>` |
| `POST` | `/api/v1/cart/merge` | Merge guest session bag into authenticated account | Required | `ROLE_CUSTOMER` | Header `X-Session-Id` | `ApiResponse<CartResponse>` |

---

## 6. Wishlist Engine (`/api/v1/wishlist`)

| Method | Endpoint | Description | Auth | Role | Request Body | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/wishlist` | Retrieve customer curated wishlist | Required | `ROLE_CUSTOMER` | None | `ApiResponse<WishlistResponse>` |
| `POST` | `/api/v1/wishlist/{variantId}` | Save fragrance variant to wishlist | Required | `ROLE_CUSTOMER` | Path variable `variantId` | `ApiResponse<WishlistResponse>` |
| `DELETE` | `/api/v1/wishlist/{variantId}` | Remove fragrance from wishlist | Required | `ROLE_CUSTOMER` | Path variable `variantId` | `ApiResponse<WishlistResponse>` |
| `POST` | `/api/v1/wishlist/{variantId}/move-to-cart` | Transfer saved item directly to bag | Required | `ROLE_CUSTOMER` | Path variable `variantId` | `ApiResponse<CartResponse>` |

---

## 7. Promotion & Coupon Engine (`/api/v1/coupons`, `/campaigns`)

| Method | Endpoint | Description | Auth | Role | Request Body / Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/coupons/validate` | Validate coupon eligibility & compute discount | Public | Anonymous | Query `code, subtotal` | `ApiResponse<CouponValidationResponse>` |
| `GET` | `/api/v1/coupons` | List all active promo codes | Required | `ROLE_ADMIN` | None | `ApiResponse<List<CouponResponse>>` |
| `POST` | `/api/v1/coupons` | Create promotional coupon rule | Required | `ROLE_ADMIN` | `CouponCreateRequest` | `ApiResponse<CouponResponse>` |
| `GET` | `/api/v1/campaigns` | List active marketing campaigns & banners | Public | Anonymous | None | `ApiResponse<List<CampaignResponse>>` |

---

## 8. Checkout Orchestration & Payments (`/api/v1/checkout`)

| Method | Endpoint | Description | Auth | Role | Headers / Request Body | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/checkout/review` | Real-time pricing matrix & tax calculator | Required | `ROLE_CUSTOMER` | Query `shippingAddressId, couponCode` | `ApiResponse<CheckoutSummaryResponse>` |
| `POST` | `/api/v1/checkout/process` | Place order & acquire 15-min stock holds | Required | `ROLE_CUSTOMER` | Header `Idempotency-Key`, `CheckoutProcessRequest` | `ApiResponse<CheckoutResultResponse>` |
| `POST` | `/api/v1/checkout/verify` | Verify payment gateway signature & commit order | Required | `ROLE_CUSTOMER` | `PaymentVerificationRequest` (orderNumber, paymentOrderId, gatewayPaymentId, gatewaySignature) | `ApiResponse<OrderResponse>` |

---

## 9. Orders & Customer Fulfillment (`/api/v1/orders`)

| Method | Endpoint | Description | Auth | Role | Request Body / Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/orders` | Customer order history (paginated) | Required | `ROLE_CUSTOMER` | Query `page, size` | `ApiResponse<ApiPaginatedResponse<OrderSummaryResponse>>` |
| `GET` | `/api/v1/orders/{orderNumber}` | Immutable order detail with item snapshots | Required | `ROLE_CUSTOMER` | Path variable `orderNumber` | `ApiResponse<OrderResponse>` |
| `POST` | `/api/v1/orders/{orderNumber}/cancel` | Cancel order & release allocated inventory | Required | `ROLE_CUSTOMER` | Path variable `orderNumber`, `CancelOrderRequest` | `ApiResponse<OrderResponse>` |
| `GET` | `/api/v1/orders/admin` | Global order management console | Required | `ROLE_ADMIN` | Query `page, size, status, search` | `ApiResponse<ApiPaginatedResponse<OrderSummaryResponse>>` |
| `PATCH` | `/api/v1/orders/{orderNumber}/status` | Admin transition order fulfillment status | Required | `ROLE_ADMIN` | `OrderStatusUpdateRequest` | `ApiResponse<OrderResponse>` |

---

## 10. Shipping, Logistics & Dispatch (`/api/v1/shipping`)

| Method | Endpoint | Description | Auth | Role | Request Body / Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/shipping/track/{trackingNumber}` | Public courier shipment tracking timeline | Public | Anonymous | Path variable `trackingNumber` | `ApiResponse<ShipmentTrackingResponse>` |
| `GET` | `/api/v1/shipping/order/{orderId}` | Order shipment status & event history | Required | `ROLE_CUSTOMER` | Path variable `orderId` | `ApiResponse<ShipmentResponse>` |
| `POST` | `/api/v1/shipping` | Dispatch shipment & assign carrier tracking | Required | `ROLE_ADMIN` | `ShipmentCreateRequest` (orderId, carrierName, trackingNumber) | `ApiResponse<ShipmentResponse>` |
| `PUT` | `/api/v1/shipping/{shipmentId}/status` | Append transit checkpoint event | Required | `ROLE_ADMIN` | `ShipmentStatusUpdateRequest` | `ApiResponse<ShipmentResponse>` |

---

## 11. Customer Reviews & Ratings (`/api/v1/reviews`)

| Method | Endpoint | Description | Auth | Role | Request Body / Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/reviews/product/{productId}` | List verified reviews & aggregate rating | Public | Anonymous | Path variable `productId` | `ApiResponse<List<ReviewResponse>>` |
| `POST` | `/api/v1/reviews` | Submit verified purchase review | Required | `ROLE_CUSTOMER` | `ReviewCreateRequest` (productId, rating, title, comment) | `ApiResponse<ReviewResponse>` |
| `DELETE` | `/api/v1/reviews/{id}` | Moderation removal of review | Required | `ROLE_ADMIN` | Path variable `id` | `ApiResponse<Void>` |

---

## 12. Returns & Reverse Logistics (`/api/v1/returns`)

| Method | Endpoint | Description | Auth | Role | Request Body / Params | Response Format |
|---|---|---|---|---|---|---|
| `POST` | `/api/v1/returns` | Initiate return request for delivered order | Required | `ROLE_CUSTOMER` | `ReturnCreateRequest` (orderNumber, reason, items) | `ApiResponse<ReturnResponse>` |
| `GET` | `/api/v1/returns/customer` | List customer return requests | Required | `ROLE_CUSTOMER` | None | `ApiResponse<List<ReturnResponse>>` |
| `PATCH` | `/api/v1/returns/{id}/status` | Approve/Reject return & process refund | Required | `ROLE_ADMIN` | `ReturnStatusUpdateRequest` | `ApiResponse<ReturnResponse>` |

---

## 13. AI Scent Concierge & Semantic Search (`/api/v1/ai`)

| Method | Endpoint | Description | Auth | Role | Request Body / Params | Response Format |
|---|---|---|---|---|---|---|
| `POST` | `/api/v1/ai/scent-finder` | Multi-dimensional fragrance quiz recommendation | Public | Anonymous | `ScentQuizRequest` (personality, occasion, season, intensity, notes) | `ApiResponse<ScentRecommendationResponse>` |
| `GET` | `/api/v1/ai/semantic-search` | Natural language olfactory search engine | Public | Anonymous | Query `q` | `ApiResponse<List<ProductSummaryResponse>>` |
| `POST` | `/api/v1/ai/concierge/chat` | AI Fragrance Advisor conversational assistant | Public | Anonymous | `AiChatRequest` (message, conversationHistory) | `ApiResponse<AiChatResponse>` |

---

## 14. CMS, Editorial & Banners (`/api/v1/stories`, `/banners`)

| Method | Endpoint | Description | Auth | Role | Request Body / Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/stories` | Published editorial fragrance articles | Public | Anonymous | None | `ApiResponse<List<StoryResponse>>` |
| `GET` | `/api/v1/stories/{slug}` | Full editorial story content & linked flacons | Public | Anonymous | Path variable `slug` | `ApiResponse<StoryDetailResponse>` |
| `GET` | `/api/v1/banners` | Promotional hero & seasonal banners | Public | Anonymous | None | `ApiResponse<List<BannerResponse>>` |

---

## 15. SEO & Schema.org Microdata (`/api/v1/seo`)

| Method | Endpoint | Description | Auth | Role | Request Body / Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/seo/product/{slug}` | Product SEO meta tags & JSON-LD schema | Public | Anonymous | Path variable `slug` | `ApiResponse<SeoMetadataResponse>` |
| `GET` | `/api/v1/seo/sitemap` | Dynamic XML sitemap generator data | Public | Anonymous | None | `ApiResponse<SitemapResponse>` |

---

## 16. Customer Notifications (`/api/v1/notifications`)

| Method | Endpoint | Description | Auth | Role | Request Body / Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/notifications` | Customer notification feed | Required | `ROLE_CUSTOMER` | None | `ApiResponse<List<NotificationResponse>>` |
| `GET` | `/api/v1/notifications/unread-count` | Unread notification badge counter | Required | `ROLE_CUSTOMER` | None | `ApiResponse<Long>` |
| `PATCH` | `/api/v1/notifications/{id}/read` | Mark individual notification as read | Required | `ROLE_CUSTOMER` | Path variable `id` | `ApiResponse<Void>` |
| `POST` | `/api/v1/notifications/read-all` | Mark all notifications as read | Required | `ROLE_CUSTOMER` | None | `ApiResponse<Void>` |

---

## 17. Admin Management & Operations Console (`/api/v1/admin`)

| Method | Endpoint | Description | Auth | Role | Request Body / Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/admin/dashboard/summary` | Executive KPIs (Revenue, Orders, Customers, AOV) | Required | `ROLE_ADMIN` | None | `ApiResponse<DashboardSummaryResponse>` |
| `GET` | `/api/v1/admin/dashboard/sales-trend` | Monthly & daily revenue breakdown | Required | `ROLE_ADMIN` | Query `period` | `ApiResponse<List<SalesTrendResponse>>` |
| `GET` | `/api/v1/admin/dashboard/top-products` | Top revenue-generating fragrance flacons | Required | `ROLE_ADMIN` | None | `ApiResponse<List<TopProductResponse>>` |
| `GET` | `/api/v1/admin/dashboard/order-status-breakdown` | Orders distribution by status | Required | `ROLE_ADMIN` | None | `ApiResponse<Map<String, Long>>` |
| `GET` | `/api/v1/admin/users` | List administrative staff & roles | Required | `ROLE_ADMIN` | None | `ApiResponse<List<UserResponse>>` |
| `GET` | `/api/v1/admin/customers` | Customer analytics directory | Required | `ROLE_ADMIN` | Query `page, size, search` | `ApiResponse<ApiPaginatedResponse<CustomerSummaryResponse>>` |
| `GET` | `/api/v1/admin/settings` | Store configuration & payment gateway parameters | Required | `ROLE_ADMIN` | None | `ApiResponse<StoreSettingsResponse>` |
| `PUT` | `/api/v1/admin/settings` | Update store configuration | Required | `ROLE_ADMIN` | `StoreSettingsUpdateRequest` | `ApiResponse<StoreSettingsResponse>` |

---

## 18. Observability, Metrics & Auditing (`/api/v1/observability`, `/health`)

| Method | Endpoint | Description | Auth | Role | Request Body / Params | Response Format |
|---|---|---|---|---|---|---|
| `GET` | `/api/v1/health` | Comprehensive system health & dependencies | Public | Anonymous | None | `ApiResponse<HealthResponse>` |
| `GET` | `/api/v1/observability/metrics` | JVM, Database connection pool & memory telemetry | Required | `ROLE_ADMIN` | None | `ApiResponse<MetricsResponse>` |
| `GET` | `/api/v1/observability/audit-logs` | Tamper-evident administrative audit trail | Required | `ROLE_ADMIN` | Query `page, size, action` | `ApiResponse<ApiPaginatedResponse<AuditLogResponse>>` |


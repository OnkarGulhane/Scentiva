# SCENTIVA --- Design Tokens & UI System

**Version:** 1.0\
**Product:** Multi-brand perfume e-commerce marketplace\
**Brand direction:** Luxury + modern + colorful\
**Logo reference:** User-provided SCENTIVA logo (deep plum background,
pale blush emblem and wordmark).\
**Scope:** Mobile-first customer storefront, desktop web application,
responsive layouts, motion and 3D guidelines.

> This is a proposed implementation-ready design-token specification
> inspired by the supplied logo and the previously discussed mobile/web
> screen concepts. Product names, prices, and brand marks shown in
> mockups are illustrative; use authorized product assets and verified
> catalog data in production.

------------------------------------------------------------------------

## 1. Brand foundation

### Brand personality

-   **Premium:** refined typography, deliberate spacing, high-quality
    product imagery.
-   **Trustworthy:** clear pricing, authenticity information,
    transparent delivery/returns.
-   **Discoverable:** easy multi-brand browsing, filters, scent finder,
    comparison and wishlist.
-   **Contemporary:** restrained motion, responsive cards, rich
    editorial campaign sections.

### Logo usage

-   Primary wordmark: `SCENTIVA` in a high-contrast serif, pale blush on
    deep plum.
-   Supporting tagline: `SINCE 2026` in uppercase, letter-spaced
    sans-serif.
-   Keep the logo's original proportions; do not stretch, skew, recolor
    arbitrarily, or add effects.
-   Clear space: at least the height of the "S" around the mark.
-   Minimum wordmark width: **112 px** web, **96 px** mobile.
-   Use a monochrome dark version on light surfaces and blush/white
    version on dark surfaces.
-   The supplied emblem is a decorative brand mark; avoid using it as a
    tiny functional icon.

------------------------------------------------------------------------

## 2. Color tokens

### Core brand palette

  -------------------------------------------------------------------------
  Token                     HEX                     Usage
  ------------------------- ----------------------- -----------------------
  `color.brand.plum.950`    `#321027`               Deepest brand surface,
                                                    footer, dark hero

  `color.brand.plum.900`    `#451333`               Primary brand
                                                    background

  `color.brand.plum.800`    `#5B1B43`               Hover/active dark brand
                                                    surfaces

  `color.brand.plum.700`    `#742653`               Accent panels and
                                                    selected states

  `color.brand.blush.300`   `#E9B7D8`               Logo/brand accent on
                                                    dark surfaces

  `color.brand.blush.200`   `#F2D2E7`               Soft accent surface

  `color.brand.blush.100`   `#FAEAF4`               Subtle blush background

  `color.brand.rose.500`    `#B85B88`               Accent actions and
                                                    decorative details

  `color.brand.gold.500`    `#C7A66A`               Premium highlight,
                                                    badges, small details

  `color.brand.gold.100`    `#F5EBD7`               Soft premium background
  -------------------------------------------------------------------------

### Neutral and semantic palette

  Token                 HEX         Usage
  --------------------- ----------- -------------------------------
  `color.neutral.0`     `#FFFFFF`   Cards, inputs
  `color.neutral.50`    `#FAF8F7`   Main light page background
  `color.neutral.100`   `#F2EEEC`   Subtle section background
  `color.neutral.200`   `#E4DCDA`   Borders/dividers
  `color.neutral.400`   `#A69A98`   Placeholder/disabled text
  `color.neutral.600`   `#6D6262`   Secondary text
  `color.neutral.800`   `#342C30`   Main text
  `color.neutral.950`   `#1D171B`   Strong text and dark surfaces
  `color.success.600`   `#26734D`   In-stock, success
  `color.warning.600`   `#94630B`   Low stock, warning
  `color.error.600`     `#B4233A`   Errors, destructive actions
  `color.info.600`      `#315F9B`   Informational states

### Semantic color aliases

-   `color.background.canvas`: `#FAF8F7`
-   `color.background.surface`: `#FFFFFF`
-   `color.background.subtle`: `#F2EEEC`
-   `color.background.brand`: `#451333`
-   `color.background.inverse`: `#1D171B`
-   `color.text.primary`: `#342C30`
-   `color.text.secondary`: `#6D6262`
-   `color.text.inverse`: `#FFFFFF`
-   `color.text.brand`: `#5B1B43`
-   `color.border.default`: `#E4DCDA`
-   `color.border.strong`: `#C8BCBA`
-   `color.action.primary`: `#451333`
-   `color.action.primary.hover`: `#5B1B43`
-   `color.action.accent`: `#C7A66A`

**Accessibility note:** Verify text/background combinations against WCAG
2.2 AA. Do not use blush or gold as small text on white without contrast
testing.

------------------------------------------------------------------------

## 3. Typography

### Font families

-   **Display / editorial:** `Cormorant Garamond`, fallback
    `Georgia, serif`
-   **UI / body:** `Inter`, fallback `Arial, sans-serif`
-   **Optional numeric emphasis:** `Inter` with tabular numbers enabled.

### Type scale

  Token                   Desktop   Mobile Weight / line height
  --------------------- --------- -------- ----------------------
  `type.display.1`          64 px    42 px 500 / 1.02
  `type.display.2`          52 px    36 px 500 / 1.08
  `type.heading.1`          40 px    30 px 600 / 1.15
  `type.heading.2`          32 px    26 px 600 / 1.2
  `type.heading.3`          24 px    22 px 600 / 1.25
  `type.heading.4`          20 px    18 px 600 / 1.3
  `type.body.large`         18 px    17 px 400 / 1.6
  `type.body.default`       16 px    15 px 400 / 1.55
  `type.body.small`         14 px    13 px 400 / 1.5
  `type.label`              12 px    11 px 600 / 1.4
  `type.caption`            12 px    11 px 400 / 1.45

### Typography rules

-   Use the display serif for brand storytelling, hero headings, and
    editorial campaign titles.
-   Use the sans-serif for navigation, product details, forms, filters,
    buttons, and prices.
-   Use sentence case for most UI. Use uppercase sparingly for tiny
    eyebrow labels.
-   Keep product names readable and avoid excessive letter spacing in
    body text.
-   Prices should use Indian currency formatting (e.g. `₹7,999`) and
    tabular numerals.

------------------------------------------------------------------------

## 4. Spacing, sizing and layout

### Spacing scale (4 px base)

  Token          Value
  ------------ -------
  `space.0`       0 px
  `space.1`       4 px
  `space.2`       8 px
  `space.3`      12 px
  `space.4`      16 px
  `space.5`      20 px
  `space.6`      24 px
  `space.8`      32 px
  `space.10`     40 px
  `space.12`     48 px
  `space.16`     64 px
  `space.20`     80 px
  `space.24`     96 px

### Responsive breakpoints

  Token                        Width Layout guidance
  ------------------ --------------- -----------------------------------
  `breakpoint.xs`          0--359 px Compact mobile
  `breakpoint.sm`        360--639 px Standard mobile
  `breakpoint.md`        640--767 px Large mobile / small tablet
  `breakpoint.lg`       768--1023 px Tablet
  `breakpoint.xl`      1024--1279 px Desktop
  `breakpoint.2xl`     1280--1535 px Wide desktop
  `breakpoint.3xl`          1536 px+ Large canvas, constrained content

### Containers and grids

-   Mobile page gutter: **16 px** (20 px on wider phones where
    appropriate).
-   Tablet page gutter: **24--32 px**.
-   Desktop page gutter: **40--64 px**.
-   Main content max width: **1440 px**.
-   Editorial text max width: **680 px**.
-   Desktop product grid: 4 columns at 1280 px+, 3 columns around 1024
    px.
-   Tablet product grid: 2--3 columns depending on width.
-   Mobile product grid: 2 columns; switch to 1 column only for list
    mode or narrow screens.
-   Desktop sidebar filters: 240--280 px; mobile filters open in a
    bottom sheet/drawer.

------------------------------------------------------------------------

## 5. Shape, borders and elevation

### Radius

-   `radius.xs`: 4 px --- tiny tags
-   `radius.sm`: 8 px --- inputs, compact chips
-   `radius.md`: 12 px --- product cards, dropdowns
-   `radius.lg`: 18 px --- campaign panels, large cards
-   `radius.xl`: 24 px --- modal panels, large feature cards
-   `radius.pill`: 999 px --- chips and pill buttons

### Borders

-   Default: 1 px solid `#E4DCDA`
-   Strong: 1 px solid `#C8BCBA`
-   Focus: 2 px solid `#B85B88` with 2 px offset
-   Dark surface divider: 1 px solid `rgba(255,255,255,0.14)`

### Shadows

-   `shadow.card`: `0 2px 12px rgba(44, 20, 35, 0.06)`
-   `shadow.card.hover`: `0 10px 28px rgba(44, 20, 35, 0.12)`
-   `shadow.popover`: `0 16px 48px rgba(29, 23, 27, 0.16)`
-   `shadow.modal`: `0 24px 80px rgba(29, 23, 27, 0.24)`
-   Avoid heavy shadows on luxury editorial sections; use contrast and
    spacing first.

------------------------------------------------------------------------

## 6. Component tokens and patterns

### Buttons

-   Primary: plum background, white text; hover `plum.800`.
-   Secondary: transparent/white background, plum text, 1 px border.
-   Accent: gold background, deep plum text; use for limited promotional
    emphasis.
-   Dark inverse: white background, deep plum text on dark sections.
-   Minimum touch target: **44 × 44 px**; preferred mobile CTA height
    **48--52 px**.
-   Button radius: 8--12 px; pill style only for chips or selected
    campaign actions.
-   Disabled state: neutral background and muted text; never rely on
    color alone.

### Inputs and search

-   Height: 48 px desktop, 48--52 px mobile.
-   Background: white; border `color.border.default`.
-   Focus ring: 2 px blush/rose with visible offset.
-   Search should support product, brand, notes, and category
    suggestions.
-   Keep labels visible; placeholder text is not a label.

### Product card

-   Image stage: square or 4:5; neutral background, consistent product
    scale.
-   Show brand, product name, concentration/size, current price,
    optional MRP/discount, stock state.
-   Include wishlist action and clear Add to Cart action.
-   Ratings/reviews only when verified data exists.
-   Mobile: compact card with readable product name and price; avoid
    hover-only interactions.
-   Hover: slight image scale (1.02--1.04) and subtle shadow,
    reduced-motion aware.

### Brand card

-   Use authorized logo assets on neutral or brand-specific tile.
-   Display brand name and optional product count.
-   Avoid stretching logos; preserve their brand guidelines.
-   Click/tap opens the brand listing page.

### Category card

-   Categories: For Her, For Him, Unisex, Luxury, Everyday, Gift Sets
    (adjust catalog as needed).
-   Use distinct but harmonious photography; label remains readable
    without hover.

### Filters and sort

-   Filters: brand, price, gender/category, fragrance family,
    concentration, size, availability, rating (only if data exists).
-   Desktop: persistent filter sidebar or collapsible panel.
-   Mobile: bottom sheet with Apply and Reset actions; show active
    filter count.
-   Sort options: Recommended, Newest, Price low--high, Price high--low,
    Popularity (define ranking logic transparently).

### Trust and service blocks

-   Authenticity statement only when supplier and sourcing processes
    support it.
-   Delivery estimate based on customer pin code and actual
    serviceability.
-   Clear shipping, return, cancellation, privacy, and contact policies.
-   Use secure-payment logos only for payment methods actually enabled.

### Navigation

-   Desktop: logo, primary links, prominent search, account, wishlist,
    cart.
-   Mobile: compact header with menu, logo, search, wishlist/cart;
    bottom navigation for Home, Shop, Wishlist, Account.
-   Cart count badge should be accessible and update dynamically.

------------------------------------------------------------------------

## 7. Mobile-first screen inventory

### Customer-facing mobile screens

1.  Splash / brand intro (optional, skippable).
2.  Home.
3.  Main menu / navigation drawer.
4.  Search landing, suggestions, and search results.
5.  Brand directory and brand detail/listing.
6.  Category landing and category product listing.
7.  All products / collection listing.
8.  Filter bottom sheet and sort sheet.
9.  Product detail --- image gallery, price, size selector, notes,
    description, stock, delivery checker, reviews, related products.
10. Fragrance finder --- preference steps, progress, results,
    recommendations.
11. Wishlist / saved products.
12. Cart --- quantity, remove/save, coupon, totals.
13. Sign in / sign up / OTP verification / password reset (as
    supported).
14. Address book --- list, add, edit, select address.
15. Checkout --- address, delivery method, order summary.
16. Payment selection and payment status.
17. Order confirmation.
18. Order history and order detail.
19. Order tracking and shipment updates.
20. Profile / account dashboard.
21. Offers and campaign landing pages.
22. Gift finder / gift collection.
23. Reviews and rating submission (if enabled).
24. Help center / FAQ / contact support.
25. Shipping, returns, privacy, terms, and authenticity policy pages.
26. Empty states, loading skeletons, error states, offline/no-results
    states.

### Mobile interaction rules

-   One primary action per screen where possible.
-   Keep bottom navigation visible on core browsing screens; hide it
    during checkout if it competes with the CTA.
-   Use bottom sheets for filters, sorting, and quick selections.
-   Keep checkout steps short and preserve cart state.
-   Respect safe areas and keyboard insets.
-   No essential content should depend on hover.

------------------------------------------------------------------------

## 8. Desktop web application screen inventory

### Storefront

1.  Home / campaign landing.
2.  Brand directory and brand detail.
3.  Category and collection listings.
4.  Search suggestions and results.
5.  Product listing with sidebar filters and sort.
6.  Product detail with gallery and purchase panel.
7.  Fragrance finder and recommendation results.
8.  Wishlist.
9.  Cart.
10. Authentication and account pages.
11. Address book.
12. Checkout, delivery selection, and payment.
13. Order confirmation, order history, details, and tracking.
14. Offers, editorial stories, gift finder.
15. Support, FAQs, policy pages, and contact.

### Admin / operations (role-protected)

1.  Admin login and role-based access.
2.  Dashboard with sales/order/catalog summaries.
3.  Product management: create, edit, archive, image, variant/size, SKU.
4.  Brand and category management.
5.  Inventory and stock adjustment.
6.  Order management and fulfillment status.
7.  Customer lookup and support history (access-controlled).
8.  Coupon and promotion management.
9.  Reviews moderation (if enabled).
10. Content management for homepage banners and editorial sections.
11. Reports and exports with permission controls.
12. Store settings, shipping, tax, payment and notification
    configuration.
13. Audit log and access management.

------------------------------------------------------------------------

## 9. Motion system --- GSAP

### Principles

-   Motion should communicate hierarchy and state, not delay shopping.
-   Prefer short, smooth, purposeful movement.
-   Animate opacity and transforms where possible; avoid animating
    layout-heavy properties.
-   Use GSAP timelines for coordinated hero and editorial sequences.
-   Use ScrollTrigger selectively for reveal/parallax sections.
-   Kill/revert animations on route changes and component unmounts.
-   Respect `prefers-reduced-motion`; provide a static alternative.

### Motion tokens

  Token                  Duration Use
  -------------------- ---------- ----------------------------------------
  `motion.instant`         100 ms Press feedback
  `motion.fast`            160 ms Hover/focus and small state changes
  `motion.standard`        240 ms Drawer, accordion, small transitions
  `motion.emphasis`        420 ms Product image and section transitions
  `motion.editorial`       700 ms Hero storytelling, restrained entrance
  `motion.long`           1000 ms Optional one-time brand intro only

### Easing tokens

-   `ease.standard`: `power2.out`
-   `ease.enter`: `power3.out`
-   `ease.exit`: `power2.in`
-   `ease.editorial`: `power4.out`
-   `ease.springLike`: `back.out(1.2)` --- use sparingly, not for
    essential commerce controls.

### Recommended GSAP usage

-   Home hero: stagger headline, supporting copy, CTA, then product
    imagery.
-   Product detail: gentle image reveal and thumbnail transition.
-   Collection page: light stagger for first visible product row only.
-   Fragrance finder: progress indicator and step transition.
-   Cart drawer: short slide/fade; keep close and checkout actions
    immediately available.
-   Avoid replaying long entrance animations on every scroll or
    navigation.

------------------------------------------------------------------------

## 10. Smooth scrolling --- Lenis

-   Use Lenis only for desktop/editorial browsing where it improves the
    experience.
-   Keep native scrolling on mobile by default, especially for checkout,
    forms, drawers, and nested scroll areas.
-   If Lenis is enabled, integrate its animation frame with GSAP
    ticker/ScrollTrigger using the official integration pattern for the
    installed versions.
-   Ensure anchor links, browser history, focus movement, keyboard
    navigation, and reduced-motion settings continue to work.
-   Avoid smooth-scroll hijacking and avoid applying Lenis to modal or
    inner scroll containers.
-   Provide a clean fallback when JavaScript is unavailable or reduced
    motion is requested.

------------------------------------------------------------------------

## 11. 3D floating objects and WebGL

### Visual direction

-   Use a floating perfume bottle or abstract scent-note elements only
    in the hero or selected campaign panels.
-   Keep 3D objects decorative; all product information and shopping
    actions must remain in accessible HTML.
-   Use a restrained rotation, slow float, soft lighting, and subtle
    pointer response on desktop.
-   On mobile, use a lightweight static render or short low-cost motion;
    do not force continuous heavy rendering.

### Performance and accessibility

-   Prefer optimized GLB/GLTF assets, compressed textures, and lazy
    loading.
-   Pause rendering when offscreen or tab is hidden.
-   Set a performance budget; provide static image fallback for
    low-power devices.
-   Respect reduced motion and do not make motion respond only to
    pointer hover.
-   Ensure 3D never blocks touch controls, text, or product imagery.
-   Do not use 3D on checkout, account, payment, or order-tracking
    screens.

------------------------------------------------------------------------

## 12. Imagery and content rules

-   Product images should use consistent lighting, crop, scale, and
    background.
-   Prefer official brand/product imagery with appropriate usage rights.
-   Do not imply a product is authentic, in stock, discounted, or
    reviewed unless the underlying information verifies it.
-   Campaign imagery can use plum, blush, cream, botanical details, and
    warm metallic accents.
-   Keep image alt text descriptive and meaningful; decorative images
    should have empty alt text.
-   Use realistic product names, prices, sizes, and availability from
    the actual catalog in the live store.

------------------------------------------------------------------------

## 13. Accessibility and responsive behavior

-   Target WCAG 2.2 AA for text contrast, keyboard operation, focus
    visibility, and accessible names.
-   Use semantic headings, landmarks, labels, and buttons.
-   All controls must be keyboard accessible and have visible focus.
-   Provide accessible validation messages and announce cart/checkout
    status changes.
-   Do not convey stock, errors, discounts, or selection by color alone.
-   Support zoom and reflow; avoid horizontal overflow at 320 px width.
-   Use `prefers-reduced-motion` for GSAP, parallax, Lenis, and 3D.
-   Test touch targets at 44 × 44 px or larger.

------------------------------------------------------------------------

## 14. Suggested CSS custom properties

``` css
:root {
  /* Brand */
  --color-brand-plum-950: #321027;
  --color-brand-plum-900: #451333;
  --color-brand-plum-800: #5B1B43;
  --color-brand-plum-700: #742653;
  --color-brand-blush-300: #E9B7D8;
  --color-brand-blush-200: #F2D2E7;
  --color-brand-blush-100: #FAEAF4;
  --color-brand-rose-500: #B85B88;
  --color-brand-gold-500: #C7A66A;
  --color-brand-gold-100: #F5EBD7;

  /* Neutrals */
  --color-neutral-0: #FFFFFF;
  --color-neutral-50: #FAF8F7;
  --color-neutral-100: #F2EEEC;
  --color-neutral-200: #E4DCDA;
  --color-neutral-400: #A69A98;
  --color-neutral-600: #6D6262;
  --color-neutral-800: #342C30;
  --color-neutral-950: #1D171B;

  /* Semantic */
  --color-bg-canvas: var(--color-neutral-50);
  --color-bg-surface: var(--color-neutral-0);
  --color-bg-brand: var(--color-brand-plum-900);
  --color-text-primary: var(--color-neutral-800);
  --color-text-secondary: var(--color-neutral-600);
  --color-text-inverse: var(--color-neutral-0);
  --color-border: var(--color-neutral-200);
  --color-action-primary: var(--color-brand-plum-900);

  /* Typography */
  --font-display: "Cormorant Garamond", Georgia, serif;
  --font-body: "Inter", Arial, sans-serif;

  /* Spacing */
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-5: 20px;
  --space-6: 24px;
  --space-8: 32px;
  --space-10: 40px;
  --space-12: 48px;
  --space-16: 64px;
  --space-20: 80px;
  --space-24: 96px;

  /* Shape and elevation */
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 18px;
  --radius-xl: 24px;
  --radius-pill: 999px;
  --shadow-card: 0 2px 12px rgba(44, 20, 35, 0.06);
  --shadow-card-hover: 0 10px 28px rgba(44, 20, 35, 0.12);

  /* Motion */
  --motion-fast: 160ms;
  --motion-standard: 240ms;
  --motion-emphasis: 420ms;
}
```

------------------------------------------------------------------------

## 15. Implementation checklist

### Before development

-   [ ] Confirm final logo files (SVG preferred) and approved color
    variants.
-   [ ] Confirm actual store name and tagline; replace "SCENTIVA" if
    needed.
-   [ ] Define catalog fields, brand assets, size/variant rules, and
    inventory source.
-   [ ] Confirm shipping, return, cancellation, and authenticity
    policies.
-   [ ] Choose supported payment and delivery integrations.

### During development

-   [ ] Build shared design tokens before individual screens.
-   [ ] Create reusable buttons, inputs, cards, filters, drawers,
    dialogs, and navigation.
-   [ ] Implement mobile layouts first, then tablet and desktop
    breakpoints.
-   [ ] Add motion only after core flows work without animation.
-   [ ] Test reduced-motion, keyboard, touch, loading, empty, error, and
    offline states.
-   [ ] Verify every product claim, price, discount, review, and stock
    message against data.

### Handoff

-   [ ] Keep tokens in a single source of truth (CSS variables or
    design-token JSON).
-   [ ] Document component variants and responsive behavior in the
    design system.
-   [ ] Record motion timings and fallback behavior.
-   [ ] Provide desktop, mobile, and key state screenshots for QA.

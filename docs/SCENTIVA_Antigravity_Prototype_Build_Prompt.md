# Antigravity Build Prompt --- SCENTIVA Multi-Brand Perfume Marketplace

**Purpose:** Build a polished, responsive, clickable prototype for the
SCENTIVA multi-brand perfume e-commerce website.\
**Target:** Mobile-first customer storefront + desktop web experience +
lightweight admin demo.\
**Brand:** SCENTIVA --- "SINCE 2026"\
**Visual direction:** Luxury + modern + colorful, using the supplied
SCENTIVA logo and the design-token specification.

------------------------------------------------------------------------

## 1. Instructions to Antigravity

Act as a senior product designer and frontend engineer. Build a
**complete, working frontend prototype**, not just a static mockup or a
single landing page.

Before coding: 1. Inspect the project repository and identify its
existing framework, scripts, and folder structure. 2. Read the provided
`SCENTIVA_Design_Tokens_Mobile_Web.md` and follow its color, typography,
spacing, responsive, component, motion, and accessibility rules. 3.
Locate and use the supplied SCENTIVA logo asset. If it is not present in
the repository, create a clearly named placeholder location such as
`public/assets/scentiva-logo.svg` and tell me exactly where to add the
real logo. **Do not redraw, distort, or invent a replacement logo.** 4.
Reuse the existing stack where possible. If the project is empty, use
React + TypeScript + Vite, Tailwind CSS (or CSS variables with a small
component system), GSAP, Lenis, and a lightweight 3D option only where
it adds value. 5. Build the screens and interactions below with
realistic demo data. Do not stop after creating the homepage.

### Required result

-   Responsive mobile and desktop layouts.
-   All listed routes/screens accessible through working navigation.
-   Functional demo interactions with local mock data; no real payment,
    order placement, or external API calls.
-   Reusable components and centralized design tokens.
-   Clear setup/run instructions in the README.
-   No broken links, empty buttons, or console errors.

------------------------------------------------------------------------

## 2. Brand and design system

### Brand identity

-   Brand name: **SCENTIVA**
-   Tagline: **SINCE 2026**
-   Style: premium, trustworthy, editorial, modern multi-brand fragrance
    marketplace.
-   Keep the logo's original proportions and use the supplied logo file.
    Provide light and dark logo treatments only if actual approved
    assets exist.

### Core colors

Use the exact values below unless the supplied design-token file defines
a more recent value.

  Token         Value
  ------------- -----------
  Plum 950      `#321027`
  Plum 900      `#451333`
  Plum 800      `#5B1B43`
  Plum 700      `#742653`
  Blush 300     `#E9B7D8`
  Blush 200     `#F2D2E7`
  Blush 100     `#FAEAF4`
  Rose 500      `#B85B88`
  Gold 500      `#C7A66A`
  Gold 100      `#F5EBD7`
  Neutral 50    `#FAF8F7`
  Neutral 100   `#F2EEEC`
  Neutral 200   `#E4DCDA`
  Neutral 600   `#6D6262`
  Neutral 800   `#342C30`
  Neutral 950   `#1D171B`
  White         `#FFFFFF`

### Typography

-   Display/editorial: `Cormorant Garamond`, fallback Georgia/serif.
-   UI/body: `Inter`, fallback Arial/sans-serif.
-   Editorial headings use the serif; controls, product details,
    navigation, prices, and forms use the sans-serif.
-   Use restrained uppercase eyebrow labels and readable body copy.

### Layout and component style

-   4 px spacing scale.
-   Page max-width: 1440 px.
-   Mobile gutter: 16 px; tablet 24--32 px; desktop 40--64 px.
-   Cards: 12--18 px radius; subtle borders and soft shadows.
-   Buttons and controls: at least 44 × 44 px touch targets.
-   Desktop product grid: 3--4 columns; mobile: 2 columns, with list
    mode where useful.
-   Use generous whitespace, premium product photography, consistent
    image crops, and clear prices.

------------------------------------------------------------------------

## 3. Responsive behavior

Implement mobile-first CSS and test these viewport widths: - 360 × 800 -
390 × 844 - 430 × 932 - 768 × 1024 - 1024 × 768 - 1440 × 900 - 1920 ×
1080

### Mobile

-   Compact header with menu, centered logo, search, wishlist/cart icons
    as space permits.
-   Bottom navigation for Home, Shop, Wishlist, Account on core shopping
    screens.
-   Filters and sort open in bottom sheets.
-   Product grids default to 2 columns.
-   Checkout uses a single-column flow with a clear sticky/visible
    primary CTA.
-   Respect safe areas and on-screen keyboard; never rely on hover.

### Desktop

-   Full header with logo, navigation, wide search, account, wishlist,
    and cart.
-   Product listing uses a filter sidebar plus grid.
-   Product detail uses a gallery and purchase panel side by side.
-   Cart and checkout use a main content column and order-summary panel.
-   Footer includes useful navigation, customer support, policy links,
    and social links.

------------------------------------------------------------------------

## 4. Pages and routes to build

Use a consistent route system. Suggested paths are below; adapt to the
existing router if necessary.

### Public storefront

1.  `/` --- Home
2.  `/shop` --- All perfumes / product listing
3.  `/brands` --- Brand directory
4.  `/brands/:slug` --- Brand detail and its products
5.  `/categories/:slug` --- Category listing
6.  `/search?q=` --- Search results
7.  `/product/:slug` --- Product details
8.  `/find-your-scent` --- Multi-step fragrance finder
9.  `/find-your-scent/results` --- Personalized demo recommendations
10. `/wishlist` --- Saved products
11. `/cart` --- Cart
12. `/checkout` --- Address, delivery, and order summary
13. `/checkout/payment` --- Demo payment selection
14. `/order/success` --- Demo order confirmation
15. `/account` --- Account dashboard
16. `/account/sign-in` --- Sign in demo
17. `/account/sign-up` --- Sign up demo
18. `/account/orders` --- Order history
19. `/account/orders/:id` --- Order detail and tracking
20. `/account/addresses` --- Address book
21. `/offers` --- Offers and campaigns
22. `/gifts` --- Gift collection / gift finder
23. `/stories` --- Fragrance stories / editorial
24. `/help` --- Help center / FAQ
25. `/contact` --- Contact page
26. `/policies/shipping` --- Shipping policy
27. `/policies/returns` --- Returns and cancellation policy
28. `/policies/privacy` --- Privacy policy
29. `/policies/terms` --- Terms and conditions

### Demo admin

30. `/admin/login` --- Demo admin sign-in
31. `/admin` --- Dashboard
32. `/admin/products` --- Product management table
33. `/admin/products/new` --- Add product form
34. `/admin/brands` --- Brand management
35. `/admin/categories` --- Category management
36. `/admin/inventory` --- Inventory view
37. `/admin/orders` --- Order management
38. `/admin/customers` --- Customer list (mock data)
39. `/admin/promotions` --- Coupons and campaigns
40. `/admin/content` --- Homepage content controls
41. `/admin/reports` --- Demo reports and charts
42. `/admin/settings` --- Store settings

If a route is too large for the prototype, build a meaningful page
template with working navigation and a clear demo state rather than
leaving it blank.

------------------------------------------------------------------------

## 5. Detailed storefront screen requirements

### A. Home (`/`)

Build a complete homepage with: - Announcement strip for a demo offer
(clearly mark as sample content). - Responsive header and search. - Hero
with the headline **"Find Your Signature Scent."**, short supporting
text, CTA "Shop Fragrances," and premium perfume visual. - Brand
carousel/grid with sample brands. - Category cards: For Her, For Him,
Unisex, Luxury, Everyday, Gift Sets. - "Find Your Perfect Scent" finder
feature. - Best Sellers product grid. - New Arrivals section. - Featured
brand/editorial campaign panel. - Trust/service strip for authenticity,
payment, shipping, and returns---use neutral demo copy and do not claim
verified authenticity unless configured. - Newsletter signup with
client-side validation and success state. - Footer with store
navigation, customer care, policies, contact, and social links.

### B. Brand directory and brand detail

-   Search/filter brands.
-   Responsive brand tiles using text/logo placeholders when licensed
    logos are unavailable.
-   Brand detail header, short description, category chips, product
    count, and product grid.
-   Brand page filters and sorting.

### C. Product listing (`/shop`, category, search)

-   Page title, breadcrumb, result count, sort control.
-   Desktop filter sidebar; mobile filter/sort bottom sheets.
-   Filters: brand, price range, category/gender, fragrance family,
    concentration, size, availability.
-   Product cards: image, brand, name, size/concentration, current
    price, optional MRP/discount, wishlist toggle, Add to Cart.
-   Functional pagination or "Load more."
-   Empty search state with suggestions and clear filters.

### D. Product details (`/product/:slug`)

-   Image gallery with thumbnails and zoom/lightbox.
-   Brand, product name, concentration, size, price, discount only if
    sample data includes it, stock status, rating only if mock reviews
    exist.
-   Size/variant selector.
-   Quantity selector.
-   Add to Cart and Buy Now demo actions.
-   Delivery pincode input with mock serviceability response clearly
    labeled as demo.
-   Fragrance notes: top, heart, base.
-   Description, fragrance family, longevity/projection as sample
    content (avoid presenting unverified claims as facts).
-   Reviews section with mock labels, related products, recently viewed.
-   Sticky mobile purchase bar where appropriate.

### E. Fragrance finder (`/find-your-scent`)

Build a multi-step quiz: 1. Fragrance family: Fresh, Woody, Floral,
Sweet, Spicy, Oriental. 2. Occasion: Everyday, Work, Date Night, Party,
Gifting. 3. Preference: Light, Balanced, Bold. 4. Budget range. 5.
Optional gender preference: For Her, For Him, Unisex, No Preference. -
Progress indicator, Back/Next, selected-state visuals, validation. -
Results page filters the local demo catalog and explains why each
product matched. - Include "Start over" and "Add to Wishlist" actions.

### F. Wishlist and cart

-   Wishlist: saved product cards, remove, move/add to cart, empty
    state.
-   Cart: quantity stepper, remove item, save for later, coupon demo,
    subtotal, discount, delivery estimate placeholder, total, continue
    shopping, checkout CTA.
-   Persist demo cart/wishlist in localStorage if suitable; handle empty
    state and refresh correctly.
-   Keep totals mathematically consistent.

### G. Checkout and order demo

-   Stepper: Address → Delivery → Payment → Review.
-   Address selection/add/edit form with field validation.
-   Delivery option cards with clearly marked sample delivery windows
    and fees.
-   Order summary with item thumbnails and correct totals.
-   Payment methods as demo-only selectable options; **do not collect
    real card or bank credentials**.
-   "Place Demo Order" creates a mock order ID and opens success page.
-   Success page includes order summary and "Track Demo Order."
-   Tracking timeline: Confirmed, Processing, Shipped, Out for Delivery,
    Delivered.
-   Make it clear that no real order or payment is processed.

### H. Account

-   Demo sign-in/sign-up forms with validation; no real authentication.
-   Dashboard cards for orders, wishlist, addresses, and support.
-   Order list, order detail, tracking timeline.
-   Address book CRUD in local demo state.
-   Settings and logout demo behavior.

### I. Offers, gifts, stories, help

-   Offers page with demo campaign cards and coupon copy action.
-   Gift collection with budget/occasion filters.
-   Stories/editorial pages with image-led layouts and accessible text.
-   FAQ accordion, contact form validation, and policy page templates.

------------------------------------------------------------------------

## 6. Admin demo requirements

Build a visually consistent but operationally distinct admin area: -
Left sidebar on desktop; drawer navigation on mobile. - Dashboard metric
cards and small charts based on local mock data. - Products table with
search, filter, sort, add/edit/archive demo actions. - Product form with
name, brand, SKU, category, size, price, stock, image URL/placeholder,
and description. - Brand/category CRUD in local demo state. - Inventory
stock adjustment with validation. - Orders table with status filters and
detail drawer/page. - Promotion/coupon editor with sample validation. -
Homepage content controls for hero copy and featured sections. - Reports
page with demo chart data and clear "sample data" label. - Settings page
with non-persisted demo controls or local persistence. - Role/access
screen can be represented as a demo login; do not imply production-grade
security.

------------------------------------------------------------------------

## 7. Functional interaction checklist

Every visible control should work: - Logo and navigation links route
correctly. - Search opens suggestions and filters the catalog. -
Brand/category tiles open relevant listing pages. - Filters, sorting,
clear-all, and product count update results. - Wishlist toggles update
header badge and wishlist page. - Add to Cart updates cart badge and
cart contents. - Quantity updates totals; remove item works. - Coupon
apply/remove updates the demo summary. - Finder steps, validation,
back/next, and results work. - Address form adds/edits/selects
addresses. - Demo checkout progresses and creates a mock order. - Order
tracking and account pages display mock data. - Newsletter/contact forms
show validation and success feedback. - Admin create/edit/archive
controls update local mock data. - Mobile menu, drawers, dialogs,
accordions, and bottom sheets open and close. - Provide loading, empty,
error, and success states where appropriate.

------------------------------------------------------------------------

## 8. Motion and interaction --- GSAP

Use GSAP for polished but restrained motion: - Home hero: stagger
eyebrow, heading, copy, CTA, then visual. - Product imagery: subtle
reveal and thumbnail transition. - Section reveals: light
opacity/translate effects, only where helpful. - Finder: short step
transition and progress animation. - Cart drawer and dialogs: short
slide/fade transitions. - Campaign sections: optional editorial
timeline. - Avoid animating every card on every scroll.

Motion tokens: - Instant: 100 ms - Fast: 160 ms - Standard: 240 ms -
Emphasis: 420 ms - Editorial: 700 ms

Implementation requirements: - Use GSAP timelines and ScrollTrigger only
where appropriate. - Use `gsap.context()` or equivalent scoped cleanup
for component animations. - Kill/revert animations on unmount and route
changes. - Avoid layout-thrashing properties; prefer transform and
opacity. - Respect `prefers-reduced-motion`; use a static/shortened
alternative.

------------------------------------------------------------------------

## 9. Smooth scrolling --- Lenis

-   Use Lenis on desktop/editorial pages only if it improves the
    experience.
-   Keep native scroll on mobile by default and for checkout, forms,
    drawers, and nested scroll areas.
-   Integrate Lenis with GSAP ScrollTrigger using the official API for
    the installed versions.
-   Do not hijack keyboard, anchor, focus, or browser history behavior.
-   Disable or simplify smooth scrolling when reduced motion is enabled.

------------------------------------------------------------------------

## 10. 3D floating perfume objects

Use a lightweight 3D perfume bottle or abstract scent-note objects only
in the homepage hero or selected editorial campaign: - Desktop: subtle
float and very gentle pointer response. - Mobile: static optimized image
or reduced-cost animation. - Lazy-load 3D assets and pause when
offscreen/tab-hidden. - Provide a static image fallback if WebGL is
unavailable or device performance is limited. - 3D must never block
text, buttons, or product controls. - Do not use continuous 3D animation
on checkout, account, payment, or tracking screens. - If no suitable 3D
asset is supplied, use a polished static image rather than inventing an
inaccurate brand bottle.

------------------------------------------------------------------------

## 11. Demo catalog and data model

Create a local mock catalog with at least 12 products across multiple
brands and categories. Use sample/placeholder data and label it as demo
content.

Suggested fields:

``` ts
type Product = {
  id: string;
  slug: string;
  brandId: string;
  brandName: string;
  name: string;
  category: "For Her" | "For Him" | "Unisex" | "Luxury" | "Gift Set";
  fragranceFamilies: string[];
  concentration: string;
  sizes: { label: string; price: number; mrp?: number; sku: string }[];
  notes: { top: string[]; heart: string[]; base: string[] };
  description: string;
  image: string;
  stock: number;
  rating?: number;
  reviewCount?: number;
  isBestSeller?: boolean;
  isNewArrival?: boolean;
};
```

Also create mock brands, customer addresses, orders, coupons, reviews,
and homepage content. Do not use unauthorized official brand logos or
claim official partnerships. Product names and prices must be clearly
treated as prototype data.

------------------------------------------------------------------------

## 12. Suggested architecture

Adapt to the repository, but keep code modular:

``` text
src/
  app/
    routes/
    layouts/
  components/
    common/
    navigation/
    product/
    forms/
    overlays/
    motion/
  features/
    home/
    catalog/
    brands/
    product-detail/
    scent-finder/
    wishlist/
    cart/
    checkout/
    account/
    orders/
    admin/
  data/
    products.ts
    brands.ts
    mockOrders.ts
  hooks/
  lib/
    storage.ts
    currency.ts
    motion.ts
  styles/
    tokens.css
    globals.css
  types/
```

Requirements: - Centralize CSS variables/design tokens. - Use reusable
components and typed data. - Keep business logic separate from
presentation. - Use consistent currency formatting (`₹`). - Add basic
form validation and accessible labels. - Use local mock state; no
backend required for this prototype.

------------------------------------------------------------------------

## 13. Quality assurance and acceptance criteria

Before declaring the prototype complete: 1. Run the project and fix all
build/runtime errors. 2. Verify all routes load directly and through
navigation. 3. Test mobile and desktop layouts at the viewport sizes
listed above. 4. Test cart, wishlist, filters, finder, checkout demo,
order tracking, and admin CRUD. 5. Verify no essential action is a dead
button. 6. Check for overflow, clipped dialogs, keyboard overlap, and
broken image fallbacks. 7. Test keyboard navigation, visible focus,
labels, and reduced-motion behavior. 8. Check browser console for errors
and warnings. 9. Ensure all demo-only behavior is clearly labeled. 10.
Update README with install/run steps, feature list, route list, and
limitations.

### Final response from the coding agent

When finished, provide: - What was built. - Framework and libraries
used. - How to run it locally. - Main routes/screens. - Any missing
assets or features. - Known limitations. - A concise QA checklist
showing what was tested.

**Start by inspecting the repository and the design-token file, then
implement the complete prototype in small, testable steps.**

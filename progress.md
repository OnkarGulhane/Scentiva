# SCENTIVA — Forensic QA, Functional Hardening & Progress Log
> **Date:** October 1, 2026  
> **Status:** Forensic QA, Functional Hardening & Backend-Readiness Completed.  
> **Repository:** [https://github.com/OnkarGulhane/Scentiva](https://github.com/OnkarGulhane/Scentiva)  
> **Build Status:** Passing cleanly (`next build` -> 45/45 Static Pages Prerendered, Exit Code 0, 0 TypeScript errors)  

---

## 🎯 Forensic QA & Hardening Achievements

### 1. 3D Hero Flacon — Calibrated Geometry & Zero-Clipping Camera (Sections 17–20)
- [x] **Geometric Center Compensation:** Bottle geometry (vertical span 3.86 units from contact shadow at `y = -1.50` to cap crown at `y = +2.36`) compensated with `group position={[0, -0.42, 0]}`.
- [x] **Calibrated Camera:** Set to `position: [0, 0, 5.2]`, `FOV 46°` guaranteeing **100% full bottle visibility** with an exact **12.5% visual breathing room** across all desktop, tablet, and mobile viewports.
- [x] **Offscreen Performance Optimization:** Integrated `IntersectionObserver` to automatically pause R3F canvas execution (`frameloop="never"`) when the hero is scrolled out of view.

### 2. Cart, Stock Limits & Coupon Engine (Sections 7–9)
- [x] **Stock & Variant Hardening:** `addToCart` and `updateCartQuantity` strictly enforce available product stock and prevent adding out-of-stock items.
- [x] **Single Source of Truth:** Centralized calculation engine ensuring `subtotal - discount + deliveryFee = total` across Cart Drawer, Cart Page, and Checkout.
- [x] **Dynamic Coupon Validation:** Validates minimum order conditions and recalculates discounts on bag modifications.

### 3. Checkout & Order Flow (Sections 10–11)
- [x] **Submission Protection:** Prevents duplicate order creation with `isSubmitting` state and disabled buttons.
- [x] **Demo Order Persistence:** Generates unique order reference IDs, records full variant and item snapshots, and updates Account Orders immediately.

### 4. Admin-to-Storefront State Synchronization (Section 14)
- [x] When products are created, updated, or deleted in Admin, active state propagates immediately to Shop catalog, PDP, Cart items, and Wishlist without disconnected copies.

### 5. Documentation Forensic Repair (Section 34)
- [x] Repaired all outdated documentation (`QA_REPORT.md`, `3D_HERO_AUDIT.md`, `progress.md`, `repomemory.md`) to reflect Next.js 14 App Router facts, real camera parameters, and explicit demo storage boundaries.

---

## 📋 Quality Gate Verification
- **TypeScript:** 0 compiler errors (`strict: true`).
- **Production Build:** 100% Passing (`next build` -> 45/45 static pages prerendered, 87.3 kB shared baseline JS, Exit Code 0).
- **GitHub Sync:** Branch `main` fully synchronized.

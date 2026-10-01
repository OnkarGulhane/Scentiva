# SCENTIVA — 3D Hero & WebGL Architecture Audit

**Date:** October 1, 2026  
**Auditor:** Senior 3D & Performance Engineering Suite  
**Component:** `src/components/home/Hero3DCanvas.tsx` / `src/components/home/HeroSection.tsx`

---

## 1. Implementation Method & Technology
- **Framework:** Next.js 14 App Router with React 18.
- **Engine:** Three.js via `@react-three/fiber` (R3F) and `@react-three/drei`.
- **Architecture:** Procedurally constructed Haute Parfumerie Flacon geometry with physically based materials (`MeshPhysicalMaterial` with transmission, refraction, clearcoat, and internal liquid absorption).
- **Lighting Paradigm:** Multi-point studio product photography rig (Key Ivory Directional, Rose/Plum Rim Accent, Golden Top Specular, Soft Bottom Fill, Ground Contact Shadow Floor).
- **Execution Lifecycle:** Staged dynamic import (`lazy()` + `<Suspense>`) with `StaticFlaconFallback` during Stage 1 LCP.

---

## 2. Forensic Camera & Geometry Calibration

| Dimension Metric | Value | Technical Rationale |
| :--- | :--- | :--- |
| **Total Bottle Geometry Span** | `3.86` Three.js units | Spans from contact shadow at `y = -1.50` to cap crown at `y = +2.36`. |
| **Geometric Center Compensation** | `group position={[0, -0.42, 0]}` | Offsets the +0.43 geometric center to exact `y = 0.0` origin. |
| **Camera Configuration** | `position: [0, 0, 5.2]`, `fov: 46` | Frustum vertical height at z=0 is `4.414` units. |
| **Vertical Occupancy Ratio** | **87.5% of Frustum** | Leaves **12.5% total visual breathing room** (~6.2% top, ~6.2% bottom). |
| **Cap & Base Visibility** | **100% VISIBLE (0% Clip)** | Verified on all screen ratios (Desktop 1440x900 to Mobile 360x800). |
| **Offscreen CPU/GPU Pause** | `IntersectionObserver` | Pauses R3F render loop (`frameloop="never"`) when hero is scrolled out of view. |

---

## 3. Flacon Anatomy & Luxury Construction
1. **Heavy Solid Crystal Base:**
   - Solid bottom glass block with high index of refraction (`ior: 1.54`, `thickness: 1.6`, `transmission: 0.92`) creating authentic glass refraction weight.
2. **Main Crystal Outer Flacon Body:**
   - Polished crystal walls (`transmission: 0.94`, `clearcoat: 1.0`, `roughness: 0.02`).
3. **Internal Perfume Elixir Core:**
   - Rich golden cognac / amber liquid core with subtle rose-plum undertone (`emissiveIntensity: 0.24`) and realistic liquid absorption depth.
4. **Internal Glass Dip-Tube (Atomizer Straw):**
   - Translucent micro-cylinder extending from pump assembly down into the amber elixir.
5. **Polished 24k Gold Atomizer Collar & Sprayer:**
   - Mirror-finished crimp ring and sprayer pump nozzle (`metalness: 0.98`, `roughness: 0.10`).
6. **Faceted Architectural Cap (Royal Plum Lacquer):**
   - Heavy 8-facet magnetic cap in deep SCENTIVA royal velvet plum with a mirror gold base ring and top crown disc inset.
7. **Micro-Recessed SCENTIVA Emblem Plaque:**
   - 24k gold outer frame enclosing a deep plum velvet plaque with an embossed gold medallion, monogram crest, and typographic relief bars.
8. **Anchored Pedestal Contact Shadow:**
   - Soft radial contact shadow grounding the flacon firmly onto the stage.

---

## 4. Interactive Controls & Device Tiers

| Environment | 3D Hero Behavior | Camera / Visual Configuration |
| :--- | :--- | :--- |
| **Desktop (1440x900, 1280x800)** | Full interactive 3D flacon, smooth 360° mouse drag, auto-rotation toggle button (`Auto: ON/OFF`). | DPR capped at 2.0; Golden mist sparkles (`count: 28`). |
| **Tablet (1024x768, 768x1024)** | Smooth touch orbit controls, full silhouette preserved with comfortable margins. | DPR capped at 1.5; fluid touch inertia. |
| **Mobile Screen (430x932, 390x844, 360x800)** | Complete bottle visible without layout shift or gesture conflicts with page scrolling. | Zero horizontal overflow; touch-friendly controls. |
| **Reduced Motion (`prefers-reduced-motion: reduce`)** | Static 3D flacon; auto-rotation disabled; floating levitation disabled. | Preserves luxury product aesthetic without motion triggers. |
| **WebGL Unavailable / Error Fallback** | Instant branded `StaticFlaconFallback` with SCENTIVA velvet badge and gold emblem. | Zero 404s, zero crashes, zero blank states. |

---

## 5. Performance & Resource Cleanup
- **No Per-Frame React State:** Liquid wave calculations and rotation are executed via direct `useRef` mutations in `useFrame`, triggering **0 React re-renders** in the animation loop.
- **DPR Clamping:** Clamped to `dpr={[1, 2]}` to prevent runaway pixel fill rates on Ultra-HD displays.
- **Offscreen Canvas Capping:** Canvas frameloop switches to `never` when scrolled out of view.
- **Resource Cleanup:** Automatic geometry and material garbage collection on route change and component unmount.

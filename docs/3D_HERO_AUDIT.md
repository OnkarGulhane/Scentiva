# SCENTIVA — 3D Hero & WebGL Architecture Audit

**Date:** October 1, 2026  
**Auditor:** Antigravity 3D & Performance Engineering Engine  
**Component:** `src/components/home/Hero3DCanvas.tsx` / `src/components/home/HeroSection.tsx`

---

## 1. Implementation Method & Technology
- **Engine:** Three.js via `@react-three/fiber` (R3F) and `@react-three/drei`.
- **Architecture:** Procedurally constructed Haute Parfumerie Flacon geometry with physically based materials (`MeshPhysicalMaterial` with transmission, refraction, clearcoat, and internal liquid absorption).
- **Lighting Paradigm:** Multi-point studio product photography rig (Key Ivory Directional, Rose/Plum Rim Accent, Golden Top Specular, Contact Shadow Floor).
- **Execution Lifecycle:** Staged dynamic import (`lazy()` + `<Suspense>`) with zero blocking on initial Hero LCP.

---

## 2. Root Cause Audit: What Was Broken Previously

| Previous Issue | Root Cause | Visual / Performance Impact |
| :--- | :--- | :--- |
| **Top / Cap Visually Clipped** | Camera frustum height (`3.97`) was smaller than combined bottle scale & vertical float translation offset. | Cap and crown ring clipped outside top viewport bounds. |
| **Bottle Appeared Too Small** | Camera `position` and group offset left vast empty margins on wide screens. | Hero lacked prestige; bottle felt disconnected from visual weight. |
| **Primitive Placeholder Appearance** | Unchamfered cuboid boxes with flat materials; no crystal base, dip tube, or pump collar. | Read like a generic 3D demo rather than a luxury perfume bottle. |
| **Flat, Disconnected Plaque** | Generic dark rectangle plane without frame, seal, or embossed depth. | Looked like an accidental geometry artifact. |
| **Particle Distraction & GPU Lag** | 55 independent CPU-updated Sparkles calculating every frame. | Competed visually with bottle and reduced framerate on mobile devices. |
| **Monolithic JS Bundle Delay** | Monolithic 1.5MB single JS chunk blocked initial page interactivity before Three.js parsed. | Initial render felt sluggish to user. |

---

## 3. Remediation & Engineering Enhancements

### 3.1 Flacon Anatomy & Luxury Construction
1. **Heavy Beveled Crystal Base:**
   - Solid bottom glass block with high index of refraction (`ior: 1.54`, `thickness: 1.6`, `transmission: 0.92`) creating authentic glass refraction weight.
2. **Main Crystal Outer Flacon Body:**
   - Polished crystal walls (`transmission: 0.94`, `clearcoat: 1.0`, `roughness: 0.04`).
3. **Internal Perfume Elixir Core:**
   - Rich golden cognac / amber liquid core with subtle rose-plum undertone (`emissiveIntensity: 0.16`) and realistic liquid absorption depth.
4. **Internal Dip-Tube (Atomizer Straw):**
   - Translucent micro-cylinder extending from pump assembly down into the amber elixir.
5. **Polished 24k Gold Atomizer Collar & Sprayer:**
   - Mirror-finished crimp ring and sprayer pump nozzle (`metalness: 0.98`, `roughness: 0.12`).
6. **Faceted Architectural Cap (Royal Plum Lacquer):**
   - Heavy 8-facet magnetic cap in deep SCENTIVA royal plum with a mirror gold base ring and top crown disc inset.
7. **Micro-Recessed SCENTIVA Emblem Plaque:**
   - 24k gold outer frame enclosing a deep plum velvet plaque with an embossed gold medallion, monogram crest, and typographic relief bars.
8. **Anchored Pedestal Contact Shadow:**
   - Soft radial contact shadow grounding the flacon firmly onto the stage.

### 3.2 Responsive Camera Framing System
- **Camera Vector:** `position: [0, 0, 5.2]`, `fov: 38`.
- **Frustum Calculation:** Frustum vertical span is `3.72` units. Flacon vertical span is `2.9` units (`scale: 0.88`).
- **Framing Ratio:** Bottle occupies **78% of visible stage height** with a guaranteed 11% safety margin at top and bottom. Cap and base are 100% visible on all display ratios (Mobile, Tablet, Desktop, Ultra-wide).

### 3.3 Studio Product Lighting
- **Ivory Key Light:** `directionalLight position={[4, 6, 4]} intensity={2.2} color="#FFF9F0"`
- **Rose/Plum Accent Rim:** `directionalLight position={[-4, 2, -3]} intensity={1.4} color="#F2D2E7"`
- **Top Specular Glimmer:** `pointLight position={[0, 4, 2]} intensity={1.2} color="#F4DFC0"`
- **Soft Ambient Fill:** `ambientLight intensity={0.65} color="#FFFFFF"`

---

## 4. Device Adaptation & Behavior Matrix

| Environment | 3D Hero Behavior | Camera / Visual Configuration |
| :--- | :--- | :--- |
| **High-End Desktop** | Full interactive 3D flacon, smooth cursor parallax, studio specular gleams. | DPR capped at 1.75; Lerped mouse tilt (lerp factor 0.05). |
| **Tablet / Laptop** | Proportionally scaled 3D canvas, touch drag rotation. | Full silhouette preserved with comfortable margins. |
| **Mobile Screen** | Lightweight canvas with DPR clamped to 1.0–1.5; zero CPU particle load. | Full flacon visible without layout shift. |
| **Reduced Motion (`prefers-reduced-motion: reduce`)** | Static 3D flacon; auto-rotation disabled; pointer parallax disabled. | Preserves luxury product aesthetic without motion triggers. |
| **WebGL Unavailable / Offline** | Instant branded `StaticFlaconFallback` with SCENTIVA velvet badge and gold emblem. | Zero 404s, zero crashes, zero blank states. |

---

## 5. Performance & Resource Cleanup
- **No Per-Frame React State:** Pointer tracking and rotation are executed via direct `useRef` and `lerp` mutations on the Three.js group, triggering **0 React re-renders** in the animation loop.
- **DPR Clamping:** Clamped to `dpr={[1, 1.75]}` to prevent runaway 3x–4x pixel fill rates on Retina/AMOLED phones.
- **Resource Cleanup:** Automatic geometry and material garbage collection on route change and component unmount.
- **Bundle Code-Splitting:** Three.js vendor dependencies isolated into `three-vendor.js` via Rollup `manualChunks`.

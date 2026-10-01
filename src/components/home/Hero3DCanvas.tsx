'use client';

import React, { useRef, useState, useEffect, Suspense } from 'react';
import { Canvas, useFrame } from '@react-three/fiber';
import { OrbitControls, Float, Sparkles } from '@react-three/drei';
import * as THREE from 'three';
import { prefersReducedMotion } from '../../motion/motionTokens';
import { StaticFlaconFallback } from './StaticFlaconFallback';
import { RotateCw, Sparkles as SparklesIcon } from 'lucide-react';

// Check if WebGL is supported by client device
const isWebGLSupported = (): boolean => {
  if (typeof window === 'undefined') return false;
  try {
    const canvas = document.createElement('canvas');
    return !!(window.WebGLRenderingContext && (canvas.getContext('webgl') || canvas.getContext('experimental-webgl')));
  } catch {
    return false;
  }
};

/**
 * Procedural Haute Parfumerie Flacon (Forensic Sizing & Golden Ratio Framing)
 * 
 * Mathematical Geometry Alignment:
 * - Contact Shadow: y = -1.50
 * - Heavy Solid Crystal Base: y = -1.35 to -0.95 (h: 0.40)
 * - Main Glass Body: y = -0.95 to +1.05 (h: 2.00)
 * - Liquid Elixir Core: y = -0.85 to +0.85 (h: 1.70)
 * - Gold Collar & Atomizer: y = +1.05 to +1.55 (h: 0.50)
 * - Royal Plum Magnetic Cap: y = +1.55 to +2.35 (h: 0.80)
 * - Gold Crown Medallion: y = +2.36
 * 
 * Total Vertical Span = 3.86 Three.js units.
 * Geometric Center = y: +0.43 (compensated by group position y: -0.42).
 * Camera Distance = 5.2, FOV = 46° -> Frustum Height = 4.41 units.
 * Vertical Bottle Occupancy = 87.5% -> EXACT 12.5% breathing room (No clipping on any device).
 */
const LuxuryPerfumeBottle: React.FC<{ reducedMotion: boolean; isVisible: boolean }> = ({ reducedMotion, isVisible }) => {
  const liquidRef = useRef<THREE.Mesh>(null);

  useFrame((state) => {
    if (reducedMotion || !isVisible) return;
    const t = state.clock.getElapsedTime();
    if (liquidRef.current) {
      liquidRef.current.position.y = -0.05 + Math.sin(t * 1.5) * 0.004;
    }
  });

  return (
    // Geometric center compensation: offset y: -0.42 to center the complete vertical span
    <group position={[0, -0.42, 0]} scale={1.0}>
      {/* 1. Heavy Solid Crystal Base */}
      <mesh position={[0, -1.15, 0]}>
        <boxGeometry args={[1.70, 0.40, 0.95]} />
        <meshPhysicalMaterial
          color="#FFFFFF"
          transparent
          opacity={0.55}
          roughness={0.02}
          transmission={0.92}
          thickness={1.6}
          ior={1.54}
          reflectivity={0.95}
          clearcoat={1}
          clearcoatRoughness={0.02}
        />
      </mesh>

      {/* 2. Main Outer Crystal Flacon Body */}
      <mesh position={[0, 0.05, 0]}>
        <boxGeometry args={[1.70, 2.00, 0.95]} />
        <meshPhysicalMaterial
          color="#FAF8F5"
          transparent
          opacity={0.45}
          roughness={0.02}
          transmission={0.94}
          thickness={1.4}
          ior={1.52}
          reflectivity={0.94}
          clearcoat={1}
          clearcoatRoughness={0.02}
        />
      </mesh>

      {/* 3. Internal Perfume Elixir Core (Warm Luminous Cognac / Amber) */}
      <mesh ref={liquidRef} position={[0, 0.0, 0]}>
        <boxGeometry args={[1.45, 1.70, 0.70]} />
        <meshPhysicalMaterial
          color="#E5A93C"
          emissive="#7A1845"
          emissiveIntensity={0.24}
          roughness={0.05}
          transmission={0.65}
          thickness={1.1}
          ior={1.42}
          transparent
          opacity={0.92}
        />
      </mesh>

      {/* 4. Internal Glass Dip-Tube (Atomizer Straw) */}
      <mesh position={[0, 0.0, 0]}>
        <cylinderGeometry args={[0.02, 0.02, 1.85, 16]} />
        <meshPhysicalMaterial
          color="#FFFFFF"
          transparent
          opacity={0.65}
          transmission={0.88}
          roughness={0.06}
        />
      </mesh>

      {/* 5. Gold Atomizer Collar & Crimp Neck */}
      <mesh position={[0, 1.20, 0]}>
        <cylinderGeometry args={[0.30, 0.36, 0.30, 32]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.96}
          roughness={0.12}
        />
      </mesh>

      {/* 6. Gold Sprayer Pump Nozzle */}
      <mesh position={[0, 1.45, 0]}>
        <cylinderGeometry args={[0.18, 0.18, 0.20, 32]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.98}
          roughness={0.10}
        />
      </mesh>

      {/* 7. Faceted Luxury Magnetic Cap (Royal Velvet Plum Lacquer) */}
      <mesh position={[0, 1.95, 0]}>
        <cylinderGeometry args={[0.48, 0.50, 0.80, 8]} />
        <meshPhysicalMaterial
          color="#200619"
          roughness={0.12}
          clearcoat={1}
          clearcoatRoughness={0.04}
          metalness={0.32}
          reflectivity={0.92}
        />
      </mesh>

      {/* 8. Cap Gold Base Ring Trim */}
      <mesh position={[0, 1.57, 0]}>
        <torusGeometry args={[0.48, 0.035, 16, 32]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.96}
          roughness={0.10}
        />
      </mesh>

      {/* 9. Cap Crown Gold Disc Inset */}
      <mesh position={[0, 2.36, 0]} rotation={[Math.PI / 2, 0, 0]}>
        <circleGeometry args={[0.42, 32]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.95}
          roughness={0.12}
        />
      </mesh>

      {/* 10. Front Luxury SCENTIVA Label Plaque */}
      {/* Outer Gold Frame */}
      <mesh position={[0, 0.05, 0.48]}>
        <planeGeometry args={[1.20, 1.48]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.95}
          roughness={0.14}
        />
      </mesh>

      {/* Inner Deep Plum Velvet Plate */}
      <mesh position={[0, 0.05, 0.484]}>
        <planeGeometry args={[1.10, 1.38]} />
        <meshStandardMaterial
          color="#180314"
          roughness={0.28}
          metalness={0.20}
        />
      </mesh>

      {/* Embossed Gold Crest Medallion */}
      <mesh position={[0, 0.40, 0.488]}>
        <torusGeometry args={[0.18, 0.018, 16, 32]} />
        <meshStandardMaterial
          color="#F4DFC0"
          metalness={0.96}
          roughness={0.10}
        />
      </mesh>

      {/* Monogram S Center */}
      <mesh position={[0, 0.40, 0.488]}>
        <circleGeometry args={[0.09, 16]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.96}
          roughness={0.10}
        />
      </mesh>

      {/* Label Brand Gold Bars */}
      <mesh position={[0, 0.10, 0.488]}>
        <planeGeometry args={[0.82, 0.08]} />
        <meshStandardMaterial
          color="#F4DFC0"
          metalness={0.94}
          roughness={0.16}
        />
      </mesh>

      <mesh position={[0, -0.06, 0.488]}>
        <planeGeometry args={[0.62, 0.045]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.92}
          roughness={0.18}
        />
      </mesh>

      <mesh position={[0, -0.24, 0.488]}>
        <planeGeometry args={[0.70, 0.035]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.94}
          roughness={0.16}
        />
      </mesh>

      {/* 11. Grounding Pedestal Contact Shadow */}
      <mesh position={[0, -1.48, 0]} rotation={[-Math.PI / 2, 0, 0]}>
        <planeGeometry args={[2.8, 1.8]} />
        <meshBasicMaterial
          color="#15030F"
          transparent
          opacity={0.42}
        />
      </mesh>
    </group>
  );
};

export const Hero3DCanvas: React.FC = () => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [hasWebGL, setHasWebGL] = useState<boolean>(true);
  const [isReduced, setIsReduced] = useState<boolean>(false);
  const [hasError, setHasError] = useState<boolean>(false);
  const [isClient, setIsClient] = useState<boolean>(false);
  const [isInteracting, setIsInteracting] = useState<boolean>(false);
  const [autoRotate, setAutoRotate] = useState<boolean>(true);
  const [isVisible, setIsVisible] = useState<boolean>(true);

  useEffect(() => {
    setIsClient(true);
    setHasWebGL(isWebGLSupported());
    setIsReduced(prefersReducedMotion());

    // Performance Optimization: Pause rendering when hero is off-screen
    if (typeof window !== 'undefined' && 'IntersectionObserver' in window && containerRef.current) {
      const observer = new IntersectionObserver(
        (entries) => {
          entries.forEach((entry) => {
            setIsVisible(entry.isIntersecting);
          });
        },
        { threshold: 0.1 }
      );
      observer.observe(containerRef.current);
      return () => observer.disconnect();
    }
  }, []);

  if (!isClient || !hasWebGL || hasError) {
    return <StaticFlaconFallback />;
  }

  return (
    <div
      ref={containerRef}
      className="w-full h-full min-h-[480px] sm:min-h-[540px] lg:min-h-[600px] xl:min-h-[640px] relative overflow-hidden rounded-3xl cursor-grab active:cursor-grabbing select-none bg-radial-gradient from-brand-blush-200/25 via-transparent to-transparent flex items-center justify-center"
    >
      <Suspense fallback={<StaticFlaconFallback />}>
        <Canvas
          // Calibrated Camera Framing: position [0, 0, 5.2], FOV 46° ensures 100% full bottle visibility with 12.5% breathing room
          camera={{ position: [0, 0, 5.2], fov: 46 }}
          dpr={[1, Math.min(typeof window !== 'undefined' ? window.devicePixelRatio : 1, 2)]}
          frameloop={isVisible ? 'always' : 'never'}
          gl={{
            antialias: true,
            alpha: true,
            powerPreference: 'high-performance',
            toneMapping: THREE.ACESFilmicToneMapping,
            toneMappingExposure: 1.22,
          }}
          onError={() => setHasError(true)}
          style={{ width: '100%', height: '100%' }}
        >
          {/* Smooth 360° Drag & Touch Orbit Controls */}
          <OrbitControls
            enableZoom={false}
            enablePan={false}
            autoRotate={autoRotate && !isInteracting && isVisible}
            autoRotateSpeed={1.6}
            minPolarAngle={Math.PI / 3.0}
            maxPolarAngle={Math.PI / 1.70}
            dampingFactor={0.07}
            onStart={() => setIsInteracting(true)}
            onEnd={() => setTimeout(() => setIsInteracting(false), 2000)}
          />

          {/* Studio Product Photography Lighting Setup */}
          <ambientLight intensity={0.75} color="#FFFFFF" />
          
          {/* Key Light (Warm Ivory) */}
          <directionalLight
            position={[4, 6, 4]}
            intensity={2.6}
            color="#FFF9F0"
          />
          
          {/* Rose/Plum Accent Rim Light */}
          <directionalLight
            position={[-4, 2, -3]}
            intensity={1.8}
            color="#F2D2E7"
          />
          
          {/* Top Specular Glimmer Light */}
          <pointLight
            position={[0, 4, 2]}
            intensity={1.5}
            color="#F4DFC0"
          />

          {/* Gentle Soft Bottom Fill */}
          <pointLight
            position={[0, -3, 2]}
            intensity={0.8}
            color="#C7A66A"
          />

          {/* Subtle Golden Fragrance Mist Particles */}
          <Sparkles
            count={28}
            scale={4.2}
            size={2.4}
            speed={0.30}
            opacity={0.65}
            color="#E5C378"
          />

          {/* Weightless Floating Levitation Effect (Controlled amplitude to preserve breathing room) */}
          {isReduced ? (
            <LuxuryPerfumeBottle reducedMotion={true} isVisible={isVisible} />
          ) : (
            <Float
              speed={1.5}
              rotationIntensity={0.08}
              floatIntensity={0.15}
              floatingRange={[-0.02, 0.02]}
            >
              <LuxuryPerfumeBottle reducedMotion={false} isVisible={isVisible} />
            </Float>
          )}
        </Canvas>
      </Suspense>

      {/* Floating Interactive Badge & Controls */}
      <div className="absolute top-4 left-4 flex items-center gap-2">
        <span className="px-3 py-1 rounded-full bg-brand-plum-950/80 backdrop-blur-md text-brand-blush-200 text-[11px] font-semibold tracking-wider uppercase border border-brand-gold-500/30 flex items-center gap-1.5 shadow-sm">
          <SparklesIcon className="w-3 h-3 text-brand-gold-400" />
          <span>100ml Pure Parfum Flacon</span>
        </span>
      </div>

      <div className="absolute bottom-4 left-4 right-4 flex items-center justify-between pointer-events-auto">
        <div className="bg-white/90 backdrop-blur-md px-3.5 py-1.5 rounded-full border border-brand-blush-300/50 text-[11px] text-brand-plum-950 font-medium shadow-md flex items-center gap-2 select-none">
          <span className="w-2 h-2 rounded-full bg-brand-gold-500 animate-pulse" />
          <span>🖱️ Drag to Rotate 360°</span>
        </div>

        <button
          type="button"
          onClick={() => setAutoRotate(prev => !prev)}
          className="bg-brand-plum-950/85 hover:bg-brand-plum-900 text-brand-blush-100 px-3 py-1.5 rounded-full border border-brand-gold-500/40 text-[11px] font-medium shadow-md flex items-center gap-1.5 transition-all active:scale-95 cursor-pointer"
          title="Toggle Auto Rotation"
        >
          <RotateCw className={`w-3 h-3 text-brand-gold-400 ${autoRotate ? 'animate-spin' : ''}`} style={{ animationDuration: '6s' }} />
          <span>{autoRotate ? 'Auto: ON' : 'Auto: OFF'}</span>
        </button>
      </div>
    </div>
  );
};

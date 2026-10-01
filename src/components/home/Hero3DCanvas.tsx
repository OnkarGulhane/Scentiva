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
 * Procedural Haute Parfumerie Flacon (Grand Scale & Masterful Refinement)
 * Authentic luxury perfume architecture:
 * - Substantial heavy beveled crystal base and clear glass sidewalls
 * - Refractive glowing amber-cognac perfume elixir core with subtle liquid dynamics
 * - Polished 24k gold atomizer collar, pump nozzle, and internal dip-tube
 * - Faceted royal plum lacquered magnetic cap with gold trim
 * - Micro-recessed gold-bordered SCENTIVA crest plaque
 * - Grounding pedestal contact shadow
 */
const LuxuryPerfumeBottle: React.FC<{ reducedMotion: boolean }> = ({ reducedMotion }) => {
  const liquidRef = useRef<THREE.Mesh>(null);

  useFrame((state) => {
    if (reducedMotion) return;
    const t = state.clock.getElapsedTime();
    if (liquidRef.current) {
      liquidRef.current.position.y = -0.05 + Math.sin(t * 1.5) * 0.005;
    }
  });

  return (
    // Scaled & centered prominently for grand luxury presence (Grand Scale = 1.52)
    <group position={[0, -0.15, 0]} scale={1.52}>
      {/* 1. Heavy Solid Crystal Base */}
      <mesh position={[0, -0.95, 0]}>
        <boxGeometry args={[1.65, 0.35, 0.92]} />
        <meshPhysicalMaterial
          color="#FFFDFB"
          transparent
          opacity={0.5}
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
      <mesh position={[0, 0.12, 0]}>
        <boxGeometry args={[1.65, 1.82, 0.92]} />
        <meshPhysicalMaterial
          color="#FAF8F5"
          transparent
          opacity={0.4}
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
      <mesh ref={liquidRef} position={[0, -0.05, 0]}>
        <boxGeometry args={[1.38, 1.52, 0.68]} />
        <meshPhysicalMaterial
          color="#DFB35A"
          emissive="#7A2255"
          emissiveIntensity={0.25}
          roughness={0.05}
          transmission={0.65}
          thickness={1.1}
          ior={1.42}
          transparent
          opacity={0.92}
        />
      </mesh>

      {/* 4. Internal Glass Dip-Tube (Atomizer Straw) */}
      <mesh position={[0, -0.05, 0]}>
        <cylinderGeometry args={[0.02, 0.02, 1.65, 16]} />
        <meshPhysicalMaterial
          color="#FFFFFF"
          transparent
          opacity={0.65}
          transmission={0.88}
          roughness={0.06}
        />
      </mesh>

      {/* 5. Gold Atomizer Collar & Crimp Neck */}
      <mesh position={[0, 1.15, 0]}>
        <cylinderGeometry args={[0.28, 0.34, 0.26, 32]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.96}
          roughness={0.12}
        />
      </mesh>

      {/* 6. Gold Sprayer Pump Nozzle */}
      <mesh position={[0, 1.32, 0]}>
        <cylinderGeometry args={[0.18, 0.18, 0.2, 32]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.98}
          roughness={0.1}
        />
      </mesh>

      {/* 7. Faceted Luxury Magnetic Cap (Royal Velvet Plum Lacquer) */}
      <mesh position={[0, 1.74, 0]}>
        <cylinderGeometry args={[0.48, 0.5, 0.72, 8]} />
        <meshPhysicalMaterial
          color="#2A0B21"
          roughness={0.12}
          clearcoat={1}
          clearcoatRoughness={0.04}
          metalness={0.32}
          reflectivity={0.92}
        />
      </mesh>

      {/* 8. Cap Gold Base Ring Trim */}
      <mesh position={[0, 1.4, 0]}>
        <torusGeometry args={[0.47, 0.035, 16, 32]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.96}
          roughness={0.1}
        />
      </mesh>

      {/* 9. Cap Crown Gold Disc Inset */}
      <mesh position={[0, 2.1, 0]} rotation={[Math.PI / 2, 0, 0]}>
        <circleGeometry args={[0.42, 32]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.95}
          roughness={0.12}
        />
      </mesh>

      {/* 10. Front Luxury SCENTIVA Label Plaque */}
      {/* Outer Gold Frame */}
      <mesh position={[0, 0.05, 0.465]}>
        <planeGeometry args={[1.14, 1.42]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.95}
          roughness={0.14}
        />
      </mesh>

      {/* Inner Deep Plum Velvet Plate */}
      <mesh position={[0, 0.05, 0.469]}>
        <planeGeometry args={[1.04, 1.32]} />
        <meshStandardMaterial
          color="#1D0517"
          roughness={0.28}
          metalness={0.2}
        />
      </mesh>

      {/* Embossed Gold Crest Medallion */}
      <mesh position={[0, 0.38, 0.473]}>
        <torusGeometry args={[0.18, 0.018, 16, 32]} />
        <meshStandardMaterial
          color="#F4DFC0"
          metalness={0.96}
          roughness={0.1}
        />
      </mesh>

      {/* Monogram S Center */}
      <mesh position={[0, 0.38, 0.473]}>
        <circleGeometry args={[0.09, 16]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.96}
          roughness={0.1}
        />
      </mesh>

      {/* Label Brand Gold Bars */}
      <mesh position={[0, 0.09, 0.473]}>
        <planeGeometry args={[0.78, 0.075]} />
        <meshStandardMaterial
          color="#F4DFC0"
          metalness={0.94}
          roughness={0.16}
        />
      </mesh>

      <mesh position={[0, -0.06, 0.473]}>
        <planeGeometry args={[0.58, 0.045]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.92}
          roughness={0.18}
        />
      </mesh>

      <mesh position={[0, -0.23, 0.473]}>
        <planeGeometry args={[0.66, 0.035]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.94}
          roughness={0.16}
        />
      </mesh>

      {/* 11. Soft Pedestal Contact Shadow */}
      <mesh position={[0, -1.22, 0]} rotation={[-Math.PI / 2, 0, 0]}>
        <planeGeometry args={[2.8, 1.8]} />
        <meshBasicMaterial
          color="#15030F"
          transparent
          opacity={0.38}
        />
      </mesh>
    </group>
  );
};

export const Hero3DCanvas: React.FC = () => {
  const [hasWebGL, setHasWebGL] = useState<boolean>(true);
  const [isReduced, setIsReduced] = useState<boolean>(false);
  const [hasError, setHasError] = useState<boolean>(false);
  const [isClient, setIsClient] = useState<boolean>(false);
  const [isInteracting, setIsInteracting] = useState<boolean>(false);
  const [autoRotate, setAutoRotate] = useState<boolean>(true);

  useEffect(() => {
    setIsClient(true);
    setHasWebGL(isWebGLSupported());
    setIsReduced(prefersReducedMotion());
  }, []);

  if (!isClient || !hasWebGL || hasError) {
    return <StaticFlaconFallback />;
  }

  return (
    <div className="w-full h-full min-h-[500px] sm:min-h-[560px] lg:min-h-[620px] xl:min-h-[660px] relative overflow-hidden rounded-3xl cursor-grab active:cursor-grabbing select-none bg-radial-gradient from-brand-blush-200/20 via-transparent to-transparent flex items-center justify-center">
      <Suspense fallback={<StaticFlaconFallback />}>
        <Canvas
          // Focused camera framing: brings the towering luxury flacon right to the center stage
          camera={{ position: [0, 0.1, 3.4], fov: 38 }}
          dpr={[1, Math.min(typeof window !== 'undefined' ? window.devicePixelRatio : 1, 2)]}
          gl={{
            antialias: true,
            alpha: true,
            powerPreference: 'high-performance',
            toneMapping: THREE.ACESFilmicToneMapping,
            toneMappingExposure: 1.25,
          }}
          onError={() => setHasError(true)}
          style={{ width: '100%', height: '100%' }}
        >
          {/* Smooth 360° Drag & Touch Orbit Controls */}
          <OrbitControls
            enableZoom={false}
            enablePan={false}
            autoRotate={autoRotate && !isInteracting}
            autoRotateSpeed={1.8}
            minPolarAngle={Math.PI / 2.9}
            maxPolarAngle={Math.PI / 1.72}
            dampingFactor={0.07}
            onStart={() => setIsInteracting(true)}
            onEnd={() => setTimeout(() => setIsInteracting(false), 2200)}
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
            count={32}
            scale={3.8}
            size={2.6}
            speed={0.35}
            opacity={0.7}
            color="#E5C378"
          />

          {/* Weightless Floating Levitation Effect */}
          {isReduced ? (
            <LuxuryPerfumeBottle reducedMotion={true} />
          ) : (
            <Float
              speed={1.6}
              rotationIntensity={0.12}
              floatIntensity={0.2}
              floatingRange={[-0.03, 0.03]}
            >
              <LuxuryPerfumeBottle reducedMotion={false} />
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

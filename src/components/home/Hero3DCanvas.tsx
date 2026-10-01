import React, { useRef, useState, useEffect, Suspense } from 'react';
import { Canvas, useFrame } from '@react-three/fiber';
import * as THREE from 'three';
import { prefersReducedMotion } from '../../motion/motionTokens';

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
 * Procedural Haute Parfumerie Flacon
 * Designed with authentic luxury perfume construction:
 * - Heavy beveled crystal base and clear glass sidewalls
 * - Refractive amber-cognac perfume elixir core with meniscus
 * - Polished 24k gold atomizer collar and dip-tube
 * - Faceted royal plum lacquered magnetic cap with gold trim
 * - Micro-recessed gold-bordered SCENTIVA crest plaque
 * - Grounding soft pedestal contact shadow
 */
const LuxuryPerfumeBottle: React.FC<{ reducedMotion: boolean }> = ({ reducedMotion }) => {
  const groupRef = useRef<THREE.Group>(null);
  const liquidRef = useRef<THREE.Mesh>(null);

  useFrame((state) => {
    if (!groupRef.current) return;
    if (reducedMotion) {
      groupRef.current.rotation.y = 0.05;
      groupRef.current.rotation.x = 0;
      return;
    }

    // Restrained, slow luxury idle rotation + subtle cursor parallax
    const t = state.clock.getElapsedTime();
    const idleY = Math.sin(t * 0.35) * 0.12;
    const idleX = Math.cos(t * 0.25) * 0.03;
    const targetY = idleY + (state.pointer.x * 0.2);
    const targetX = idleX + (-state.pointer.y * 0.08);

    groupRef.current.rotation.y = THREE.MathUtils.lerp(groupRef.current.rotation.y, targetY, 0.05);
    groupRef.current.rotation.x = THREE.MathUtils.lerp(groupRef.current.rotation.x, targetX, 0.05);

    if (liquidRef.current) {
      liquidRef.current.position.y = -0.05 + Math.sin(t * 1.2) * 0.008;
    }
  });

  return (
    // Flacon center offset: shifts bounding box so exact flacon center sits at (0, 0, 0)
    <group ref={groupRef} position={[0, -0.38, 0]} scale={0.88}>
      {/* 1. Heavy Solid Crystal Base */}
      <mesh position={[0, -0.95, 0]}>
        <boxGeometry args={[1.58, 0.32, 0.88]} />
        <meshPhysicalMaterial
          color="#FFFDFB"
          transparent
          opacity={0.4}
          roughness={0.03}
          transmission={0.92}
          thickness={1.6}
          ior={1.54}
          reflectivity={0.95}
          clearcoat={1}
          clearcoatRoughness={0.02}
        />
      </mesh>

      {/* 2. Main Outer Crystal Flacon Body */}
      <mesh position={[0, 0.1, 0]}>
        <boxGeometry args={[1.58, 1.8, 0.88]} />
        <meshPhysicalMaterial
          color="#FAF8F5"
          transparent
          opacity={0.35}
          roughness={0.04}
          transmission={0.94}
          thickness={1.4}
          ior={1.52}
          reflectivity={0.9}
          clearcoat={1}
          clearcoatRoughness={0.03}
        />
      </mesh>

      {/* 3. Internal Perfume Elixir Core (Warm Golden Amber / Cognac) */}
      <mesh ref={liquidRef} position={[0, -0.05, 0]}>
        <boxGeometry args={[1.32, 1.48, 0.64]} />
        <meshPhysicalMaterial
          color="#DFB35A"
          emissive="#8B3A62"
          emissiveIntensity={0.16}
          roughness={0.08}
          transmission={0.62}
          thickness={0.9}
          ior={1.38}
          transparent
          opacity={0.88}
        />
      </mesh>

      {/* 4. Internal Glass Dip-Tube (Atomizer Straw) */}
      <mesh position={[0, -0.05, 0]}>
        <cylinderGeometry args={[0.018, 0.018, 1.6, 16]} />
        <meshPhysicalMaterial
          color="#FFFFFF"
          transparent
          opacity={0.5}
          transmission={0.8}
          roughness={0.1}
        />
      </mesh>

      {/* 5. Gold Atomizer Collar & Crimp Neck */}
      <mesh position={[0, 1.12, 0]}>
        <cylinderGeometry args={[0.26, 0.32, 0.24, 32]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.96}
          roughness={0.14}
        />
      </mesh>

      {/* 6. Gold Sprayer Pump Nozzle */}
      <mesh position={[0, 1.28, 0]}>
        <cylinderGeometry args={[0.16, 0.16, 0.18, 32]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.98}
          roughness={0.12}
        />
      </mesh>

      {/* 7. Faceted Luxury Magnetic Cap (Royal Plum Lacquer) */}
      <mesh position={[0, 1.68, 0]}>
        <cylinderGeometry args={[0.44, 0.46, 0.68, 8]} />
        <meshPhysicalMaterial
          color="#2A0B21"
          roughness={0.16}
          clearcoat={1}
          clearcoatRoughness={0.06}
          metalness={0.28}
          reflectivity={0.85}
        />
      </mesh>

      {/* 8. Cap Gold Base Ring Trim */}
      <mesh position={[0, 1.36, 0]}>
        <torusGeometry args={[0.43, 0.03, 16, 32]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.96}
          roughness={0.1}
        />
      </mesh>

      {/* 9. Cap Crown Gold Disc Inset */}
      <mesh position={[0, 2.02, 0]} rotation={[Math.PI / 2, 0, 0]}>
        <circleGeometry args={[0.38, 32]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.95}
          roughness={0.15}
        />
      </mesh>

      {/* 10. Front Luxury SCENTIVA Label Plaque */}
      {/* Outer Gold Frame */}
      <mesh position={[0, 0.05, 0.445]}>
        <planeGeometry args={[1.08, 1.36]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.92}
          roughness={0.18}
        />
      </mesh>

      {/* Inner Deep Plum Velvet Plate */}
      <mesh position={[0, 0.05, 0.449]}>
        <planeGeometry args={[0.98, 1.26]} />
        <meshStandardMaterial
          color="#200619"
          roughness={0.35}
          metalness={0.15}
        />
      </mesh>

      {/* Embossed Gold Crest Medallion */}
      <mesh position={[0, 0.36, 0.453]}>
        <torusGeometry args={[0.16, 0.016, 16, 32]} />
        <meshStandardMaterial
          color="#F4DFC0"
          metalness={0.95}
          roughness={0.1}
        />
      </mesh>

      {/* Monogram S Center */}
      <mesh position={[0, 0.36, 0.453]}>
        <circleGeometry args={[0.08, 16]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.95}
          roughness={0.12}
        />
      </mesh>

      {/* Label Brand Gold Bars (Simulating Embossed Typography) */}
      <mesh position={[0, 0.08, 0.453]}>
        <planeGeometry args={[0.72, 0.07]} />
        <meshStandardMaterial
          color="#F4DFC0"
          metalness={0.9}
          roughness={0.2}
        />
      </mesh>

      <mesh position={[0, -0.06, 0.453]}>
        <planeGeometry args={[0.54, 0.04]} />
        <meshStandardMaterial
          color="#C7A66A"
          metalness={0.88}
          roughness={0.25}
        />
      </mesh>

      <mesh position={[0, -0.22, 0.453]}>
        <planeGeometry args={[0.62, 0.03]} />
        <meshStandardMaterial
          color="#DFBA73"
          metalness={0.9}
          roughness={0.2}
        />
      </mesh>

      {/* 11. Soft Pedestal Contact Shadow */}
      <mesh position={[0, -1.2, 0]} rotation={[-Math.PI / 2, 0, 0]}>
        <planeGeometry args={[2.2, 1.4]} />
        <meshBasicMaterial
          color="#15030F"
          transparent
          opacity={0.32}
        />
      </mesh>
    </group>
  );
};

import { StaticFlaconFallback } from './StaticFlaconFallback';

export const Hero3DCanvas: React.FC = () => {
  const [hasWebGL, setHasWebGL] = useState<boolean>(true);
  const [isReduced, setIsReduced] = useState<boolean>(false);
  const [hasError, setHasError] = useState<boolean>(false);
  const [isClient, setIsClient] = useState<boolean>(false);

  useEffect(() => {
    setIsClient(true);
    setHasWebGL(isWebGLSupported());
    setIsReduced(prefersReducedMotion());
  }, []);

  if (!isClient || !hasWebGL || hasError) {
    return <StaticFlaconFallback />;
  }

  return (
    <div className="w-full h-full min-h-[380px] sm:min-h-[460px] lg:min-h-[540px] relative overflow-hidden rounded-3xl">
      <Suspense fallback={<StaticFlaconFallback />}>
        <Canvas
          // Responsive camera framing: fov 38, position [0, 0, 5.2] guarantees the cap and base are fully framed
          camera={{ position: [0, 0, 5.2], fov: 38 }}
          // Performance caps: DPR clamped between 1.0 and 1.75 to prevent mobile GPU throttling
          dpr={[1, Math.min(typeof window !== 'undefined' ? window.devicePixelRatio : 1, 1.75)]}
          gl={{
            antialias: true,
            alpha: true,
            powerPreference: 'high-performance',
            toneMapping: THREE.ACESFilmicToneMapping,
            toneMappingExposure: 1.15,
          }}
          onError={() => setHasError(true)}
          style={{ width: '100%', height: '100%', pointerEvents: 'auto' }}
        >
          {/* Studio Product Photography Lighting */}
          <ambientLight intensity={0.65} color="#FFFFFF" />
          
          {/* Key Light (Warm Ivory) */}
          <directionalLight
            position={[4, 6, 4]}
            intensity={2.2}
            color="#FFF9F0"
          />
          
          {/* Rose/Plum Accent Rim Light */}
          <directionalLight
            position={[-4, 2, -3]}
            intensity={1.4}
            color="#F2D2E7"
          />
          
          {/* Top Specular Glimmer Light */}
          <pointLight
            position={[0, 4, 2]}
            intensity={1.2}
            color="#F4DFC0"
          />

          {/* Gentle Soft Bottom Fill */}
          <pointLight
            position={[0, -3, 2]}
            intensity={0.6}
            color="#C7A66A"
          />

          {/* Procedural Luxury Flacon */}
          <LuxuryPerfumeBottle reducedMotion={isReduced} />
        </Canvas>
      </Suspense>

      {/* Floating Micro Badge Indicator */}
      <div className="absolute bottom-4 right-4 bg-white/85 backdrop-blur-md px-3 py-1.5 rounded-full border border-brand-blush-300/40 text-[11px] text-brand-plum-950 font-medium shadow-sm flex items-center gap-1.5 pointer-events-none select-none">
        <span className="w-2 h-2 rounded-full bg-brand-gold-500 animate-pulse" />
        <span>3D Interactive Flacon • Move Cursor</span>
      </div>
    </div>
  );
};

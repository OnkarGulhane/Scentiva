import { useEffect, useRef, DependencyList } from 'react';
import gsap from 'gsap';
import { prefersReducedMotion } from './motionTokens';

/**
 * Custom React hook for safely creating scoped GSAP animations.
 * Ensures all timelines, tweens, and ScrollTriggers are killed and reverted on unmount.
 */
export const useGsapContext = (
  callback: (context: gsap.Context) => void,
  scopeRef: React.RefObject<HTMLElement | null>,
  deps: DependencyList = []
) => {
  const isReduced = prefersReducedMotion();

  useEffect(() => {
    if (isReduced || !scopeRef.current) return;

    const ctx = gsap.context((self) => {
      callback(self);
    }, scopeRef);

    return () => {
      ctx.revert(); // Complete cleanup
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isReduced, ...deps]);
};

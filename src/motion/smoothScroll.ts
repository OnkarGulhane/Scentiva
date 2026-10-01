import Lenis from 'lenis';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { prefersReducedMotion } from './motionTokens';

gsap.registerPlugin(ScrollTrigger);

let lenisInstance: Lenis | null = null;
let rafCallback: ((time: number) => void) | null = null;

export const initSmoothScroll = (): Lenis | null => {
  if (typeof window === 'undefined') return null;

  // Respect reduced motion preference
  if (prefersReducedMotion()) {
    return null;
  }

  // Prevent multiple initializations
  if (lenisInstance) {
    return lenisInstance;
  }

  try {
    lenisInstance = new Lenis({
      duration: 1.2,
      easing: (t: number) => Math.min(1, 1.001 - Math.pow(2, -10 * t)),
      smoothWheel: true,
      touchMultiplier: 1.5,
    });

    // Synchronize Lenis with GSAP ScrollTrigger
    lenisInstance.on('scroll', ScrollTrigger.update);

    rafCallback = (time: number) => {
      lenisInstance?.raf(time * 1000);
    };

    gsap.ticker.add(rafCallback);
    gsap.ticker.lagSmoothing(0);

    return lenisInstance;
  } catch (error) {
    console.warn('Lenis smooth scrolling initialization bypassed:', error);
    return null;
  }
};

export const getLenisInstance = (): Lenis | null => {
  return lenisInstance;
};

export const destroySmoothScroll = () => {
  if (rafCallback) {
    gsap.ticker.remove(rafCallback);
    rafCallback = null;
  }
  if (lenisInstance) {
    lenisInstance.destroy();
    lenisInstance = null;
  }
};

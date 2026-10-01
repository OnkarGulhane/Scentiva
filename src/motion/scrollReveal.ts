import { useEffect, useRef } from 'react';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { MOTION_DURATIONS, MOTION_EASINGS, prefersReducedMotion } from './motionTokens';

gsap.registerPlugin(ScrollTrigger);

interface ScrollRevealOptions {
  y?: number;
  opacity?: number;
  duration?: number;
  delay?: number;
  stagger?: number;
  start?: string;
  selector?: string;
}

export const useScrollReveal = (
  containerRef: React.RefObject<HTMLElement | null>,
  options: ScrollRevealOptions = {}
) => {
  const {
    y = 30,
    opacity = 0,
    duration = MOTION_DURATIONS.editorial,
    delay = 0,
    stagger = 0.1,
    start = 'top 85%',
    selector,
  } = options;

  useEffect(() => {
    if (prefersReducedMotion() || !containerRef.current) return;

    const target = selector 
      ? containerRef.current.querySelectorAll(selector)
      : containerRef.current;

    if (!target || (target instanceof NodeList && target.length === 0)) return;

    const ctx = gsap.context(() => {
      gsap.from(target, {
        y,
        opacity,
        duration,
        delay,
        stagger: selector ? stagger : 0,
        ease: MOTION_EASINGS.easeOut,
        scrollTrigger: {
          trigger: containerRef.current,
          start,
          toggleActions: 'play none none none',
          once: true,
        },
      });
    }, containerRef);

    return () => ctx.revert();
  }, [containerRef, y, opacity, duration, delay, stagger, start, selector]);
};

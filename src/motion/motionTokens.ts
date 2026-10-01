/**
 * SCENTIVA Motion Tokens & Easing Constants
 * Follows the motion.md specification:
 * - Instant: 100ms
 * - Fast: 160ms
 * - Standard: 240ms
 * - Emphasis: 420ms
 * - Editorial: 700ms
 */

export const MOTION_DURATIONS = {
  instant: 0.1,    // 100ms - Micro-interactions, button presses, toggles
  fast: 0.16,      // 160ms - Hover states, small popovers, badges
  standard: 0.24,  // 240ms - Dropdowns, toast reveals, modal entrances
  emphasis: 0.42,  // 420ms - Drawers, page transitions, accordion expands
  editorial: 0.7,  // 700ms - Hero reveal timelines, editorial section entrances
} as const;

export const MOTION_EASINGS = {
  easeOut: 'power3.out',
  easeInOut: 'power2.inOut',
  luxury: 'expo.out',
  spring: 'back.out(1.4)',
} as const;

export const prefersReducedMotion = (): boolean => {
  if (typeof window === 'undefined') return false;
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
};

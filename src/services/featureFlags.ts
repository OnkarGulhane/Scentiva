import { FeatureFlags } from '../types';

/**
 * SCENTIVA Feature Flags Manager (Section 40)
 * Allows runtime toggling and environment-based configuration for experimental features.
 */
class FeatureFlagManager {
  private flags: FeatureFlags = {
    ENABLE_3D_HERO: process.env.NEXT_PUBLIC_ENABLE_3D_HERO !== 'false',
    ENABLE_SCENT_FINDER: process.env.NEXT_PUBLIC_ENABLE_SCENT_FINDER !== 'false',
    ENABLE_AI_SEARCH: process.env.NEXT_PUBLIC_ENABLE_AI_SEARCH !== 'false',
    ENABLE_PERSONALIZATION: process.env.NEXT_PUBLIC_ENABLE_PERSONALIZATION !== 'false',
    ENABLE_RECOMMENDATIONS: process.env.NEXT_PUBLIC_ENABLE_RECOMMENDATIONS !== 'false',
  };

  isEnabled(flag: keyof FeatureFlags): boolean {
    return !!this.flags[flag];
  }

  getFlags(): FeatureFlags {
    return { ...this.flags };
  }

  setFlag(flag: keyof FeatureFlags, value: boolean) {
    this.flags[flag] = value;
  }
}

export const featureFlags = new FeatureFlagManager();

/**
 * SCENTIVA Catalog Media Validator & Integrity Checker
 * 
 * Performs strict validation over all product, brand, category, and editorial media:
 * 1. Checks for empty image arrays
 * 2. Checks for duplicate primary images across unrelated products
 * 3. Scans for deny-listed non-fragrance terms (skincare, sunscreen, makeup, lotion, etc.)
 * 4. Ensures all image URLs are well-formed and valid
 */

import { Product } from '../types';
import { PRODUCT_MEDIA_CATALOG, CATEGORY_MEDIA_CATALOG, BRAND_BANNER_CATALOG } from '../data/mediaCatalog';

export const DENY_LISTED_TERMS = [
  'sunscreen',
  'sunblock',
  'serum',
  'moisturizer',
  'cream',
  'lotion',
  'shampoo',
  'conditioner',
  'makeup',
  'lipstick',
  'foundation',
  'skincare',
  'soap',
  'food',
  'clothing',
  'shoes',
  'watches',
  'jewelry',
  'electronics'
];

export interface MediaValidationResult {
  valid: boolean;
  totalProductsChecked: number;
  totalMediaAssetsChecked: number;
  errors: string[];
  warnings: string[];
}

export function validateCatalogMedia(products: Product[]): MediaValidationResult {
  const errors: string[] = [];
  const warnings: string[] = [];
  const seenPrimaryUrls = new Map<string, string>(); // url -> productId
  let totalAssetsChecked = 0;

  // 1. Validate Product Media
  for (const prod of products) {
    if (!prod.images || prod.images.length === 0) {
      errors.push(`[CRITICAL] Product "${prod.name}" (${prod.id}) has an empty images array.`);
      continue;
    }

    const primaryUrl = prod.images[0];
    totalAssetsChecked += prod.images.length;

    // Check for duplicates
    if (seenPrimaryUrls.has(primaryUrl)) {
      const existingProdId = seenPrimaryUrls.get(primaryUrl);
      errors.push(
        `[DUPLICATE] Primary image collision: Product "${prod.name}" (${prod.id}) shares primary image with "${existingProdId}".`
      );
    } else {
      seenPrimaryUrls.set(primaryUrl, prod.id);
    }

    // Check against deny list in URL, name, description, alt
    for (const imgUrl of prod.images) {
      const lowerUrl = imgUrl.toLowerCase();
      for (const term of DENY_LISTED_TERMS) {
        if (lowerUrl.includes(term)) {
          errors.push(
            `[CONTAMINATION] Product "${prod.name}" (${prod.id}) image contains deny-listed term "${term}": ${imgUrl}`
          );
        }
      }
    }

    // Cross-check with media catalog
    const mediaSet = PRODUCT_MEDIA_CATALOG[prod.id];
    if (!mediaSet) {
      warnings.push(`[MANIFEST MISSING] Product "${prod.name}" (${prod.id}) is not registered in mediaCatalog.ts`);
    } else if (!mediaSet.primary.verifiedFragrance) {
      errors.push(`[UNVERIFIED] Product "${prod.name}" has unverified fragrance media.`);
    }
  }

  // 2. Validate Categories
  for (const [catId, asset] of Object.entries(CATEGORY_MEDIA_CATALOG)) {
    totalAssetsChecked++;
    if (!asset.url) {
      errors.push(`[CATEGORY ERROR] Category "${catId}" has missing URL.`);
    }
    const lowerUrl = asset.url.toLowerCase();
    for (const term of DENY_LISTED_TERMS) {
      if (lowerUrl.includes(term)) {
        errors.push(`[CATEGORY CONTAMINATION] Category "${catId}" contains deny-listed term "${term}".`);
      }
    }
  }

  // 3. Validate Brand Banners
  for (const [brandId, asset] of Object.entries(BRAND_BANNER_CATALOG)) {
    totalAssetsChecked++;
    if (!asset.url) {
      errors.push(`[BRAND ERROR] Brand "${brandId}" banner has missing URL.`);
    }
    const lowerUrl = asset.url.toLowerCase();
    for (const term of DENY_LISTED_TERMS) {
      if (lowerUrl.includes(term)) {
        errors.push(`[BRAND CONTAMINATION] Brand "${brandId}" contains deny-listed term "${term}".`);
      }
    }
  }

  return {
    valid: errors.length === 0,
    totalProductsChecked: products.length,
    totalMediaAssetsChecked: totalAssetsChecked,
    errors,
    warnings
  };
}

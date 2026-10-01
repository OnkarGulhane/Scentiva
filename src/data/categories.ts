import { CATEGORY_MEDIA_CATALOG } from './mediaCatalog';

export type CategoryItem = {
  id: string;
  slug: string;
  title: string;
  tagline: string;
  description: string;
  image: string;
  accentColor: string;
  productCount: number;
};

export const CATEGORIES: CategoryItem[] = [
  {
    id: 'cat-her',
    slug: 'for-her',
    title: 'For Her',
    tagline: 'Elegant & Feminine',
    description: 'Enchanting florals, radiant amber, and sensual gourmand perfumes designed to make an indelible impression.',
    image: CATEGORY_MEDIA_CATALOG['cat-her'].url,
    accentColor: '#F2D2E7',
    productCount: 14,
  },
  {
    id: 'cat-him',
    slug: 'for-him',
    title: 'For Him',
    tagline: 'Bold & Confident',
    description: 'Crisp bergamot, smoldering woods, spicy leather, and marine accords crafted for modern distinction.',
    image: CATEGORY_MEDIA_CATALOG['cat-him'].url,
    accentColor: '#321027',
    productCount: 12,
  },
  {
    id: 'cat-unisex',
    slug: 'unisex',
    title: 'Unisex',
    tagline: 'Unique & Versatile',
    description: 'Boundary-defying olfactory creations where smokey vetiver, incense, and rare saffron harmonize seamlessly.',
    image: CATEGORY_MEDIA_CATALOG['cat-unisex'].url,
    accentColor: '#742653',
    productCount: 10,
  },
  {
    id: 'cat-luxury',
    slug: 'luxury-niche',
    title: 'Luxury & Niche',
    tagline: 'Exclusively Yours',
    description: 'Rare botanical extractions, high-concentration Millésimes, and limited artisan collector flacons.',
    image: CATEGORY_MEDIA_CATALOG['cat-luxury'].url,
    accentColor: '#C7A66A',
    productCount: 8,
  },
  {
    id: 'cat-everyday',
    slug: 'everyday',
    title: 'Everyday Fresh',
    tagline: 'Clean & Invigorating',
    description: 'Effortlessly uplifting citrus, green tea, and aquatic notes ideal for work, morning commutes, and casual brunches.',
    image: CATEGORY_MEDIA_CATALOG['cat-everyday'].url,
    accentColor: '#FAEAF4',
    productCount: 9,
  },
  {
    id: 'cat-gifts',
    slug: 'gift-sets',
    title: 'Gift Sets & Discovery',
    tagline: 'Memorable Moments',
    description: 'Curated travel coffrets, luxury miniature sets, and bespoke discovery boxes presented in embossed keepsake cases.',
    image: CATEGORY_MEDIA_CATALOG['cat-gifts'].url,
    accentColor: '#B85B88',
    productCount: 6,
  }
];

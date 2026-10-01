import { Brand } from '../types';
import { BRAND_BANNER_CATALOG } from './mediaCatalog';

export const BRANDS: Brand[] = [
  {
    id: 'b-dior',
    slug: 'dior',
    name: 'Christian Dior',
    origin: 'Paris, France',
    foundedYear: 1946,
    description: 'Iconic French luxury house crafting unforgettable olfactory signatures that embody haute couture elegance and French savoir-faire.',
    bannerImage: BRAND_BANNER_CATALOG['b-dior'].url,
    featuredProductCount: 8,
    tier: 'Luxury'
  },
  {
    id: 'b-chanel',
    slug: 'chanel',
    name: 'CHANEL',
    origin: 'Neuilly-sur-Seine, France',
    foundedYear: 1910,
    description: 'Timeless luxury and rebellious elegance. Chanel redefined modern perfumery with revolutionary floral aldehydes and timeless compositions.',
    bannerImage: BRAND_BANNER_CATALOG['b-chanel'].url,
    featuredProductCount: 7,
    tier: 'Luxury'
  },
  {
    id: 'b-tom-ford',
    slug: 'tom-ford',
    name: 'Tom Ford',
    origin: 'New York, USA',
    foundedYear: 2005,
    description: 'Sensual, glamorous, and provocative. Tom Ford Private Blend fragrances offer rare artisan extractions and intoxicating spicy woody notes.',
    bannerImage: BRAND_BANNER_CATALOG['b-tom-ford'].url,
    featuredProductCount: 5,
    tier: 'Luxury'
  },
  {
    id: 'b-ysl',
    slug: 'ysl',
    name: 'Yves Saint Laurent',
    origin: 'Paris, France',
    foundedYear: 1961,
    description: 'Audacious, vibrant, and chic. YSL fragrances fuse lavender from France with Moroccan orange blossom for a statement of absolute freedom.',
    bannerImage: BRAND_BANNER_CATALOG['b-ysl'].url,
    featuredProductCount: 6,
    tier: 'Designer'
  },
  {
    id: 'b-versace',
    slug: 'versace',
    name: 'Versace',
    origin: 'Milan, Italy',
    foundedYear: 1978,
    description: 'Italian passion, radiant Mediterranean citrus, and vibrant mythological allure encapsulated in opulent glass flacons.',
    bannerImage: BRAND_BANNER_CATALOG['b-versace'].url,
    featuredProductCount: 6,
    tier: 'Designer'
  },
  {
    id: 'b-armani',
    slug: 'armani',
    name: 'Giorgio Armani',
    origin: 'Milan, Italy',
    foundedYear: 1975,
    description: 'Understated Italian sophistication and aquatic freshness capturing the essence of the Mediterranean breeze and volcanic earth.',
    bannerImage: BRAND_BANNER_CATALOG['b-armani'].url,
    featuredProductCount: 5,
    tier: 'Designer'
  },
  {
    id: 'b-creed',
    slug: 'creed',
    name: 'House of Creed',
    origin: 'London / Paris',
    foundedYear: 1760,
    description: 'Royal lineage and artisanal Millésime extractions, hand-crafted using ancient infusion techniques for true connoisseurs.',
    bannerImage: BRAND_BANNER_CATALOG['b-creed'].url,
    featuredProductCount: 4,
    tier: 'Niche'
  },
  {
    id: 'b-byredo',
    slug: 'byredo',
    name: 'Byredo',
    origin: 'Stockholm, Sweden',
    foundedYear: 2006,
    description: 'Scandinavian minimalism meets emotional storytelling. Distinctive olfactory journeys inspired by memories, wanderlust, and poetry.',
    bannerImage: BRAND_BANNER_CATALOG['b-byredo'].url,
    featuredProductCount: 4,
    tier: 'Niche'
  },
  {
    id: 'b-gucci',
    slug: 'gucci',
    name: 'Gucci',
    origin: 'Florence, Italy',
    foundedYear: 1921,
    description: 'Eclectic, contemporary, and romantic. Gucci fragrances celebrate self-expression and lush botanical gardens in bloom.',
    bannerImage: BRAND_BANNER_CATALOG['b-gucci'].url,
    featuredProductCount: 4,
    tier: 'Designer'
  },
  {
    id: 'b-prada',
    slug: 'prada',
    name: 'Prada',
    origin: 'Milan, Italy',
    foundedYear: 1913,
    description: 'Avant-garde luxury exploring multidimensional femininity and crisp woody amber paradigms.',
    bannerImage: BRAND_BANNER_CATALOG['b-prada'].url,
    featuredProductCount: 3,
    tier: 'Designer'
  }
];

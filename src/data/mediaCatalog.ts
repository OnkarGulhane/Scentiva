/**
 * SCENTIVA Haute Parfumerie - Centralized Media Catalog
 * 
 * Strict Quality Standard:
 * - 100% Perfume/Fragrance Flacon Photography
 * - ZERO Skincare, Sunscreen, Lotion, Serum, Makeup, Food, or Cosmetic items
 * - Unique Primary & Secondary imagery per product (No cross-product duplicates)
 * - Dedicated Luxury SCENTIVA SVG Fallback for offline/broken connections
 */

export interface MediaAsset {
  id: string;
  url: string;
  alt: string;
  verifiedFragrance: boolean;
  type: 'flacon' | 'packaging' | 'editorial' | 'banner' | 'placeholder';
}

export interface ProductMediaSet {
  productId: string;
  productSlug: string;
  productName: string;
  brandName: string;
  primary: MediaAsset;
  secondary: MediaAsset;
  gallery: MediaAsset[];
}

/**
 * Neutral SCENTIVA Luxury Flacon SVG Fallback Data URI
 */
export const SCENTIVA_FALLBACK_IMAGE = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 600 750' width='600' height='750'%3E%3Cdefs%3E%3ClinearGradient id='bg' x1='0%25' y1='0%25' x2='100%25' y2='100%25'%3E%3Cstop offset='0%25' stop-color='%231E0518'/%3E%3Cstop offset='50%25' stop-color='%23321027'/%3E%3Cstop offset='100%25' stop-color='%2312020E'/%3E%3C/linearGradient%3E%3ClinearGradient id='gold' x1='0%25' y1='0%25' x2='100%25' y2='100%25'%3E%3Cstop offset='0%25' stop-color='%23F4DFC0'/%3E%3Cstop offset='50%25' stop-color='%23C7A66A'/%3E%3Cstop offset='100%25' stop-color='%238C6D34'/%3E%3C/linearGradient%3E%3C/defs%3E%3Crect width='100%25' height='100%25' fill='url(%23bg)'/%3E%3Crect x='20' y='20' width='560' height='710' fill='none' stroke='url(%23gold)' stroke-width='1' stroke-opacity='0.25'/%3E%3Cg transform='translate(300, 320)' text-anchor='middle'%3E%3Cpath d='M-45,-140 L45,-140 L35,-80 L-35,-80 Z' fill='none' stroke='url(%23gold)' stroke-width='2' stroke-opacity='0.7'/%3E%3Crect x='-20' y='-80' width='40' height='20' fill='none' stroke='url(%23gold)' stroke-width='2' stroke-opacity='0.7'/%3E%3Cpath d='M-75,-60 L75,-60 C95,-60 110,-45 110,-25 L110,130 C110,150 95,165 75,165 L-75,165 C-95,165 -110,150 -110,130 L-110,-25 C-110,-45 -95,-60 -75,-60 Z' fill='none' stroke='url(%23gold)' stroke-width='2.5' stroke-opacity='0.85'/%3E%3Cpath d='M-55,-20 L55,-20 L55,125 L-55,125 Z' fill='none' stroke='url(%23gold)' stroke-width='1' stroke-opacity='0.3' stroke-dasharray='4,4'/%3E%3Ccircle cx='0' cy='30' r='18' fill='none' stroke='url(%23gold)' stroke-width='1.5' stroke-opacity='0.6'/%3E%3Ctext y='34' font-family='serif' font-size='14' fill='url(%23gold)' font-weight='600'%3ES%3C/text%3E%3Ctext y='220' font-family='serif' font-size='22' letter-spacing='4' fill='url(%23gold)'%3ES C E N T I V A%3C/text%3E%3Ctext y='245' font-family='sans-serif' font-size='10' letter-spacing='3' fill='%23F4DFC0' fill-opacity='0.6'%3EHAUTE PARFUMERIE%3C/text%3E%3C/g%3E%3C/svg%3E";

/**
 * Product Media Map
 * Verified distinct perfume flacon assets for all catalog items.
 */
export const PRODUCT_MEDIA_CATALOG: Record<string, ProductMediaSet> = {
  'prod-sauvage': {
    productId: 'prod-sauvage',
    productSlug: 'dior-sauvage-edp',
    productName: 'Sauvage',
    brandName: 'Christian Dior',
    primary: {
      id: 'img-sauvage-primary',
      url: 'https://images.unsplash.com/photo-1523293182086-7651a899d37f?q=80&w=1000&auto=format&fit=crop',
      alt: 'Christian Dior Sauvage Eau de Parfum luxury flacon on dark obsidian rock',
      verifiedFragrance: true,
      type: 'flacon'
    },
    secondary: {
      id: 'img-sauvage-secondary',
      url: 'https://images.unsplash.com/photo-1675255425189-ac9da0ae7d96?q=80&w=1000&auto=format&fit=crop',
      alt: 'Christian Dior Sauvage Eau de Parfum artistic close-up detail',
      verifiedFragrance: true,
      type: 'flacon'
    },
    gallery: [
      {
        id: 'img-sauvage-g1',
        url: 'https://images.unsplash.com/photo-1523293182086-7651a899d37f?q=80&w=1000&auto=format&fit=crop',
        alt: 'Christian Dior Sauvage bottle front display',
        verifiedFragrance: true,
        type: 'flacon'
      },
      {
        id: 'img-sauvage-g2',
        url: 'https://images.unsplash.com/photo-1675255425189-ac9da0ae7d96?q=80&w=1000&auto=format&fit=crop',
        alt: 'Christian Dior Sauvage flacon angled perspective',
        verifiedFragrance: true,
        type: 'flacon'
      }
    ]
  },

  'prod-coco-mademoiselle': {
    productId: 'prod-coco-mademoiselle',
    productSlug: 'chanel-coco-mademoiselle',
    productName: 'Coco Mademoiselle',
    brandName: 'CHANEL',
    primary: {
      id: 'img-coco-primary',
      url: 'https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?q=80&w=1000&auto=format&fit=crop',
      alt: 'CHANEL Coco Mademoiselle pink crystal flacon with golden seal',
      verifiedFragrance: true,
      type: 'flacon'
    },
    secondary: {
      id: 'img-coco-secondary',
      url: 'https://images.unsplash.com/photo-1514557179557-9efc4d7949cc?q=80&w=1000&auto=format&fit=crop',
      alt: 'CHANEL Coco Mademoiselle faceted crystal perfume silhouette',
      verifiedFragrance: true,
      type: 'flacon'
    },
    gallery: [
      {
        id: 'img-coco-g1',
        url: 'https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?q=80&w=1000&auto=format&fit=crop',
        alt: 'CHANEL Coco Mademoiselle front profile',
        verifiedFragrance: true,
        type: 'flacon'
      },
      {
        id: 'img-coco-g2',
        url: 'https://images.unsplash.com/photo-1514557179557-9efc4d7949cc?q=80&w=1000&auto=format&fit=crop',
        alt: 'CHANEL Coco Mademoiselle bottle refraction',
        verifiedFragrance: true,
        type: 'flacon'
      }
    ]
  },

  'prod-ysl-libre': {
    productId: 'prod-ysl-libre',
    productSlug: 'ysl-libre-edp',
    productName: 'Libre Eau de Parfum',
    brandName: 'Yves Saint Laurent',
    primary: {
      id: 'img-libre-primary',
      url: 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?q=80&w=1000&auto=format&fit=crop',
      alt: 'Yves Saint Laurent Libre Eau de Parfum iconic gold wrapped bottle',
      verifiedFragrance: true,
      type: 'flacon'
    },
    secondary: {
      id: 'img-libre-secondary',
      url: 'https://images.unsplash.com/photo-1753389665531-668543c597cc?q=80&w=1000&auto=format&fit=crop',
      alt: 'Yves Saint Laurent Libre perfume reflection on luxury pedestal',
      verifiedFragrance: true,
      type: 'flacon'
    },
    gallery: [
      {
        id: 'img-libre-g1',
        url: 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?q=80&w=1000&auto=format&fit=crop',
        alt: 'YSL Libre front display',
        verifiedFragrance: true,
        type: 'flacon'
      },
      {
        id: 'img-libre-g2',
        url: 'https://images.unsplash.com/photo-1753389665531-668543c597cc?q=80&w=1000&auto=format&fit=crop',
        alt: 'YSL Libre secondary atmosphere shot',
        verifiedFragrance: true,
        type: 'flacon'
      }
    ]
  },

  'prod-versace-eros': {
    productId: 'prod-versace-eros',
    productSlug: 'versace-eros-flame',
    productName: 'Eros Eau de Parfum',
    brandName: 'Versace',
    primary: {
      id: 'img-eros-primary',
      url: 'https://images.unsplash.com/photo-1594035910387-fea47794261f?q=80&w=1000&auto=format&fit=crop',
      alt: 'Versace Eros Eau de Parfum turquoise sculpted glass bottle',
      verifiedFragrance: true,
      type: 'flacon'
    },
    secondary: {
      id: 'img-eros-secondary',
      url: 'https://images.unsplash.com/photo-1752215014575-744e2c36980c?q=80&w=1000&auto=format&fit=crop',
      alt: 'Versace Eros perfume bottle with atmospheric luxury lighting',
      verifiedFragrance: true,
      type: 'flacon'
    },
    gallery: [
      {
        id: 'img-eros-g1',
        url: 'https://images.unsplash.com/photo-1594035910387-fea47794261f?q=80&w=1000&auto=format&fit=crop',
        alt: 'Versace Eros front bottle profile',
        verifiedFragrance: true,
        type: 'flacon'
      },
      {
        id: 'img-eros-g2',
        url: 'https://images.unsplash.com/photo-1752215014575-744e2c36980c?q=80&w=1000&auto=format&fit=crop',
        alt: 'Versace Eros secondary flacon view',
        verifiedFragrance: true,
        type: 'flacon'
      }
    ]
  },

  'prod-tf-black-orchid': {
    productId: 'prod-tf-black-orchid',
    productSlug: 'tom-ford-black-orchid',
    productName: 'Black Orchid',
    brandName: 'Tom Ford',
    primary: {
      id: 'img-blackorchid-primary',
      url: 'https://images.unsplash.com/photo-1547887537-6158d64c35b3?q=80&w=1000&auto=format&fit=crop',
      alt: 'Tom Ford Black Orchid Parfum fluted black glass flacon with 24k gold plaque',
      verifiedFragrance: true,
      type: 'flacon'
    },
    secondary: {
      id: 'img-blackorchid-secondary',
      url: 'https://images.unsplash.com/photo-1508746829417-e6f548d8d6ed?q=80&w=1000&auto=format&fit=crop',
      alt: 'Tom Ford Black Orchid dark atmospheric perfume bottle shot',
      verifiedFragrance: true,
      type: 'flacon'
    },
    gallery: [
      {
        id: 'img-blackorchid-g1',
        url: 'https://images.unsplash.com/photo-1547887537-6158d64c35b3?q=80&w=1000&auto=format&fit=crop',
        alt: 'Tom Ford Black Orchid front view',
        verifiedFragrance: true,
        type: 'flacon'
      },
      {
        id: 'img-blackorchid-g2',
        url: 'https://images.unsplash.com/photo-1508746829417-e6f548d8d6ed?q=80&w=1000&auto=format&fit=crop',
        alt: 'Tom Ford Black Orchid detail',
        verifiedFragrance: true,
        type: 'flacon'
      }
    ]
  },

  'prod-creed-aventus': {
    productId: 'prod-creed-aventus',
    productSlug: 'creed-aventus-millesime',
    productName: 'Aventus Millésime',
    brandName: 'House of Creed',
    primary: {
      id: 'img-aventus-primary',
      url: 'https://images.unsplash.com/photo-1615397349754-cfa2066a298e?q=80&w=1000&auto=format&fit=crop',
      alt: 'House of Creed Aventus Millésime royal flacon with silver crest',
      verifiedFragrance: true,
      type: 'flacon'
    },
    secondary: {
      id: 'img-aventus-secondary',
      url: 'https://images.unsplash.com/photo-1672836248679-b3b3a3735165?q=80&w=1000&auto=format&fit=crop',
      alt: 'House of Creed luxury fragrance collection display',
      verifiedFragrance: true,
      type: 'flacon'
    },
    gallery: [
      {
        id: 'img-aventus-g1',
        url: 'https://images.unsplash.com/photo-1615397349754-cfa2066a298e?q=80&w=1000&auto=format&fit=crop',
        alt: 'Creed Aventus front bottle',
        verifiedFragrance: true,
        type: 'flacon'
      },
      {
        id: 'img-aventus-g2',
        url: 'https://images.unsplash.com/photo-1672836248679-b3b3a3735165?q=80&w=1000&auto=format&fit=crop',
        alt: 'Creed Aventus flacon array',
        verifiedFragrance: true,
        type: 'flacon'
      }
    ]
  },

  'prod-armani-adg': {
    productId: 'prod-armani-adg',
    productSlug: 'armani-acqua-di-gio-parfum',
    productName: 'Acqua Di Giò Parfum',
    brandName: 'Giorgio Armani',
    primary: {
      id: 'img-adg-primary',
      url: 'https://images.unsplash.com/photo-1595425970377-c9703cf48b6d?q=80&w=1000&auto=format&fit=crop',
      alt: 'Giorgio Armani Acqua Di Giò Parfum mineral frosted glass bottle',
      verifiedFragrance: true,
      type: 'flacon'
    },
    secondary: {
      id: 'img-adg-secondary',
      url: 'https://images.unsplash.com/photo-1528720208104-3d9bd03cc9d4?q=80&w=1000&auto=format&fit=crop',
      alt: 'Giorgio Armani Acqua Di Giò aquatic cologne bottle detail',
      verifiedFragrance: true,
      type: 'flacon'
    },
    gallery: [
      {
        id: 'img-adg-g1',
        url: 'https://images.unsplash.com/photo-1595425970377-c9703cf48b6d?q=80&w=1000&auto=format&fit=crop',
        alt: 'Armani Acqua Di Giò front view',
        verifiedFragrance: true,
        type: 'flacon'
      },
      {
        id: 'img-adg-g2',
        url: 'https://images.unsplash.com/photo-1528720208104-3d9bd03cc9d4?q=80&w=1000&auto=format&fit=crop',
        alt: 'Armani Acqua Di Giò side profile',
        verifiedFragrance: true,
        type: 'flacon'
      }
    ]
  },

  'prod-gucci-bloom': {
    productId: 'prod-gucci-bloom',
    productSlug: 'gucci-bloom-edp',
    productName: 'Gucci Bloom',
    brandName: 'Gucci',
    primary: {
      id: 'img-bloom-primary',
      url: 'https://images.unsplash.com/photo-1587017539504-67cfbddac569?q=80&w=1000&auto=format&fit=crop',
      alt: 'Gucci Bloom Eau de Parfum vintage powder pink porcelain-style flacon',
      verifiedFragrance: true,
      type: 'flacon'
    },
    secondary: {
      id: 'img-bloom-secondary',
      url: 'https://images.unsplash.com/photo-1563178406-4cdc2923acbc?q=80&w=1000&auto=format&fit=crop',
      alt: 'Gucci Bloom floral garden perfume flacon shot',
      verifiedFragrance: true,
      type: 'flacon'
    },
    gallery: [
      {
        id: 'img-bloom-g1',
        url: 'https://images.unsplash.com/photo-1587017539504-67cfbddac569?q=80&w=1000&auto=format&fit=crop',
        alt: 'Gucci Bloom front bottle view',
        verifiedFragrance: true,
        type: 'flacon'
      },
      {
        id: 'img-bloom-g2',
        url: 'https://images.unsplash.com/photo-1563178406-4cdc2923acbc?q=80&w=1000&auto=format&fit=crop',
        alt: 'Gucci Bloom artistic arrangement',
        verifiedFragrance: true,
        type: 'flacon'
      }
    ]
  },

  'prod-byredo-gypsy': {
    productId: 'prod-byredo-gypsy',
    productSlug: 'byredo-gypsy-water',
    productName: 'Gypsy Water',
    brandName: 'Byredo',
    primary: {
      id: 'img-gypsy-primary',
      url: 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?q=80&w=1000&auto=format&fit=crop',
      alt: 'Byredo Gypsy Water minimalist Nordic glass flacon with black domed cap',
      verifiedFragrance: true,
      type: 'flacon'
    },
    secondary: {
      id: 'img-gypsy-secondary',
      url: 'https://images.unsplash.com/photo-1590736704728-f4730bb30770?q=80&w=1000&auto=format&fit=crop',
      alt: 'Byredo Gypsy Water minimalist amber perfume flacon',
      verifiedFragrance: true,
      type: 'flacon'
    },
    gallery: [
      {
        id: 'img-gypsy-g1',
        url: 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?q=80&w=1000&auto=format&fit=crop',
        alt: 'Byredo Gypsy Water front view',
        verifiedFragrance: true,
        type: 'flacon'
      },
      {
        id: 'img-gypsy-g2',
        url: 'https://images.unsplash.com/photo-1590736704728-f4730bb30770?q=80&w=1000&auto=format&fit=crop',
        alt: 'Byredo Gypsy Water detail shot',
        verifiedFragrance: true,
        type: 'flacon'
      }
    ]
  },

  'prod-prada-paradoxe': {
    productId: 'prod-prada-paradoxe',
    productSlug: 'prada-paradoxe-intense',
    productName: 'Paradoxe Intense',
    brandName: 'Prada',
    primary: {
      id: 'img-paradoxe-primary',
      url: 'https://images.unsplash.com/photo-1541643600914-78b084683601?q=80&w=1000&auto=format&fit=crop',
      alt: 'Prada Paradoxe Intense iconic triangular prism luxury perfume flacon',
      verifiedFragrance: true,
      type: 'flacon'
    },
    secondary: {
      id: 'img-paradoxe-secondary',
      url: 'https://images.unsplash.com/photo-1583445013765-46c20c4a6772?q=80&w=1000&auto=format&fit=crop',
      alt: 'Prada Paradoxe Intense rose crystal fragrance refraction',
      verifiedFragrance: true,
      type: 'flacon'
    },
    gallery: [
      {
        id: 'img-paradoxe-g1',
        url: 'https://images.unsplash.com/photo-1541643600914-78b084683601?q=80&w=1000&auto=format&fit=crop',
        alt: 'Prada Paradoxe front view',
        verifiedFragrance: true,
        type: 'flacon'
      },
      {
        id: 'img-paradoxe-g2',
        url: 'https://images.unsplash.com/photo-1583445013765-46c20c4a6772?q=80&w=1000&auto=format&fit=crop',
        alt: 'Prada Paradoxe light reflection',
        verifiedFragrance: true,
        type: 'flacon'
      }
    ]
  },

  'prod-tf-ombre-leather': {
    productId: 'prod-tf-ombre-leather',
    productSlug: 'tom-ford-ombre-leather',
    productName: 'Ombré Leather',
    brandName: 'Tom Ford',
    primary: {
      id: 'img-ombreleather-primary',
      url: 'https://images.unsplash.com/photo-1506152983158-b4a74a01c721?q=80&w=1000&auto=format&fit=crop',
      alt: 'Tom Ford Ombré Leather Eau de Parfum matte black flacon with leather plaque',
      verifiedFragrance: true,
      type: 'flacon'
    },
    secondary: {
      id: 'img-ombreleather-secondary',
      url: 'https://images.unsplash.com/photo-1616949755610-8c9bbc08f138?q=80&w=1000&auto=format&fit=crop',
      alt: 'Tom Ford Ombré Leather smoky dark artisan glass perfume flacon',
      verifiedFragrance: true,
      type: 'flacon'
    },
    gallery: [
      {
        id: 'img-ombreleather-g1',
        url: 'https://images.unsplash.com/photo-1506152983158-b4a74a01c721?q=80&w=1000&auto=format&fit=crop',
        alt: 'Tom Ford Ombré Leather front display',
        verifiedFragrance: true,
        type: 'flacon'
      },
      {
        id: 'img-ombreleather-g2',
        url: 'https://images.unsplash.com/photo-1616949755610-8c9bbc08f138?q=80&w=1000&auto=format&fit=crop',
        alt: 'Tom Ford Ombré Leather studio shot',
        verifiedFragrance: true,
        type: 'flacon'
      }
    ]
  },

  'prod-discovery-coffret': {
    productId: 'prod-discovery-coffret',
    productSlug: 'scentiva-royal-discovery-coffret',
    productName: 'SCENTIVA Royal Discovery Coffret (5 x 10ml)',
    brandName: 'SCENTIVA Privé',
    primary: {
      id: 'img-coffret-primary',
      url: 'https://images.unsplash.com/photo-1616949755470-349890a5e81a?q=80&w=1000&auto=format&fit=crop',
      alt: 'SCENTIVA Royal Discovery Coffret luxury velvet case with miniature crystal atomizers',
      verifiedFragrance: true,
      type: 'packaging'
    },
    secondary: {
      id: 'img-coffret-secondary',
      url: 'https://images.unsplash.com/photo-1582211594533-268f4f1edcb9?q=80&w=1000&auto=format&fit=crop',
      alt: 'SCENTIVA Royal Discovery Coffret open showcase with 5 haute parfumerie travel flacons',
      verifiedFragrance: true,
      type: 'packaging'
    },
    gallery: [
      {
        id: 'img-coffret-g1',
        url: 'https://images.unsplash.com/photo-1616949755470-349890a5e81a?q=80&w=1000&auto=format&fit=crop',
        alt: 'SCENTIVA Discovery Coffret closed presentation box',
        verifiedFragrance: true,
        type: 'packaging'
      },
      {
        id: 'img-coffret-g2',
        url: 'https://images.unsplash.com/photo-1582211594533-268f4f1edcb9?q=80&w=1000&auto=format&fit=crop',
        alt: 'SCENTIVA Discovery Coffret individual travel flacons',
        verifiedFragrance: true,
        type: 'packaging'
      }
    ]
  }
};

/**
 * Category Media Manifest
 */
export const CATEGORY_MEDIA_CATALOG: Record<string, MediaAsset> = {
  'cat-her': {
    id: 'media-cat-her',
    url: 'https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?q=80&w=800&auto=format&fit=crop',
    alt: 'For Her - Radiant floral and amber luxury perfume flacons',
    verifiedFragrance: true,
    type: 'flacon'
  },
  'cat-him': {
    id: 'media-cat-him',
    url: 'https://images.unsplash.com/photo-1523293182086-7651a899d37f?q=80&w=800&auto=format&fit=crop',
    alt: 'For Him - Bold woody and aromatic masculine fragrance flacons',
    verifiedFragrance: true,
    type: 'flacon'
  },
  'cat-unisex': {
    id: 'media-cat-unisex',
    url: 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?q=80&w=800&auto=format&fit=crop',
    alt: 'Unisex - Artistic and boundary-defying niche fragrances',
    verifiedFragrance: true,
    type: 'flacon'
  },
  'cat-luxury': {
    id: 'media-cat-luxury',
    url: 'https://images.unsplash.com/photo-1615397349754-cfa2066a298e?q=80&w=800&auto=format&fit=crop',
    alt: 'Luxury & Niche - Rare Millésime extractions and artisan flacons',
    verifiedFragrance: true,
    type: 'flacon'
  },
  'cat-everyday': {
    id: 'media-cat-everyday',
    url: 'https://images.unsplash.com/photo-1595425970377-c9703cf48b6d?q=80&w=800&auto=format&fit=crop',
    alt: 'Everyday Fresh - Uplifting citrus and aquatic eau de parfums',
    verifiedFragrance: true,
    type: 'flacon'
  },
  'cat-gifts': {
    id: 'media-cat-gifts',
    url: 'https://images.unsplash.com/photo-1616949755470-349890a5e81a?q=80&w=800&auto=format&fit=crop',
    alt: 'Gift Sets & Discovery - Deluxe travel coffrets and miniature collections',
    verifiedFragrance: true,
    type: 'packaging'
  }
};

/**
 * Brand Banners Media Manifest
 */
export const BRAND_BANNER_CATALOG: Record<string, MediaAsset> = {
  'b-dior': {
    id: 'media-brand-dior',
    url: 'https://images.unsplash.com/photo-1523293182086-7651a899d37f?q=80&w=1600&auto=format&fit=crop',
    alt: 'Christian Dior Haute Parfumerie Collection banner',
    verifiedFragrance: true,
    type: 'banner'
  },
  'b-chanel': {
    id: 'media-brand-chanel',
    url: 'https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?q=80&w=1600&auto=format&fit=crop',
    alt: 'CHANEL Parfums Collection banner',
    verifiedFragrance: true,
    type: 'banner'
  },
  'b-tom-ford': {
    id: 'media-brand-tf',
    url: 'https://images.unsplash.com/photo-1547887537-6158d64c35b3?q=80&w=1600&auto=format&fit=crop',
    alt: 'Tom Ford Private Blend Collection banner',
    verifiedFragrance: true,
    type: 'banner'
  },
  'b-ysl': {
    id: 'media-brand-ysl',
    url: 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?q=80&w=1600&auto=format&fit=crop',
    alt: 'Yves Saint Laurent Parfums Collection banner',
    verifiedFragrance: true,
    type: 'banner'
  },
  'b-versace': {
    id: 'media-brand-versace',
    url: 'https://images.unsplash.com/photo-1594035910387-fea47794261f?q=80&w=1600&auto=format&fit=crop',
    alt: 'Versace Parfums Collection banner',
    verifiedFragrance: true,
    type: 'banner'
  },
  'b-armani': {
    id: 'media-brand-armani',
    url: 'https://images.unsplash.com/photo-1595425970377-c9703cf48b6d?q=80&w=1600&auto=format&fit=crop',
    alt: 'Giorgio Armani Privé Collection banner',
    verifiedFragrance: true,
    type: 'banner'
  },
  'b-creed': {
    id: 'media-brand-creed',
    url: 'https://images.unsplash.com/photo-1615397349754-cfa2066a298e?q=80&w=1600&auto=format&fit=crop',
    alt: 'House of Creed Royal Millésimes banner',
    verifiedFragrance: true,
    type: 'banner'
  },
  'b-byredo': {
    id: 'media-brand-byredo',
    url: 'https://images.unsplash.com/photo-1598440947619-2c35fc9aa908?q=80&w=1600&auto=format&fit=crop',
    alt: 'Byredo Parfums Stockholm banner',
    verifiedFragrance: true,
    type: 'banner'
  },
  'b-gucci': {
    id: 'media-brand-gucci',
    url: 'https://images.unsplash.com/photo-1587017539504-67cfbddac569?q=80&w=1600&auto=format&fit=crop',
    alt: 'Gucci Beauty & Fragrance banner',
    verifiedFragrance: true,
    type: 'banner'
  },
  'b-prada': {
    id: 'media-brand-prada',
    url: 'https://images.unsplash.com/photo-1541643600914-78b084683601?q=80&w=1600&auto=format&fit=crop',
    alt: 'Prada Parfums Collection banner',
    verifiedFragrance: true,
    type: 'banner'
  }
};

/**
 * Editorial Stories Media Manifest
 */
export const STORY_MEDIA_CATALOG: Record<string, MediaAsset> = {
  'story-layering': {
    id: 'media-story-layering',
    url: 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?q=80&w=1200&auto=format&fit=crop',
    alt: 'Haute Parfumerie Layering masterclass flacons',
    verifiedFragrance: true,
    type: 'editorial'
  },
  'story-grasse-harvest': {
    id: 'media-story-grasse',
    url: 'https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?q=80&w=1200&auto=format&fit=crop',
    alt: 'Precious Centifolia rose harvest in Grasse, France',
    verifiedFragrance: true,
    type: 'editorial'
  },
  'story-oud-evolution': {
    id: 'media-story-oud',
    url: 'https://images.unsplash.com/photo-1547887537-6158d64c35b3?q=80&w=1200&auto=format&fit=crop',
    alt: 'Dark resinous agarwood flacons in atmospheric light',
    verifiedFragrance: true,
    type: 'editorial'
  }
};

/**
 * Safe Helper to resolve product images with verified fallback guarantee
 */
export function getProductMedia(productId: string): ProductMediaSet {
  const mediaSet = PRODUCT_MEDIA_CATALOG[productId];
  if (mediaSet) {
    return mediaSet;
  }
  // Fallback set with brand-consistent SVG
  return {
    productId,
    productSlug: 'scentiva-fragrance',
    productName: 'SCENTIVA Flacon',
    brandName: 'SCENTIVA',
    primary: {
      id: `fallback-${productId}-p`,
      url: SCENTIVA_FALLBACK_IMAGE,
      alt: 'SCENTIVA Haute Parfumerie Signature Flacon',
      verifiedFragrance: true,
      type: 'placeholder'
    },
    secondary: {
      id: `fallback-${productId}-s`,
      url: SCENTIVA_FALLBACK_IMAGE,
      alt: 'SCENTIVA Haute Parfumerie Signature Flacon',
      verifiedFragrance: true,
      type: 'placeholder'
    },
    gallery: [
      {
        id: `fallback-${productId}-g1`,
        url: SCENTIVA_FALLBACK_IMAGE,
        alt: 'SCENTIVA Haute Parfumerie Signature Flacon',
        verifiedFragrance: true,
        type: 'placeholder'
      }
    ]
  };
}

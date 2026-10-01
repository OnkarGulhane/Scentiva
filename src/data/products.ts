import { Product } from '../types';
import { PRODUCT_MEDIA_CATALOG } from './mediaCatalog';

export const PRODUCTS: Product[] = [
  {
    id: 'prod-sauvage',
    slug: 'dior-sauvage-edp',
    name: 'Sauvage',
    brandId: 'b-dior',
    brandName: 'Christian Dior',
    tagline: 'Raw, noble, and deeply magnetic.',
    category: 'For Him',
    fragranceFamilies: ['Woody', 'Fresh', 'Spicy', 'Aromatic'],
    concentration: 'Eau de Parfum (EDP)',
    variants: [
      { size: '30ml', price: 5499, mrp: 6999, sku: 'CD-SAUV-30', inStock: true },
      { size: '60ml', price: 7999, mrp: 10500, sku: 'CD-SAUV-60', inStock: true },
      { size: '100ml', price: 11499, mrp: 14999, sku: 'CD-SAUV-100', inStock: true },
      { size: '200ml', price: 17999, mrp: 21999, sku: 'CD-SAUV-200', inStock: true }
    ],
    notes: {
      top: ['Reggio Bergamot', 'Calabrian Pepper', 'Mandarin'],
      heart: ['Lavender', 'Sichuan Pepper', 'Geranium', 'Pink Pepper'],
      base: ['Ambroxan', 'Cedarwood', 'Papua New Guinean Vanilla', 'Labdanum']
    },
    sillage: 'Strong',
    longevity: '8-12 Hours',
    season: ['All Season', 'Autumn', 'Spring'],
    occasion: ['Everyday', 'Work / Office', 'Date Night', 'Party'],
    description: 'A boldly fresh composition, dictated by a name that has the ring of a manifesto. Radiant top notes burst with the juicy freshness of Reggio di Calabria Bergamot. Ambroxan unleashes a powerfully woody trail.',
    story: 'Created by François Demachy, Dior Perfumer-Creator, drawing inspiration from wide-open spaces under a blue sky that dominates a white-hot desert landscape.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-sauvage'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-sauvage'].secondary.url
    ],
    stock: 45,
    rating: 4.8,
    reviewCount: 2345,
    isBestSeller: true,
    isNewArrival: false,
    isFeatured: true,
    discountPercentage: 24
  },
  {
    id: 'prod-coco-mademoiselle',
    slug: 'chanel-coco-mademoiselle',
    name: 'Coco Mademoiselle',
    brandId: 'b-chanel',
    brandName: 'CHANEL',
    tagline: 'The essence of a bold and free woman.',
    category: 'For Her',
    fragranceFamilies: ['Floral', 'Oriental', 'Fresh', 'Citrus'],
    concentration: 'Eau de Parfum (EDP)',
    variants: [
      { size: '35ml', price: 7999, mrp: 9999, sku: 'CH-COCO-35', inStock: true },
      { size: '50ml', price: 11499, mrp: 14500, sku: 'CH-COCO-50', inStock: true },
      { size: '100ml', price: 16999, mrp: 19999, sku: 'CH-COCO-100', inStock: true }
    ],
    notes: {
      top: ['Sicilian Orange', 'Calabrian Bergamot', 'Grapefruit'],
      heart: ['May Rose', 'Italian Jasmine', 'Lychee'],
      base: ['Indonesian Patchouli', 'Haitian Vetiver', 'Bourbon Vanilla', 'White Musk']
    },
    sillage: 'Moderate',
    longevity: '8-12 Hours',
    season: ['Spring', 'Summer', 'Autumn'],
    occasion: ['Work / Office', 'Date Night', 'Special Occasion', 'Evening Gala'],
    description: 'An amber fragrance with a strong personality, yet surprisingly fresh. Sparks of vibrant orange immediately awaken the senses, followed by a clear and sensual heart of Jasmine and Rose.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-coco-mademoiselle'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-coco-mademoiselle'].secondary.url
    ],
    stock: 32,
    rating: 4.9,
    reviewCount: 1890,
    isBestSeller: true,
    isNewArrival: false,
    isFeatured: true,
    discountPercentage: 20
  },
  {
    id: 'prod-ysl-libre',
    slug: 'ysl-libre-edp',
    name: 'Libre Eau de Parfum',
    brandId: 'b-ysl',
    brandName: 'Yves Saint Laurent',
    tagline: 'The fragrance of freedom.',
    category: 'For Her',
    fragranceFamilies: ['Floral', 'Aromatic', 'Sweet & Gourmand'],
    concentration: 'Eau de Parfum (EDP)',
    variants: [
      { size: '30ml', price: 6899, mrp: 8500, sku: 'YSL-LIB-30', inStock: true },
      { size: '50ml', price: 10999, mrp: 14000, sku: 'YSL-LIB-50', inStock: true },
      { size: '90ml', price: 14999, mrp: 18500, sku: 'YSL-LIB-90', inStock: true }
    ],
    notes: {
      top: ['Lavender Essence', 'Mandarin Oil', 'Blackcurrant', 'Petitgrain'],
      heart: ['Moroccan Orange Blossom', 'Jasmine Sambac', 'French Lavender'],
      base: ['Madagascar Vanilla', 'Cedarwood', 'Ambergris', 'Musk']
    },
    sillage: 'Strong',
    longevity: '8-12 Hours',
    season: ['Autumn', 'Winter', 'Spring'],
    occasion: ['Date Night', 'Party', 'Evening Gala', 'Work / Office'],
    description: 'A grand floral Eau de Parfum with an unequivocal YSL twist. The burning sensuality of noble Moroccan orange blossom harmonizes with the aromatic boldness of Diva lavender from France.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-ysl-libre'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-ysl-libre'].secondary.url
    ],
    stock: 28,
    rating: 4.8,
    reviewCount: 1450,
    isBestSeller: true,
    isNewArrival: false,
    isFeatured: true,
    discountPercentage: 22
  },
  {
    id: 'prod-versace-eros',
    slug: 'versace-eros-flame',
    name: 'Eros Eau de Parfum',
    brandId: 'b-versace',
    brandName: 'Versace',
    tagline: 'Passion, desire, and sublime masculine strength.',
    category: 'For Him',
    fragranceFamilies: ['Woody', 'Oriental', 'Fresh', 'Sweet & Gourmand'],
    concentration: 'Eau de Parfum (EDP)',
    variants: [
      { size: '50ml', price: 6499, mrp: 8500, sku: 'VER-EROS-50', inStock: true },
      { size: '100ml', price: 8999, mrp: 11999, sku: 'VER-EROS-100', inStock: true },
      { size: '200ml', price: 13999, mrp: 17500, sku: 'VER-EROS-200', inStock: true }
    ],
    notes: {
      top: ['Mint Leaves', 'Italian Lemon Zest', 'Crisp Green Apple'],
      heart: ['Tonka Bean', 'Ambroxan', 'Geranium Flower', 'Clary Sage'],
      base: ['Madagascar Vanilla', 'Virginian Cedarwood', 'Atlas Cedar', 'Oakmoss']
    },
    sillage: 'Enormous',
    longevity: '12+ Hours',
    season: ['Winter', 'Autumn', 'Night'],
    occasion: ['Party', 'Date Night', 'Special Occasion'],
    description: 'Love, passion, beauty and desire are key elements behind this luminous fragrance. A luminous aura with an intense, vibrant and glowing freshness obtained from the combination of mint leaves and Italian lemon zest.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-versace-eros'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-versace-eros'].secondary.url
    ],
    stock: 50,
    rating: 4.7,
    reviewCount: 3120,
    isBestSeller: true,
    isNewArrival: false,
    isFeatured: false,
    discountPercentage: 25
  },
  {
    id: 'prod-tf-black-orchid',
    slug: 'tom-ford-black-orchid',
    name: 'Black Orchid',
    brandId: 'b-tom-ford',
    brandName: 'Tom Ford',
    tagline: 'A luxurious, dark, and sensual spell.',
    category: 'Unisex',
    fragranceFamilies: ['Oriental', 'Woody', 'Spicy', 'Floral'],
    concentration: 'Parfum',
    variants: [
      { size: '50ml', price: 14999, mrp: 18500, sku: 'TF-BO-50', inStock: true },
      { size: '100ml', price: 21999, mrp: 26000, sku: 'TF-BO-100', inStock: true }
    ],
    notes: {
      top: ['Black Truffle', 'Ylang-Ylang', 'Bergamot', 'Blackcurrant'],
      heart: ['Black Orchid', 'Rich Spices', 'Lotus Wood', 'Floral Accords'],
      base: ['Patchouli', 'Incense', 'Vetiver', 'Mexican Chocolate', 'Sandalwood']
    },
    sillage: 'Enormous',
    longevity: '12+ Hours',
    season: ['Winter', 'Autumn'],
    occasion: ['Evening Gala', 'Date Night', 'Special Occasion'],
    description: 'A luxurious and sensual fragrance of rich, dark accords and an alluring potion of black orchids and spice, Tom Ford Black Orchid is both modern and timeless. Bottled in fluted, black glass.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-tf-black-orchid'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-tf-black-orchid'].secondary.url
    ],
    stock: 22,
    rating: 4.9,
    reviewCount: 980,
    isBestSeller: false,
    isNewArrival: false,
    isFeatured: true,
    discountPercentage: 19
  },
  {
    id: 'prod-creed-aventus',
    slug: 'creed-aventus-millesime',
    name: 'Aventus Millésime',
    brandId: 'b-creed',
    brandName: 'House of Creed',
    tagline: 'The iconic fragrance of strength, power, and success.',
    category: 'Luxury & Niche',
    fragranceFamilies: ['Woody', 'Fresh', 'Citrus', 'Spicy'],
    concentration: 'Extrait de Parfum',
    variants: [
      { size: '50ml', price: 24999, mrp: 29000, sku: 'CR-AV-50', inStock: true },
      { size: '100ml', price: 34999, mrp: 39999, sku: 'CR-AV-100', inStock: true }
    ],
    notes: {
      top: ['Blackcurrant', 'Italian Bergamot', 'French Apple', 'Royal Pineapple'],
      heart: ['Birch Wood', 'Moroccan Jasmine', 'Patchouli', 'Rose'],
      base: ['Musk', 'Oakmoss', 'Ambergris', 'Vanilla']
    },
    sillage: 'Strong',
    longevity: '12+ Hours',
    season: ['All Season', 'Spring', 'Summer'],
    occasion: ['Special Occasion', 'Evening Gala', 'Work / Office'],
    description: 'Celebrating strength, power, vision and success, inspired by the dramatic life of war, peace and romance lived by Emperor Napoleon. Handcrafted using rare ingredients sourced globally.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-creed-aventus'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-creed-aventus'].secondary.url
    ],
    stock: 14,
    rating: 4.9,
    reviewCount: 1620,
    isBestSeller: true,
    isNewArrival: false,
    isFeatured: true,
    discountPercentage: 15
  },
  {
    id: 'prod-armani-adg',
    slug: 'armani-acqua-di-gio-parfum',
    name: 'Acqua Di Giò Parfum',
    brandId: 'b-armani',
    brandName: 'Giorgio Armani',
    tagline: 'Deep, aquatic sensations meeting volcanic minerals.',
    category: 'For Him',
    fragranceFamilies: ['Aquatic', 'Fresh', 'Woody', 'Aromatic'],
    concentration: 'Parfum',
    variants: [
      { size: '40ml', price: 5999, mrp: 7500, sku: 'GA-ADG-40', inStock: true },
      { size: '75ml', price: 9499, mrp: 12000, sku: 'GA-ADG-75', inStock: true },
      { size: '125ml', price: 13999, mrp: 16500, sku: 'GA-ADG-125', inStock: true }
    ],
    notes: {
      top: ['Marine Notes', 'Calabrian Bergamot', 'Ginger'],
      heart: ['Rosemary', 'Geranium Bourbon', 'Incense'],
      base: ['Guatemalan Patchouli', 'Smoky Incense', 'Cedarwood']
    },
    sillage: 'Moderate',
    longevity: '8-12 Hours',
    season: ['Summer', 'Spring', 'All Season'],
    occasion: ['Everyday', 'Work / Office', 'Casual Lunch'],
    description: 'A deeply charismatic fragrance where aquatic freshness fuses with the intense smoky warmth of incense and mineral woods.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-armani-adg'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-armani-adg'].secondary.url
    ],
    stock: 38,
    rating: 4.8,
    reviewCount: 2150,
    isBestSeller: false,
    isNewArrival: true,
    isFeatured: true,
    discountPercentage: 21
  },
  {
    id: 'prod-gucci-bloom',
    slug: 'gucci-bloom-edp',
    name: 'Gucci Bloom',
    brandId: 'b-gucci',
    brandName: 'Gucci',
    tagline: 'An abundant floral garden captured in powdery elegance.',
    category: 'For Her',
    fragranceFamilies: ['Floral', 'Fresh', 'Sweet & Gourmand'],
    concentration: 'Eau de Parfum (EDP)',
    variants: [
      { size: '30ml', price: 6299, mrp: 7999, sku: 'GUC-BL-30', inStock: true },
      { size: '50ml', price: 9999, mrp: 12500, sku: 'GUC-BL-50', inStock: true },
      { size: '100ml', price: 14499, mrp: 17999, sku: 'GUC-BL-100', inStock: true }
    ],
    notes: {
      top: ['Rangoon Creeper', 'Neroli', 'Green Accord'],
      heart: ['Tuberose', 'Jasmine Bud Extract', 'Orange Blossom'],
      base: ['Sandalwood', 'White Musk', 'Vanilla Accord']
    },
    sillage: 'Moderate',
    longevity: '6-8 Hours',
    season: ['Spring', 'Summer'],
    occasion: ['Everyday', 'Work / Office', 'Special Occasion'],
    description: 'Envisioned as a thriving garden full of diverse types of flowers and the rich fragrance it emits; Bloom is created to unfold like its name.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-gucci-bloom'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-gucci-bloom'].secondary.url
    ],
    stock: 25,
    rating: 4.6,
    reviewCount: 840,
    isBestSeller: false,
    isNewArrival: false,
    isFeatured: false,
    discountPercentage: 20
  },
  {
    id: 'prod-byredo-gypsy',
    slug: 'byredo-gypsy-water',
    name: 'Gypsy Water',
    brandId: 'b-byredo',
    brandName: 'Byredo',
    tagline: 'An ode to the beauty of Romany culture and deep pine forests.',
    category: 'Unisex',
    fragranceFamilies: ['Woody', 'Aromatic', 'Fresh', 'Citrus'],
    concentration: 'Eau de Parfum (EDP)',
    variants: [
      { size: '50ml', price: 16999, mrp: 19999, sku: 'BYR-GW-50', inStock: true },
      { size: '100ml', price: 23999, mrp: 27500, sku: 'BYR-GW-100', inStock: true }
    ],
    notes: {
      top: ['Juniper Berries', 'Lemon', 'Bergamot', 'Pepper'],
      heart: ['Pine Needles', 'Incense', 'Orris Root'],
      base: ['Sandalwood', 'Amber', 'Vanilla']
    },
    sillage: 'Intimate',
    longevity: '6-8 Hours',
    season: ['All Season', 'Autumn', 'Spring'],
    occasion: ['Everyday', 'Work / Office', 'Casual Lunch'],
    description: 'A glamorization of the Romany lifestyle based on a fascination for the myth. The scent of fresh soil, deep forests and campfires evokes the dream of a free, colorful lifestyle close to nature.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-byredo-gypsy'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-byredo-gypsy'].secondary.url
    ],
    stock: 18,
    rating: 4.8,
    reviewCount: 650,
    isBestSeller: false,
    isNewArrival: true,
    isFeatured: true,
    discountPercentage: 15
  },
  {
    id: 'prod-prada-paradoxe',
    slug: 'prada-paradoxe-intense',
    name: 'Paradoxe Intense',
    brandId: 'b-prada',
    brandName: 'Prada',
    tagline: 'Exploring the boundless paradoxes of modern femininity.',
    category: 'For Her',
    fragranceFamilies: ['Floral', 'Amber', 'Sweet & Gourmand'],
    concentration: 'Eau de Parfum (EDP)',
    variants: [
      { size: '30ml', price: 6999, mrp: 8500, sku: 'PRA-PAR-30', inStock: true },
      { size: '50ml', price: 10499, mrp: 13000, sku: 'PRA-PAR-50', inStock: true },
      { size: '90ml', price: 15499, mrp: 18999, sku: 'PRA-PAR-90', inStock: true }
    ],
    notes: {
      top: ['Bergamot Essence', 'Neroli Bud', 'Pear Accord'],
      heart: ['Neroli Essence', 'Superinfusion Jasmine', 'Moss Accord'],
      base: ['Ambrofix', 'Infusion of Bourbon Vanilla', 'Serenolide Musk']
    },
    sillage: 'Strong',
    longevity: '8-12 Hours',
    season: ['All Season', 'Autumn', 'Winter'],
    occasion: ['Date Night', 'Party', 'Special Occasion'],
    description: 'This feminine floral fragrance reinvents classic codes of perfumery to reveal new olfactory experiences. Encapsulated in the signature triangular refillable bottle.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-prada-paradoxe'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-prada-paradoxe'].secondary.url
    ],
    stock: 26,
    rating: 4.8,
    reviewCount: 920,
    isBestSeller: false,
    isNewArrival: true,
    isFeatured: false,
    discountPercentage: 18
  },
  {
    id: 'prod-tf-ombre-leather',
    slug: 'tom-ford-ombre-leather',
    name: 'Ombré Leather',
    brandId: 'b-tom-ford',
    brandName: 'Tom Ford',
    tagline: 'Vast, untethered, and tactile desert warmth.',
    category: 'Unisex',
    fragranceFamilies: ['Woody', 'Spicy', 'Oriental'],
    concentration: 'Eau de Parfum (EDP)',
    variants: [
      { size: '50ml', price: 12999, mrp: 15500, sku: 'TF-OL-50', inStock: true },
      { size: '100ml', price: 18999, mrp: 22500, sku: 'TF-OL-100', inStock: true }
    ],
    notes: {
      top: ['Cardamom', 'Saffron'],
      heart: ['Black Leather', 'Jasmine Sambac', 'Orris'],
      base: ['Patchouli', 'Amber', 'Moss']
    },
    sillage: 'Strong',
    longevity: '12+ Hours',
    season: ['Autumn', 'Winter'],
    occasion: ['Date Night', 'Evening Gala', 'Special Occasion'],
    description: 'Vast. Untethered. Driven. Freedom comes from within; the desert heart of the west wrapped in leather. It moves forward, untethered, through the still air of wide-open space.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-tf-ombre-leather'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-tf-ombre-leather'].secondary.url
    ],
    stock: 19,
    rating: 4.9,
    reviewCount: 1420,
    isBestSeller: true,
    isNewArrival: false,
    isFeatured: true,
    discountPercentage: 16
  },
  {
    id: 'prod-discovery-coffret',
    slug: 'scentiva-royal-discovery-coffret',
    name: 'SCENTIVA Royal Discovery Coffret (5 x 10ml)',
    brandId: 'b-dior',
    brandName: 'SCENTIVA Privé',
    tagline: 'An exquisite voyage through world perfumery.',
    category: 'Gift Sets',
    fragranceFamilies: ['Floral', 'Woody', 'Fresh', 'Oriental'],
    concentration: 'Extrait de Parfum',
    variants: [
      { size: '5 x 10ml Deluxe Set', price: 8499, mrp: 12000, sku: 'SCT-DISC-50', inStock: true }
    ],
    notes: {
      top: ['Rare Italian Citrus', 'French Lavender', 'Pink Peppercorn'],
      heart: ['Grasse Rose', 'Florentine Iris', 'Indian Sandalwood'],
      base: ['Smoky Amber', 'White Oud', 'Tahitian Vanilla']
    },
    sillage: 'Strong',
    longevity: '8-12 Hours',
    season: ['All Season'],
    occasion: ['Special Occasion', 'Evening Gala', 'Gifting'],
    description: 'A master collection of five miniature flacons nestled in an embossed burgundy velvet showcase. Perfect for fragrance connoisseurs and unforgettable gifting.',
    images: [
      PRODUCT_MEDIA_CATALOG['prod-discovery-coffret'].primary.url,
      PRODUCT_MEDIA_CATALOG['prod-discovery-coffret'].secondary.url
    ],
    stock: 50,
    rating: 5.0,
    reviewCount: 420,
    isBestSeller: true,
    isNewArrival: true,
    isFeatured: true,
    discountPercentage: 29
  }
];

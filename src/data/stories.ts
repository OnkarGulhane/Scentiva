import { STORY_MEDIA_CATALOG } from './mediaCatalog';

export type FragranceStory = {
  id: string;
  slug: string;
  title: string;
  subtitle: string;
  category: string;
  readTime: string;
  author: string;
  date: string;
  heroImage: string;
  content: {
    intro: string;
    sections: {
      heading: string;
      body: string;
      image?: string;
    }[];
  };
};

export const FRAGRANCE_STORIES: FragranceStory[] = [
  {
    id: 'story-layering',
    slug: 'the-art-of-scent-layering',
    title: 'The Art of Scent Layering: Crafting an Unrepeatable Signature',
    subtitle: 'How master noses combine opposing fragrance families to evoke bespoke personal aura.',
    category: 'Masterclass',
    readTime: '4 min read',
    author: 'Clara Delacroix, Senior Fragrance Editor',
    date: 'Sep 24, 2026',
    heroImage: STORY_MEDIA_CATALOG['story-layering'].url,
    content: {
      intro: 'In an era of mass-market ubiquity, the ultimate luxury is a scent that belongs exclusively to you. Fragrance layering is not merely spraying two bottles at random—it is an intricate balance of molecular weights, evaporating rates, and harmonious note affinities.',
      sections: [
        {
          heading: '1. Establish the Anchor: Heavy Resins & Woods First',
          body: 'Always apply your heaviest, longest-lasting concentration (Extrait or EDP containing oud, amber, cedarwood, or vanilla) onto warm pulse points first. Allow 2 minutes for the drydown oils to settle with your skin pH.'
        },
        {
          heading: '2. Crown with High-Vibration Florals or Citrus',
          body: 'Once the base note anchor is set, layer a radiant EDT or Cologne possessing crisp bergamot, orange blossom, or dewy rose. As the top notes effervesce, the heavier woods will steadily emerge, creating dynamic dimension.'
        },
        {
          heading: '3. Strategic Placement Over Pure Blending',
          body: 'Instead of applying both fragrances on the exact same spot, spray the deeper fragrance on your collarbone and behind knees, and the lighter floral fragrance on your wrists and hair ends for an atmospheric scent cloud that changes as you move.'
        }
      ]
    }
  },
  {
    id: 'story-grasse-harvest',
    slug: 'the-secret-blooms-of-grasse',
    title: 'Dawn in Grasse: The Secret Harvest of Centifolia Rose',
    subtitle: 'Behind the velvet curtains of the world’s most revered perfume capital in the South of France.',
    category: 'Heritage & Origins',
    readTime: '6 min read',
    author: 'Marc Fontaine, Grasse Historian',
    date: 'Sep 18, 2026',
    heroImage: STORY_MEDIA_CATALOG['story-grasse-harvest'].url,
    content: {
      intro: 'Between 5:30 AM and 8:00 AM in May, before the Mediterranean sun reaches its apex, the fields of Grasse release a heavenly honeyed aroma that has inspired French royalty for centuries.',
      sections: [
        {
          heading: 'The Precious 100-Petaled Rose',
          body: 'Rose de Mai, or Rosa Centifolia, cannot be machine harvested. Each delicate bloom is hand-plucked in the pre-dawn mist to preserve volatile aromatic esters before the heat of the day causes essential oils to evaporate.'
        },
        {
          heading: 'Three Tons of Petals for a Single Litre',
          body: 'It requires over 3,000 kilograms of freshly picked rose petals to extract merely one single litre of absolute essence—making it one of the rarest and most treasured raw materials in haute perfumery.'
        }
      ]
    }
  },
  {
    id: 'story-oud-evolution',
    slug: 'black-gold-the-mystique-of-rare-agarwood',
    title: 'Black Gold: The Allure & Mystery of Pure Agarwood',
    subtitle: 'From ancient temple ceremonies in Southeast Asia to the forefront of modern niche perfumery.',
    category: 'Prestige Ingredients',
    readTime: '5 min read',
    author: 'Zayd Al-Mansoor, Master Parfumeur',
    date: 'Sep 10, 2026',
    heroImage: STORY_MEDIA_CATALOG['story-oud-evolution'].url,
    content: {
      intro: 'Known as "Wood of the Gods", genuine wild oud oil is worth more per gram than solid gold. Its dark, complex profile combines medicinal leather, smoky honey, and velvet moss.',
      sections: [
        {
          heading: 'A Miracle Born of Natural Defense',
          body: 'Oud is only produced when the sacred Aquilaria tree is infected with a specific mold, prompting the tree to generate a dark, intensely fragrant protective resin over decades of slow maturation.'
        }
      ]
    }
  }
];

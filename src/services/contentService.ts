import { FRAGRANCE_STORIES, FragranceStory } from '../data/stories';

const STORIES_STORAGE_KEY = 'scentiva_stories';

export interface AnnouncementItem {
  id: string;
  text: string;
  linkText: string;
  linkTo: string;
  active: boolean;
}

export const INITIAL_ANNOUNCEMENTS: AnnouncementItem[] = [
  {
    id: 'ann-1',
    text: 'Complimentary Pan-India Express Delivery on all luxury orders above ₹999',
    linkText: 'Shop Catalog',
    linkTo: '/shop',
    active: true
  },
  {
    id: 'ann-2',
    text: 'Use code WELCOME10 for 10% off your premier order above ₹2,999',
    linkText: 'Explore Offers',
    linkTo: '/offers',
    active: true
  },
  {
    id: 'ann-3',
    text: 'Artisanal Perfume Discovery Sets now available with custom complimentary coffret',
    linkText: 'Discover Sets',
    linkTo: '/gifts',
    active: true
  }
];

export const getStoredStories = (): FragranceStory[] => {
  if (typeof window === 'undefined') return FRAGRANCE_STORIES;
  try {
    const raw = localStorage.getItem(STORIES_STORAGE_KEY);
    if (!raw) return FRAGRANCE_STORIES;
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) && parsed.length > 0 ? parsed : FRAGRANCE_STORIES;
  } catch {
    return FRAGRANCE_STORIES;
  }
};

export const saveStoredStories = (stories: FragranceStory[]): void => {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(STORIES_STORAGE_KEY, JSON.stringify(stories));
  } catch (err) {
    console.error('Failed to save stories to localStorage:', err);
  }
};

export const ContentService = {
  getStories: (): FragranceStory[] => {
    return getStoredStories();
  },

  getStoryBySlug: (slug: string): FragranceStory | undefined => {
    const stories = getStoredStories();
    return stories.find(s => s.slug === slug || s.id === slug);
  },

  createStory: (storyData: Omit<FragranceStory, 'id' | 'slug'>): FragranceStory => {
    const stories = getStoredStories();
    const slug = storyData.title.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
    const story: FragranceStory = {
      ...storyData,
      id: `story-${Date.now()}`,
      slug
    };
    const updated = [story, ...stories];
    saveStoredStories(updated);
    return story;
  },

  deleteStory: (id: string): boolean => {
    const stories = getStoredStories();
    const filtered = stories.filter(s => s.id !== id);
    if (filtered.length === stories.length) return false;
    saveStoredStories(filtered);
    return true;
  },

  getAnnouncements: (): AnnouncementItem[] => {
    return INITIAL_ANNOUNCEMENTS;
  }
};

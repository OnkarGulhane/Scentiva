import React, { Suspense } from 'react';
import { Metadata } from 'next';
import { StoryDetailPage } from '@/views/StoryDetailPage';
import { FRAGRANCE_STORIES } from '@/data/stories';

interface Props {
  params: {
    slug: string;
  };
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const story = FRAGRANCE_STORIES.find(s => s.slug === params.slug || s.id === params.slug);

  if (!story) {
    return {
      title: 'Story Not Found | SCENTIVA Journal',
      description: 'The requested article could not be found.',
    };
  }

  return {
    title: `${story.title} | SCENTIVA Journal`,
    description: story.subtitle,
    openGraph: {
      title: story.title,
      description: story.subtitle,
      images: story.heroImage ? [{ url: story.heroImage, width: 1200, height: 630, alt: story.title }] : [],
    },
  };
}

export default function StoryDetailRoute() {
  return <StoryDetailPage />;
}

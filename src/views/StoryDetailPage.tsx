'use client';

import React from 'react';
import { useParams } from '@/hooks/useNavigation';
import { Link } from '@/components/common/Link';
import { FRAGRANCE_STORIES } from '../data/stories';
import { ArrowLeft, Clock, Calendar, User, Share2, Sparkles } from 'lucide-react';

export const StoryDetailPage: React.FC = () => {
  const { slug } = useParams<{ slug: string }>();
  const story = FRAGRANCE_STORIES.find(s => s.slug === slug || s.id === slug);

  if (!story) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center p-6 text-center">
        <h2 className="font-serif text-2xl font-bold text-neutral-900 mb-2">Article Not Found</h2>
        <Link to="/stories" className="px-6 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold">
          Return to Journal
        </Link>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-neutral-50 pb-20">
      {/* Hero Image & Headline */}
      <div className="relative bg-neutral-950 text-white py-16 lg:py-24 overflow-hidden">
        <img
          src={story.heroImage}
          alt={story.title}
          className="absolute inset-0 w-full h-full object-cover opacity-35"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-brand-plum-950 via-brand-plum-950/70 to-transparent" />

        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10 space-y-4">
          <Link
            to="/stories"
            className="inline-flex items-center gap-1.5 text-xs font-medium text-brand-blush-200 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>All Journal Stories</span>
          </Link>

          <span className="text-xs uppercase font-bold tracking-widest text-brand-gold-500 block">
            {story.category}
          </span>
          <h1 className="font-serif text-3xl sm:text-5xl lg:text-6xl font-bold text-white leading-tight">
            {story.title}
          </h1>

          <div className="flex flex-wrap items-center gap-4 text-xs text-brand-blush-200 pt-2">
            <span className="flex items-center gap-1.5">
              <User className="w-3.5 h-3.5" />
              {story.author}
            </span>
            <span>•</span>
            <span className="flex items-center gap-1.5">
              <Calendar className="w-3.5 h-3.5" />
              {story.date}
            </span>
            <span>•</span>
            <span className="flex items-center gap-1.5">
              <Clock className="w-3.5 h-3.5" />
              {story.readTime}
            </span>
          </div>
        </div>
      </div>

      {/* Article Body */}
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 pt-12 space-y-8">
        <div className="p-6 sm:p-8 rounded-3xl bg-white border border-neutral-200 shadow-xs space-y-6">
          <p className="font-serif text-lg sm:text-xl text-brand-plum-950 leading-relaxed font-medium italic border-l-4 border-brand-rose-500 pl-4">
            {story.content.intro}
          </p>

          <div className="space-y-6 text-sm text-neutral-700 leading-relaxed pt-4 border-t border-neutral-100">
            {story.content.sections.map((section, idx) => (
              <div key={idx} className="space-y-2">
                <h3 className="font-serif text-xl sm:text-2xl font-bold text-neutral-900">
                  {section.heading}
                </h3>
                <p>{section.body}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Back and Explore Footer */}
        <div className="text-center pt-6">
          <Link
            to="/shop"
            className="inline-flex items-center gap-2 px-8 py-3.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold hover:bg-brand-plum-800 shadow-md"
          >
            <span>Explore Fragrances Mentioned</span>
          </Link>
        </div>
      </div>
    </div>
  );
};

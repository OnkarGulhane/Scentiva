'use client';

import React from 'react';
import { Link } from '@/components/common/Link';
import { FRAGRANCE_STORIES } from '../data/stories';
import { BookOpen, Clock, ArrowRight, Sparkles } from 'lucide-react';

export const StoriesPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-10">
        {/* Header */}
        <div className="text-center max-w-2xl mx-auto space-y-3">
          <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500 flex items-center justify-center gap-1.5">
            <BookOpen className="w-3.5 h-3.5" />
            Editorial Journal
          </span>
          <h1 className="font-serif text-3xl sm:text-5xl font-bold text-neutral-900">
            The SCENTIVA Fragrance Journal
          </h1>
          <p className="text-xs sm:text-sm text-neutral-600">
            Immerse yourself in deep-dive masterclasses on note layering, rare Grasse harvests, and the secrets of master noses.
          </p>
        </div>

        {/* Stories Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {FRAGRANCE_STORIES.map(story => (
            <Link
              key={story.id}
              to={`/stories/${story.slug}`}
              className="group bg-white rounded-3xl overflow-hidden border border-neutral-200/80 hover:border-brand-blush-300 hover:shadow-card-hover transition-all duration-300 flex flex-col"
            >
              <div className="aspect-[16/10] overflow-hidden bg-neutral-100">
                <img
                  src={story.heroImage}
                  alt={story.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-700 ease-out"
                />
              </div>

              <div className="p-6 flex-1 flex flex-col justify-between space-y-4">
                <div className="space-y-2">
                  <div className="flex items-center justify-between text-[11px] text-neutral-400">
                    <span className="font-semibold text-brand-rose-500 uppercase tracking-wider">
                      {story.category}
                    </span>
                    <span className="flex items-center gap-1">
                      <Clock className="w-3 h-3" />
                      {story.readTime}
                    </span>
                  </div>

                  <h3 className="font-serif text-xl font-bold text-neutral-900 group-hover:text-brand-plum-900 transition-colors">
                    {story.title}
                  </h3>

                  <p className="text-xs text-neutral-600 line-clamp-3 leading-relaxed">
                    {story.subtitle}
                  </p>
                </div>

                <div className="pt-3 border-t border-neutral-100 flex items-center justify-between text-xs font-semibold text-brand-plum-900">
                  <span className="text-neutral-500 font-normal">{story.author}</span>
                  <span className="group-hover:translate-x-1 transition-transform flex items-center gap-1">
                    Read Article →
                  </span>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
};

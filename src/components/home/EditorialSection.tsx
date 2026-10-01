import React from 'react';
import { Link } from '../common/Link';
import { FRAGRANCE_STORIES } from '../../data/stories';
import { ArrowRight, BookOpen, Clock } from 'lucide-react';

export const EditorialSection: React.FC = () => {
  return (
    <section className="py-16 bg-neutral-50 border-b border-neutral-200/60">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-10">
        <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
          <div className="space-y-1">
            <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500 flex items-center gap-1.5">
              <BookOpen className="w-3.5 h-3.5" />
              SCENTIVA Journal
            </span>
            <h2 className="font-serif text-3xl sm:text-4xl font-bold text-neutral-900">
              Olfactory Stories & Masterclasses
            </h2>
            <p className="text-xs sm:text-sm text-neutral-500">
              Behind the flacon: raw ingredients, master noses, and ancient distillation traditions.
            </p>
          </div>

          <Link
            to="/stories"
            className="text-xs font-semibold text-brand-plum-900 hover:text-brand-rose-500 flex items-center gap-1 transition-colors"
          >
            <span>Read All Articles</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {FRAGRANCE_STORIES.map(story => (
            <Link
              key={story.id}
              to={`/stories/${story.slug}`}
              className="group bg-white rounded-2xl overflow-hidden border border-neutral-200/70 hover:border-brand-blush-300 hover:shadow-card-hover transition-all duration-300 flex flex-col"
            >
              <div className="aspect-[16/10] overflow-hidden bg-neutral-100">
                <img
                  src={story.heroImage}
                  alt={story.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-700 ease-out"
                />
              </div>

              <div className="p-5 flex-1 flex flex-col justify-between space-y-3">
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

                  <h3 className="font-serif text-lg font-bold text-neutral-900 group-hover:text-brand-plum-900 transition-colors line-clamp-2">
                    {story.title}
                  </h3>

                  <p className="text-xs text-neutral-600 line-clamp-2 leading-relaxed">
                    {story.subtitle}
                  </p>
                </div>

                <div className="pt-2 border-t border-neutral-100 flex items-center justify-between text-xs font-medium text-brand-plum-900">
                  <span className="text-[11px] text-neutral-500">{story.author.split(',')[0]}</span>
                  <span className="group-hover:translate-x-1 transition-transform flex items-center gap-1 text-xs">
                    Read Story →
                  </span>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
};

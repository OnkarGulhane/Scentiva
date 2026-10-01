'use client';

import React, { useState } from 'react';
import { ContentService } from '../../services/contentService';
import { FragranceStory } from '../../data/stories';
import { Plus, Edit2, Trash2, BookOpen, Sparkles, X, Check } from 'lucide-react';
import { useStore } from '../../context/StoreContext';

export const AdminContentPage: React.FC = () => {
  const [stories, setStories] = useState<FragranceStory[]>(() => ContentService.getStories());
  const announcements = ContentService.getAnnouncements();
  const { showToast } = useStore();

  const [modalOpen, setModalOpen] = useState(false);
  const [title, setTitle] = useState('');
  const [subtitle, setSubtitle] = useState('');
  const [category, setCategory] = useState('Masterclass');
  const [readTime, setReadTime] = useState('5 min read');
  const [author, setAuthor] = useState('Senior Fragrance Editor');
  const [heroImage, setHeroImage] = useState('https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?q=80&w=1200&auto=format&fit=crop');
  const [intro, setIntro] = useState('');

  const handleCreateStory = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !intro.trim()) {
      showToast('Please provide story title and intro', 'warning');
      return;
    }

    ContentService.createStory({
      title,
      subtitle,
      category,
      readTime,
      author,
      date: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
      heroImage,
      content: {
        intro,
        sections: [
          { heading: 'Artisanal Extraction', body: 'Exploring the delicate botanical extractions and sensory memories.' }
        ]
      }
    });

    setStories(ContentService.getStories());
    showToast('Editorial Masterclass published!', 'success');
    setModalOpen(false);
  };

  const handleDeleteStory = (id: string, stTitle: string) => {
    if (confirm(`Delete story "${stTitle}"?`)) {
      ContentService.deleteStory(id);
      setStories(ContentService.getStories());
      showToast('Story removed', 'info');
    }
  };

  return (
    <div className="space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-neutral-200">
        <div>
          <h1 className="font-serif text-3xl font-bold text-neutral-900">Editorial & Content CMS</h1>
          <p className="text-xs text-neutral-500">Manage masterclass stories, journals, and store announcements.</p>
        </div>
        <button
          onClick={() => setModalOpen(true)}
          className="px-5 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold hover:bg-brand-plum-800 transition-colors shadow-sm flex items-center gap-1.5 self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          <span>New Journal Story</span>
        </button>
      </div>

      {/* Announcements Bar Section */}
      <div className="bg-white rounded-3xl p-6 border border-neutral-200 shadow-card space-y-4">
        <h3 className="font-serif text-lg font-bold text-brand-plum-950 flex items-center gap-2">
          <Sparkles className="w-4 h-4 text-brand-gold-500" />
          Live Storefront Announcement Ticker
        </h3>

        <div className="space-y-2">
          {announcements.map(ann => (
            <div key={ann.id} className="flex items-center justify-between p-3.5 rounded-2xl bg-neutral-50 border border-neutral-200 text-xs">
              <div className="flex items-center gap-3">
                <span className="w-2 h-2 rounded-full bg-semantic-success" />
                <span className="text-neutral-800 font-medium">{ann.text}</span>
              </div>
              <div className="flex items-center gap-2 text-neutral-500">
                <span className="font-mono bg-white px-2 py-0.5 rounded border">{ann.linkText} → {ann.linkTo}</span>
                <span className="text-[10px] text-semantic-success font-bold uppercase">Active</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Editorial Masterclasses Grid */}
      <div className="space-y-4">
        <h3 className="font-serif text-xl font-bold text-neutral-900">Published Editorial Stories ({stories.length})</h3>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {stories.map(st => (
            <div key={st.id} className="bg-white rounded-3xl overflow-hidden border border-neutral-200 shadow-card flex flex-col justify-between">
              <div className="relative h-44 bg-neutral-900">
                <img src={st.heroImage} alt={st.title} className="w-full h-full object-cover opacity-80" />
                <div className="absolute top-3 right-3">
                  <button
                    onClick={() => handleDeleteStory(st.id, st.title)}
                    className="p-1.5 bg-white/90 backdrop-blur-xs text-neutral-700 hover:text-semantic-error rounded-xl shadow-sm"
                    title="Delete Story"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
                <div className="absolute bottom-3 left-4 text-white">
                  <span className="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded-full bg-brand-plum-900 border border-brand-blush-300/40">
                    {st.category}
                  </span>
                </div>
              </div>

              <div className="p-5 space-y-2 flex-1 flex flex-col justify-between text-xs">
                <div>
                  <h4 className="font-serif text-lg font-bold text-brand-plum-950 leading-snug">{st.title}</h4>
                  <p className="text-neutral-500 text-[11px] mt-1 line-clamp-2">{st.subtitle}</p>
                </div>

                <div className="pt-3 border-t border-neutral-100 flex items-center justify-between text-neutral-400 text-[11px]">
                  <span>{st.author}</span>
                  <span>{st.readTime}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* New Story Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-neutral-950/70 backdrop-blur-sm">
          <div className="bg-white rounded-3xl p-6 sm:p-8 max-w-lg w-full shadow-modal border border-neutral-200 space-y-4">
            <div className="flex items-center justify-between pb-2 border-b border-neutral-100">
              <h3 className="font-serif text-xl font-bold text-neutral-900">Publish New Editorial Story</h3>
              <button onClick={() => setModalOpen(false)} className="p-1 text-neutral-500 hover:bg-neutral-100 rounded-full">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleCreateStory} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Story Title</label>
                <input
                  type="text"
                  required
                  value={title}
                  onChange={e => setTitle(e.target.value)}
                  placeholder="e.g. The Alchemy of Wild Vetiver"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Subtitle / Summary</label>
                <input
                  type="text"
                  required
                  value={subtitle}
                  onChange={e => setSubtitle(e.target.value)}
                  placeholder="Short engaging excerpt..."
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">Category</label>
                  <select
                    value={category}
                    onChange={e => setCategory(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none bg-white"
                  >
                    <option value="Masterclass">Masterclass</option>
                    <option value="Heritage & Origins">Heritage & Origins</option>
                    <option value="Prestige Ingredients">Prestige Ingredients</option>
                    <option value="Perfumery Art">Perfumery Art</option>
                  </select>
                </div>
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">Read Time</label>
                  <input
                    type="text"
                    value={readTime}
                    onChange={e => setReadTime(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                  />
                </div>
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Author Byline</label>
                <input
                  type="text"
                  value={author}
                  onChange={e => setAuthor(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Hero Image URL</label>
                <input
                  type="url"
                  required
                  value={heroImage}
                  onChange={e => setHeroImage(e.target.value)}
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Introductory Paragraph</label>
                <textarea
                  rows={3}
                  required
                  value={intro}
                  onChange={e => setIntro(e.target.value)}
                  placeholder="The story opening..."
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div className="pt-3 flex justify-end gap-2 border-t border-neutral-100">
                <button
                  type="button"
                  onClick={() => setModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-neutral-600 font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-6 py-2 rounded-xl bg-brand-plum-900 text-white font-semibold"
                >
                  Publish Story
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

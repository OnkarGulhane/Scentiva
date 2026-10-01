'use client';

import React, { useState } from 'react';
import { CategoryService } from '../../services/categoryService';
import { CategoryItem } from '../../data/categories';
import { Plus, Edit2, Trash2, X, Sparkles } from 'lucide-react';
import { useStore } from '../../context/StoreContext';

export const AdminCategoriesPage: React.FC = () => {
  const [categories, setCategories] = useState<CategoryItem[]>(() => CategoryService.getAll());
  const { showToast } = useStore();

  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);

  const [title, setTitle] = useState('');
  const [tagline, setTagline] = useState('');
  const [description, setDescription] = useState('');
  const [image, setImage] = useState('');
  const [accentColor, setAccentColor] = useState('#F2D2E7');

  const handleOpenAdd = () => {
    setEditingId(null);
    setTitle('');
    setTagline('');
    setDescription('');
    setImage('https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?q=80&w=800&auto=format&fit=crop');
    setAccentColor('#F2D2E7');
    setModalOpen(true);
  };

  const handleOpenEdit = (cat: CategoryItem) => {
    setEditingId(cat.id);
    setTitle(cat.title);
    setTagline(cat.tagline);
    setDescription(cat.description);
    setImage(cat.image);
    setAccentColor(cat.accentColor);
    setModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !description.trim()) {
      showToast('Please fill all required category fields', 'warning');
      return;
    }

    if (editingId) {
      const res = CategoryService.update(editingId, {
        title,
        tagline,
        description,
        image,
        accentColor
      });
      if (res) {
        setCategories(CategoryService.getAll());
        showToast('Category updated successfully', 'success');
      }
    } else {
      CategoryService.create({
        title,
        tagline,
        description,
        image,
        accentColor,
        productCount: 1
      });
      setCategories(CategoryService.getAll());
      showToast(`Added ${title} category!`, 'success');
    }
    setModalOpen(false);
  };

  const handleDelete = (id: string, catTitle: string) => {
    if (confirm(`Delete category "${catTitle}"?`)) {
      CategoryService.delete(id);
      setCategories(CategoryService.getAll());
      showToast('Category removed', 'info');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-neutral-200">
        <div>
          <h1 className="font-serif text-3xl font-bold text-neutral-900">Category Management</h1>
          <p className="text-xs text-neutral-500">Manage storefront fragrance categories, landing banners, and accents.</p>
        </div>
        <button
          onClick={handleOpenAdd}
          className="px-5 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold hover:bg-brand-plum-800 transition-colors shadow-sm flex items-center gap-1.5 self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          <span>Add New Category</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {categories.map(cat => (
          <div
            key={cat.id}
            className="bg-white rounded-3xl overflow-hidden border border-neutral-200 shadow-card flex flex-col justify-between"
          >
            <div className="relative h-40 bg-neutral-900">
              <img
                src={cat.image}
                alt={cat.title}
                className="w-full h-full object-cover opacity-80"
              />
              <div className="absolute top-3 right-3 flex gap-1 bg-white/90 backdrop-blur-xs p-1 rounded-xl shadow-sm">
                <button
                  onClick={() => handleOpenEdit(cat)}
                  className="p-1.5 text-neutral-700 hover:text-brand-plum-900"
                  title="Edit Category"
                >
                  <Edit2 className="w-3.5 h-3.5" />
                </button>
                <button
                  onClick={() => handleDelete(cat.id, cat.title)}
                  className="p-1.5 text-neutral-700 hover:text-semantic-error"
                  title="Delete Category"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </button>
              </div>
              <div className="absolute bottom-3 left-4 text-white">
                <span className="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded-full bg-black/50 backdrop-blur-xs">
                  {cat.tagline}
                </span>
                <h3 className="font-serif text-2xl font-bold mt-1 leading-tight">{cat.title}</h3>
              </div>
            </div>

            <div className="p-5 space-y-3 flex-1 flex flex-col justify-between text-xs">
              <p className="text-neutral-600 line-clamp-2 leading-relaxed">
                {cat.description}
              </p>

              <div className="pt-3 border-t border-neutral-100 flex items-center justify-between text-neutral-500 font-medium">
                <span>Slug: <strong className="font-mono text-neutral-800">{cat.slug}</strong></span>
                <span className="text-[11px] text-brand-plum-900 font-semibold">{cat.productCount} Flacons</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Category Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-neutral-950/70 backdrop-blur-sm">
          <div className="bg-white rounded-3xl p-6 sm:p-8 max-w-md w-full shadow-modal border border-neutral-200 space-y-4">
            <div className="flex items-center justify-between pb-2 border-b border-neutral-100">
              <h3 className="font-serif text-xl font-bold text-neutral-900">
                {editingId ? 'Edit Category' : 'Add New Category'}
              </h3>
              <button onClick={() => setModalOpen(false)} className="p-1 text-neutral-500 hover:bg-neutral-100 rounded-full">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Category Title</label>
                <input
                  type="text"
                  required
                  value={title}
                  onChange={e => setTitle(e.target.value)}
                  placeholder="e.g. Rare Attars & Ouds"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Short Tagline</label>
                <input
                  type="text"
                  required
                  value={tagline}
                  onChange={e => setTagline(e.target.value)}
                  placeholder="e.g. Sacred & Smoldering"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Description</label>
                <textarea
                  rows={3}
                  required
                  value={description}
                  onChange={e => setDescription(e.target.value)}
                  placeholder="Collection overview..."
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Hero Banner Image URL</label>
                <input
                  type="url"
                  required
                  value={image}
                  onChange={e => setImage(e.target.value)}
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
                  Save Category
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

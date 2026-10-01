'use client';

import React, { useState } from 'react';
import { BrandService } from '../../services/brandService';
import { Brand } from '../../types';
import { Plus, Edit2, Trash2, Globe, Sparkles, X, ShieldCheck } from 'lucide-react';
import { useStore } from '../../context/StoreContext';

export const AdminBrandsPage: React.FC = () => {
  const [brands, setBrands] = useState<Brand[]>(() => BrandService.getAll());
  const { showToast } = useStore();

  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);

  const [name, setName] = useState('');
  const [origin, setOrigin] = useState('Paris, France');
  const [foundedYear, setFoundedYear] = useState(1975);
  const [tier, setTier] = useState<Brand['tier']>('Luxury');
  const [description, setDescription] = useState('');
  const [bannerImage, setBannerImage] = useState('https://images.unsplash.com/photo-1547887537-6158d64c35b3?q=80&w=1600&auto=format&fit=crop');

  const handleOpenAdd = () => {
    setEditingId(null);
    setName('');
    setOrigin('Paris, France');
    setFoundedYear(1980);
    setTier('Luxury');
    setDescription('');
    setBannerImage('https://images.unsplash.com/photo-1547887537-6158d64c35b3?q=80&w=1600&auto=format&fit=crop');
    setModalOpen(true);
  };

  const handleOpenEdit = (b: Brand) => {
    setEditingId(b.id);
    setName(b.name);
    setOrigin(b.origin);
    setFoundedYear(b.foundedYear);
    setTier(b.tier);
    setDescription(b.description);
    setBannerImage(b.bannerImage);
    setModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !description.trim()) {
      showToast('Please fill all required brand fields', 'warning');
      return;
    }

    if (editingId) {
      const updated = BrandService.update(editingId, {
        name,
        origin,
        foundedYear: Number(foundedYear),
        tier,
        description,
        bannerImage
      });
      if (updated) {
        setBrands(BrandService.getAll());
        showToast('Brand details updated', 'success');
      }
    } else {
      BrandService.create({
        name,
        origin,
        foundedYear: Number(foundedYear),
        tier,
        description,
        bannerImage,
        featuredProductCount: 1
      });
      setBrands(BrandService.getAll());
      showToast(`Added ${name} to brand directory!`, 'success');
    }
    setModalOpen(false);
  };

  const handleDelete = (id: string, brandName: string) => {
    if (confirm(`Remove "${brandName}" from directory?`)) {
      BrandService.delete(id);
      setBrands(BrandService.getAll());
      showToast('Brand deleted', 'info');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-neutral-200">
        <div>
          <h1 className="font-serif text-3xl font-bold text-neutral-900">Brand Directory Management</h1>
          <p className="text-xs text-neutral-500">Manage luxury houses, niche ateliers, origins, and heritage bios.</p>
        </div>
        <button
          onClick={handleOpenAdd}
          className="px-5 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold hover:bg-brand-plum-800 transition-colors shadow-sm flex items-center gap-1.5 self-start sm:self-auto"
        >
          <Plus className="w-4 h-4" />
          <span>Add New Brand</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {brands.map(brand => (
          <div
            key={brand.id}
            className="bg-white rounded-3xl overflow-hidden border border-neutral-200 shadow-card flex flex-col justify-between"
          >
            <div className="relative h-36 bg-neutral-900">
              <img
                src={brand.bannerImage}
                alt={brand.name}
                className="w-full h-full object-cover opacity-75"
              />
              <div className="absolute top-3 right-3 flex gap-1 bg-white/90 backdrop-blur-xs p-1 rounded-xl shadow-sm">
                <button
                  onClick={() => handleOpenEdit(brand)}
                  className="p-1.5 text-neutral-700 hover:text-brand-plum-900"
                  title="Edit Brand"
                >
                  <Edit2 className="w-3.5 h-3.5" />
                </button>
                <button
                  onClick={() => handleDelete(brand.id, brand.name)}
                  className="p-1.5 text-neutral-700 hover:text-semantic-error"
                  title="Delete Brand"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </button>
              </div>
              <div className="absolute bottom-3 left-4 text-white">
                <span className="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded-full bg-brand-plum-900 border border-brand-blush-300/40">
                  {brand.tier}
                </span>
                <h3 className="font-serif text-xl font-bold mt-1 leading-tight">{brand.name}</h3>
              </div>
            </div>

            <div className="p-5 space-y-3 flex-1 flex flex-col justify-between text-xs">
              <div className="space-y-2">
                <div className="flex items-center gap-1.5 text-neutral-500">
                  <Globe className="w-3.5 h-3.5 text-brand-gold-500" />
                  <span>{brand.origin} • Est. {brand.foundedYear}</span>
                </div>
                <p className="text-neutral-600 line-clamp-3 leading-relaxed">
                  {brand.description}
                </p>
              </div>

              <div className="pt-3 border-t border-neutral-100 flex items-center justify-between text-neutral-500 font-medium">
                <span>{brand.featuredProductCount} catalog flacons</span>
                <span className="text-[10px] text-brand-plum-900 font-mono font-bold uppercase">Active Partner</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Brand Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-neutral-950/70 backdrop-blur-sm">
          <div className="bg-white rounded-3xl p-6 sm:p-8 max-w-lg w-full shadow-modal border border-neutral-200 space-y-4">
            <div className="flex items-center justify-between pb-2 border-b border-neutral-100">
              <h3 className="font-serif text-xl font-bold text-neutral-900">
                {editingId ? 'Edit Brand House' : 'Add New Brand House'}
              </h3>
              <button onClick={() => setModalOpen(false)} className="p-1 text-neutral-500 hover:bg-neutral-100 rounded-full">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Brand Name</label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={e => setName(e.target.value)}
                  placeholder="e.g. Maison Francis Kurkdjian"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">Origin (City, Country)</label>
                  <input
                    type="text"
                    required
                    value={origin}
                    onChange={e => setOrigin(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">Founded Year</label>
                  <input
                    type="number"
                    required
                    value={foundedYear}
                    onChange={e => setFoundedYear(Number(e.target.value))}
                    className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                  />
                </div>
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Brand Prestige Tier</label>
                <select
                  value={tier}
                  onChange={e => setTier(e.target.value as any)}
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none bg-white"
                >
                  <option value="Luxury">Luxury</option>
                  <option value="Niche">Niche</option>
                  <option value="Designer">Designer</option>
                  <option value="Artisanal">Artisanal</option>
                </select>
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Heritage Bio / Description</label>
                <textarea
                  rows={3}
                  required
                  value={description}
                  onChange={e => setDescription(e.target.value)}
                  placeholder="Brand backstory and craftsmanship..."
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Banner Image URL</label>
                <input
                  type="url"
                  required
                  value={bannerImage}
                  onChange={e => setBannerImage(e.target.value)}
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
                  Save Brand
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

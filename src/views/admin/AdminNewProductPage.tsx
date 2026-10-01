'use client';

import React, { useState } from 'react';
import { useNavigate } from '@/hooks/useNavigation';
import { Link } from '@/components/common/Link';
import { useStore } from '../../context/StoreContext';
import { BRANDS } from '../../data/brands';
import { FragranceFamily, FragranceCategory } from '../../types';
import { ArrowLeft, Plus, Trash2, Sparkles, Check } from 'lucide-react';

const FRAGRANCE_FAMILIES: FragranceFamily[] = [
  'Fresh', 'Woody', 'Floral', 'Oriental', 'Sweet & Gourmand', 'Spicy', 'Citrus', 'Aquatic', 'Aromatic'
];

const CATEGORIES: FragranceCategory[] = [
  'For Her', 'For Him', 'Unisex', 'Luxury & Niche', 'Everyday', 'Gift Sets'
];

export const AdminNewProductPage: React.FC = () => {
  const { addProduct, showToast } = useStore();
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [brandId, setBrandId] = useState(BRANDS[0].id);
  const [tagline, setTagline] = useState('');
  const [category, setCategory] = useState<FragranceCategory>('Unisex');
  const [families, setFamilies] = useState<FragranceFamily[]>(['Woody']);
  const [concentration, setConcentration] = useState<'Parfum' | 'Eau de Parfum (EDP)' | 'Eau de Toilette (EDT)' | 'Eau de Cologne (EDC)' | 'Extrait de Parfum'>('Eau de Parfum (EDP)');
  const [description, setDescription] = useState('');
  const [story, setStory] = useState('');
  
  // Notes
  const [topNotes, setTopNotes] = useState('Bergamot, Cardamom');
  const [heartNotes, setHeartNotes] = useState('Iris, Cedarwood');
  const [baseNotes, setBaseNotes] = useState('Amber, Pure Oud, Musk');

  const [sillage, setSillage] = useState<'Intimate' | 'Moderate' | 'Strong' | 'Enormous'>('Strong');
  const [longevity, setLongevity] = useState<'4-6 Hours' | '6-8 Hours' | '8-12 Hours' | '12+ Hours'>('8-12 Hours');

  // Variants
  const [variants, setVariants] = useState([
    { size: '50ml', price: 9500, mrp: 11000, sku: `SKU-${Date.now().toString().slice(-4)}-50`, inStock: true },
    { size: '100ml', price: 15500, mrp: 18000, sku: `SKU-${Date.now().toString().slice(-4)}-100`, inStock: true }
  ]);

  const [stock, setStock] = useState(25);
  const [image1, setImage1] = useState('https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?q=80&w=800&auto=format&fit=crop');
  const [image2, setImage2] = useState('https://images.unsplash.com/photo-1547887537-6158d64c35b3?q=80&w=800&auto=format&fit=crop');

  const toggleFamily = (fam: FragranceFamily) => {
    setFamilies(prev => prev.includes(fam) ? prev.filter(f => f !== fam) : [...prev, fam]);
  };

  const handleAddVariant = () => {
    setVariants(prev => [
      ...prev,
      { size: '30ml', price: 6500, mrp: 7500, sku: `SKU-${Date.now().toString().slice(-4)}-30`, inStock: true }
    ]);
  };

  const handleRemoveVariant = (idx: number) => {
    if (variants.length > 1) {
      setVariants(prev => prev.filter((_, i) => i !== idx));
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !description.trim()) {
      showToast('Please provide fragrance name and description', 'warning');
      return;
    }

    const selectedBrandObj = BRANDS.find(b => b.id === brandId) || BRANDS[0];

    addProduct({
      name: name.trim(),
      brandId: selectedBrandObj.id,
      brandName: selectedBrandObj.name,
      tagline: tagline.trim() || undefined,
      category,
      fragranceFamilies: families,
      concentration,
      variants,
      notes: {
        top: topNotes.split(',').map(s => s.trim()).filter(Boolean),
        heart: heartNotes.split(',').map(s => s.trim()).filter(Boolean),
        base: baseNotes.split(',').map(s => s.trim()).filter(Boolean)
      },
      sillage,
      longevity,
      season: ['Autumn', 'Winter', 'Night'],
      occasion: ['Evening Gala', 'Date Night', 'Special Occasion'],
      description: description.trim(),
      story: story.trim() || undefined,
      images: [image1, image2].filter(Boolean),
      stock: Number(stock),
      rating: 4.9,
      reviewCount: 1,
      isNewArrival: true,
      isBestSeller: false
    });

    navigate('/admin/products');
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto pb-12">
      {/* Navigation Breadcrumb */}
      <div className="flex items-center gap-2 text-xs text-neutral-500">
        <Link to="/admin/products" className="hover:text-brand-plum-900 flex items-center gap-1 font-medium">
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>Back to Products</span>
        </Link>
        <span>/</span>
        <span className="text-neutral-900 font-semibold">New Fragrance</span>
      </div>

      <div className="flex items-center justify-between pb-4 border-b border-neutral-200">
        <div>
          <h1 className="font-serif text-3xl font-bold text-neutral-900">Add New Luxury Fragrance</h1>
          <p className="text-xs text-neutral-500">Register an artisanal flacon with full notes pyramid and size variants.</p>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6 text-xs">
        {/* Basic Identity Card */}
        <div className="bg-white rounded-3xl p-6 border border-neutral-200 shadow-xs space-y-4">
          <h3 className="font-serif text-lg font-bold text-brand-plum-950">1. Fragrance Identity</h3>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Fragrance Name</label>
              <input
                type="text"
                required
                value={name}
                onChange={e => setName(e.target.value)}
                placeholder="e.g. Royal Oud Sovereign"
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
              />
            </div>

            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Perfume House / Brand</label>
              <select
                value={brandId}
                onChange={e => setBrandId(e.target.value)}
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none bg-white"
              >
                {BRANDS.map(b => (
                  <option key={b.id} value={b.id}>{b.name} ({b.tier})</option>
                ))}
              </select>
            </div>

            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Short Tagline</label>
              <input
                type="text"
                value={tagline}
                onChange={e => setTagline(e.target.value)}
                placeholder="e.g. Pure Smoldering Elegance"
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
              />
            </div>

            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Category</label>
              <select
                value={category}
                onChange={e => setCategory(e.target.value as any)}
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none bg-white"
              >
                {CATEGORIES.map(c => (
                  <option key={c} value={c}>{c}</option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <label className="font-semibold text-neutral-700 block mb-1.5">Fragrance Families</label>
            <div className="flex flex-wrap gap-2">
              {FRAGRANCE_FAMILIES.map(fam => (
                <button
                  type="button"
                  key={fam}
                  onClick={() => toggleFamily(fam)}
                  className={`px-3 py-1.5 rounded-full text-xs font-medium border transition-colors ${
                    families.includes(fam)
                      ? 'border-brand-plum-900 bg-brand-plum-900 text-white'
                      : 'border-neutral-200 text-neutral-700 hover:bg-neutral-100'
                  }`}
                >
                  {fam}
                </button>
              ))}
            </div>
          </div>

          <div>
            <label className="font-semibold text-neutral-700 block mb-1">Description</label>
            <textarea
              rows={3}
              required
              value={description}
              onChange={e => setDescription(e.target.value)}
              placeholder="Sensory overview of the fragrance..."
              className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
            />
          </div>
        </div>

        {/* Notes Pyramid Card */}
        <div className="bg-white rounded-3xl p-6 border border-neutral-200 shadow-xs space-y-4">
          <h3 className="font-serif text-lg font-bold text-brand-plum-950">2. Olfactory Pyramid & Performance</h3>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Top Notes (Comma separated)</label>
              <input
                type="text"
                value={topNotes}
                onChange={e => setTopNotes(e.target.value)}
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
              />
            </div>
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Heart Notes (Comma separated)</label>
              <input
                type="text"
                value={heartNotes}
                onChange={e => setHeartNotes(e.target.value)}
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
              />
            </div>
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Base Notes (Comma separated)</label>
              <input
                type="text"
                value={baseNotes}
                onChange={e => setBaseNotes(e.target.value)}
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-2">
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Concentration</label>
              <select
                value={concentration}
                onChange={e => setConcentration(e.target.value as any)}
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none bg-white"
              >
                <option value="Eau de Parfum (EDP)">Eau de Parfum (EDP)</option>
                <option value="Parfum">Parfum</option>
                <option value="Extrait de Parfum">Extrait de Parfum</option>
                <option value="Eau de Toilette (EDT)">Eau de Toilette (EDT)</option>
                <option value="Eau de Cologne (EDC)">Eau de Cologne (EDC)</option>
              </select>
            </div>
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Sillage Projection</label>
              <select
                value={sillage}
                onChange={e => setSillage(e.target.value as any)}
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none bg-white"
              >
                <option value="Intimate">Intimate</option>
                <option value="Moderate">Moderate</option>
                <option value="Strong">Strong</option>
                <option value="Enormous">Enormous</option>
              </select>
            </div>
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Longevity</label>
              <select
                value={longevity}
                onChange={e => setLongevity(e.target.value as any)}
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none bg-white"
              >
                <option value="4-6 Hours">4-6 Hours</option>
                <option value="6-8 Hours">6-8 Hours</option>
                <option value="8-12 Hours">8-12 Hours</option>
                <option value="12+ Hours">12+ Hours</option>
              </select>
            </div>
          </div>
        </div>

        {/* Variants & Pricing */}
        <div className="bg-white rounded-3xl p-6 border border-neutral-200 shadow-xs space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="font-serif text-lg font-bold text-brand-plum-950">3. Size Variants & Inventory</h3>
            <button
              type="button"
              onClick={handleAddVariant}
              className="px-3 py-1.5 rounded-xl bg-brand-blush-100 text-brand-plum-900 font-semibold hover:bg-brand-blush-200 flex items-center gap-1"
            >
              <Plus className="w-3.5 h-3.5" />
              <span>Add Size</span>
            </button>
          </div>

          <div className="space-y-3">
            {variants.map((v, idx) => (
              <div key={idx} className="flex flex-wrap sm:flex-nowrap items-center gap-3 bg-neutral-50 p-3 rounded-2xl border border-neutral-200">
                <input
                  type="text"
                  placeholder="Size (e.g. 50ml)"
                  value={v.size}
                  onChange={e => {
                    const up = [...variants];
                    up[idx].size = e.target.value;
                    setVariants(up);
                  }}
                  className="w-24 p-2 rounded-lg border border-neutral-300"
                />
                <input
                  type="number"
                  placeholder="Selling Price ₹"
                  value={v.price}
                  onChange={e => {
                    const up = [...variants];
                    up[idx].price = Number(e.target.value);
                    setVariants(up);
                  }}
                  className="w-32 p-2 rounded-lg border border-neutral-300"
                />
                <input
                  type="number"
                  placeholder="MRP ₹"
                  value={v.mrp}
                  onChange={e => {
                    const up = [...variants];
                    up[idx].mrp = Number(e.target.value);
                    setVariants(up);
                  }}
                  className="w-32 p-2 rounded-lg border border-neutral-300"
                />
                <input
                  type="text"
                  placeholder="SKU"
                  value={v.sku}
                  onChange={e => {
                    const up = [...variants];
                    up[idx].sku = e.target.value;
                    setVariants(up);
                  }}
                  className="flex-1 p-2 rounded-lg border border-neutral-300 font-mono"
                />
                {variants.length > 1 && (
                  <button
                    type="button"
                    onClick={() => handleRemoveVariant(idx)}
                    className="p-2 text-semantic-error hover:bg-semantic-error/10 rounded-lg"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                )}
              </div>
            ))}
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-2">
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Total Vault Stock Units</label>
              <input
                type="number"
                value={stock}
                onChange={e => setStock(Number(e.target.value))}
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
              />
            </div>
            <div className="sm:col-span-2">
              <label className="font-semibold text-neutral-700 block mb-1">Primary Bottle Image URL</label>
              <input
                type="url"
                value={image1}
                onChange={e => setImage1(e.target.value)}
                className="w-full p-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
              />
            </div>
          </div>
        </div>

        {/* Submit Actions */}
        <div className="flex justify-end gap-3 pt-4 border-t border-neutral-200">
          <Link
            to="/admin/products"
            className="px-6 py-3 rounded-full border border-neutral-300 text-neutral-700 font-semibold"
          >
            Cancel
          </Link>
          <button
            type="submit"
            className="px-8 py-3 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white font-semibold shadow-card"
          >
            Publish to Store Catalog
          </button>
        </div>
      </form>
    </div>
  );
};

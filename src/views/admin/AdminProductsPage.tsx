'use client';

import React, { useState } from 'react';
import { useStore } from '../../context/StoreContext';
import { Product, FragranceCategory, FragranceFamily } from '../../types';
import { BRANDS } from '../../data/brands';
import { 
  Package, 
  Plus, 
  Search, 
  Edit, 
  Trash2, 
  X, 
  Check, 
  Star, 
  Sparkles 
} from 'lucide-react';

export const AdminProductsPage: React.FC = () => {
  const { products, addProduct, updateProduct, deleteProduct, formatPrice, showToast } = useStore();

  const [search, setSearch] = useState('');
  const [selectedBrand, setSelectedBrand] = useState('All');

  // Modals
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);

  // New Product Form State
  const [name, setName] = useState('');
  const [brandId, setBrandId] = useState(BRANDS[0].id);
  const [category, setCategory] = useState<FragranceCategory>('Unisex');
  const [concentration, setConcentration] = useState<'Parfum' | 'Eau de Parfum (EDP)' | 'Eau de Toilette (EDT)'>('Eau de Parfum (EDP)');
  const [price, setPrice] = useState('8999');
  const [size, setSize] = useState('100ml');
  const [stock, setStock] = useState('30');
  const [description, setDescription] = useState('');
  const [imageUrl, setImageUrl] = useState('https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?q=80&w=1000&auto=format&fit=crop');

  const filteredProducts = products.filter(p => {
    const matchesSearch = p.name.toLowerCase().includes(search.toLowerCase()) ||
                          p.brandName.toLowerCase().includes(search.toLowerCase());
    const matchesBrand = selectedBrand === 'All' || p.brandId === selectedBrand;
    return matchesSearch && matchesBrand;
  });

  const handleCreateProduct = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !description.trim()) {
      showToast('Please fill all required fields', 'warning');
      return;
    }

    const brand = BRANDS.find(b => b.id === brandId) || BRANDS[0];
    const numPrice = Number(price) || 7999;
    const numStock = Number(stock) || 20;

    addProduct({
      name,
      brandId: brand.id,
      brandName: brand.name,
      category,
      fragranceFamilies: ['Woody', 'Fresh'],
      concentration,
      variants: [
        { size: '50ml', price: Math.round(numPrice * 0.75), mrp: Math.round(numPrice * 0.95), sku: `${name.substring(0, 3).toUpperCase()}-50`, inStock: true },
        { size: '100ml', price: numPrice, mrp: Math.round(numPrice * 1.25), sku: `${name.substring(0, 3).toUpperCase()}-100`, inStock: true }
      ],
      notes: {
        top: ['Calabrian Bergamot', 'Pink Pepper'],
        heart: ['Grasse Rose', 'French Lavender'],
        base: ['Cedarwood', 'Bourbon Vanilla']
      },
      sillage: 'Strong',
      longevity: '8-12 Hours',
      season: ['All Season'],
      occasion: ['Special Occasion', 'Evening Gala'],
      description,
      images: [imageUrl],
      stock: numStock,
      rating: 4.9,
      reviewCount: 1,
      isBestSeller: false,
      isNewArrival: true
    });

    setShowAddModal(false);
    setName('');
    setDescription('');
  };

  const handleUpdateProduct = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingProduct) return;
    updateProduct(editingProduct.id, {
      name: editingProduct.name,
      stock: Number(editingProduct.stock),
      variants: editingProduct.variants
    });
    setEditingProduct(null);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="text-xs font-bold uppercase tracking-widest text-brand-rose-500">
            Catalog Management
          </span>
          <h1 className="font-serif text-3xl font-bold text-neutral-900">
            Fragrance Inventory ({products.length})
          </h1>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="px-5 py-3 rounded-2xl bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold flex items-center gap-2 shadow-md transition-all active:scale-98"
        >
          <Plus className="w-4 h-4" />
          <span>Add New Flacon</span>
        </button>
      </div>

      {/* Filter & Search Bar */}
      <div className="bg-white p-4 rounded-2xl border border-neutral-200 shadow-xs flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="relative w-full sm:w-80">
          <input
            type="text"
            placeholder="Search by perfume or house..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="w-full pl-9 pr-4 py-2 text-xs rounded-xl border border-neutral-300 focus:outline-none focus:border-brand-plum-900"
          />
          <Search className="w-4 h-4 text-neutral-400 absolute left-3 top-1/2 -translate-y-1/2" />
        </div>

        <div className="flex items-center gap-2 w-full sm:w-auto">
          <span className="text-xs text-neutral-500 whitespace-nowrap">Filter House:</span>
          <select
            value={selectedBrand}
            onChange={e => setSelectedBrand(e.target.value)}
            className="px-3 py-2 text-xs rounded-xl border border-neutral-300 bg-white font-medium text-neutral-800"
          >
            <option value="All">All Houses</option>
            {BRANDS.map(b => (
              <option key={b.id} value={b.id}>{b.name}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Products Table */}
      <div className="bg-white rounded-3xl border border-neutral-200 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-neutral-50 text-neutral-500 font-semibold uppercase tracking-wider border-b border-neutral-200">
              <tr>
                <th className="py-3.5 px-4">Fragrance Flacon</th>
                <th className="py-3.5 px-4">House</th>
                <th className="py-3.5 px-4">Category</th>
                <th className="py-3.5 px-4">Base Price</th>
                <th className="py-3.5 px-4">Stock</th>
                <th className="py-3.5 px-4">Status</th>
                <th className="py-3.5 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-neutral-100">
              {filteredProducts.map(product => (
                <tr key={product.id} className="hover:bg-neutral-50/70 transition-colors">
                  <td className="py-3 px-4">
                    <div className="flex items-center gap-3">
                      <img
                        src={product.images[0]}
                        alt=""
                        className="w-11 h-11 rounded-lg object-cover bg-neutral-100 border border-neutral-200"
                      />
                      <div>
                        <span className="font-bold text-neutral-900 block">{product.name}</span>
                        <span className="text-[10px] text-neutral-500">{product.concentration}</span>
                      </div>
                    </div>
                  </td>
                  <td className="py-3 px-4 font-medium text-brand-rose-500">{product.brandName}</td>
                  <td className="py-3 px-4">{product.category}</td>
                  <td className="py-3 px-4 font-bold tabular-nums text-brand-plum-950">
                    {formatPrice(product.variants[0]?.price || 0)}
                  </td>
                  <td className="py-3 px-4 tabular-nums font-semibold">
                    {product.stock} units
                  </td>
                  <td className="py-3 px-4">
                    <span className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase ${
                      product.stock > 10 ? 'bg-green-100 text-semantic-success' : 'bg-amber-100 text-semantic-warning'
                    }`}>
                      {product.stock > 10 ? 'In Stock' : 'Low Stock'}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button
                        onClick={() => setEditingProduct(product)}
                        className="p-1.5 text-neutral-500 hover:text-brand-plum-900 hover:bg-neutral-100 rounded-lg transition-colors"
                        aria-label="Edit"
                      >
                        <Edit className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => deleteProduct(product.id)}
                        className="p-1.5 text-neutral-400 hover:text-semantic-error hover:bg-neutral-100 rounded-lg transition-colors"
                        aria-label="Delete"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Product Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-neutral-950/70 backdrop-blur-sm overflow-y-auto">
          <div className="bg-white rounded-3xl p-6 sm:p-8 max-w-lg w-full shadow-modal border border-neutral-200 space-y-4 my-8">
            <div className="flex items-center justify-between pb-2 border-b border-neutral-100">
              <h3 className="font-serif text-2xl font-bold text-neutral-900">Add New Perfume</h3>
              <button onClick={() => setShowAddModal(false)} className="p-1 text-neutral-400 hover:text-neutral-900">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateProduct} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Fragrance Name</label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={e => setName(e.target.value)}
                  placeholder="e.g. Velvet Tonka Millésime"
                  className="w-full px-3 py-2 rounded-xl border border-neutral-300 focus:outline-none focus:border-brand-plum-900"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">Brand House</label>
                  <select
                    value={brandId}
                    onChange={e => setBrandId(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-neutral-300"
                  >
                    {BRANDS.map(b => (
                      <option key={b.id} value={b.id}>{b.name}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">Collection Category</label>
                  <select
                    value={category}
                    onChange={e => setCategory(e.target.value as FragranceCategory)}
                    className="w-full px-3 py-2 rounded-xl border border-neutral-300"
                  >
                    <option value="For Her">For Her</option>
                    <option value="For Him">For Him</option>
                    <option value="Unisex">Unisex</option>
                    <option value="Luxury & Niche">Luxury & Niche</option>
                    <option value="Gift Sets">Gift Sets</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">Base Price (₹)</label>
                  <input
                    type="number"
                    required
                    value={price}
                    onChange={e => setPrice(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-neutral-300"
                  />
                </div>
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">Vault Stock Units</label>
                  <input
                    type="number"
                    required
                    value={stock}
                    onChange={e => setStock(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-neutral-300"
                  />
                </div>
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Product Description</label>
                <textarea
                  rows={3}
                  required
                  value={description}
                  onChange={e => setDescription(e.target.value)}
                  placeholder="Describe top, heart, base notes and olfactory sillage..."
                  className="w-full px-3 py-2 rounded-xl border border-neutral-300 focus:outline-none focus:border-brand-plum-900"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">High-Res Flacon Image URL</label>
                <input
                  type="url"
                  value={imageUrl}
                  onChange={e => setImageUrl(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-neutral-300"
                />
              </div>

              <div className="flex gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="flex-1 py-2.5 rounded-xl border border-neutral-300 font-semibold text-neutral-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 rounded-xl bg-brand-plum-900 text-white font-semibold hover:bg-brand-plum-800 shadow-md"
                >
                  Save Product to Catalog
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Product Modal */}
      {editingProduct && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-neutral-950/70 backdrop-blur-sm">
          <div className="bg-white rounded-3xl p-6 sm:p-8 max-w-md w-full shadow-modal border border-neutral-200 space-y-4">
            <div className="flex items-center justify-between pb-2 border-b border-neutral-100">
              <h3 className="font-serif text-xl font-bold text-neutral-900">Edit {editingProduct.name}</h3>
              <button onClick={() => setEditingProduct(null)} className="p-1 text-neutral-400 hover:text-neutral-900">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleUpdateProduct} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Product Title</label>
                <input
                  type="text"
                  required
                  value={editingProduct.name}
                  onChange={e => setEditingProduct({ ...editingProduct, name: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl border border-neutral-300"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Available Stock Units</label>
                <input
                  type="number"
                  required
                  value={editingProduct.stock}
                  onChange={e => setEditingProduct({ ...editingProduct, stock: Number(e.target.value) })}
                  className="w-full px-3 py-2 rounded-xl border border-neutral-300"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Primary Price (100ml / Standard)</label>
                <input
                  type="number"
                  required
                  value={editingProduct.variants[0]?.price || 0}
                  onChange={e => {
                    const val = Number(e.target.value);
                    const updatedVariants = [...editingProduct.variants];
                    if (updatedVariants[0]) updatedVariants[0].price = val;
                    setEditingProduct({ ...editingProduct, variants: updatedVariants });
                  }}
                  className="w-full px-3 py-2 rounded-xl border border-neutral-300"
                />
              </div>

              <div className="flex gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setEditingProduct(null)}
                  className="flex-1 py-2.5 rounded-xl border border-neutral-300 font-semibold text-neutral-700"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 rounded-xl bg-brand-plum-900 text-white font-semibold hover:bg-brand-plum-800 shadow-md"
                >
                  Save Changes
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

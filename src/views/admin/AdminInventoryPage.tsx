'use client';

import React from 'react';
import { useStore } from '../../context/StoreContext';
import { Layers, Plus, Minus, AlertTriangle, CheckCircle2 } from 'lucide-react';

export const AdminInventoryPage: React.FC = () => {
  const { products, updateProduct, formatPrice } = useStore();

  const handleAdjustStock = (productId: string, currentStock: number, delta: number) => {
    const newStock = Math.max(0, currentStock + delta);
    updateProduct(productId, { stock: newStock });
  };

  const totalVaultBottles = products.reduce((acc, p) => acc + p.stock, 0);
  const lowStockCount = products.filter(p => p.stock < 20).length;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="text-xs font-bold uppercase tracking-widest text-brand-rose-500">
            Stock Replenishment
          </span>
          <h1 className="font-serif text-3xl font-bold text-neutral-900">
            Vault Inventory Control
          </h1>
        </div>

        <div className="flex items-center gap-3 text-xs">
          <div className="p-3 px-4 rounded-xl bg-white border border-neutral-200">
            <span className="text-neutral-500 block">Total Flacons in Vault:</span>
            <strong className="text-base text-brand-plum-950 font-serif">{totalVaultBottles} units</strong>
          </div>
          <div className="p-3 px-4 rounded-xl bg-amber-50 border border-amber-200">
            <span className="text-amber-800 block">Low Stock Alerts:</span>
            <strong className="text-base text-amber-900 font-serif">{lowStockCount} lines</strong>
          </div>
        </div>
      </div>

      {/* Inventory Table */}
      <div className="bg-white rounded-3xl border border-neutral-200 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-neutral-50 text-neutral-500 font-semibold uppercase tracking-wider border-b border-neutral-200">
              <tr>
                <th className="py-3.5 px-4">Perfume Name</th>
                <th className="py-3.5 px-4">House</th>
                <th className="py-3.5 px-4">Variants SKUs</th>
                <th className="py-3.5 px-4">Units in Vault</th>
                <th className="py-3.5 px-4">Quick Adjust</th>
                <th className="py-3.5 px-4 text-right">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-neutral-100">
              {products.map(product => (
                <tr key={product.id} className="hover:bg-neutral-50/70">
                  <td className="py-3 px-4">
                    <div className="flex items-center gap-3">
                      <img src={product.images[0]} alt="" className="w-10 h-10 rounded-lg object-cover bg-neutral-100 border border-neutral-200" />
                      <div>
                        <span className="font-bold text-neutral-900 block">{product.name}</span>
                        <span className="text-[10px] text-neutral-500">{product.concentration}</span>
                      </div>
                    </div>
                  </td>
                  <td className="py-3 px-4 font-medium text-brand-rose-500">{product.brandName}</td>
                  <td className="py-3 px-4 font-mono text-[10px] text-neutral-600">
                    {product.variants.map(v => `${v.size}: ${v.sku}`).join(' | ')}
                  </td>
                  <td className="py-3 px-4">
                    <span className="font-bold text-sm text-brand-plum-950 tabular-nums">
                      {product.stock}
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <div className="flex items-center gap-1.5">
                      <button
                        onClick={() => handleAdjustStock(product.id, product.stock, -5)}
                        className="p-1 px-2 rounded-lg bg-neutral-100 hover:bg-neutral-200 text-neutral-700 font-bold"
                      >
                        -5
                      </button>
                      <button
                        onClick={() => handleAdjustStock(product.id, product.stock, -1)}
                        className="p-1 px-2 rounded-lg bg-neutral-100 hover:bg-neutral-200 text-neutral-700 font-bold"
                      >
                        -1
                      </button>
                      <button
                        onClick={() => handleAdjustStock(product.id, product.stock, 1)}
                        className="p-1 px-2 rounded-lg bg-brand-blush-100 hover:bg-brand-blush-200 text-brand-plum-900 font-bold"
                      >
                        +1
                      </button>
                      <button
                        onClick={() => handleAdjustStock(product.id, product.stock, 10)}
                        className="p-1 px-2 rounded-lg bg-brand-plum-900 hover:bg-brand-plum-800 text-white font-bold"
                      >
                        +10
                      </button>
                    </div>
                  </td>
                  <td className="py-3 px-4 text-right">
                    {product.stock < 20 ? (
                      <span className="inline-flex items-center gap-1 text-[11px] font-bold text-semantic-warning bg-amber-50 px-2.5 py-0.5 rounded-full border border-amber-200">
                        <AlertTriangle className="w-3 h-3" /> Reorder Soon
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 text-[11px] font-bold text-semantic-success bg-green-50 px-2.5 py-0.5 rounded-full border border-green-200">
                        <CheckCircle2 className="w-3 h-3" /> Healthy Stock
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

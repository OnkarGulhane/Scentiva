'use client';

import React, { useState } from 'react';
import { useStore } from '../../context/StoreContext';
import { Sliders, Save, CheckCircle2, ShieldCheck, DollarSign, Truck } from 'lucide-react';

export const AdminSettingsPage: React.FC = () => {
  const { showToast } = useStore();
  const [storeName, setStoreName] = useState('SCENTIVA Luxury Fragrance Vault');
  const [currency, setCurrency] = useState('INR (₹)');
  const [freeShippingThreshold, setFreeShippingThreshold] = useState('999');
  const [expressDeliveryFee, setExpressDeliveryFee] = useState('0');
  const [supportEmail, setSupportEmail] = useState('concierge@scentiva.com');
  const [tagline, setTagline] = useState('SINCE 2026');

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    showToast('Store configurations saved successfully!', 'success');
  };

  return (
    <div className="space-y-6 max-w-3xl">
      <div>
        <span className="text-xs font-bold uppercase tracking-widest text-brand-rose-500">
          Store Configuration
        </span>
        <h1 className="font-serif text-3xl font-bold text-neutral-900">
          Settings & Logistics Parameters
        </h1>
      </div>

      <form onSubmit={handleSave} className="bg-white p-6 sm:p-8 rounded-3xl border border-neutral-200 shadow-xs space-y-6 text-xs">
        <div className="space-y-4">
          <h3 className="font-serif text-lg font-bold text-neutral-900 pb-2 border-b border-neutral-100">
            Brand Identity Settings
          </h3>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Store Name</label>
              <input
                type="text"
                value={storeName}
                onChange={e => setStoreName(e.target.value)}
                className="w-full px-3 py-2 rounded-xl border border-neutral-300"
              />
            </div>
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Brand Tagline</label>
              <input
                type="text"
                value={tagline}
                onChange={e => setTagline(e.target.value)}
                className="w-full px-3 py-2 rounded-xl border border-neutral-300"
              />
            </div>
          </div>

          <div>
            <label className="font-semibold text-neutral-700 block mb-1">Concierge Support Email</label>
            <input
              type="email"
              value={supportEmail}
              onChange={e => setSupportEmail(e.target.value)}
              className="w-full px-3 py-2 rounded-xl border border-neutral-300"
            />
          </div>
        </div>

        <div className="space-y-4 pt-4 border-t border-neutral-100">
          <h3 className="font-serif text-lg font-bold text-neutral-900 pb-2 border-b border-neutral-100">
            Shipping & Tax Rules
          </h3>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Free Shipping Min Order (₹)</label>
              <input
                type="number"
                value={freeShippingThreshold}
                onChange={e => setFreeShippingThreshold(e.target.value)}
                className="w-full px-3 py-2 rounded-xl border border-neutral-300"
              />
            </div>
            <div>
              <label className="font-semibold text-neutral-700 block mb-1">Store Currency Symbol</label>
              <input
                type="text"
                value={currency}
                disabled
                className="w-full px-3 py-2 rounded-xl border border-neutral-200 bg-neutral-100 text-neutral-500"
              />
            </div>
          </div>
        </div>

        <div className="pt-4 border-t border-neutral-100 flex justify-end">
          <button
            type="submit"
            className="px-8 py-3 rounded-2xl bg-brand-plum-900 text-white font-semibold flex items-center gap-2 hover:bg-brand-plum-800 shadow-md"
          >
            <Save className="w-4 h-4" />
            <span>Save Store Configuration</span>
          </button>
        </div>
      </form>
    </div>
  );
};

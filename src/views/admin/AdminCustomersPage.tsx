'use client';

import React, { useState } from 'react';
import { CustomerService } from '../../services/customerService';
import { Customer } from '../../types';
import { useStore } from '../../context/StoreContext';
import { User, Award, Mail, Phone, Search, ShieldCheck, ChevronRight } from 'lucide-react';

export const AdminCustomersPage: React.FC = () => {
  const [customers, setCustomers] = useState<Customer[]>(() => CustomerService.getAll());
  const [search, setSearch] = useState('');
  const { formatPrice, showToast } = useStore();

  const filtered = customers.filter(c =>
    c.name.toLowerCase().includes(search.toLowerCase()) ||
    c.email.toLowerCase().includes(search.toLowerCase()) ||
    c.phone.includes(search) ||
    c.tier.toLowerCase().includes(search.toLowerCase())
  );

  const handleTierChange = (id: string, newTier: Customer['tier']) => {
    CustomerService.updateTier(id, newTier);
    setCustomers(CustomerService.getAll());
    showToast(`VIP tier updated to ${newTier}`, 'success');
  };

  const getTierBadge = (tier: string) => {
    switch (tier) {
      case 'Privé Diamond':
        return 'bg-brand-plum-900 text-brand-gold-100 border-brand-gold-500';
      case 'Privé Gold':
        return 'bg-brand-gold-100 text-brand-plum-950 border-brand-gold-500/40';
      case 'Privé Silver':
        return 'bg-neutral-100 text-neutral-800 border-neutral-300';
      default:
        return 'bg-brand-blush-100 text-brand-plum-900 border-brand-blush-300';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-neutral-200">
        <div>
          <h1 className="font-serif text-3xl font-bold text-neutral-900">Customer & Privé Registry</h1>
          <p className="text-xs text-neutral-500">Track connoisseur accounts, lifetime spend, and VIP rewards tiers.</p>
        </div>

        <div className="relative w-full sm:w-72">
          <Search className="w-4 h-4 text-neutral-400 absolute left-3.5 top-3" />
          <input
            type="text"
            placeholder="Search customers..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="w-full pl-9 pr-4 py-2 rounded-xl bg-white border border-neutral-300 text-xs focus:outline-none focus:border-brand-plum-700"
          />
        </div>
      </div>

      <div className="bg-white rounded-3xl border border-neutral-200 shadow-card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-neutral-50 text-neutral-600 font-semibold border-b border-neutral-200 uppercase tracking-wider text-[10px]">
              <tr>
                <th className="p-4">Customer</th>
                <th className="p-4">Contact</th>
                <th className="p-4">VIP Privé Tier</th>
                <th className="p-4">Total Orders</th>
                <th className="p-4">Lifetime Spend</th>
                <th className="p-4">Member Since</th>
                <th className="p-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-neutral-100">
              {filtered.map(c => (
                <tr key={c.id} className="hover:bg-neutral-50/80 transition-colors">
                  <td className="p-4">
                    <div className="flex items-center gap-3">
                      <div className="w-9 h-9 rounded-full bg-brand-plum-900 text-brand-gold-100 font-serif font-bold flex items-center justify-center">
                        {c.name.slice(0, 2).toUpperCase()}
                      </div>
                      <div>
                        <div className="font-bold text-neutral-900">{c.name}</div>
                        <div className="text-[10px] text-neutral-400 font-mono">{c.id}</div>
                      </div>
                    </div>
                  </td>
                  <td className="p-4 text-neutral-600 space-y-0.5">
                    <div className="flex items-center gap-1.5"><Mail className="w-3 h-3 text-neutral-400" />{c.email}</div>
                    <div className="flex items-center gap-1.5"><Phone className="w-3 h-3 text-neutral-400" />{c.phone}</div>
                  </td>
                  <td className="p-4">
                    <select
                      value={c.tier}
                      onChange={e => handleTierChange(c.id, e.target.value as any)}
                      className={`px-2.5 py-1 rounded-full text-[11px] font-bold border ${getTierBadge(c.tier)} focus:outline-none cursor-pointer`}
                    >
                      <option value="Privé Bronze">Privé Bronze</option>
                      <option value="Privé Silver">Privé Silver</option>
                      <option value="Privé Gold">Privé Gold</option>
                      <option value="Privé Diamond">Privé Diamond</option>
                    </select>
                  </td>
                  <td className="p-4 font-bold text-neutral-800 tabular-nums">
                    {c.totalOrders} {c.totalOrders === 1 ? 'order' : 'orders'}
                  </td>
                  <td className="p-4 font-serif font-bold text-brand-plum-950 text-sm tabular-nums">
                    {formatPrice(c.totalSpend)}
                  </td>
                  <td className="p-4 text-neutral-500">
                    {c.joinedDate}
                  </td>
                  <td className="p-4 text-right">
                    <span className="text-[10px] px-2 py-0.5 rounded-full bg-semantic-success/10 text-semantic-success font-bold">
                      {c.status}
                    </span>
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

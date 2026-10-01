'use client';

import React from 'react';
import { useStore } from '../../context/StoreContext';
import { 
  TrendingUp, 
  ShoppingBag, 
  Users, 
  Percent, 
  Package, 
  ArrowUpRight, 
  ArrowRight,
  Sparkles
} from 'lucide-react';
import { Link } from '@/components/common/Link';

export const AdminDashboardPage: React.FC = () => {
  const { products, orders, formatPrice } = useStore();

  const METRICS = [
    { label: 'Total Gross Revenue', value: '₹4,28,750', change: '+18.4%', icon: TrendingUp, positive: true },
    { label: 'Total Orders Processed', value: `${124 + orders.length}`, change: '+12.0%', icon: ShoppingBag, positive: true },
    { label: 'Active Connoisseur Base', value: '1,204', change: '+8.6%', icon: Users, positive: true },
    { label: 'Conversion Rate', value: '3.8%', change: '+1.2%', icon: Percent, positive: true }
  ];

  return (
    <div className="space-y-8">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="text-xs font-bold uppercase tracking-widest text-brand-rose-500">
            Real-Time Store Operations
          </span>
          <h1 className="font-serif text-3xl font-bold text-neutral-900">
            Commerce Analytics & Vault Overview
          </h1>
        </div>

        <Link
          to="/admin/products"
          className="px-5 py-2.5 rounded-xl bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold flex items-center gap-1.5 shadow-sm"
        >
          <span>Manage Fragrance Catalog</span>
          <ArrowRight className="w-3.5 h-3.5" />
        </Link>
      </div>

      {/* 4 Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6">
        {METRICS.map((metric, idx) => {
          const Icon = metric.icon;
          return (
            <div
              key={idx}
              className="bg-white p-5 rounded-2xl border border-neutral-200/80 shadow-xs space-y-3"
            >
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-neutral-500">{metric.label}</span>
                <div className="p-2 rounded-xl bg-brand-blush-100 text-brand-plum-900">
                  <Icon className="w-4 h-4" />
                </div>
              </div>
              <div className="flex items-baseline justify-between">
                <span className="text-2xl font-bold font-serif text-neutral-900">{metric.value}</span>
                <span className="text-xs font-bold text-semantic-success flex items-center gap-0.5">
                  <ArrowUpRight className="w-3.5 h-3.5" /> {metric.change}
                </span>
              </div>
            </div>
          );
        })}
      </div>

      {/* Chart & Top Products Row */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Sales Trend Simulation (7 Cols) */}
        <div className="lg:col-span-7 bg-white p-6 rounded-3xl border border-neutral-200 shadow-xs space-y-4">
          <div className="flex items-center justify-between border-b border-neutral-100 pb-3">
            <div>
              <h3 className="font-serif text-lg font-bold text-neutral-900">Sales Volume Trend (Last 7 Days)</h3>
              <p className="text-[11px] text-neutral-500">Daily luxury fragrance revenue in INR (₹)</p>
            </div>
            <span className="text-xs font-bold text-brand-plum-900 bg-brand-blush-100 px-2.5 py-1 rounded-full">
              September 2026
            </span>
          </div>

          {/* Bar Chart Visualization */}
          <div className="h-48 flex items-end justify-between gap-3 pt-4 px-2">
            {[
              { day: 'Mon', amount: 48000, height: '55%' },
              { day: 'Tue', amount: 59000, height: '68%' },
              { day: 'Wed', amount: 42000, height: '48%' },
              { day: 'Thu', amount: 78000, height: '88%' },
              { day: 'Fri', amount: 92000, height: '100%' },
              { day: 'Sat', amount: 84000, height: '92%' },
              { day: 'Sun', amount: 65000, height: '74%' }
            ].map((bar, i) => (
              <div key={i} className="flex-1 flex flex-col items-center gap-2 h-full justify-end group">
                <div className="text-[10px] text-neutral-500 opacity-0 group-hover:opacity-100 font-bold transition-opacity">
                  ₹{(bar.amount / 1000).toFixed(0)}k
                </div>
                <div
                  className="w-full bg-brand-plum-900 group-hover:bg-brand-gold-500 rounded-t-lg transition-all duration-300"
                  style={{ height: bar.height }}
                />
                <span className="text-xs font-medium text-neutral-600">{bar.day}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Top Selling Flacons (5 Cols) */}
        <div className="lg:col-span-5 bg-white p-6 rounded-3xl border border-neutral-200 shadow-xs space-y-4">
          <div className="flex items-center justify-between border-b border-neutral-100 pb-3">
            <h3 className="font-serif text-lg font-bold text-neutral-900">Top Performing Perfumes</h3>
            <span className="text-xs text-brand-rose-500 font-semibold">By Volume</span>
          </div>

          <div className="space-y-3">
            {products.slice(0, 4).map((p, i) => (
              <div key={p.id} className="flex items-center justify-between text-xs p-2 rounded-xl hover:bg-neutral-50">
                <div className="flex items-center gap-3">
                  <span className="font-bold text-neutral-400 font-mono">0{i + 1}</span>
                  <img src={p.images[0]} alt="" className="w-10 h-10 rounded-lg object-cover bg-neutral-100" />
                  <div>
                    <span className="font-bold text-neutral-900 block truncate max-w-[150px]">{p.name}</span>
                    <span className="text-[10px] text-neutral-500">{p.brandName}</span>
                  </div>
                </div>
                <div className="text-right font-semibold text-brand-plum-950 tabular-nums">
                  {formatPrice(p.variants[0].price)}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Recent Orders Stream */}
      <div className="bg-white p-6 rounded-3xl border border-neutral-200 shadow-xs space-y-4">
        <div className="flex items-center justify-between border-b border-neutral-100 pb-3">
          <h3 className="font-serif text-lg font-bold text-neutral-900">Recent Customer Orders</h3>
          <Link to="/admin/orders" className="text-xs font-semibold text-brand-plum-900 hover:text-brand-rose-500">
            View All Shipments →
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-neutral-50 text-neutral-500 font-semibold uppercase tracking-wider border-b border-neutral-200">
              <tr>
                <th className="py-3 px-4">Order Ref</th>
                <th className="py-3 px-4">Customer</th>
                <th className="py-3 px-4">Perfumes</th>
                <th className="py-3 px-4">Total</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4 text-right">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-neutral-100">
              {orders.map(o => (
                <tr key={o.id} className="hover:bg-neutral-50/80">
                  <td className="py-3 px-4 font-bold font-mono text-brand-plum-950">{o.orderNumber}</td>
                  <td className="py-3 px-4">{o.shippingAddress.fullName}</td>
                  <td className="py-3 px-4">{o.items.length} item(s)</td>
                  <td className="py-3 px-4 font-bold tabular-nums">{formatPrice(o.total)}</td>
                  <td className="py-3 px-4">
                    <span className="bg-brand-blush-100 text-brand-plum-900 font-bold px-2.5 py-0.5 rounded-full text-[10px] uppercase">
                      {o.status}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-right">
                    <Link
                      to={`/account/orders/${o.id}`}
                      className="text-xs text-brand-plum-900 font-semibold hover:underline"
                    >
                      Track
                    </Link>
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

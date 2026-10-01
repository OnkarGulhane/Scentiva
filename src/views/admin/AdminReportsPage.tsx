'use client';

import React, { useState } from 'react';
import { useStore } from '../../context/StoreContext';
import { 
  BarChart3, 
  TrendingUp, 
  Download, 
  Calendar, 
  Layers, 
  Sparkles, 
  PieChart, 
  ArrowUpRight,
  ShieldCheck
} from 'lucide-react';

export const AdminReportsPage: React.FC = () => {
  const { formatPrice, showToast } = useStore();
  const [timeRange, setTimeRange] = useState<'30d' | '90d' | 'ytd'>('30d');

  const handleExportCSV = () => {
    showToast('Exporting SCENTIVA_Sales_Report_2026.csv (Demo Simulation)', 'info');
  };

  const familySales = [
    { family: 'Woody & Oud', share: '38%', revenue: 162925, count: 18 },
    { family: 'Sensual Amber & Gourmand', share: '26%', revenue: 111475, count: 12 },
    { family: 'Fresh & Mediterranean Citrus', share: '21%', revenue: 90037, count: 10 },
    { family: 'Sensual Florals', share: '15%', revenue: 64312, count: 7 },
  ];

  const brandPerformance = [
    { brand: 'Christian Dior', revenue: 118400, units: 12, growth: '+18.4%' },
    { brand: 'CHANEL', revenue: 98900, units: 9, growth: '+14.2%' },
    { brand: 'Tom Ford', revenue: 84500, units: 5, growth: '+22.6%' },
    { brand: 'House of Creed', revenue: 72000, units: 3, growth: '+9.1%' },
    { brand: 'Yves Saint Laurent', revenue: 54950, units: 6, growth: '+11.8%' }
  ];

  return (
    <div className="space-y-8">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-neutral-200">
        <div>
          <h1 className="font-serif text-3xl font-bold text-neutral-900">Analytics & Sales Reports</h1>
          <p className="text-xs text-neutral-500">Holistic performance metrics, brand share, and average basket value.</p>
        </div>

        <div className="flex items-center gap-3">
          <select
            value={timeRange}
            onChange={e => setTimeRange(e.target.value as any)}
            className="px-3 py-2 rounded-xl bg-white border border-neutral-300 text-xs font-semibold text-neutral-800"
          >
            <option value="30d">Last 30 Days</option>
            <option value="90d">Last Quarter (90 Days)</option>
            <option value="ytd">Year to Date 2026</option>
          </select>

          <button
            onClick={handleExportCSV}
            className="px-4 py-2 rounded-xl bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold flex items-center gap-1.5 shadow-sm transition-colors"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Export CSV</span>
          </button>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-6 rounded-3xl border border-neutral-200 shadow-card space-y-1">
          <span className="text-[10px] text-neutral-400 font-bold uppercase tracking-wider">Gross Merchandise Value</span>
          <div className="text-2xl font-serif font-bold text-brand-plum-950">₹4,28,750</div>
          <div className="text-xs text-semantic-success flex items-center gap-1 font-semibold">
            <ArrowUpRight className="w-3.5 h-3.5" /> +24.8% vs prior period
          </div>
        </div>

        <div className="bg-white p-6 rounded-3xl border border-neutral-200 shadow-card space-y-1">
          <span className="text-[10px] text-neutral-400 font-bold uppercase tracking-wider">Average Basket Value (AOV)</span>
          <div className="text-2xl font-serif font-bold text-brand-plum-950">₹14,291</div>
          <div className="text-xs text-semantic-success flex items-center gap-1 font-semibold">
            <ArrowUpRight className="w-3.5 h-3.5" /> +8.5% premium uplift
          </div>
        </div>

        <div className="bg-white p-6 rounded-3xl border border-neutral-200 shadow-card space-y-1">
          <span className="text-[10px] text-neutral-400 font-bold uppercase tracking-wider">Storefront Conversion</span>
          <div className="text-2xl font-serif font-bold text-brand-plum-950">3.82%</div>
          <div className="text-xs text-neutral-500 font-medium">Industry luxury benchmark: 2.4%</div>
        </div>

        <div className="bg-white p-6 rounded-3xl border border-neutral-200 shadow-card space-y-1">
          <span className="text-[10px] text-neutral-400 font-bold uppercase tracking-wider">Repeat Customer Rate</span>
          <div className="text-2xl font-serif font-bold text-brand-plum-950">46.2%</div>
          <div className="text-xs text-brand-gold-500 font-semibold flex items-center gap-1">
            <Sparkles className="w-3.5 h-3.5" /> Privé VIP loyalty driver
          </div>
        </div>
      </div>

      {/* Breakdown Grids */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        {/* Fragrance Families Revenue Distribution */}
        <div className="lg:col-span-6 bg-white rounded-3xl p-6 border border-neutral-200 shadow-card space-y-4">
          <h3 className="font-serif text-lg font-bold text-brand-plum-950 flex items-center gap-2">
            <PieChart className="w-4 h-4 text-brand-rose-500" />
            Revenue by Olfactory Family
          </h3>

          <div className="space-y-3 pt-2">
            {familySales.map(item => (
              <div key={item.family} className="space-y-1 text-xs">
                <div className="flex justify-between font-semibold">
                  <span className="text-neutral-800">{item.family}</span>
                  <span className="text-brand-plum-950 font-serif font-bold">{formatPrice(item.revenue)} ({item.share})</span>
                </div>
                <div className="w-full bg-neutral-100 h-2 rounded-full overflow-hidden">
                  <div
                    className="bg-brand-plum-900 h-full rounded-full"
                    style={{ width: item.share }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Brand House Performance Table */}
        <div className="lg:col-span-6 bg-white rounded-3xl p-6 border border-neutral-200 shadow-card space-y-4">
          <h3 className="font-serif text-lg font-bold text-brand-plum-950 flex items-center gap-2">
            <TrendingUp className="w-4 h-4 text-brand-gold-500" />
            Top Performing Perfume Houses
          </h3>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="text-neutral-500 border-b border-neutral-100 uppercase text-[10px]">
                <tr>
                  <th className="pb-2">Brand House</th>
                  <th className="pb-2">Revenue</th>
                  <th className="pb-2">Units Sold</th>
                  <th className="pb-2 text-right">Growth</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-neutral-100">
                {brandPerformance.map(b => (
                  <tr key={b.brand} className="py-2.5">
                    <td className="py-2.5 font-bold text-neutral-900">{b.brand}</td>
                    <td className="py-2.5 font-serif font-bold text-brand-plum-950">{formatPrice(b.revenue)}</td>
                    <td className="py-2.5 text-neutral-600">{b.units} flacons</td>
                    <td className="py-2.5 text-right font-semibold text-semantic-success">{b.growth}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

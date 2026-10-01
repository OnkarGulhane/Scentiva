'use client';

import React, { useState } from 'react';
import { NavLink, Link } from '@/components/common/Link';
import { useNavigate } from '@/hooks/useNavigation';
import { 
  LayoutDashboard, 
  Package, 
  ShoppingBag, 
  Layers, 
  Tag, 
  Sliders, 
  ExternalLink, 
  LogOut, 
  ShieldCheck, 
  Menu, 
  X,
  PlusCircle,
  Building,
  FolderTree,
  Users,
  FileText,
  BarChart3,
  Sparkles
} from 'lucide-react';

interface AdminLayoutProps {
  children?: React.ReactNode;
}

export const AdminLayout: React.FC<AdminLayoutProps> = ({ children }) => {
  const [mobileDrawerOpen, setMobileDrawerOpen] = useState(false);
  const navigate = useNavigate();

  const NAV_ITEMS = [
    { to: '/admin', label: 'Dashboard', icon: LayoutDashboard, end: true },
    { to: '/admin/products', label: 'Products', icon: Package, end: true },
    { to: '/admin/products/new', label: 'Add New Fragrance', icon: PlusCircle },
    { to: '/admin/brands', label: 'Brand Directory', icon: Building },
    { to: '/admin/categories', label: 'Categories', icon: FolderTree },
    { to: '/admin/inventory', label: 'Inventory Control', icon: Layers },
    { to: '/admin/orders', label: 'Orders & Shipments', icon: ShoppingBag },
    { to: '/admin/customers', label: 'Customer Registry', icon: Users },
    { to: '/admin/promotions', label: 'Coupons & Offers', icon: Tag },
    { to: '/admin/content', label: 'Editorial CMS', icon: FileText },
    { to: '/admin/reports', label: 'Analytics & Reports', icon: BarChart3 },
    { to: '/admin/settings', label: 'Store Settings', icon: Sliders }
  ];

  return (
    <div className="min-h-screen bg-neutral-100 flex flex-col lg:flex-row">
      {/* Mobile Admin Header */}
      <div className="lg:hidden bg-brand-plum-950 text-white p-4 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-full bg-brand-plum-900 border border-brand-blush-300 p-0.5 flex items-center justify-center">
            <img src="/assets/scentiva-emblem.svg" alt="SCENTIVA" className="w-full h-full" />
          </div>
          <span className="font-serif text-lg font-bold">SCENTIVA Admin</span>
        </div>
        <button
          onClick={() => setMobileDrawerOpen(true)}
          className="p-2 text-neutral-300 hover:text-white"
          aria-label="Open Admin Menu"
        >
          <Menu className="w-6 h-6" />
        </button>
      </div>

      {/* Sidebar for Desktop & Mobile Drawer */}
      <aside
        className={`fixed inset-y-0 left-0 z-50 w-64 bg-brand-plum-950 text-white flex flex-col justify-between p-5 transition-transform duration-300 ${
          mobileDrawerOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        } lg:static lg:h-screen lg:sticky lg:top-0 shadow-modal border-r border-brand-plum-900 overflow-y-auto`}
      >
        <div className="space-y-6">
          {/* Logo Header */}
          <div className="flex items-center justify-between pb-4 border-b border-brand-plum-800">
            <Link to="/admin" className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-full bg-brand-plum-900 border border-brand-blush-300/40 p-1 flex items-center justify-center">
                <img src="/assets/scentiva-emblem.svg" alt="SCENTIVA" className="w-full h-full" />
              </div>
              <div>
                <span className="font-serif text-lg font-bold tracking-wider text-white">SCENTIVA</span>
                <span className="block text-[9px] font-mono tracking-widest text-brand-gold-500 uppercase">
                  OPERATIONS DEMO
                </span>
              </div>
            </Link>
            <button
              onClick={() => setMobileDrawerOpen(false)}
              className="lg:hidden text-neutral-400 hover:text-white p-1"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Navigation Links */}
          <nav className="space-y-1">
            {NAV_ITEMS.map(item => {
              const Icon = item.icon;
              return (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.end}
                  onClick={() => setMobileDrawerOpen(false)}
                  className={({ isActive }) =>
                    `flex items-center gap-3 px-3 py-2 rounded-xl text-xs font-semibold transition-colors ${
                      isActive
                        ? 'bg-brand-gold-500 text-brand-plum-950 shadow-sm'
                        : 'text-neutral-300 hover:bg-white/10 hover:text-white'
                    }`
                  }
                >
                  <Icon className="w-4 h-4 shrink-0" />
                  <span className="truncate">{item.label}</span>
                </NavLink>
              );
            })}
          </nav>
        </div>

        {/* Footer Actions */}
        <div className="pt-4 border-t border-brand-plum-800 space-y-2 shrink-0">
          <Link
            to="/"
            className="flex items-center justify-between px-3.5 py-2.5 rounded-xl text-xs font-semibold text-brand-blush-200 bg-white/5 hover:bg-white/10 transition-colors"
          >
            <span className="flex items-center gap-2">
              <ExternalLink className="w-4 h-4" />
              <span>Customer Store</span>
            </span>
            <span className="text-[10px] text-brand-gold-500 font-mono">LIVE</span>
          </Link>
          <div className="text-[10px] text-neutral-400 text-center pt-1">
            SCENTIVA v1.0 • Prototype Mode
          </div>
        </div>
      </aside>

      {/* Main Admin Workspace Container */}
      <main className="flex-1 p-4 sm:p-6 lg:p-8 overflow-y-auto max-w-7xl mx-auto w-full">
        {children}
      </main>
    </div>
  );
};


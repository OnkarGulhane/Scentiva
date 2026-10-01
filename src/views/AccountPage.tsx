'use client';

import React, { useState } from 'react';
import { Link } from '@/components/common/Link';
import { useNavigate } from '@/hooks/useNavigation';
import { useStore } from '../context/StoreContext';
import { 
  User, 
  Package, 
  Heart, 
  MapPin, 
  Award, 
  Sparkles, 
  Plus, 
  Trash2, 
  ExternalLink, 
  ShieldCheck, 
  LogOut, 
  ChevronRight,
  Truck,
  HelpCircle
} from 'lucide-react';

export const AccountPage: React.FC = () => {
  const { currentUser, isLoggedIn, signOut, orders, addresses, wishlist, deleteAddress, formatPrice, showToast } = useStore();
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState<'orders' | 'addresses' | 'rewards' | 'settings'>('orders');

  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
        {/* User Hero Banner */}
        <div className="bg-gradient-to-r from-brand-plum-950 via-brand-plum-900 to-brand-plum-950 text-white rounded-3xl p-6 sm:p-8 border border-brand-blush-300/20 shadow-modal flex flex-col sm:flex-row items-center justify-between gap-6">
          <div className="flex items-center gap-4 text-center sm:text-left">
            <div className="w-16 h-16 rounded-full bg-brand-gold-500 text-brand-plum-950 font-serif text-2xl font-bold flex items-center justify-center shadow-md">
              {currentUser?.name ? currentUser.name.slice(0, 2).toUpperCase() : 'DC'}
            </div>
            <div>
              <div className="flex flex-wrap items-center gap-2 justify-center sm:justify-start">
                <h1 className="font-serif text-2xl sm:text-3xl font-bold">
                  {currentUser?.name || 'Demo Connoisseur'}
                </h1>
                <span className="text-[10px] uppercase font-bold tracking-wider px-2.5 py-0.5 rounded-full bg-brand-gold-100 text-brand-plum-950">
                  {currentUser?.tier || 'Privé Gold'}
                </span>
              </div>
              <p className="text-xs text-brand-blush-200 mt-1">
                {currentUser?.email || 'connoisseur@scentiva.com'} • Member since 2026 (Demo)
              </p>
            </div>
          </div>

          <div className="grid grid-cols-3 gap-3 text-center w-full sm:w-auto">
            <Link to="/account/orders" className="bg-white/10 hover:bg-white/15 px-4 py-2.5 rounded-2xl border border-white/10 transition-colors">
              <div className="text-lg font-bold font-serif">{orders.length}</div>
              <div className="text-[10px] text-brand-blush-200 uppercase">Orders</div>
            </Link>
            <Link to="/wishlist" className="bg-white/10 hover:bg-white/15 px-4 py-2.5 rounded-2xl border border-white/10 transition-colors">
              <div className="text-lg font-bold font-serif">{wishlist.length}</div>
              <div className="text-[10px] text-brand-blush-200 uppercase">Saved</div>
            </Link>
            <div className="bg-white/10 px-4 py-2.5 rounded-2xl border border-white/10">
              <div className="text-lg font-bold font-serif text-brand-gold-500">{currentUser?.points || 450}</div>
              <div className="text-[10px] text-brand-blush-200 uppercase">Scent Pts</div>
            </div>
          </div>
        </div>

        {/* Account Tabs & Content Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          {/* Navigation Sidebar */}
          <div className="lg:col-span-3 bg-white rounded-3xl p-4 border border-neutral-200 shadow-xs space-y-1">
            <button
              onClick={() => setActiveTab('orders')}
              className={`w-full flex items-center justify-between px-4 py-3 rounded-2xl text-xs font-semibold transition-all ${
                activeTab === 'orders'
                  ? 'bg-brand-plum-900 text-white shadow-xs'
                  : 'text-neutral-700 hover:bg-neutral-100'
              }`}
            >
              <span className="flex items-center gap-2">
                <Package className="w-4 h-4" />
                <span>My Orders ({orders.length})</span>
              </span>
              <ChevronRight className="w-4 h-4 opacity-70" />
            </button>

            <button
              onClick={() => setActiveTab('addresses')}
              className={`w-full flex items-center justify-between px-4 py-3 rounded-2xl text-xs font-semibold transition-all ${
                activeTab === 'addresses'
                  ? 'bg-brand-plum-900 text-white shadow-xs'
                  : 'text-neutral-700 hover:bg-neutral-100'
              }`}
            >
              <span className="flex items-center gap-2">
                <MapPin className="w-4 h-4" />
                <span>Saved Addresses ({addresses.length})</span>
              </span>
              <ChevronRight className="w-4 h-4 opacity-70" />
            </button>

            <button
              onClick={() => setActiveTab('rewards')}
              className={`w-full flex items-center justify-between px-4 py-3 rounded-2xl text-xs font-semibold transition-all ${
                activeTab === 'rewards'
                  ? 'bg-brand-plum-900 text-white shadow-xs'
                  : 'text-neutral-700 hover:bg-neutral-100'
              }`}
            >
              <span className="flex items-center gap-2">
                <Award className="w-4 h-4" />
                <span>Privé VIP Tier</span>
              </span>
              <ChevronRight className="w-4 h-4 opacity-70" />
            </button>

            <Link
              to="/wishlist"
              className="w-full flex items-center justify-between px-4 py-3 rounded-2xl text-xs font-semibold text-neutral-700 hover:bg-neutral-100 transition-all"
            >
              <span className="flex items-center gap-2">
                <Heart className="w-4 h-4" />
                <span>My Wishlist ({wishlist.length})</span>
              </span>
              <ChevronRight className="w-4 h-4 opacity-70" />
            </Link>

            <Link
              to="/help"
              className="w-full flex items-center justify-between px-4 py-3 rounded-2xl text-xs font-semibold text-neutral-700 hover:bg-neutral-100 transition-all"
            >
              <span className="flex items-center gap-2">
                <HelpCircle className="w-4 h-4" />
                <span>Help & Concierge</span>
              </span>
              <ChevronRight className="w-4 h-4 opacity-70" />
            </Link>

            <div className="pt-3 border-t border-neutral-100">
              <button
                onClick={() => {
                  signOut();
                  navigate('/account/sign-in');
                }}
                className="w-full flex items-center gap-2 px-4 py-3 rounded-2xl text-xs font-semibold text-semantic-error hover:bg-semantic-error/10 transition-colors"
              >
                <LogOut className="w-4 h-4" />
                <span>Sign Out Demo Session</span>
              </button>
            </div>
          </div>

          {/* Main Tab Content */}
          <div className="lg:col-span-9 space-y-6">
            {/* Orders Tab */}
            {activeTab === 'orders' && (
              <div className="space-y-4 animate-in fade-in duration-200">
                <div className="flex items-center justify-between">
                  <h3 className="font-serif text-xl font-bold text-neutral-900">Recent Purchases</h3>
                  <Link to="/account/orders" className="text-xs text-brand-plum-900 font-semibold hover:underline">
                    View Full Order History →
                  </Link>
                </div>

                {orders.length > 0 ? (
                  orders.slice(0, 3).map(order => (
                    <div
                      key={order.id}
                      className="bg-white rounded-3xl p-6 border border-neutral-200 shadow-xs space-y-4"
                    >
                      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-4 border-b border-neutral-100">
                        <div>
                          <div className="text-sm font-bold text-neutral-900 flex items-center gap-2">
                            <span>Order #{order.orderNumber}</span>
                            <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-brand-blush-100 text-brand-plum-900">
                              {order.status}
                            </span>
                          </div>
                          <div className="text-xs text-neutral-500 mt-0.5">
                            Placed on {new Date(order.createdAt).toLocaleDateString()} • {order.deliveryMethod}
                          </div>
                        </div>

                        <div className="text-left sm:text-right">
                          <div className="text-sm font-bold font-serif text-brand-plum-950 tabular-nums">
                            {formatPrice(order.total)}
                          </div>
                          <div className="text-[11px] text-neutral-400">
                            {order.items.length} {order.items.length === 1 ? 'item' : 'items'}
                          </div>
                        </div>
                      </div>

                      <div className="flex items-center justify-between text-xs pt-1">
                        <div className="text-neutral-600">
                          Tracking: <span className="font-mono font-semibold text-neutral-800">{order.trackingNumber}</span>
                        </div>
                        <Link
                          to={`/account/orders/${order.id}`}
                          className="px-4 py-2 rounded-xl bg-brand-plum-900 text-white font-semibold hover:bg-brand-plum-800 transition-colors shadow-xs"
                        >
                          Track Delivery
                        </Link>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="bg-white rounded-3xl p-8 text-center border border-neutral-200 text-xs text-neutral-500">
                    No orders placed yet.
                  </div>
                )}
              </div>
            )}

            {/* Addresses Tab */}
            {activeTab === 'addresses' && (
              <div className="space-y-4 animate-in fade-in duration-200">
                <div className="flex items-center justify-between">
                  <h3 className="font-serif text-xl font-bold text-neutral-900">Saved Addresses</h3>
                  <Link to="/account/addresses" className="text-xs text-brand-plum-900 font-semibold hover:underline">
                    Manage Address Book →
                  </Link>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {addresses.map(addr => (
                    <div
                      key={addr.id}
                      className="bg-white rounded-3xl p-6 border border-neutral-200 shadow-xs space-y-2"
                    >
                      <div className="flex items-center justify-between">
                        <span className="text-xs font-bold uppercase tracking-wider bg-neutral-100 px-2.5 py-0.5 rounded-full">
                          {addr.type}
                        </span>
                        {addr.isDefault && (
                          <span className="text-[10px] font-bold text-semantic-success">DEFAULT</span>
                        )}
                      </div>
                      <h4 className="text-sm font-bold text-neutral-900">{addr.fullName}</h4>
                      <p className="text-xs text-neutral-600 leading-relaxed">
                        {addr.addressLine1}, {addr.city}, {addr.state} - {addr.pincode}
                      </p>
                      <div className="text-xs text-neutral-500">Phone: {addr.phoneNumber}</div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Rewards Tab */}
            {activeTab === 'rewards' && (
              <div className="bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-xs space-y-6 animate-in fade-in duration-200">
                <div className="flex items-center justify-between pb-4 border-b border-neutral-100">
                  <div className="space-y-1">
                    <h3 className="font-serif text-2xl font-bold text-brand-plum-950">Privé Connoisseur Club</h3>
                    <p className="text-xs text-neutral-500">Earn 10 Scent Points per ₹1,000 spent on luxury flacons.</p>
                  </div>
                  <div className="text-right">
                    <div className="text-2xl font-serif font-bold text-brand-gold-500">{currentUser?.points || 450}</div>
                    <div className="text-[10px] text-neutral-400 uppercase">Available Points</div>
                  </div>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                  {[
                    { tier: 'Privé Bronze', req: 'Entry Level', perk: 'Complimentary 2ml sample with every order' },
                    { tier: 'Privé Gold', req: '₹50,000+ spend', perk: 'Free Express Air dispatch + velvet gift coffret', current: true },
                    { tier: 'Privé Diamond', req: '₹1,50,000+ spend', perk: 'Exclusive Masterclass invites & bespoke nose sessions' },
                  ].map(t => (
                    <div
                      key={t.tier}
                      className={`p-5 rounded-2xl border ${
                        t.current ? 'border-brand-gold-500 bg-brand-gold-100/30 shadow-xs' : 'border-neutral-200 bg-neutral-50'
                      }`}
                    >
                      <div className="text-xs font-bold text-brand-plum-950">{t.tier}</div>
                      <div className="text-[10px] text-neutral-500 mt-0.5">{t.req}</div>
                      <p className="text-xs text-neutral-700 mt-3">{t.perk}</p>
                      {t.current && (
                        <div className="text-[10px] font-bold text-brand-gold-500 mt-2 uppercase">Your Active Tier</div>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

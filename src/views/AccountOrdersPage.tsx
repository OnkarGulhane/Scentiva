'use client';

import React from 'react';
import { Link } from '@/components/common/Link';
import { useStore } from '../context/StoreContext';
import { Package, ChevronRight, Truck, ArrowLeft, Clock, ShieldCheck, FileText } from 'lucide-react';

export const AccountOrdersPage: React.FC = () => {
  const { orders, formatPrice } = useStore();

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Delivered':
        return 'bg-semantic-success/15 text-semantic-success border-semantic-success/30';
      case 'Out for Delivery':
        return 'bg-brand-gold-100 text-brand-plum-950 border-brand-gold-500/40';
      case 'Shipped':
      case 'Processing':
        return 'bg-brand-blush-100 text-brand-plum-900 border-brand-blush-300';
      default:
        return 'bg-neutral-100 text-neutral-700 border-neutral-300';
    }
  };

  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 space-y-6">
        {/* Navigation Breadcrumb */}
        <div className="flex items-center gap-2 text-xs text-neutral-500">
          <Link to="/account" className="hover:text-brand-plum-900 flex items-center gap-1 font-medium">
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Account Dashboard</span>
          </Link>
          <span>/</span>
          <span className="text-neutral-900 font-semibold">Order History</span>
        </div>

        {/* Page Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-neutral-200">
          <div>
            <h1 className="font-serif text-3xl font-bold text-brand-plum-950">
              My Orders & Shipments
            </h1>
            <p className="text-xs text-neutral-500">
              Track active dispatches and review previous luxury purchases.
            </p>
          </div>
          <Link
            to="/shop"
            className="px-5 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold hover:bg-brand-plum-800 transition-colors shadow-sm self-start sm:self-auto"
          >
            Explore Catalog
          </Link>
        </div>

        {/* Order Cards List */}
        {orders.length > 0 ? (
          <div className="space-y-4">
            {orders.map(order => (
              <div
                key={order.id}
                className="bg-white rounded-3xl p-6 border border-neutral-200 shadow-card hover:shadow-card-hover transition-all space-y-4"
              >
                {/* Header row */}
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-4 border-b border-neutral-100">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-2xl bg-brand-blush-100 text-brand-plum-900 flex items-center justify-center font-bold">
                      <Package className="w-5 h-5" />
                    </div>
                    <div>
                      <div className="text-sm font-bold text-neutral-900 flex items-center gap-2">
                        <span>Order #{order.orderNumber}</span>
                        <span className={`text-[10px] uppercase font-bold px-2.5 py-0.5 rounded-full border ${getStatusColor(order.status)}`}>
                          {order.status}
                        </span>
                      </div>
                      <div className="text-xs text-neutral-500 mt-0.5">
                        Placed on {new Date(order.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                      </div>
                    </div>
                  </div>

                  <div className="text-left sm:text-right">
                    <div className="text-sm font-bold font-serif text-brand-plum-950 tabular-nums">
                      {formatPrice(order.total)}
                    </div>
                    <div className="text-[11px] text-neutral-400">
                      {order.items.length} {order.items.length === 1 ? 'item' : 'items'} • {order.paymentMethod}
                    </div>
                  </div>
                </div>

                {/* Items Preview */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  {order.items.map((item, idx) => (
                    <div key={idx} className="flex items-center gap-3 bg-neutral-50 p-3 rounded-2xl border border-neutral-100">
                      <img
                        src={item.product.images[0]}
                        alt={item.product.name}
                        className="w-12 h-12 rounded-xl object-cover bg-white"
                      />
                      <div className="flex-1 min-w-0">
                        <div className="text-xs font-semibold text-neutral-900 truncate">{item.product.name}</div>
                        <div className="text-[11px] text-neutral-500">{item.product.brandName} • {item.variant.size} (Qty: {item.quantity})</div>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Footer Action */}
                <div className="pt-2 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 text-xs">
                  <div className="flex items-center gap-1.5 text-neutral-500">
                    <Truck className="w-4 h-4 text-brand-gold-500" />
                    <span>Tracking Number: <strong className="text-neutral-800 font-mono">{order.trackingNumber}</strong></span>
                  </div>

                  <div className="flex items-center gap-2 w-full sm:w-auto">
                    <Link
                      to={`/account/orders/${order.orderNumber || order.id}/invoice`}
                      className="flex-1 sm:flex-initial px-4 py-2.5 rounded-full border border-neutral-300 text-neutral-800 hover:bg-neutral-50 text-xs font-semibold flex items-center justify-center gap-1.5 transition-colors"
                    >
                      <FileText className="w-3.5 h-3.5 text-[#8C6B28]" />
                      <span>Tax Invoice</span>
                    </Link>
                    <Link
                      to={`/account/orders/${order.id}`}
                      className="flex-1 sm:flex-initial px-5 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold flex items-center justify-center gap-1.5 hover:bg-brand-plum-800 transition-colors shadow-xs"
                    >
                      <span>Track Shipment</span>
                      <ChevronRight className="w-3.5 h-3.5" />
                    </Link>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="bg-white rounded-3xl p-12 text-center border border-neutral-200 shadow-sm space-y-4">
            <div className="w-16 h-16 rounded-full bg-brand-blush-100 text-brand-plum-900 flex items-center justify-center mx-auto">
              <Package className="w-8 h-8" />
            </div>
            <h3 className="font-serif text-2xl font-bold text-neutral-900">No Orders Placed Yet</h3>
            <p className="text-xs text-neutral-500 max-w-sm mx-auto">
              Your luxury order history is currently empty. Explore our catalog and find your signature flacon.
            </p>
            <Link
              to="/shop"
              className="inline-block px-6 py-3 rounded-full bg-brand-plum-900 text-white text-xs font-semibold shadow-sm"
            >
              Browse Fragrance Catalog
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

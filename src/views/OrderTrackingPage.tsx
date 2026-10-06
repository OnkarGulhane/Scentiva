'use client';

import React from 'react';
import { useParams } from '@/hooks/useNavigation';
import { Link } from '@/components/common/Link';
import { useStore } from '../context/StoreContext';
import { OrderStatus } from '../types';
import { 
  CheckCircle2, 
  Clock, 
  Truck, 
  Package, 
  MapPin, 
  ShieldCheck, 
  ArrowLeft,
  Phone,
  Sparkles,
  FileText
} from 'lucide-react';

export const OrderTrackingPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { orders, updateOrderStatus, formatPrice } = useStore();

  const order = orders.find(o => o.id === id || o.orderNumber.toLowerCase() === id?.toLowerCase());

  if (!order) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center p-6 text-center">
        <h2 className="font-serif text-2xl font-bold text-neutral-900 mb-2">Order Tracking Unavailable</h2>
        <p className="text-xs text-neutral-500 mb-4">No matching order record was found.</p>
        <Link to="/account/orders" className="px-6 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold">
          View All Orders
        </Link>
      </div>
    );
  }

  const STAGES: { status: OrderStatus; label: string; desc: string }[] = [
    { status: 'Order Placed', label: 'Order Confirmed', desc: 'Verified and allocated from SCENTIVA central vault' },
    { status: 'Processing', label: 'Artisanal Packaging', desc: 'Inspected under UV illumination and sealed with wax stamp' },
    { status: 'Shipped', label: 'In Air Transit', desc: 'Dispatched via BlueDart Apex Air Express' },
    { status: 'Out for Delivery', label: 'Out for Delivery', desc: 'Courier associate en route to destination' },
    { status: 'Delivered', label: 'Delivered', desc: 'Handed over with signature verification' }
  ];

  // Helper to advance demo status
  const handleAdvanceStatus = () => {
    const currentIndex = STAGES.findIndex(s => s.status === order.status);
    if (currentIndex < STAGES.length - 1) {
      const nextStatus = STAGES[currentIndex + 1].status;
      updateOrderStatus(order.id, nextStatus);
    }
  };

  const getStageCompleted = (stageStatus: OrderStatus) => {
    const orderIndex = STAGES.findIndex(s => s.status === order.status);
    const stageIndex = STAGES.findIndex(s => s.status === stageStatus);
    return stageIndex <= orderIndex;
  };

  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
        {/* Navigation */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <Link
            to="/account/orders"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-neutral-600 hover:text-brand-plum-900 transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>Back to Orders</span>
          </Link>

          <div className="flex items-center gap-3">
            <Link
              to={`/account/orders/${order.orderNumber || order.id}/invoice`}
              className="inline-flex items-center gap-1.5 text-xs font-semibold px-4 py-1.5 rounded-full border border-neutral-300 bg-white hover:bg-neutral-50 text-neutral-800 shadow-xs transition-colors"
            >
              <FileText className="w-3.5 h-3.5 text-[#8C6B28]" />
              <span>View Tax Invoice</span>
            </Link>

            <span className="text-xs bg-brand-blush-100 text-brand-plum-900 font-bold px-3 py-1.5 rounded-full border border-brand-blush-300">
              {order.status}
            </span>
          </div>
        </div>

        {/* Tracking Header Box */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-xs space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-neutral-100 pb-4">
            <div>
              <span className="text-[11px] font-bold uppercase tracking-wider text-brand-rose-500 block">
                Air Waybill #{order.trackingNumber}
              </span>
              <h1 className="font-serif text-2xl sm:text-3xl font-bold text-neutral-900">
                Order {order.orderNumber}
              </h1>
            </div>

            <div className="sm:text-right">
              <span className="text-xs text-neutral-500 block">Estimated Arrival:</span>
              <span className="text-sm font-bold text-brand-plum-950 font-serif">
                {order.estimatedDelivery}
              </span>
            </div>
          </div>

          {/* 5-Stage Live Timeline */}
          <div className="py-6 relative">
            <div className="space-y-6 relative before:absolute before:inset-0 before:left-4 before:top-2 before:bottom-2 before:w-0.5 before:bg-neutral-200">
              {STAGES.map((stage, idx) => {
                const isDone = getStageCompleted(stage.status);
                const isCurrent = order.status === stage.status;

                return (
                  <div key={idx} className="relative flex items-start gap-4">
                    <div
                      className={`relative z-10 w-8 h-8 rounded-full flex items-center justify-center border-2 transition-all ${
                        isDone
                          ? 'bg-brand-plum-900 border-brand-plum-900 text-white shadow-sm'
                          : 'bg-white border-neutral-300 text-neutral-400'
                      }`}
                    >
                      {isDone ? <CheckCircle2 className="w-4 h-4" /> : <span className="text-xs font-bold">{idx + 1}</span>}
                    </div>

                    <div className="flex-1 space-y-0.5 pt-0.5">
                      <div className="flex items-center justify-between">
                        <h4 className={`text-sm font-bold ${isCurrent ? 'text-brand-plum-900' : isDone ? 'text-neutral-900' : 'text-neutral-400'}`}>
                          {stage.label}
                        </h4>
                        {isCurrent && (
                          <span className="text-[10px] font-bold uppercase tracking-wider bg-brand-gold-100 text-brand-plum-950 px-2 py-0.5 rounded border border-brand-gold-500/40 animate-pulse">
                            ACTIVE STAGE
                          </span>
                        )}
                      </div>
                      <p className="text-xs text-neutral-500">{stage.desc}</p>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Live Dispatch Tracker Controls */}
          <div className="p-4 rounded-2xl bg-brand-blush-100/40 border border-brand-blush-200 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs">
            <div>
              <span className="font-bold text-brand-plum-950 block">Live Courier Tracker Controls:</span>
              <p className="text-neutral-600">Simulate courier progression through all dispatch milestones.</p>
            </div>
            <button
              onClick={handleAdvanceStatus}
              disabled={order.status === 'Delivered'}
              className="px-4 py-2 rounded-xl bg-brand-plum-900 text-white font-semibold disabled:opacity-50 disabled:cursor-not-allowed hover:bg-brand-plum-800 transition-colors"
            >
              Advance Milestone →
            </button>
          </div>
        </div>

        {/* Delivery Courier Partner Card & Destination */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
          <div className="bg-white rounded-3xl p-6 border border-neutral-200 shadow-xs space-y-3">
            <div className="flex items-center gap-2 text-xs font-bold text-neutral-900 uppercase tracking-wide">
              <Truck className="w-4 h-4 text-brand-rose-500" />
              <span>Courier Partner Information</span>
            </div>
            <div className="space-y-1 text-xs text-neutral-600">
              <p><strong>Carrier:</strong> BlueDart Apex Air Express (Priority Thermal)</p>
              <p><strong>Tracking Number:</strong> {order.trackingNumber}</p>
              <p><strong>Package Type:</strong> Insulated Luxury Vault Coffret</p>
              <p><strong>Security Seal:</strong> Verified & Tamper-Evident</p>
            </div>
          </div>

          <div className="bg-white rounded-3xl p-6 border border-neutral-200 shadow-xs space-y-3">
            <div className="flex items-center gap-2 text-xs font-bold text-neutral-900 uppercase tracking-wide">
              <MapPin className="w-4 h-4 text-brand-rose-500" />
              <span>Shipping Address</span>
            </div>
            <div className="space-y-1 text-xs text-neutral-600">
              <p className="font-bold text-neutral-900">{order.shippingAddress.fullName}</p>
              <p>{order.shippingAddress.addressLine1}</p>
              <p>{order.shippingAddress.city}, {order.shippingAddress.state} - {order.shippingAddress.pincode}</p>
              <p>Phone: {order.shippingAddress.phoneNumber}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

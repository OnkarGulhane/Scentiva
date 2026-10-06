'use client';

import React, { useEffect } from 'react';
import { useSearchParams } from '@/hooks/useNavigation';
import { Link } from '@/components/common/Link';
import { useStore } from '../context/StoreContext';
import { CheckCircle2, Sparkles, Package, Truck, ArrowRight, ShieldCheck, FileText } from 'lucide-react';
import confetti from 'canvas-confetti';

export const OrderSuccessPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const { getOrderById, formatPrice } = useStore();

  const orderId = searchParams.get('id') || '';
  const order = getOrderById(orderId);

  useEffect(() => {
    confetti({
      particleCount: 120,
      spread: 90,
      origin: { y: 0.5 },
      colors: ['#E9B7D8', '#C7A66A', '#451333', '#B85B88']
    });
  }, []);

  if (!order) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center p-6 text-center">
        <h2 className="font-serif text-2xl font-bold text-neutral-900 mb-2">Order Confirmed</h2>
        <p className="text-xs text-neutral-500 mb-4">Your order has been logged in the SCENTIVA Vault.</p>
        <Link to="/account/orders" className="px-6 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold">
          View Order History
        </Link>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-neutral-50 py-12 lg:py-20">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
        {/* Success Hero Card */}
        <div className="bg-white rounded-3xl p-8 sm:p-12 border border-neutral-200/90 shadow-modal text-center space-y-5">
          <div className="w-20 h-20 rounded-full bg-brand-blush-100 text-brand-plum-900 mx-auto flex items-center justify-center animate-bounce duration-1000">
            <CheckCircle2 className="w-10 h-10 text-semantic-success" />
          </div>

          <div className="space-y-2">
            <span className="text-xs font-bold uppercase tracking-widest text-brand-rose-500">
              Order Placed Successfully
            </span>
            <h1 className="font-serif text-3xl sm:text-4xl font-bold text-brand-plum-950">
              Thank You For Your Connoisseur Order
            </h1>
            <p className="text-xs sm:text-sm text-neutral-600 max-w-md mx-auto">
              Your reference number is <strong className="text-brand-plum-900">{order.orderNumber}</strong>. A confirmation and authenticity certificate have been dispatched to your profile.
            </p>
          </div>

          {/* Action Buttons */}
          <div className="flex flex-col sm:flex-row items-center justify-center gap-3 pt-3">
            <Link
              to={`/account/orders/${order.orderNumber || order.id}/invoice`}
              className="w-full sm:w-auto px-6 py-3.5 rounded-full bg-[#C7A66A] hover:bg-[#b5955a] text-white text-xs font-semibold flex items-center justify-center gap-2 shadow-sm transition-all"
            >
              <FileText className="w-4 h-4" />
              <span>View Tax Invoice</span>
            </Link>
            <Link
              to={`/account/orders/${order.id}`}
              className="w-full sm:w-auto px-6 py-3.5 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold flex items-center justify-center gap-2 shadow-md transition-all"
            >
              <Truck className="w-4 h-4" />
              <span>Track Live Delivery</span>
            </Link>
            <Link
              to="/shop"
              className="w-full sm:w-auto px-6 py-3.5 rounded-full border border-neutral-300 text-neutral-800 hover:bg-neutral-100 text-xs font-semibold flex items-center justify-center gap-2 transition-all"
            >
              <span>Continue Shopping</span>
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>

        {/* Order Details Preview */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-xs space-y-6">
          <h3 className="font-serif text-xl font-bold text-neutral-900 pb-2 border-b border-neutral-100">
            Ordered Fragrances ({order.items.length})
          </h3>

          <div className="divide-y divide-neutral-100">
            {order.items.map((item, idx) => (
              <div key={idx} className="py-4 first:pt-0 last:pb-0 flex items-center justify-between gap-4">
                <div className="flex items-center gap-4">
                  <img
                    src={item.product.images[0]}
                    alt=""
                    className="w-16 h-16 rounded-xl object-cover bg-neutral-100 border border-neutral-200"
                  />
                  <div>
                    <span className="text-[10px] font-bold text-brand-rose-500 uppercase tracking-wider block">
                      {item.product.brandName}
                    </span>
                    <span className="font-serif text-base font-bold text-neutral-900 block">
                      {item.product.name}
                    </span>
                    <span className="text-xs text-neutral-500">
                      Volume: {item.variant.size} • Qty: {item.quantity}
                    </span>
                  </div>
                </div>

                <div className="text-right font-semibold text-brand-plum-950 tabular-nums">
                  {formatPrice(item.price * item.quantity)}
                </div>
              </div>
            ))}
          </div>

          <div className="pt-4 border-t border-neutral-100 grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs text-neutral-600">
            <div className="space-y-1">
              <span className="font-bold text-neutral-900 block">Shipping Destination:</span>
              <p>{order.shippingAddress.fullName}</p>
              <p>{order.shippingAddress.addressLine1}, {order.shippingAddress.city} - {order.shippingAddress.pincode}</p>
              <p>Phone: {order.shippingAddress.phoneNumber}</p>
            </div>

            <div className="space-y-1 sm:text-right">
              <span className="font-bold text-neutral-900 block">Payment Summary:</span>
              <p>Method: {order.paymentMethod}</p>
              <p>Delivery: {order.deliveryMethod}</p>
              <p className="text-sm font-bold text-brand-plum-950 pt-1">
                Grand Total: {formatPrice(order.total)}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

'use client';

import React, { useState } from 'react';
import { useStore } from '../../context/StoreContext';
import { OrderStatus } from '../../types';
import { ShoppingBag, CheckCircle2, Truck, Eye, Search, FileText, Trash2, ExternalLink, Loader2 } from 'lucide-react';
import { Link } from '@/components/common/Link';

export const AdminOrdersPage: React.FC = () => {
  const { orders, updateOrderStatus, dispatchShiprocket, deleteOrder, formatPrice } = useStore();
  const [selectedStatus, setSelectedStatus] = useState<string>('All');
  const [search, setSearch] = useState('');
  const [shippingLoadingId, setShippingLoadingId] = useState<string | null>(null);

  const STATUSES: (OrderStatus | 'All')[] = [
    'All',
    'Order Placed',
    'Processing',
    'Shipped',
    'Out for Delivery',
    'Delivered'
  ];

  const filteredOrders = orders.filter(o => {
    const matchesStatus = selectedStatus === 'All' || o.status === selectedStatus;
    const matchesSearch = o.orderNumber.toLowerCase().includes(search.toLowerCase()) ||
                          o.shippingAddress.fullName.toLowerCase().includes(search.toLowerCase());
    return matchesStatus && matchesSearch;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <span className="text-xs font-bold uppercase tracking-widest text-brand-rose-500">
            Fulfillment & Logistics
          </span>
          <h1 className="font-serif text-3xl font-bold text-neutral-900">
            Order Management ({orders.length})
          </h1>
        </div>
      </div>

      {/* Filter Tabs & Search */}
      <div className="bg-white p-4 rounded-2xl border border-neutral-200 shadow-xs flex flex-col md:flex-row items-center justify-between gap-4">
        <div className="flex items-center gap-1.5 overflow-x-auto w-full md:w-auto pb-1 md:pb-0">
          {STATUSES.map(status => (
            <button
              key={status}
              onClick={() => setSelectedStatus(status)}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
                selectedStatus === status
                  ? 'bg-brand-plum-900 text-white shadow-xs'
                  : 'bg-neutral-100 text-neutral-600 hover:bg-neutral-200'
              }`}
            >
              {status}
            </button>
          ))}
        </div>

        <div className="relative w-full md:w-64">
          <input
            type="text"
            placeholder="Search order or customer..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="w-full pl-9 pr-3 py-1.5 text-xs rounded-xl border border-neutral-300 focus:outline-none focus:border-brand-plum-900"
          />
          <Search className="w-4 h-4 text-neutral-400 absolute left-3 top-1/2 -translate-y-1/2" />
        </div>
      </div>

      {/* Orders Table */}
      <div className="bg-white rounded-3xl border border-neutral-200 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-neutral-50 text-neutral-500 font-semibold uppercase tracking-wider border-b border-neutral-200">
              <tr>
                <th className="py-3.5 px-4">Order ID</th>
                <th className="py-3.5 px-4">Customer & City</th>
                <th className="py-3.5 px-4">Date</th>
                <th className="py-3.5 px-4">Perfumes Ordered</th>
                <th className="py-3.5 px-4">Total Value</th>
                <th className="py-3.5 px-4">Status & Update</th>
                <th className="py-3.5 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-neutral-100">
              {filteredOrders.map(order => (
                <tr key={order.id} className="hover:bg-neutral-50/70 transition-colors">
                  <td className="py-3 px-4 font-mono font-bold text-brand-plum-950">
                    {order.orderNumber}
                  </td>
                  <td className="py-3 px-4">
                    <span className="font-bold text-neutral-900 block">{order.shippingAddress.fullName}</span>
                    <span className="text-[10px] text-neutral-500">{order.shippingAddress.city}, {order.shippingAddress.pincode}</span>
                  </td>
                  <td className="py-3 px-4 text-neutral-600">
                    {new Date(order.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                  </td>
                  <td className="py-3 px-4">
                    <div className="space-y-0.5">
                      {order.items.map((it, idx) => (
                        <div key={idx} className="text-[11px] text-neutral-700">
                          {it.product.name} ({it.variant.size}) × {it.quantity}
                        </div>
                      ))}
                    </div>
                  </td>
                  <td className="py-3 px-4 font-bold tabular-nums text-brand-plum-950">
                    {formatPrice(order.total)}
                  </td>
                  <td className="py-3 px-4">
                    <div className="space-y-1.5">
                      <select
                        value={order.status}
                        onChange={e => updateOrderStatus(order.id, e.target.value as OrderStatus)}
                        className="px-2.5 py-1 text-[11px] rounded-lg border border-neutral-300 font-semibold bg-brand-blush-100/50 text-brand-plum-950 cursor-pointer block w-full max-w-[140px]"
                      >
                        <option value="Order Placed">Order Placed</option>
                        <option value="Processing">Processing</option>
                        <option value="Shipped">Shipped</option>
                        <option value="Out for Delivery">Out for Delivery</option>
                        <option value="Delivered">Delivered</option>
                      </select>

                      {order.shippingProvider === 'SHIPROCKET' || order.trackingNumber?.startsWith('SR-') ? (
                        <div className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md bg-purple-50 text-[10px] text-purple-700 font-semibold border border-purple-200" title={`Shiprocket AWB: ${order.trackingNumber}`}>
                          <Truck className="w-3 h-3 text-purple-600" />
                          <span>Shiprocket AWB</span>
                        </div>
                      ) : (order.status === 'Order Placed' || order.status === 'Processing') ? (
                        <button
                          onClick={async () => {
                            setShippingLoadingId(order.id);
                            await dispatchShiprocket(order.id);
                            setShippingLoadingId(null);
                          }}
                          disabled={shippingLoadingId === order.id}
                          className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-gradient-to-r from-purple-700 to-brand-plum-900 hover:from-purple-800 hover:to-brand-plum-950 text-white text-[10px] font-bold shadow-xs transition-all active:scale-95 disabled:opacity-50"
                          title="Generate Shiprocket AWB & Dispatch Courier"
                        >
                          {shippingLoadingId === order.id ? (
                            <Loader2 className="w-3 h-3 animate-spin" />
                          ) : (
                            <Truck className="w-3 h-3 text-brand-gold-300" />
                          )}
                          <span>Ship via Shiprocket</span>
                        </button>
                      ) : null}
                    </div>
                  </td>
                  <td className="py-3 px-4 text-right">
                    <div className="flex items-center justify-end gap-2.5">
                      {order.trackingNumber?.startsWith('SR-') && (
                        <a
                          href={`https://shiprocket.co/tracking/${order.trackingNumber}`}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-flex items-center gap-1 text-[11px] text-purple-700 hover:text-purple-900 font-semibold"
                          title="Track on Shiprocket Logistics Hub"
                        >
                          <ExternalLink className="w-3 h-3" />
                          <span>Shiprocket</span>
                        </a>
                      )}
                      <Link
                        to={`/account/orders/${order.orderNumber || order.id}/invoice`}
                        className="inline-flex items-center gap-1 text-xs text-[#8C6B28] hover:text-[#72551e] font-semibold"
                        title="View / Print Tax Invoice"
                      >
                        <FileText className="w-3.5 h-3.5" />
                        <span>Invoice</span>
                      </Link>
                      <Link
                        to={`/account/orders/${order.id}`}
                        className="inline-flex items-center gap-1 text-xs text-brand-plum-900 font-semibold hover:underline"
                        title="Track Shipment Details"
                      >
                        <Eye className="w-3.5 h-3.5" />
                        <span>Track</span>
                      </Link>
                      <button
                        onClick={() => {
                          if (window.confirm(`Are you sure you want to delete and remove order "${order.orderNumber}"?`)) {
                            deleteOrder(order.id);
                          }
                        }}
                        className="inline-flex items-center gap-1 text-xs text-rose-600 hover:text-rose-800 font-semibold transition-colors"
                        title="Delete Order"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                        <span>Delete</span>
                      </button>
                    </div>
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

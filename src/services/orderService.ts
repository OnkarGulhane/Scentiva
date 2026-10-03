import { Order, OrderStatus, Address, CartItem } from '../types';

const ORDERS_STORAGE_KEY = 'scentiva_orders';

export const INITIAL_DEMO_ORDERS: Order[] = [];

export const getStoredOrders = (): Order[] => {
  if (typeof window === 'undefined') return [];
  try {
    const raw = localStorage.getItem(ORDERS_STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
};

export const saveStoredOrders = (orders: Order[]): void => {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(ORDERS_STORAGE_KEY, JSON.stringify(orders));
  } catch (err) {
    console.error('Failed to save orders to localStorage:', err);
  }
};

export const OrderService = {
  getAll: (): Order[] => {
    return getStoredOrders();
  },

  getById: (orderId: string): Order | undefined => {
    const all = getStoredOrders();
    const cleanId = orderId.trim().toLowerCase();
    return all.find(o => o.id.toLowerCase() === cleanId || o.orderNumber.toLowerCase() === cleanId);
  },

  create: (params: {
    cart: CartItem[];
    shippingAddress: Address;
    deliveryMethod: 'Standard Delivery' | 'Express Luxury Delivery';
    paymentMethod: 'UPI / QR' | 'Credit / Debit Card' | 'Net Banking' | 'Cash on Delivery';
    subtotal: number;
    discount: number;
    couponCode?: string;
    deliveryFee: number;
    total: number;
  }): Order => {
    const all = getStoredOrders();
    const orderNum = `SCT-${Math.floor(10000 + Math.random() * 90000)}`;
    const trackingNum = `SCT-TRK-${Math.floor(100000 + Math.random() * 900000)}`;

    const orderItems = params.cart.map(c => ({
      product: c.product,
      variant: c.selectedVariant,
      quantity: c.quantity,
      price: c.selectedVariant.price
    }));

    const newOrder: Order = {
      id: `ord-${Date.now()}`,
      orderNumber: orderNum,
      createdAt: new Date().toISOString(),
      items: orderItems,
      shippingAddress: params.shippingAddress,
      deliveryMethod: params.deliveryMethod,
      deliveryFee: params.deliveryFee,
      subtotal: params.subtotal,
      discount: params.discount,
      couponCode: params.couponCode,
      total: params.total,
      status: 'Order Placed',
      trackingNumber: trackingNum,
      estimatedDelivery: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
      }),
      paymentMethod: params.paymentMethod,
      paymentStatus: params.paymentMethod === 'Cash on Delivery' ? 'Pending' : 'Paid',
      timeline: [
        {
          status: 'Order Placed',
          timestamp: 'Just now',
          completed: true,
          description: 'Order confirmed and registered in SCENTIVA vault'
        },
        {
          status: 'Processing',
          timestamp: 'Pending',
          completed: false,
          description: 'Artisanal packaging and protective silk ribbon wrapping'
        },
        {
          status: 'Shipped',
          timestamp: 'Pending',
          completed: false,
          description: 'Handover to priority courier partner'
        },
        {
          status: 'Out for Delivery',
          timestamp: 'Pending',
          completed: false,
          description: 'Local courier escorting order to your doorstep'
        },
        {
          status: 'Delivered',
          timestamp: 'Pending',
          completed: false,
          description: 'Signature delivery completed'
        }
      ]
    };

    const updated = [newOrder, ...all];
    saveStoredOrders(updated);
    return newOrder;
  },

  updateStatus: (orderId: string, status: OrderStatus): Order | null => {
    const all = getStoredOrders();
    const index = all.findIndex(o => o.id === orderId || o.orderNumber === orderId);
    if (index === -1) return null;

    const currentOrder = all[index];
    const updatedTimeline = currentOrder.timeline.map(t => {
      if (t.status === status) {
        return { ...t, completed: true, timestamp: 'Updated by Admin' };
      }
      return t;
    });

    const updatedOrder: Order = {
      ...currentOrder,
      status,
      timeline: updatedTimeline
    };

    all[index] = updatedOrder;
    saveStoredOrders(all);
    return updatedOrder;
  }
};

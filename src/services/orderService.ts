import { Order, OrderStatus, Address, CartItem } from '../types';

const ORDERS_STORAGE_KEY = 'scentiva_orders';

export const INITIAL_DEMO_ORDERS: Order[] = [
  {
    id: 'ord-sct12345',
    orderNumber: 'SCT-12345',
    createdAt: '2026-09-24T10:30:00Z',
    items: [],
    shippingAddress: {
      id: 'addr-demo-1',
      fullName: 'Demo Connoisseur',
      phoneNumber: '+91 98765 43210',
      addressLine1: 'Villa 14, Royal Palm Residences',
      addressLine2: 'Koregaon Park',
      city: 'Pune',
      state: 'Maharashtra',
      pincode: '411001',
      type: 'Home',
      isDefault: true
    },
    deliveryMethod: 'Express Luxury Delivery',
    deliveryFee: 0,
    subtotal: 19498,
    discount: 2500,
    couponCode: 'WELCOME10',
    total: 16998,
    status: 'Delivered',
    trackingNumber: 'SCT-EXP-992144',
    estimatedDelivery: 'Sep 27, 2026',
    paymentMethod: 'UPI / QR',
    paymentStatus: 'Paid',
    timeline: [
      { status: 'Order Placed', timestamp: 'Sep 24, 2026 - 10:30 AM', completed: true, description: 'Order verified & registered in SCENTIVA vault' },
      { status: 'Processing', timestamp: 'Sep 24, 2026 - 02:15 PM', completed: true, description: 'Bottles inspected and sealed in velvet-lined gift box' },
      { status: 'Shipped', timestamp: 'Sep 25, 2026 - 09:20 AM', completed: true, description: 'Dispatched via Priority Air Express' },
      { status: 'Out for Delivery', timestamp: 'Sep 27, 2026 - 08:00 AM', completed: true, description: 'Courier associate out for white-glove delivery' },
      { status: 'Delivered', timestamp: 'Sep 27, 2026 - 03:45 PM', completed: true, description: 'Signature handoff completed' }
    ]
  },
  {
    id: 'ord-sct12344',
    orderNumber: 'SCT-12344',
    createdAt: '2026-09-28T14:15:00Z',
    items: [],
    shippingAddress: {
      id: 'addr-demo-1',
      fullName: 'Demo Connoisseur',
      phoneNumber: '+91 98765 43210',
      addressLine1: 'Villa 14, Royal Palm Residences',
      city: 'Pune',
      state: 'Maharashtra',
      pincode: '411001',
      type: 'Home',
      isDefault: true
    },
    deliveryMethod: 'Standard Delivery',
    deliveryFee: 0,
    subtotal: 10999,
    discount: 1000,
    couponCode: 'FIRSTSCENT',
    total: 9999,
    status: 'Out for Delivery',
    trackingNumber: 'SCT-STD-884102',
    estimatedDelivery: 'Sep 30, 2026',
    paymentMethod: 'Credit / Debit Card',
    paymentStatus: 'Paid',
    timeline: [
      { status: 'Order Placed', timestamp: 'Sep 28, 2026 - 02:15 PM', completed: true, description: 'Order placed & payment verified' },
      { status: 'Processing', timestamp: 'Sep 28, 2026 - 06:30 PM', completed: true, description: 'Artisanal gift wrapping sealed' },
      { status: 'Shipped', timestamp: 'Sep 29, 2026 - 08:00 AM', completed: true, description: 'In transit to local hub' },
      { status: 'Out for Delivery', timestamp: 'Sep 29, 2026 - 04:30 PM', completed: true, description: 'Out with delivery associate' },
      { status: 'Delivered', timestamp: 'Estimated Sep 30, 2026', completed: false, description: 'Pending recipient handoff' }
    ]
  }
];

export const getStoredOrders = (): Order[] => {
  if (typeof window === 'undefined') return INITIAL_DEMO_ORDERS;
  try {
    const raw = localStorage.getItem(ORDERS_STORAGE_KEY);
    if (!raw) return INITIAL_DEMO_ORDERS;
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) && parsed.length > 0 ? parsed : INITIAL_DEMO_ORDERS;
  } catch {
    return INITIAL_DEMO_ORDERS;
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

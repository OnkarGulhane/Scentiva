'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';
import { Product, ProductVariant, CartItem, Address, Order, Coupon, OrderStatus } from '../types';
import { ProductService } from '../services/productService';
import { OrderService } from '../services/orderService';
import { PromotionService } from '../services/promotionService';
import { PRODUCTS } from '../data/products';

export interface ToastState {
  id: string;
  message: string;
  type: 'success' | 'info' | 'warning' | 'error';
}

export interface DemoUser {
  id: string;
  name: string;
  email: string;
  tier: 'Privé Bronze' | 'Privé Silver' | 'Privé Gold' | 'Privé Diamond';
  points: number;
}

interface StoreContextType {
  // Demo Auth
  currentUser: DemoUser | null;
  isLoggedIn: boolean;
  signIn: (email: string, password?: string) => { success: boolean; message: string };
  signUp: (name: string, email: string, password?: string) => { success: boolean; message: string };
  signOut: () => void;

  // Products & Admin state
  products: Product[];
  addProduct: (product: Omit<Product, 'id' | 'slug'>) => void;
  updateProduct: (id: string, updated: Partial<Product>) => void;
  deleteProduct: (id: string) => void;

  // Cart
  cart: CartItem[];
  addToCart: (product: Product, variant?: ProductVariant, quantity?: number) => void;
  removeFromCart: (productId: string, variantSku: string) => void;
  updateCartQuantity: (productId: string, variantSku: string, quantity: number) => void;
  clearCart: () => void;
  cartCount: number;
  cartSubtotal: number;
  cartDiscount: number;
  appliedCoupon: Coupon | null;
  applyCoupon: (code: string) => { success: boolean; message: string };
  removeCoupon: () => void;
  cartDeliveryFee: number;
  cartTotal: number;
  isCartDrawerOpen: boolean;
  setIsCartDrawerOpen: (open: boolean) => void;

  // Wishlist
  wishlist: Product[];
  toggleWishlist: (product: Product) => void;
  isInWishlist: (productId: string) => boolean;
  clearWishlist: () => void;

  // Addresses
  addresses: Address[];
  selectedAddress: Address | null;
  setSelectedAddress: (address: Address) => void;
  addAddress: (address: Omit<Address, 'id'>) => Address;
  updateAddress: (id: string, address: Partial<Address>) => void;
  deleteAddress: (id: string) => void;

  // Orders
  orders: Order[];
  placeOrder: (orderData: {
    shippingAddress: Address;
    deliveryMethod: 'Standard Delivery' | 'Express Luxury Delivery';
    paymentMethod: 'UPI / QR' | 'Credit / Debit Card' | 'Net Banking' | 'Cash on Delivery';
  }) => Order;
  getOrderById: (orderId: string) => Order | undefined;
  updateOrderStatus: (orderId: string, status: OrderStatus) => void;

  // Quick View Modal
  quickViewProduct: Product | null;
  setQuickViewProduct: (product: Product | null) => void;

  // Toast
  toasts: ToastState[];
  showToast: (message: string, type?: 'success' | 'info' | 'warning' | 'error') => void;
  removeToast: (id: string) => void;

  // Utilities
  formatPrice: (amount: number) => string;
}

const StoreContext = createContext<StoreContextType | undefined>(undefined);

// Safe parsing helper with fallback
function safeGetStorage<T>(key: string, fallback: T): T {
  if (typeof window === 'undefined') return fallback;
  try {
    const item = localStorage.getItem(key);
    if (!item) return fallback;
    const parsed = JSON.parse(item);
    return parsed !== undefined && parsed !== null ? parsed : fallback;
  } catch (err) {
    console.warn(`Error reading localStorage key "${key}", using fallback:`, err);
    return fallback;
  }
}

const INITIAL_DEMO_USER: DemoUser = {
  id: 'usr-demo-1',
  name: 'Demo Connoisseur',
  email: 'connoisseur@scentiva.com',
  tier: 'Privé Gold',
  points: 450
};

const INITIAL_ADDRESSES: Address[] = [
  {
    id: 'addr-demo-1',
    fullName: 'Demo Connoisseur',
    phoneNumber: '+91 98765 43210',
    addressLine1: 'Villa 14, Royal Palm Residences',
    addressLine2: 'Koregaon Park Road',
    city: 'Pune',
    state: 'Maharashtra',
    pincode: '411001',
    type: 'Home',
    isDefault: true
  },
  {
    id: 'addr-demo-2',
    fullName: 'Demo Connoisseur',
    phoneNumber: '+91 98765 43210',
    addressLine1: 'Level 7, Cyber Tower Alpha',
    addressLine2: 'Hinjawadi Phase 1',
    city: 'Pune',
    state: 'Maharashtra',
    pincode: '411057',
    type: 'Office',
    isDefault: false
  }
];

export const StoreProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Auth state
  const [currentUser, setCurrentUser] = useState<DemoUser | null>(() => {
    return safeGetStorage<DemoUser | null>('scentiva_user', INITIAL_DEMO_USER);
  });

  // Products state
  const [products, setProducts] = useState<Product[]>(() => {
    return ProductService.getAll();
  });

  // Cart state
  const [cart, setCart] = useState<CartItem[]>(() => {
    return safeGetStorage<CartItem[]>('scentiva_cart', [
      {
        productId: PRODUCTS[0].id,
        product: PRODUCTS[0],
        selectedVariant: PRODUCTS[0].variants[1],
        quantity: 1
      },
      {
        productId: PRODUCTS[1].id,
        product: PRODUCTS[1],
        selectedVariant: PRODUCTS[1].variants[1],
        quantity: 1
      }
    ]);
  });

  // Wishlist state
  const [wishlist, setWishlist] = useState<Product[]>(() => {
    return safeGetStorage<Product[]>('scentiva_wishlist', [PRODUCTS[2], PRODUCTS[4]]);
  });

  // Addresses state
  const [addresses, setAddresses] = useState<Address[]>(() => {
    return safeGetStorage<Address[]>('scentiva_addresses', INITIAL_ADDRESSES);
  });

  const [selectedAddress, setSelectedAddress] = useState<Address | null>(() => {
    return addresses.find(a => a.isDefault) || addresses[0] || null;
  });

  // Orders state
  const [orders, setOrders] = useState<Order[]>(() => {
    return OrderService.getAll();
  });

  // Coupon state
  const [appliedCoupon, setAppliedCoupon] = useState<Coupon | null>(() => {
    return safeGetStorage<Coupon | null>('scentiva_coupon', null);
  });

  const [isCartDrawerOpen, setIsCartDrawerOpen] = useState<boolean>(false);
  const [quickViewProduct, setQuickViewProduct] = useState<Product | null>(null);
  const [toasts, setToasts] = useState<ToastState[]>([]);

  // Persistent LocalStorage synchronization
  useEffect(() => {
    try {
      localStorage.setItem('scentiva_user', JSON.stringify(currentUser));
    } catch (e) {
      console.error(e);
    }
  }, [currentUser]);

  useEffect(() => {
    try {
      localStorage.setItem('scentiva_cart', JSON.stringify(cart));
    } catch (e) {
      console.error(e);
    }
  }, [cart]);

  useEffect(() => {
    try {
      localStorage.setItem('scentiva_wishlist', JSON.stringify(wishlist));
    } catch (e) {
      console.error(e);
    }
  }, [wishlist]);

  useEffect(() => {
    try {
      localStorage.setItem('scentiva_addresses', JSON.stringify(addresses));
    } catch (e) {
      console.error(e);
    }
  }, [addresses]);

  useEffect(() => {
    try {
      localStorage.setItem('scentiva_orders', JSON.stringify(orders));
    } catch (e) {
      console.error(e);
    }
  }, [orders]);

  useEffect(() => {
    try {
      if (appliedCoupon) {
        localStorage.setItem('scentiva_coupon', JSON.stringify(appliedCoupon));
      } else {
        localStorage.removeItem('scentiva_coupon');
      }
    } catch (e) {
      console.error(e);
    }
  }, [appliedCoupon]);

  // Toast Helpers
  const showToast = (message: string, type: 'success' | 'info' | 'warning' | 'error' = 'success') => {
    const id = Math.random().toString(36).substring(2, 9);
    setToasts(prev => [...prev, { id, message, type }]);
    setTimeout(() => {
      removeToast(id);
    }, 3800);
  };

  const removeToast = (id: string) => {
    setToasts(prev => prev.filter(t => t.id !== id));
  };

  // Currency Formatter with standard Indian Rupee formatting
  const formatPrice = (amount: number): string => {
    return `₹${Math.round(amount).toLocaleString('en-IN')}`;
  };

  // Auth Operations
  const signIn = (email: string) => {
    const demoUser: DemoUser = {
      id: `usr-${Date.now()}`,
      name: email.split('@')[0].replace(/[^a-zA-Z]/g, ' ') || 'Demo Connoisseur',
      email: email.trim(),
      tier: 'Privé Gold',
      points: 500
    };
    setCurrentUser(demoUser);
    showToast(`Welcome back, ${demoUser.name}! (Demo Sign-in)`, 'success');
    return { success: true, message: 'Signed in successfully' };
  };

  const signUp = (name: string, email: string) => {
    const newUser: DemoUser = {
      id: `usr-${Date.now()}`,
      name: name.trim() || 'Demo Member',
      email: email.trim(),
      tier: 'Privé Bronze',
      points: 100
    };
    setCurrentUser(newUser);
    showToast(`Welcome to SCENTIVA Privé, ${newUser.name}! (Demo Sign-up)`, 'success');
    return { success: true, message: 'Account created successfully' };
  };

  const signOut = () => {
    setCurrentUser(null);
    showToast('Signed out of demo session', 'info');
  };

  // Cart Actions
  const addToCart = (product: Product, variant?: ProductVariant, quantity: number = 1) => {
    const targetVariant = variant || product.variants[0];
    setCart(prev => {
      const existingIndex = prev.findIndex(
        item => item.productId === product.id && item.selectedVariant.sku === targetVariant.sku
      );

      if (existingIndex > -1) {
        const updated = [...prev];
        const newQty = Math.min(10, updated[existingIndex].quantity + quantity);
        updated[existingIndex].quantity = newQty;
        return updated;
      } else {
        return [
          ...prev,
          {
            productId: product.id,
            product,
            selectedVariant: targetVariant,
            quantity: Math.min(10, Math.max(1, quantity))
          }
        ];
      }
    });

    showToast(`Added ${product.name} (${targetVariant.size}) to your cart!`, 'success');
  };

  const removeFromCart = (productId: string, variantSku: string) => {
    setCart(prev => prev.filter(item => !(item.productId === productId && item.selectedVariant.sku === variantSku)));
    showToast('Item removed from cart', 'info');
  };

  const updateCartQuantity = (productId: string, variantSku: string, quantity: number) => {
    if (quantity <= 0) {
      removeFromCart(productId, variantSku);
      return;
    }
    const safeQty = Math.min(10, quantity);
    setCart(prev =>
      prev.map(item =>
        item.productId === productId && item.selectedVariant.sku === variantSku
          ? { ...item, quantity: safeQty }
          : item
      )
    );
  };

  const clearCart = () => {
    setCart([]);
    setAppliedCoupon(null);
  };

  // Cart Calculations
  const cartCount = cart.reduce((total, item) => total + item.quantity, 0);
  const cartSubtotal = cart.reduce((total, item) => total + (item.selectedVariant.price * item.quantity), 0);

  let cartDiscount = 0;
  if (appliedCoupon) {
    if (appliedCoupon.discountType === 'percentage') {
      cartDiscount = Math.round((cartSubtotal * appliedCoupon.discountValue) / 100);
    } else {
      cartDiscount = appliedCoupon.discountValue;
    }
  }

  // Free delivery threshold: above ₹999
  const cartDeliveryFee = cartSubtotal === 0 || cartSubtotal >= 999 ? 0 : 199;
  const cartTotal = Math.max(0, cartSubtotal - cartDiscount + cartDeliveryFee);

  const applyCoupon = (code: string) => {
    const res = PromotionService.validate(code, cartSubtotal);
    if (!res.valid || !res.coupon) {
      showToast(res.message, 'warning');
      return { success: false, message: res.message };
    }
    setAppliedCoupon(res.coupon);
    showToast(res.message, 'success');
    return { success: true, message: res.message };
  };

  const removeCoupon = () => {
    setAppliedCoupon(null);
    showToast('Coupon removed', 'info');
  };

  // Wishlist Actions
  const toggleWishlist = (product: Product) => {
    setWishlist(prev => {
      const exists = prev.some(item => item.id === product.id);
      if (exists) {
        showToast(`Removed ${product.name} from Wishlist`, 'info');
        return prev.filter(item => item.id !== product.id);
      } else {
        showToast(`Saved ${product.name} to Wishlist`, 'success');
        return [...prev, product];
      }
    });
  };

  const isInWishlist = (productId: string) => {
    return wishlist.some(item => item.id === productId);
  };

  const clearWishlist = () => {
    setWishlist([]);
  };

  // Address CRUD
  const addAddress = (addrData: Omit<Address, 'id'>): Address => {
    const newAddr: Address = {
      ...addrData,
      id: `addr-${Date.now()}`
    };
    if (newAddr.isDefault) {
      setAddresses(prev => prev.map(a => ({ ...a, isDefault: false })).concat(newAddr));
      setSelectedAddress(newAddr);
    } else {
      setAddresses(prev => [...prev, newAddr]);
      if (!selectedAddress) setSelectedAddress(newAddr);
    }
    showToast('New shipping address saved!', 'success');
    return newAddr;
  };

  const updateAddress = (id: string, updated: Partial<Address>) => {
    setAddresses(prev =>
      prev.map(a => {
        if (a.id === id) {
          const updatedAddr = { ...a, ...updated };
          if (updated.isDefault) {
            setSelectedAddress(updatedAddr);
          }
          return updatedAddr;
        }
        return updated.isDefault ? { ...a, isDefault: false } : a;
      })
    );
    showToast('Address updated successfully', 'success');
  };

  const deleteAddress = (id: string) => {
    setAddresses(prev => prev.filter(a => a.id !== id));
    if (selectedAddress?.id === id) {
      const remaining = addresses.filter(a => a.id !== id);
      setSelectedAddress(remaining[0] || null);
    }
    showToast('Address removed', 'info');
  };

  // Orders Management
  const placeOrder = (orderData: {
    shippingAddress: Address;
    deliveryMethod: 'Standard Delivery' | 'Express Luxury Delivery';
    paymentMethod: 'UPI / QR' | 'Credit / Debit Card' | 'Net Banking' | 'Cash on Delivery';
  }): Order => {
    const fee = orderData.deliveryMethod === 'Express Luxury Delivery' ? 199 : 0;
    const finalTotal = cartTotal + fee;

    const newOrder = OrderService.create({
      cart,
      shippingAddress: orderData.shippingAddress,
      deliveryMethod: orderData.deliveryMethod,
      paymentMethod: orderData.paymentMethod,
      subtotal: cartSubtotal,
      discount: cartDiscount,
      couponCode: appliedCoupon?.code,
      deliveryFee: fee,
      total: finalTotal
    });

    setOrders(prev => [newOrder, ...prev]);
    clearCart();
    return newOrder;
  };

  const getOrderById = (orderId: string) => {
    return orders.find(o => o.id === orderId || o.orderNumber.toLowerCase() === orderId.toLowerCase()) || OrderService.getById(orderId);
  };

  const updateOrderStatus = (orderId: string, status: OrderStatus) => {
    const updated = OrderService.updateStatus(orderId, status);
    if (updated) {
      setOrders(prev => prev.map(o => (o.id === orderId || o.orderNumber === orderId ? updated : o)));
      showToast(`Order status updated to "${status}"`, 'info');
    }
  };

  // Admin Products State
  const addProduct = (prodData: Omit<Product, 'id' | 'slug'>) => {
    const created = ProductService.create(prodData);
    setProducts(prev => [created, ...prev]);
    showToast(`Added "${created.name}" to store catalog!`, 'success');
  };

  const updateProduct = (id: string, updated: Partial<Product>) => {
    const res = ProductService.update(id, updated);
    if (res) {
      setProducts(prev => prev.map(p => (p.id === id ? res : p)));
      showToast('Product updated successfully', 'success');
    }
  };

  const deleteProduct = (id: string) => {
    ProductService.delete(id);
    setProducts(prev => prev.filter(p => p.id !== id));
    showToast('Product removed from catalog', 'info');
  };

  return (
    <StoreContext.Provider
      value={{
        currentUser,
        isLoggedIn: !!currentUser,
        signIn,
        signUp,
        signOut,
        products,
        addProduct,
        updateProduct,
        deleteProduct,
        cart,
        addToCart,
        removeFromCart,
        updateCartQuantity,
        clearCart,
        cartCount,
        cartSubtotal,
        cartDiscount,
        appliedCoupon,
        applyCoupon,
        removeCoupon,
        cartDeliveryFee,
        cartTotal,
        isCartDrawerOpen,
        setIsCartDrawerOpen,
        wishlist,
        toggleWishlist,
        isInWishlist,
        clearWishlist,
        addresses,
        selectedAddress,
        setSelectedAddress,
        addAddress,
        updateAddress,
        deleteAddress,
        orders,
        placeOrder,
        getOrderById,
        updateOrderStatus,
        quickViewProduct,
        setQuickViewProduct,
        toasts,
        showToast,
        removeToast,
        formatPrice
      }}
    >
      {children}
    </StoreContext.Provider>
  );
};

export const useStore = () => {
  const context = useContext(StoreContext);
  if (!context) {
    throw new Error('useStore must be used within a StoreProvider');
  }
  return context;
};

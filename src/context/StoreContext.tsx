'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';
import { Product, ProductVariant, CartItem, Address, Order, Coupon, OrderStatus } from '../types';
import { ProductService } from '../services/productService';
import { OrderService, INITIAL_DEMO_ORDERS } from '../services/orderService';
import { PromotionService } from '../services/promotionService';
import { AuthApiService } from '../services/authApiService';
import { AddressApiService } from '../services/addressApiService';
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
  role?: string;
  userId?: number;
  customerId?: number;
}

interface StoreContextType {
  // Hydration state
  isHydrated: boolean;

  // Auth
  currentUser: DemoUser | null;
  isLoggedIn: boolean;
  signIn: (email: string, password?: string) => Promise<{ success: boolean; message: string }>;
  signUp: (name: string, email: string, password?: string) => Promise<{ success: boolean; message: string }>;
  loginWithGoogle: (idToken: string) => Promise<{ success: boolean; message: string }>;
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
  mergeGuestCartItems: (guestItems: CartItem[]) => void;
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
    paymentMethod: 'Razorpay Secure (UPI, Cards, NetBanking)' | 'Razorpay' | 'UPI / QR' | 'Credit / Debit Card' | 'Net Banking' | 'Cash on Delivery' | string;
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

const INITIAL_ADDRESSES: Address[] = [
  {
    id: 'addr-default-1',
    fullName: 'Omkar Gulhane',
    phoneNumber: '+91 98765 43210',
    addressLine1: 'Bungalow 7, Koregaon Park North Main Road',
    city: 'Pune',
    state: 'Maharashtra',
    pincode: '411001',
    type: 'Home',
    isDefault: true
  }
];

const INITIAL_DEMO_USER: DemoUser = {
  id: 'usr-omkar',
  name: 'Omkar Gulhane',
  email: 'omkar@scentiva.com',
  tier: 'Privé Gold',
  points: 1250,
  role: 'ROLE_CUSTOMER'
};

const INITIAL_WISHLIST: Product[] = [PRODUCTS[2], PRODUCTS[4]];

export const StoreProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isHydrated, setIsHydrated] = useState<boolean>(false);

  // Auth state: deterministic default for SSR
  const [currentUser, setCurrentUser] = useState<DemoUser | null>(INITIAL_DEMO_USER);

  // Products state (Canonical Storefront Data)
  const [products, setProducts] = useState<Product[]>(PRODUCTS);

  // Cart state: deterministic default for SSR
  const [cart, setCart] = useState<CartItem[]>([]);

  // Wishlist state: deterministic default for SSR
  const [wishlist, setWishlist] = useState<Product[]>(INITIAL_WISHLIST);

  // Addresses state: deterministic default for SSR
  const [addresses, setAddresses] = useState<Address[]>(INITIAL_ADDRESSES);
  const [selectedAddress, setSelectedAddress] = useState<Address | null>(INITIAL_ADDRESSES[0]);

  // Orders state: deterministic default for SSR
  const [orders, setOrders] = useState<Order[]>(INITIAL_DEMO_ORDERS);

  // Coupon state: deterministic default for SSR
  const [appliedCoupon, setAppliedCoupon] = useState<Coupon | null>(null);

  const [isCartDrawerOpen, setIsCartDrawerOpen] = useState<boolean>(false);
  const [quickViewProduct, setQuickViewProduct] = useState<Product | null>(null);
  const [toasts, setToasts] = useState<ToastState[]>([]);

  const loadUserAddresses = async () => {
    try {
      const backendAddrs = await AddressApiService.getAddresses();
      if (Array.isArray(backendAddrs) && backendAddrs.length > 0) {
        setAddresses(backendAddrs);
        const def = backendAddrs.find(a => a.isDefault) || backendAddrs[0] || null;
        setSelectedAddress(def);
        return;
      }
    } catch (e) {
      console.warn('Could not load user addresses from API:', e);
    }
  };

  // Client-side hydration & background auth verification
  useEffect(() => {
    try {
      // 1. Hydrate user
      const storedUser = safeGetStorage<DemoUser | null>('scentiva_user', INITIAL_DEMO_USER);
      if (storedUser) {
        setCurrentUser(storedUser);
      }

      // 2. Hydrate products if stored
      const storedProducts = safeGetStorage<Product[] | null>('scentiva_products', null);
      if (storedProducts && storedProducts.length > 0) {
        setProducts(storedProducts);
      }

      // 3. Hydrate cart
      const storedCart = safeGetStorage<CartItem[]>('scentiva_cart', []);
      if (storedCart && storedCart.length > 0) {
        setCart(storedCart);
      }

      // 4. Hydrate wishlist
      const storedWishlist = safeGetStorage<Product[]>('scentiva_wishlist', INITIAL_WISHLIST);
      if (storedWishlist) {
        setWishlist(storedWishlist);
      }

      // 5. Hydrate addresses
      const storedAddresses = safeGetStorage<Address[]>('scentiva_addresses', INITIAL_ADDRESSES);
      if (storedAddresses && storedAddresses.length > 0) {
        setAddresses(storedAddresses);
        const def = storedAddresses.find(a => a.isDefault) || storedAddresses[0] || null;
        setSelectedAddress(def);
      }

      // 6. Hydrate orders
      const storedOrders = safeGetStorage<Order[]>('scentiva_orders', INITIAL_DEMO_ORDERS);
      if (storedOrders) {
        setOrders(storedOrders);
      }

      // 7. Hydrate coupon
      const storedCoupon = safeGetStorage<Coupon | null>('scentiva_coupon', null);
      if (storedCoupon) {
        setAppliedCoupon(storedCoupon);
      }

      // 8. Background token verification & sync
      const token = typeof window !== 'undefined' ? (localStorage.getItem('scentiva_auth_token') || localStorage.getItem('scentiva_token')) : null;
      if (token && storedUser) {
        AuthApiService.getMe()
          .then(profile => {
            if (profile) {
              const fullName = profile.fullName || `${profile.firstName || ''} ${profile.lastName || ''}`.trim() || storedUser.name;
              setCurrentUser(prev => prev ? {
                ...prev,
                name: fullName,
                email: profile.email || prev.email,
                role: profile.role || prev.role,
                userId: profile.userId || profile.id,
                customerId: profile.customerId
              } : null);
            }
          })
          .catch((err: any) => {
            console.warn('Background token check note:', err?.message);
          });

        loadUserAddresses();
      }
    } catch (err) {
      console.warn('Error during client hydration:', err);
    } finally {
      setIsHydrated(true);
    }
  }, []);

  // Persistent LocalStorage synchronization (only after hydration)
  useEffect(() => {
    if (!isHydrated) return;
    try {
      if (currentUser) {
        localStorage.setItem('scentiva_user', JSON.stringify(currentUser));
      } else {
        localStorage.removeItem('scentiva_user');
      }
    } catch (e) {
      console.error(e);
    }
  }, [currentUser, isHydrated]);

  useEffect(() => {
    if (!isHydrated) return;
    try {
      if (products && products.length > 0) {
        localStorage.setItem('scentiva_products', JSON.stringify(products));
      }
    } catch (e) {
      console.error(e);
    }
  }, [products, isHydrated]);

  useEffect(() => {
    if (!isHydrated) return;
    try {
      localStorage.setItem('scentiva_cart', JSON.stringify(cart));
    } catch (e) {
      console.error(e);
    }
  }, [cart, isHydrated]);

  useEffect(() => {
    if (!isHydrated) return;
    try {
      localStorage.setItem('scentiva_wishlist', JSON.stringify(wishlist));
    } catch (e) {
      console.error(e);
    }
  }, [wishlist, isHydrated]);

  useEffect(() => {
    if (!isHydrated) return;
    try {
      localStorage.setItem('scentiva_addresses', JSON.stringify(addresses));
    } catch (e) {
      console.error(e);
    }
  }, [addresses, isHydrated]);

  useEffect(() => {
    if (!isHydrated) return;
    try {
      localStorage.setItem('scentiva_orders', JSON.stringify(orders));
    } catch (e) {
      console.error(e);
    }
  }, [orders, isHydrated]);

  useEffect(() => {
    if (!isHydrated) return;
    try {
      if (appliedCoupon) {
        localStorage.setItem('scentiva_coupon', JSON.stringify(appliedCoupon));
      } else {
        localStorage.removeItem('scentiva_coupon');
      }
    } catch (e) {
      console.error(e);
    }
  }, [appliedCoupon, isHydrated]);

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

  // Safe Cart Merge Helper (Preserves guest items and combines duplicates safely)
  const mergeGuestCartItems = (guestItems: CartItem[]) => {
    if (!guestItems || guestItems.length === 0) return;

    setCart(prev => {
      const merged = [...prev];
      for (const item of guestItems) {
        const existingIdx = merged.findIndex(
          m => m.productId === item.productId && m.selectedVariant.sku === item.selectedVariant.sku
        );
        const availableStock = typeof item.product.stock === 'number' ? item.product.stock : 10;

        if (existingIdx > -1) {
          const combinedQty = Math.min(availableStock, merged[existingIdx].quantity + item.quantity);
          merged[existingIdx] = {
            ...merged[existingIdx],
            quantity: combinedQty
          };
        } else {
          merged.push({
            ...item,
            quantity: Math.min(availableStock, item.quantity)
          });
        }
      }
      return merged;
    });
  };

  // Auth Operations with Cart Preservation & Real Backend Integration
  const signIn = async (email: string, password?: string): Promise<{ success: boolean; message: string }> => {
    const cleanEmail = email.trim().toLowerCase();
    if (!cleanEmail) {
      throw new Error('Please enter a valid email address.');
    }

    let userName = cleanEmail.split('@')[0].replace(/[^a-zA-Z]/g, ' ') || 'Connoisseur Member';
    let tier: 'Privé Bronze' | 'Privé Silver' | 'Privé Gold' | 'Privé Diamond' = 'Privé Gold';
    let role = 'ROLE_CUSTOMER';
    let userId: number | undefined = undefined;
    let customerId: number | undefined = undefined;

    if (password) {
      const backendRes = await AuthApiService.login({ email: cleanEmail, password });
      if (backendRes) {
        const profile = backendRes.user;
        if (profile) {
          const fullName = profile.fullName || `${profile.firstName || ''} ${profile.lastName || ''}`.trim();
          if (fullName) userName = fullName;
          if (profile.role) role = profile.role;
          userId = profile.userId || profile.id;
          customerId = profile.customerId;
          if (profile.loyaltyTier) {
            if (profile.loyaltyTier.includes('DIAMOND')) tier = 'Privé Diamond';
            else if (profile.loyaltyTier.includes('GOLD')) tier = 'Privé Gold';
            else if (profile.loyaltyTier.includes('SILVER')) tier = 'Privé Silver';
            else tier = 'Privé Bronze';
          }
        } else {
          const combinedName = `${backendRes.firstName || ''} ${backendRes.lastName || ''}`.trim();
          if (combinedName) userName = combinedName;
          if (backendRes.role) role = backendRes.role;
          userId = backendRes.userId;
        }
      }
    }

    const authenticatedUser: DemoUser = {
      id: userId ? String(userId) : `usr-${Date.now()}`,
      name: userName,
      email: cleanEmail,
      tier,
      points: 500,
      role,
      userId,
      customerId
    };

    setCurrentUser(authenticatedUser);
    await loadUserAddresses();
    showToast(`Welcome back, ${authenticatedUser.name}!`, 'success');
    return { success: true, message: 'Signed in successfully' };
  };

  const signUp = async (name: string, email: string, password?: string): Promise<{ success: boolean; message: string }> => {
    const cleanName = name.trim();
    if (!cleanName) {
      throw new Error('Please enter your full name.');
    }
    const cleanEmail = email.trim().toLowerCase();
    if (!cleanEmail) {
      throw new Error('Please enter a valid email address.');
    }

    const nameParts = cleanName.split(' ');
    const firstName = nameParts[0] || 'Connoisseur';
    const lastName = nameParts.slice(1).join(' ').trim() || nameParts[0];

    let role = 'ROLE_CUSTOMER';
    let userId: number | undefined = undefined;
    let customerId: number | undefined = undefined;

    if (password) {
      const backendRes = await AuthApiService.register({
        email: cleanEmail,
        password,
        firstName,
        lastName
      });
      if (backendRes) {
        const profile = backendRes.user;
        if (profile) {
          userId = profile.userId || profile.id;
          customerId = profile.customerId;
          if (profile.role) role = profile.role;
        } else {
          userId = backendRes.userId;
          if (backendRes.role) role = backendRes.role;
        }
      }
    }

    const newUser: DemoUser = {
      id: userId ? String(userId) : `usr-${Date.now()}`,
      name: cleanName,
      email: cleanEmail,
      tier: 'Privé Bronze',
      points: 100,
      role,
      userId,
      customerId
    };

    setCurrentUser(newUser);
    setAddresses([]);
    setSelectedAddress(null);
    showToast(`Welcome to SCENTIVA Privé, ${newUser.name}!`, 'success');
    return { success: true, message: 'Account created successfully' };
  };

  const loginWithGoogle = async (idToken: string): Promise<{ success: boolean; message: string }> => {
    if (!idToken) {
      throw new Error('Google authentication credential is missing.');
    }

    const backendRes = await AuthApiService.googleLogin(idToken);
    let userName = 'Connoisseur Member';
    let userEmail = 'client@scentiva.luxury';
    let tier: 'Privé Bronze' | 'Privé Silver' | 'Privé Gold' | 'Privé Diamond' = 'Privé Bronze';
    let role = 'ROLE_CUSTOMER';
    let userId: number | undefined = undefined;
    let customerId: number | undefined = undefined;

    if (backendRes) {
      const profile = backendRes.user;
      if (profile) {
        const fullName = profile.fullName || `${profile.firstName || ''} ${profile.lastName || ''}`.trim();
        if (fullName) userName = fullName;
        if (profile.email) userEmail = profile.email;
        if (profile.role) role = profile.role;
        userId = profile.userId || profile.id;
        customerId = profile.customerId;
        if (profile.loyaltyTier) {
          if (profile.loyaltyTier.includes('DIAMOND')) tier = 'Privé Diamond';
          else if (profile.loyaltyTier.includes('GOLD')) tier = 'Privé Gold';
          else if (profile.loyaltyTier.includes('SILVER')) tier = 'Privé Silver';
          else tier = 'Privé Bronze';
        }
      } else {
        const combinedName = `${backendRes.firstName || ''} ${backendRes.lastName || ''}`.trim();
        if (combinedName) userName = combinedName;
        if (backendRes.email) userEmail = backendRes.email;
        if (backendRes.role) role = backendRes.role;
        userId = backendRes.userId;
      }
    }

    const authenticatedUser: DemoUser = {
      id: userId ? String(userId) : `usr-${Date.now()}`,
      name: userName,
      email: userEmail,
      tier,
      points: 500,
      role,
      userId,
      customerId
    };

    setCurrentUser(authenticatedUser);
    await loadUserAddresses();
    showToast(`Welcome to SCENTIVA Privé, ${authenticatedUser.name}!`, 'success');
    return { success: true, message: 'Signed in with Google successfully' };
  };

  const signOut = () => {
    try {
      AuthApiService.logout();
    } catch {}
    setCurrentUser(null);
    setAddresses([]);
    setSelectedAddress(null);
    if (typeof window !== 'undefined') {
      localStorage.removeItem('scentiva_user');
      localStorage.removeItem('scentiva_auth_token');
      localStorage.removeItem('scentiva_token');
      localStorage.removeItem('scentiva_addresses');
    }
    showToast('Signed out successfully', 'info');
  };

  // Cart Actions with Stock & Variant Hardening
  const addToCart = (product: Product, variant?: ProductVariant, quantity: number = 1) => {
    const targetVariant = variant || product.variants[0];

    if (!targetVariant || !targetVariant.inStock || product.stock <= 0) {
      showToast(`${product.name} (${targetVariant?.size || 'Variant'}) is currently out of stock.`, 'warning');
      return;
    }

    const availableStock = typeof product.stock === 'number' ? product.stock : 10;
    const safeQty = Math.max(1, quantity);

    setCart(prev => {
      const existingIndex = prev.findIndex(
        item => item.productId === product.id && item.selectedVariant.sku === targetVariant.sku
      );

      if (existingIndex > -1) {
        const updated = [...prev];
        const newQty = Math.min(availableStock, updated[existingIndex].quantity + safeQty);
        updated[existingIndex] = {
          ...updated[existingIndex],
          product,
          selectedVariant: targetVariant,
          quantity: newQty
        };
        return updated;
      } else {
        return [
          ...prev,
          {
            productId: product.id,
            product,
            selectedVariant: targetVariant,
            quantity: Math.min(availableStock, safeQty)
          }
        ];
      }
    });

    showToast(`Added ${product.name} (${targetVariant.size}) to your bag!`, 'success');
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

    setCart(prev =>
      prev.map(item => {
        if (item.productId === productId && item.selectedVariant.sku === variantSku) {
          const availableStock = typeof item.product.stock === 'number' ? item.product.stock : 10;
          const safeQty = Math.min(availableStock, quantity);
          return { ...item, quantity: safeQty };
        }
        return item;
      })
    );
  };

  const clearCart = () => {
    setCart([]);
    setAppliedCoupon(null);
  };

  // Cart Calculation Engine (Single Source of Truth)
  const cartCount = cart.reduce((total, item) => total + item.quantity, 0);
  const cartSubtotal = cart.reduce((total, item) => total + (item.selectedVariant.price * item.quantity), 0);

  // Dynamic coupon validation & recalculation
  let cartDiscount = 0;
  if (appliedCoupon && cartSubtotal >= (appliedCoupon.minOrderValue || 0)) {
    if (appliedCoupon.discountType === 'percentage') {
      cartDiscount = Math.round((cartSubtotal * appliedCoupon.discountValue) / 100);
    } else {
      cartDiscount = Math.min(cartSubtotal, appliedCoupon.discountValue);
    }
  }

  // Free standard delivery above ₹999, else ₹99 (₹0 if bag is empty)
  const cartDeliveryFee = cartSubtotal === 0 || cartSubtotal >= 999 ? 0 : 99;
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

  // Address CRUD with Backend Persistence
  const addAddress = (addrData: Omit<Address, 'id'>): Address => {
    const tempId = `addr-${Date.now()}`;
    const newAddr: Address = {
      ...addrData,
      id: tempId
    };

    if (newAddr.isDefault || addresses.length === 0) {
      setAddresses(prev => prev.map(a => ({ ...a, isDefault: false })).concat({ ...newAddr, isDefault: true }));
      setSelectedAddress({ ...newAddr, isDefault: true });
    } else {
      setAddresses(prev => [...prev, newAddr]);
      if (!selectedAddress) setSelectedAddress(newAddr);
    }

    // Persist to backend
    AddressApiService.addAddress(addrData)
      .then(saved => {
        if (saved && saved.id) {
          setAddresses(prev => prev.map(a => a.id === tempId ? saved : a));
          setSelectedAddress(prev => prev?.id === tempId ? saved : prev);
        }
      })
      .catch(err => {
        console.warn('Backend address save notification:', err);
      });

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

    AddressApiService.updateAddress(id, updated).catch(err => console.warn(err));
    if (updated.isDefault) {
      AddressApiService.setDefaultAddress(id).catch(err => console.warn(err));
    }
    showToast('Address updated successfully', 'success');
  };

  const deleteAddress = (id: string) => {
    setAddresses(prev => prev.filter(a => a.id !== id));
    if (selectedAddress?.id === id) {
      const remaining = addresses.filter(a => a.id !== id);
      setSelectedAddress(remaining[0] || null);
    }
    AddressApiService.deleteAddress(id).catch(err => console.warn(err));
    showToast('Address removed', 'info');
  };

  // Order Creation & Management (Demo Order Flow)
  const placeOrder = (orderData: {
    shippingAddress: Address;
    deliveryMethod: 'Standard Delivery' | 'Express Luxury Delivery';
    paymentMethod: 'Razorpay Secure (UPI, Cards, NetBanking)' | 'Razorpay' | 'UPI / QR' | 'Credit / Debit Card' | 'Net Banking' | 'Cash on Delivery' | string;
  }): Order => {
    if (cart.length === 0) {
      throw new Error('Cannot place an order with an empty cart.');
    }

    const baseDeliveryFee = cartSubtotal === 0 || cartSubtotal >= 999 ? 0 : 99;
    const expressFee = orderData.deliveryMethod === 'Express Luxury Delivery' ? 199 : 0;
    const totalDeliveryFee = baseDeliveryFee + expressFee;
    const finalTotal = Math.max(0, cartSubtotal - cartDiscount + totalDeliveryFee);

    const newOrder = OrderService.create({
      cart: [...cart],
      shippingAddress: orderData.shippingAddress,
      deliveryMethod: orderData.deliveryMethod,
      paymentMethod: orderData.paymentMethod,
      subtotal: cartSubtotal,
      discount: cartDiscount,
      couponCode: appliedCoupon && cartDiscount > 0 ? appliedCoupon.code : undefined,
      deliveryFee: totalDeliveryFee,
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

  // Admin Products State & Storefront Synchronization
  const addProduct = (prodData: Omit<Product, 'id' | 'slug'>) => {
    const created = ProductService.create(prodData);
    setProducts(prev => [created, ...prev]);
    showToast(`Added "${created.name}" to store catalog!`, 'success');
  };

  const updateProduct = (id: string, updated: Partial<Product>) => {
    const res = ProductService.update(id, updated);
    if (res) {
      setProducts(prev => prev.map(p => (p.id === id ? res : p)));
      
      // Synchronize active cart items so prices & stock stay unified
      setCart(prev =>
        prev.map(item => {
          if (item.productId === id) {
            const updatedVariant = res.variants.find(v => v.sku === item.selectedVariant.sku) || res.variants[0];
            return {
              ...item,
              product: res,
              selectedVariant: updatedVariant || item.selectedVariant,
              quantity: Math.min(res.stock, item.quantity)
            };
          }
          return item;
        })
      );

      // Synchronize wishlist
      setWishlist(prev => prev.map(item => (item.id === id ? res : item)));
      showToast('Product updated across store catalog!', 'success');
    }
  };

  const deleteProduct = (id: string) => {
    ProductService.delete(id);
    setProducts(prev => prev.filter(p => p.id !== id));
    setCart(prev => prev.filter(item => item.productId !== id));
    setWishlist(prev => prev.filter(item => item.id !== id));
    showToast('Product removed from catalog', 'info');
  };

  return (
    <StoreContext.Provider
      value={{
        isHydrated,
        currentUser,
        isLoggedIn: !!currentUser,
        signIn,
        signUp,
        loginWithGoogle,
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
        mergeGuestCartItems,
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

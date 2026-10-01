import { Customer } from '../types';

const CUSTOMERS_STORAGE_KEY = 'scentiva_customers';

export const INITIAL_DEMO_CUSTOMERS: Customer[] = [
  {
    id: 'cust-1',
    name: 'Elena Vance',
    email: 'elena.vance@example.com',
    phone: '+91 98234 11223',
    tier: 'Privé Diamond',
    totalOrders: 14,
    totalSpend: 142500,
    joinedDate: 'Jan 2026',
    status: 'VIP'
  },
  {
    id: 'cust-2',
    name: 'Aarav Singhania',
    email: 'aarav.s@example.com',
    phone: '+91 99876 54321',
    tier: 'Privé Gold',
    totalOrders: 8,
    totalSpend: 78900,
    joinedDate: 'Feb 2026',
    status: 'VIP'
  },
  {
    id: 'cust-3',
    name: 'Meera Deshmukh',
    email: 'meera.d@example.com',
    phone: '+91 97654 32109',
    tier: 'Privé Silver',
    totalOrders: 4,
    totalSpend: 34200,
    joinedDate: 'Mar 2026',
    status: 'Active'
  },
  {
    id: 'cust-4',
    name: 'Devansh Malhotra',
    email: 'devansh.m@example.com',
    phone: '+91 98111 22334',
    tier: 'Privé Bronze',
    totalOrders: 2,
    totalSpend: 18500,
    joinedDate: 'May 2026',
    status: 'Active'
  },
  {
    id: 'cust-5',
    name: 'Demo Connoisseur',
    email: 'demo@scentiva.com',
    phone: '+91 98765 43210',
    tier: 'Privé Gold',
    totalOrders: 3,
    totalSpend: 26997,
    joinedDate: 'Sep 2026',
    status: 'Active'
  }
];

export const getStoredCustomers = (): Customer[] => {
  if (typeof window === 'undefined') return INITIAL_DEMO_CUSTOMERS;
  try {
    const raw = localStorage.getItem(CUSTOMERS_STORAGE_KEY);
    if (!raw) return INITIAL_DEMO_CUSTOMERS;
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) && parsed.length > 0 ? parsed : INITIAL_DEMO_CUSTOMERS;
  } catch {
    return INITIAL_DEMO_CUSTOMERS;
  }
};

export const saveStoredCustomers = (customers: Customer[]): void => {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(CUSTOMERS_STORAGE_KEY, JSON.stringify(customers));
  } catch (err) {
    console.error('Failed to save customers to localStorage:', err);
  }
};

export const CustomerService = {
  getAll: (): Customer[] => {
    return getStoredCustomers();
  },

  getById: (id: string): Customer | undefined => {
    const all = getStoredCustomers();
    return all.find(c => c.id === id);
  },

  updateTier: (id: string, tier: Customer['tier']): Customer | null => {
    const all = getStoredCustomers();
    const index = all.findIndex(c => c.id === id);
    if (index === -1) return null;
    all[index] = { ...all[index], tier };
    saveStoredCustomers(all);
    return all[index];
  }
};

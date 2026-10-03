import { apiClient } from '../lib/api/apiClient';
import { Address } from '../types';

export interface BackendAddressResponse {
  id: number;
  customerId: number;
  fullName: string;
  phone: string;
  addressLine1: string;
  addressLine2?: string;
  landmark?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  isDefault: boolean;
  addressType: 'HOME' | 'OFFICE' | 'OTHER' | string;
}

export interface BackendAddressCreateRequest {
  fullName: string;
  phone: string;
  addressLine1: string;
  addressLine2?: string;
  landmark?: string;
  city: string;
  state: string;
  postalCode: string;
  country?: string;
  isDefault?: boolean;
  addressType?: 'HOME' | 'OFFICE' | 'OTHER';
}

export function mapBackendAddressToFrontend(backendAddr: BackendAddressResponse): Address {
  const typeMap: Record<string, 'Home' | 'Office' | 'Other'> = {
    'HOME': 'Home',
    'OFFICE': 'Office',
    'OTHER': 'Other',
  };
  return {
    id: String(backendAddr.id),
    fullName: backendAddr.fullName,
    phoneNumber: backendAddr.phone,
    addressLine1: backendAddr.addressLine1,
    addressLine2: backendAddr.addressLine2 || undefined,
    city: backendAddr.city,
    state: backendAddr.state,
    pincode: backendAddr.postalCode,
    type: typeMap[backendAddr.addressType?.toUpperCase()] || 'Home',
    isDefault: Boolean(backendAddr.isDefault),
  };
}

export function mapFrontendAddressToBackend(addr: Omit<Address, 'id'>): BackendAddressCreateRequest {
  const typeMap: Record<'Home' | 'Office' | 'Other', 'HOME' | 'OFFICE' | 'OTHER'> = {
    'Home': 'HOME',
    'Office': 'OFFICE',
    'Other': 'OTHER',
  };
  return {
    fullName: addr.fullName,
    phone: addr.phoneNumber,
    addressLine1: addr.addressLine1,
    addressLine2: addr.addressLine2,
    city: addr.city,
    state: addr.state,
    postalCode: addr.pincode,
    country: 'India',
    isDefault: Boolean(addr.isDefault),
    addressType: typeMap[addr.type] || 'HOME',
  };
}

export const AddressApiService = {
  async getAddresses(): Promise<Address[]> {
    try {
      const res = await apiClient.get<BackendAddressResponse[]>('/customer/addresses');
      if (Array.isArray(res.data)) {
        return res.data.map(mapBackendAddressToFrontend);
      }
      return [];
    } catch (err) {
      console.warn('Could not fetch addresses from backend:', err);
      return [];
    }
  },

  async addAddress(addr: Omit<Address, 'id'>): Promise<Address> {
    const payload = mapFrontendAddressToBackend(addr);
    try {
      const res = await apiClient.post<BackendAddressResponse>('/customer/addresses', payload);
      if (res.data) {
        return mapBackendAddressToFrontend(res.data);
      }
    } catch (err) {
      console.warn('Backend address creation failed, falling back to local entity:', err);
    }
    return {
      ...addr,
      id: `addr-${Date.now()}`,
    };
  },

  async updateAddress(id: string, addr: Partial<Address>): Promise<Address | null> {
    const numericId = parseInt(id, 10);
    if (!isNaN(numericId)) {
      try {
        const payload: BackendAddressCreateRequest = {
          fullName: addr.fullName || '',
          phone: addr.phoneNumber || '',
          addressLine1: addr.addressLine1 || '',
          addressLine2: addr.addressLine2,
          city: addr.city || '',
          state: addr.state || '',
          postalCode: addr.pincode || '',
          country: 'India',
          isDefault: addr.isDefault,
          addressType: addr.type ? (addr.type.toUpperCase() as any) : 'HOME',
        };
        const res = await apiClient.put<BackendAddressResponse>(`/customer/addresses/${numericId}`, payload);
        if (res.data) return mapBackendAddressToFrontend(res.data);
      } catch (err) {
        console.warn('Backend address update failed:', err);
      }
    }
    return null;
  },

  async deleteAddress(id: string): Promise<void> {
    const numericId = parseInt(id, 10);
    if (!isNaN(numericId)) {
      try {
        await apiClient.delete<void>(`/customer/addresses/${numericId}`);
      } catch (err) {
        console.warn('Backend address delete failed:', err);
      }
    }
  },

  async setDefaultAddress(id: string): Promise<Address | null> {
    const numericId = parseInt(id, 10);
    if (!isNaN(numericId)) {
      try {
        const res = await apiClient.put<BackendAddressResponse>(`/customer/addresses/${numericId}/default`);
        if (res.data) return mapBackendAddressToFrontend(res.data);
      } catch (err) {
        console.warn('Backend default address set failed:', err);
      }
    }
    return null;
  }
};

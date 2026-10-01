import { Product } from '../types';
import { ProductService, getStoredProducts, saveStoredProducts } from './productService';

export const InventoryService = {
  getAllInventory: (): { product: Product; totalStock: number; status: 'In Stock' | 'Low Stock' | 'Out of Stock' }[] => {
    const products = ProductService.getAll();
    return products.map(p => {
      let status: 'In Stock' | 'Low Stock' | 'Out of Stock' = 'In Stock';
      if (p.stock <= 0) {
        status = 'Out of Stock';
      } else if (p.stock <= 10) {
        status = 'Low Stock';
      }
      return {
        product: p,
        totalStock: p.stock,
        status
      };
    });
  },

  adjustStock: (productId: string, delta: number): Product | null => {
    const products = getStoredProducts();
    const index = products.findIndex(p => p.id === productId);
    if (index === -1) return null;

    const currentStock = products[index].stock;
    const newStock = Math.max(0, currentStock + delta);
    
    // Also update variants stock flag
    const updatedVariants = products[index].variants.map(v => ({
      ...v,
      inStock: newStock > 0
    }));

    products[index] = {
      ...products[index],
      stock: newStock,
      variants: updatedVariants
    };

    saveStoredProducts(products);
    return products[index];
  },

  setStock: (productId: string, exactStock: number): Product | null => {
    const products = getStoredProducts();
    const index = products.findIndex(p => p.id === productId);
    if (index === -1) return null;

    const stock = Math.max(0, exactStock);
    const updatedVariants = products[index].variants.map(v => ({
      ...v,
      inStock: stock > 0
    }));

    products[index] = {
      ...products[index],
      stock,
      variants: updatedVariants
    };

    saveStoredProducts(products);
    return products[index];
  }
};

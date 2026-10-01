import { MetadataRoute } from 'next';
import { ProductService } from '@/services/productService';
import { BrandService } from '@/services/brandService';
import { CategoryService } from '@/services/categoryService';
import { ContentService } from '@/services/contentService';

export default function sitemap(): MetadataRoute.Sitemap {
  const baseUrl = 'https://scentiva.luxury';

  const staticRoutes = [
    '',
    '/shop',
    '/search',
    '/brands',
    '/find-your-scent',
    '/offers',
    '/gifts',
    '/stories',
    '/help',
    '/contact',
    '/policies/shipping',
    '/policies/returns',
    '/policies/privacy',
    '/policies/terms',
  ].map(route => ({
    url: `${baseUrl}${route}`,
    lastModified: new Date(),
    changeFrequency: 'daily' as const,
    priority: route === '' ? 1.0 : 0.8,
  }));

  const products = ProductService.getAll().map(product => ({
    url: `${baseUrl}/product/${product.slug}`,
    lastModified: new Date(),
    changeFrequency: 'weekly' as const,
    priority: 0.9,
  }));

  const brands = BrandService.getAll().map(brand => ({
    url: `${baseUrl}/brands/${brand.slug}`,
    lastModified: new Date(),
    changeFrequency: 'weekly' as const,
    priority: 0.8,
  }));

  const categories = CategoryService.getAll().map(category => ({
    url: `${baseUrl}/categories/${category.slug}`,
    lastModified: new Date(),
    changeFrequency: 'weekly' as const,
    priority: 0.8,
  }));

  const stories = ContentService.getStories().map(story => ({
    url: `${baseUrl}/stories/${story.slug}`,
    lastModified: new Date(),
    changeFrequency: 'monthly' as const,
    priority: 0.7,
  }));

  return [...staticRoutes, ...products, ...brands, ...categories, ...stories];
}

import { NextResponse } from 'next/server';
import { PRODUCTS } from '@/data/products';

export async function GET(request: Request) {
  const { searchParams } = new URL(request.url);
  const brand = searchParams.get('brand');
  const category = searchParams.get('category');
  const q = searchParams.get('q');

  let results = [...PRODUCTS];

  if (brand) {
    results = results.filter(p => p.brandId === brand || p.brandId.replace('b-', '') === brand);
  }
  if (category) {
    results = results.filter(p => p.category.toLowerCase().includes(category.toLowerCase()));
  }
  if (q) {
    const query = q.toLowerCase();
    results = results.filter(
      p =>
        p.name.toLowerCase().includes(query) ||
        p.brandName.toLowerCase().includes(query) ||
        p.fragranceFamilies.some(f => f.toLowerCase().includes(query))
    );
  }

  return NextResponse.json({
    total: results.length,
    products: results,
  });
}

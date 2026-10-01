import { NextResponse } from 'next/server';

export async function GET() {
  return NextResponse.json({
    status: 'ok',
    app: 'SCENTIVA Haute Parfumerie & Marketplace',
    version: '1.0.0-production',
    timestamp: new Date().toISOString(),
    catalogSize: 12,
    engine: 'Next.js App Router',
  });
}

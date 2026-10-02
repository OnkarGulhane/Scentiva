'use client';

import { useState, useEffect } from 'react';
import { useRouter as useNextRouter, usePathname, useParams as useNextParams } from 'next/navigation';

/**
 * Next.js Navigation Adapter
 * Provides familiar navigation APIs (`useNavigate`, `useLocation`, `useParams`, `useSearchParams`)
 * backed natively by the Next.js App Router without triggering Suspense de-optimizations.
 */
export function useNavigate() {
  const router = useNextRouter();
  
  return (target: string | number, options?: { replace?: boolean }) => {
    if (typeof target === 'number') {
      if (target === -1 && typeof window !== 'undefined') {
        window.history.back();
      }
      return;
    }
    if (options?.replace) {
      router.replace(target);
    } else {
      router.push(target);
    }
  };
}

export function useLocation() {
  const pathname = usePathname() || '/';
  const [search, setSearch] = useState<string>('');
  const [hash, setHash] = useState<string>('');

  useEffect(() => {
    if (typeof window !== 'undefined') {
      setSearch(window.location.search);
      setHash(window.location.hash);
    }
  }, [pathname]);
  
  return {
    pathname,
    search,
    hash,
    state: null,
  };
}

export function useParams<T extends Record<string, string | string[]> = Record<string, string>>() {
  return useNextParams() as T;
}

export function useSearchParams() {
  const [searchParams, setSearchParamsState] = useState<URLSearchParams>(() => {
    if (typeof window !== 'undefined') {
      return new URLSearchParams(window.location.search);
    }
    return new URLSearchParams();
  });

  const pathname = usePathname() || '';
  const router = useNextRouter();

  useEffect(() => {
    if (typeof window !== 'undefined') {
      setSearchParamsState(new URLSearchParams(window.location.search));
    }
  }, [pathname]);

  const setSearchParams = (params: Record<string, string> | URLSearchParams) => {
    const nextParams = new URLSearchParams(params.toString());
    setSearchParamsState(nextParams);
    router.push(`${pathname}?${nextParams.toString()}`);
  };

  return [searchParams, setSearchParams] as const;
}


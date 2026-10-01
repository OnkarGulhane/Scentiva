'use client';

import { useRouter as useNextRouter, usePathname, useSearchParams as useNextSearchParams, useParams as useNextParams } from 'next/navigation';

/**
 * Next.js Navigation Adapter
 * Provides familiar navigation APIs (`useNavigate`, `useLocation`, `useParams`, `useSearchParams`)
 * backed natively by the Next.js App Router.
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
  const searchParams = useNextSearchParams();
  const search = searchParams && searchParams.toString() ? `?${searchParams.toString()}` : '';
  
  return {
    pathname,
    search,
    hash: typeof window !== 'undefined' ? window.location.hash : '',
    state: null,
  };
}

export function useParams<T extends Record<string, string | string[]> = Record<string, string>>() {
  return useNextParams() as T;
}

export function useSearchParams() {
  const searchParams = useNextSearchParams();
  const router = useNextRouter();
  const pathname = usePathname();

  const setSearchParams = (params: Record<string, string> | URLSearchParams) => {
    const nextParams = new URLSearchParams(params.toString());
    router.push(`${pathname}?${nextParams.toString()}`);
  };

  return [searchParams || new URLSearchParams(), setSearchParams] as const;
}

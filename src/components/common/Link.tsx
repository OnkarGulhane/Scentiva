'use client';

import React from 'react';
import NextLink from 'next/link';
import { usePathname } from 'next/navigation';

export interface LinkProps extends Omit<React.AnchorHTMLAttributes<HTMLAnchorElement>, 'href' | 'className' | 'children'> {
  href?: string;
  to?: string;
  className?: string | ((props: { isActive: boolean }) => string);
  children?: React.ReactNode | ((props: { isActive: boolean }) => React.ReactNode);
  replace?: boolean;
  scroll?: boolean;
  end?: boolean;
}

/**
 * Universal Next.js Link Wrapper
 * Seamlessly accepts both `href` and `to` properties for robust Next.js navigation.
 */
export const Link: React.FC<LinkProps> = ({ href, to, className, children, ...props }) => {
  const targetHref = href || to || '/';
  const resolvedClass = typeof className === 'function' ? className({ isActive: false }) : className;
  const resolvedChildren = typeof children === 'function' ? children({ isActive: false }) : children;

  return (
    <NextLink href={targetHref} className={resolvedClass} {...props}>
      {resolvedChildren}
    </NextLink>
  );
};

export const NavLink: React.FC<LinkProps> = ({ href, to, className, children, end, ...props }) => {
  const pathname = usePathname() || '/';
  const targetHref = href || to || '/';
  const isActive = end ? pathname === targetHref : pathname.startsWith(targetHref);
  
  const resolvedClass = typeof className === 'function' ? className({ isActive }) : className;
  const resolvedChildren = typeof children === 'function' ? children({ isActive }) : children;

  return (
    <NextLink href={targetHref} className={resolvedClass} {...props}>
      {resolvedChildren}
    </NextLink>
  );
};

export default Link;


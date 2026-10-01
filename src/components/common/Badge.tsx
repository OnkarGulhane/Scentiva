import React from 'react';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'gold' | 'blush' | 'plum' | 'outline' | 'success' | 'dark';
  size?: 'sm' | 'md';
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({
  children,
  variant = 'blush',
  size = 'sm',
  className = '',
}) => {
  const sizeClasses = size === 'sm' 
    ? 'text-[11px] px-2.5 py-0.5 tracking-wider' 
    : 'text-xs px-3 py-1 tracking-wide';

  const variantClasses = {
    gold: 'bg-brand-gold-100 text-brand-plum-950 border border-brand-gold-500/40 font-medium',
    blush: 'bg-brand-blush-100 text-brand-plum-900 border border-brand-blush-300/60 font-medium',
    plum: 'bg-brand-plum-900 text-brand-blush-100 font-medium',
    outline: 'border border-neutral-200 text-neutral-600 bg-white/80 font-normal',
    success: 'bg-green-50 text-semantic-success border border-green-200 font-medium',
    dark: 'bg-neutral-950 text-brand-blush-200 border border-brand-blush-300/20 font-medium'
  };

  return (
    <span className={`inline-flex items-center rounded-full uppercase ${sizeClasses} ${variantClasses[variant]} ${className}`}>
      {children}
    </span>
  );
};

import React from 'react';
import { ShieldCheck, Truck, RotateCcw, Award } from 'lucide-react';

export const TrustBadges: React.FC = () => {
  const BADGES = [
    {
      icon: ShieldCheck,
      title: '100% Authentic Guarantee',
      desc: 'Sourced directly from authorized European & American fragrance ateliers with batch authentication.'
    },
    {
      icon: Truck,
      title: 'Temperature-Controlled Delivery',
      desc: 'Complimentary expedited shipping in thermal-insulated packaging on all orders above ₹999.'
    },
    {
      icon: RotateCcw,
      title: '7-Day Easy Return Policy',
      desc: 'Complimentary return or exchange with intact tamper-evident seals and trial discovery samples.'
    },
    {
      icon: Award,
      title: 'White-Glove Concierge',
      desc: 'Expert fragrance sommeliers available 7 days a week for bespoke recommendations and gifting.'
    }
  ];

  return (
    <section className="py-12 bg-white border-b border-neutral-200/60">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {BADGES.map((badge, idx) => {
            const Icon = badge.icon;
            return (
              <div
                key={idx}
                className="flex items-start gap-4 p-4 rounded-xl bg-neutral-50/70 border border-neutral-200/70"
              >
                <div className="p-3 rounded-xl bg-brand-blush-100/70 text-brand-plum-900 flex-shrink-0">
                  <Icon className="w-6 h-6 text-brand-plum-900" />
                </div>
                <div className="space-y-1">
                  <h4 className="text-xs font-bold text-neutral-900 uppercase tracking-wide">
                    {badge.title}
                  </h4>
                  <p className="text-xs text-neutral-500 leading-relaxed">
                    {badge.desc}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
};

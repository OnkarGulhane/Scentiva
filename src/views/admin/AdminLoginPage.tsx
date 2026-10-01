'use client';

import React, { useState } from 'react';
import { useNavigate } from '@/hooks/useNavigation';
import { Link } from '@/components/common/Link';
import { ShieldCheck, Lock, Mail, ArrowRight, Sparkles } from 'lucide-react';
import { useStore } from '../../context/StoreContext';

export const AdminLoginPage: React.FC = () => {
  const [email, setEmail] = useState('admin@scentiva.internal');
  const [password, setPassword] = useState('scentiva-operations-2026');
  const { showToast } = useStore();
  const navigate = useNavigate();

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    showToast('Authenticated to SCENTIVA Admin Console (Demo)', 'success');
    navigate('/admin');
  };

  const handleQuickFill = () => {
    setEmail('admin@scentiva.internal');
    setPassword('scentiva-operations-2026');
  };

  return (
    <div className="min-h-screen bg-brand-plum-950 flex items-center justify-center p-4">
      <div className="max-w-md w-full space-y-6">
        <div className="text-center space-y-2">
          <div className="w-14 h-14 rounded-full bg-brand-plum-900 border border-brand-gold-500/50 p-2.5 mx-auto flex items-center justify-center shadow-lg">
            <img src="/assets/scentiva-emblem.svg" alt="SCENTIVA" className="w-full h-full" />
          </div>
          <h1 className="font-serif text-3xl font-bold text-white tracking-wide">
            SCENTIVA Operations
          </h1>
          <p className="text-xs text-brand-blush-200">
            Internal administrative portal & store fulfillment console
          </p>
        </div>

        <div className="bg-white rounded-3xl p-6 sm:p-8 shadow-modal border border-white/20 space-y-6">
          <div className="p-3.5 rounded-2xl bg-brand-gold-100/50 border border-brand-gold-500/30 text-xs text-brand-plum-950 space-y-1">
            <div className="flex items-center gap-1.5 font-bold">
              <ShieldCheck className="w-4 h-4 text-brand-gold-500" />
              <span>Operations Demo Portal</span>
            </div>
            <p className="text-[11px] text-neutral-600">
              Role-based simulation with inventory replenishment, product CRUD, and real-time tracking dispatch simulation.
            </p>
          </div>

          <form onSubmit={handleLogin} className="space-y-4 text-xs">
            <div>
              <label className="font-semibold text-neutral-700 block mb-1.5">Admin Email</label>
              <div className="relative">
                <Mail className="w-4 h-4 text-neutral-400 absolute left-3.5 top-3.5" />
                <input
                  type="email"
                  required
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>
            </div>

            <div>
              <div className="flex justify-between items-center mb-1.5">
                <label className="font-semibold text-neutral-700">Security Passcode</label>
                <button
                  type="button"
                  onClick={handleQuickFill}
                  className="text-xs text-brand-rose-500 hover:text-brand-plum-900 font-semibold"
                >
                  Reset Defaults
                </button>
              </div>
              <div className="relative">
                <Lock className="w-4 h-4 text-neutral-400 absolute left-3.5 top-3.5" />
                <input
                  type="password"
                  required
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full py-3.5 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white font-semibold text-xs uppercase tracking-wider flex items-center justify-center gap-2 shadow-card transition-all active:scale-98"
            >
              <span>Access Admin Console</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </form>

          <div className="pt-2 text-center">
            <Link to="/" className="text-xs text-neutral-500 hover:text-brand-plum-900 font-medium">
              ← Return to Customer Storefront
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

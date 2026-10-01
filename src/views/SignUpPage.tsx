'use client';

import React, { useState } from 'react';
import { useNavigate } from '@/hooks/useNavigation';
import { Link } from '@/components/common/Link';
import { useStore } from '../context/StoreContext';
import { Lock, Mail, User, Sparkles, ArrowRight, ShieldCheck } from 'lucide-react';

export const SignUpPage: React.FC = () => {
  const { signUp, showToast } = useStore();
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleDemoSignUp = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim() || !email.trim()) {
      showToast('Please fill all required fields', 'warning');
      return;
    }
    if (password && confirmPassword && password !== confirmPassword) {
      showToast('Passwords do not match', 'warning');
      return;
    }
    signUp(name.trim(), email.trim(), password);
    navigate('/account');
  };

  return (
    <div className="min-h-screen bg-neutral-50 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-6">
        {/* Brand Header */}
        <div className="text-center space-y-3">
          <div className="w-14 h-14 rounded-full bg-brand-plum-900 border border-brand-blush-300 p-2 mx-auto flex items-center justify-center shadow-md">
            <img src="/assets/scentiva-emblem.svg" alt="SCENTIVA" className="w-full h-full" />
          </div>
          <h1 className="font-serif text-3xl font-bold text-brand-plum-950">
            Join SCENTIVA Privé
          </h1>
          <p className="text-xs text-neutral-500">
            Complimentary samples with every order, early access to limited edition coffrets, and personalized olfactory consultations.
          </p>
        </div>

        {/* Form Container */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-modal space-y-6">
          <div className="p-3.5 rounded-2xl bg-brand-blush-100/60 border border-brand-blush-300/40 text-xs text-brand-plum-950 space-y-1">
            <div className="flex items-center gap-1.5 font-bold">
              <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
              <span>Demo Registration</span>
            </div>
            <p className="text-[11px] text-neutral-600">
              Creates a local demo customer session with initial welcome rewards points.
            </p>
          </div>

          <form onSubmit={handleDemoSignUp} className="space-y-4 text-xs">
            <div>
              <label className="font-semibold text-neutral-700 block mb-1.5">Full Name</label>
              <div className="relative">
                <User className="w-4 h-4 text-neutral-400 absolute left-3.5 top-3.5" />
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. Elena Vance"
                  className="w-full pl-10 pr-4 py-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none focus:ring-1 focus:ring-brand-blush-200"
                />
              </div>
            </div>

            <div>
              <label className="font-semibold text-neutral-700 block mb-1.5">Email Address</label>
              <div className="relative">
                <Mail className="w-4 h-4 text-neutral-400 absolute left-3.5 top-3.5" />
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="name@example.com"
                  className="w-full pl-10 pr-4 py-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none focus:ring-1 focus:ring-brand-blush-200"
                />
              </div>
            </div>

            <div>
              <label className="font-semibold text-neutral-700 block mb-1.5">Password</label>
              <div className="relative">
                <Lock className="w-4 h-4 text-neutral-400 absolute left-3.5 top-3.5" />
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Create a password"
                  className="w-full pl-10 pr-4 py-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none focus:ring-1 focus:ring-brand-blush-200"
                />
              </div>
            </div>

            <div>
              <label className="font-semibold text-neutral-700 block mb-1.5">Confirm Password</label>
              <div className="relative">
                <Lock className="w-4 h-4 text-neutral-400 absolute left-3.5 top-3.5" />
                <input
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  placeholder="Repeat your password"
                  className="w-full pl-10 pr-4 py-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none focus:ring-1 focus:ring-brand-blush-200"
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full py-3.5 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white font-semibold text-xs uppercase tracking-wider flex items-center justify-center gap-2 shadow-card transition-all active:scale-98"
            >
              <span>Create Privé Account</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </form>

          <div className="pt-4 border-t border-neutral-100 text-center text-xs text-neutral-500">
            Already have an account?{' '}
            <Link to="/account/sign-in" className="font-semibold text-brand-plum-900 hover:underline">
              Sign In Instead
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

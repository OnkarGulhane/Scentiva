'use client';

import React, { useState } from 'react';
import { useNavigate, useSearchParams, useLocation } from '@/hooks/useNavigation';
import { useStore } from '../context/StoreContext';
import { getSafeRedirectUrl } from '@/lib/utils/url';
import { Lock, Mail, User, Sparkles, ArrowRight, ShieldCheck, ShoppingBag, Loader2 } from 'lucide-react';

export const SignInPage: React.FC = () => {
  const { signIn, signUp, showToast, cartCount } = useStore();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const location = useLocation();

  // Extract redirect query parameter
  const redirectParam = searchParams.get('redirect') || (new URLSearchParams(location.search).get('redirect'));
  const isCheckoutRedirect = redirectParam === '/checkout' || (redirectParam && redirectParam.includes('checkout'));

  // Active Tab: 'signin' | 'signup'
  const [activeTab, setActiveTab] = useState<'signin' | 'signup'>('signin');

  // Sign In Form States
  const [signInEmail, setSignInEmail] = useState('');
  const [signInPassword, setSignInPassword] = useState('');

  // Create Account Form States
  const [signUpName, setSignUpName] = useState('');
  const [signUpEmail, setSignUpEmail] = useState('');
  const [signUpPassword, setSignUpPassword] = useState('');
  const [signUpConfirmPassword, setSignUpConfirmPassword] = useState('');

  const [isSubmitting, setIsSubmitting] = useState(false);

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  const handleSignIn = async (e: React.FormEvent) => {
    e.preventDefault();
    const cleanEmail = signInEmail.trim();
    if (!cleanEmail) {
      showToast('Please enter your email address', 'warning');
      return;
    }
    if (!emailRegex.test(cleanEmail)) {
      showToast('Please enter a valid email address (e.g. name@example.com)', 'warning');
      return;
    }
    if (!signInPassword) {
      showToast('Please enter your password', 'warning');
      return;
    }

    setIsSubmitting(true);
    try {
      const res = await signIn(cleanEmail, signInPassword);
      if (res.success) {
        const destination = getSafeRedirectUrl(redirectParam, '/account');
        navigate(destination);
      }
    } catch (err: any) {
      showToast(err.message || 'Email or password is incorrect.', 'error');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleSignUp = async (e: React.FormEvent) => {
    e.preventDefault();
    const cleanName = signUpName.trim();
    const cleanEmail = signUpEmail.trim();

    if (!cleanName) {
      showToast('Please enter your full name', 'warning');
      return;
    }
    if (!cleanEmail) {
      showToast('Please enter your email address', 'warning');
      return;
    }
    if (!emailRegex.test(cleanEmail)) {
      showToast('Please enter a valid email address (e.g. name@example.com)', 'warning');
      return;
    }
    if (!signUpPassword) {
      showToast('Please create a password', 'warning');
      return;
    }
    if (signUpPassword.length < 8) {
      showToast('Password must be at least 8 characters long', 'warning');
      return;
    }
    if (signUpPassword !== signUpConfirmPassword) {
      showToast('Passwords do not match.', 'warning');
      return;
    }

    setIsSubmitting(true);
    try {
      const res = await signUp(cleanName, cleanEmail, signUpPassword);
      if (res.success) {
        const destination = getSafeRedirectUrl(redirectParam, '/account');
        navigate(destination);
      }
    } catch (err: any) {
      showToast(err.message || 'Something went wrong. Please try again.', 'error');
    } finally {
      setIsSubmitting(false);
    }
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
            {isCheckoutRedirect ? 'Continue to Checkout' : 'SCENTIVA Privé Access'}
          </h1>
          <p className="text-xs text-neutral-500">
            {isCheckoutRedirect
              ? 'Sign in or create an account to complete your order. Your shopping bag is saved.'
              : 'Access your olfactory orders, bespoke recommendations, and connoisseur rewards.'}
          </p>
        </div>

        {/* Form Container */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-modal space-y-6">
          {/* Dual Segmented Tabs */}
          <div className="flex rounded-2xl bg-neutral-100 p-1 border border-neutral-200/80">
            <button
              type="button"
              onClick={() => setActiveTab('signin')}
              className={`flex-1 py-2.5 rounded-xl text-xs font-bold transition-all ${
                activeTab === 'signin'
                  ? 'bg-white text-brand-plum-950 shadow-xs'
                  : 'text-neutral-500 hover:text-neutral-800'
              }`}
            >
              Sign In
            </button>
            <button
              type="button"
              onClick={() => setActiveTab('signup')}
              className={`flex-1 py-2.5 rounded-xl text-xs font-bold transition-all ${
                activeTab === 'signup'
                  ? 'bg-white text-brand-plum-950 shadow-xs'
                  : 'text-neutral-500 hover:text-neutral-800'
              }`}
            >
              Create Account
            </button>
          </div>

          {/* Context Banner if Redirected from Checkout */}
          {isCheckoutRedirect ? (
            <div className="p-3.5 rounded-2xl bg-brand-blush-100 border border-brand-blush-300 text-xs text-brand-plum-950 space-y-1">
              <div className="flex items-center gap-1.5 font-bold">
                <ShoppingBag className="w-4 h-4 text-brand-rose-500" />
                <span>Checkout Authentication Required</span>
              </div>
              <p className="text-[11px] text-neutral-700 leading-relaxed">
                Your shopping bag ({cartCount} {cartCount === 1 ? 'item' : 'items'}) has been saved. Complete authentication to finalize delivery and payment.
              </p>
            </div>
          ) : (
            <div className="p-3.5 rounded-2xl bg-brand-blush-100/60 border border-brand-blush-300/40 text-xs text-brand-plum-950 space-y-1">
              <div className="flex items-center gap-1.5 font-bold">
                <Sparkles className="w-3.5 h-3.5 text-brand-gold-500" />
                <span>Privé Client Benefits</span>
              </div>
              <p className="text-[11px] text-neutral-600">
                Unlock complimentary discovery samples, order tracking, and private consultations.
              </p>
            </div>
          )}

          {/* SIGN IN FORM */}
          {activeTab === 'signin' && (
            <form onSubmit={handleSignIn} className="space-y-4 text-xs animate-in fade-in duration-200">
              <div>
                <label className="font-semibold text-neutral-700 block mb-1.5">Email Address</label>
                <div className="relative">
                  <Mail className="w-4 h-4 text-neutral-400 absolute left-3.5 top-3.5" />
                  <input
                    type="email"
                    required
                    value={signInEmail}
                    onChange={(e) => setSignInEmail(e.target.value)}
                    placeholder="name@example.com"
                    className="w-full pl-10 pr-4 py-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none focus:ring-1 focus:ring-brand-blush-200"
                  />
                </div>
              </div>

              <div>
                <div className="flex items-center justify-between mb-1.5">
                  <label className="font-semibold text-neutral-700">Password</label>
                  <span className="text-[11px] text-neutral-400">Secure entry</span>
                </div>
                <div className="relative">
                  <Lock className="w-4 h-4 text-neutral-400 absolute left-3.5 top-3.5" />
                  <input
                    type="password"
                    value={signInPassword}
                    onChange={(e) => setSignInPassword(e.target.value)}
                    placeholder="••••••••••••"
                    className="w-full pl-10 pr-4 py-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none focus:ring-1 focus:ring-brand-blush-200"
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={isSubmitting}
                className={`w-full py-3.5 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white font-semibold text-xs uppercase tracking-wider flex items-center justify-center gap-2 shadow-card transition-all active:scale-98 ${
                  isSubmitting ? 'opacity-70 cursor-not-allowed' : ''
                }`}
              >
                {isSubmitting ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin" />
                    <span>Signing In...</span>
                  </>
                ) : (
                  <>
                    <span>{isCheckoutRedirect ? 'Sign In & Continue to Checkout' : 'Sign In'}</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>

              <div className="pt-3 text-center text-xs text-neutral-500">
                New to SCENTIVA?{' '}
                <button
                  type="button"
                  onClick={() => setActiveTab('signup')}
                  className="font-semibold text-brand-plum-900 hover:underline"
                >
                  Create Account
                </button>
              </div>
            </form>
          )}

          {/* CREATE ACCOUNT FORM */}
          {activeTab === 'signup' && (
            <form onSubmit={handleSignUp} className="space-y-4 text-xs animate-in fade-in duration-200">
              <div>
                <label className="font-semibold text-neutral-700 block mb-1.5">Full Name</label>
                <div className="relative">
                  <User className="w-4 h-4 text-neutral-400 absolute left-3.5 top-3.5" />
                  <input
                    type="text"
                    required
                    value={signUpName}
                    onChange={(e) => setSignUpName(e.target.value)}
                    placeholder="e.g. Olivia Vane"
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
                    value={signUpEmail}
                    onChange={(e) => setSignUpEmail(e.target.value)}
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
                    required
                    value={signUpPassword}
                    onChange={(e) => setSignUpPassword(e.target.value)}
                    placeholder="Create a secure password"
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
                    required
                    value={signUpConfirmPassword}
                    onChange={(e) => setSignUpConfirmPassword(e.target.value)}
                    placeholder="Repeat your password"
                    className="w-full pl-10 pr-4 py-3 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none focus:ring-1 focus:ring-brand-blush-200"
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={isSubmitting}
                className={`w-full py-3.5 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white font-semibold text-xs uppercase tracking-wider flex items-center justify-center gap-2 shadow-card transition-all active:scale-98 ${
                  isSubmitting ? 'opacity-70 cursor-not-allowed' : ''
                }`}
              >
                {isSubmitting ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin" />
                    <span>Creating Account...</span>
                  </>
                ) : (
                  <>
                    <span>{isCheckoutRedirect ? 'Create Account & Continue to Checkout' : 'Create Account'}</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>

              <div className="pt-3 text-center text-xs text-neutral-500">
                Already have an account?{' '}
                <button
                  type="button"
                  onClick={() => setActiveTab('signin')}
                  className="font-semibold text-brand-plum-900 hover:underline"
                >
                  Sign In
                </button>
              </div>
            </form>
          )}

          <div className="pt-3 border-t border-neutral-100 flex items-center justify-center gap-2 text-[11px] text-neutral-400">
            <ShieldCheck className="w-3.5 h-3.5 text-brand-gold-500" />
            <span>256-Bit SSL Encrypted Privé Authentication</span>
          </div>
        </div>
      </div>
    </div>
  );
};

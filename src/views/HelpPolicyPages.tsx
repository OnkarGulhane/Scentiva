'use client';

import React, { useState } from 'react';
import { useLocation } from '@/hooks/useNavigation';
import { Link } from '@/components/common/Link';
import { useStore } from '../context/StoreContext';
import { ShieldCheck, Truck, RotateCcw, HelpCircle, Mail, Send, ChevronDown, CheckCircle2 } from 'lucide-react';

export const HelpPolicyPages: React.FC = () => {
  const location = useLocation();
  const { showToast } = useStore();

  const [contactName, setContactName] = useState('');
  const [contactEmail, setContactEmail] = useState('');
  const [contactSubject, setContactSubject] = useState('Order Tracking');
  const [contactMessage, setContactMessage] = useState('');
  const [contactSubmitted, setContactSubmitted] = useState(false);

  const [openFaqIndex, setOpenFaqIndex] = useState<number | null>(0);

  const handleContactSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setContactSubmitted(true);
    showToast('Your inquiry has been received by SCENTIVA Concierge!', 'success');
  };

  const FAQS = [
    {
      q: 'How do I know my fragrance from SCENTIVA is 100% authentic?',
      a: 'All perfumes in the SCENTIVA Vault are procured directly from official brand houses or verified European master distributors. Every bottle includes original batch codes on both box and flacon base, tamper-evident holographic seal, and an official Certificate of Authenticity.'
    },
    {
      q: 'How is delicate perfume protected during summer/warm transit?',
      a: 'We utilize specialized temperature-regulated thermal insulated shipping coffrets with cold-retention liners to prevent excessive heat degradation, preserving delicate top note terpenes and volatile citrus oils.'
    },
    {
      q: 'What is your 7-Day Easy Return policy?',
      a: 'If you are unsatisfied with your selection, you may return the unopened flacon with intact tamper seals within 7 days. Every order includes a complimentary 2ml discovery spray of the purchased perfume so you can test it on skin before breaking the main flacon seal.'
    },
    {
      q: 'What payment methods do you accept?',
      a: 'We accept all major UPI apps (Google Pay, PhonePe, Paytm, BHIM), Credit/Debit Cards (Visa, MasterCard, Amex, RuPay), Net Banking across 50+ Indian banks, and Cash on Delivery.'
    }
  ];

  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 space-y-12">
        {/* Header */}
        <div className="text-center space-y-2">
          <span className="text-xs font-semibold uppercase tracking-widest text-brand-rose-500">
            SCENTIVA Concierge & Policies
          </span>
          <h1 className="font-serif text-3xl sm:text-4xl font-bold text-neutral-900">
            Help Center, Authenticity & Store Policies
          </h1>
        </div>

        {/* Policy Highlights Strip */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="p-5 rounded-2xl bg-white border border-neutral-200 shadow-xs space-y-2 text-center">
            <ShieldCheck className="w-8 h-8 text-brand-gold-500 mx-auto" />
            <h3 className="font-serif text-base font-bold text-neutral-900">100% Authentic Guarantee</h3>
            <p className="text-xs text-neutral-500">Directly sourced batch codes with verified French & Italian lab certification.</p>
          </div>

          <div className="p-5 rounded-2xl bg-white border border-neutral-200 shadow-xs space-y-2 text-center">
            <Truck className="w-8 h-8 text-brand-gold-500 mx-auto" />
            <h3 className="font-serif text-base font-bold text-neutral-900">Thermal Expedited Shipping</h3>
            <p className="text-xs text-neutral-500">Complimentary priority air delivery on all orders above ₹999.</p>
          </div>

          <div className="p-5 rounded-2xl bg-white border border-neutral-200 shadow-xs space-y-2 text-center">
            <RotateCcw className="w-8 h-8 text-brand-gold-500 mx-auto" />
            <h3 className="font-serif text-base font-bold text-neutral-900">7-Day Seal Return</h3>
            <p className="text-xs text-neutral-500">Hassle-free doorstep collection with trial discovery vial included.</p>
          </div>
        </div>

        {/* Frequently Asked Questions */}
        <div className="bg-white rounded-3xl p-6 sm:p-10 border border-neutral-200 shadow-xs space-y-6">
          <h2 className="font-serif text-2xl font-bold text-neutral-900 pb-2 border-b border-neutral-100">
            Frequently Asked Questions
          </h2>

          <div className="space-y-3">
            {FAQS.map((faq, idx) => (
              <div
                key={idx}
                className="border border-neutral-200/80 rounded-2xl overflow-hidden transition-all"
              >
                <button
                  onClick={() => setOpenFaqIndex(openFaqIndex === idx ? null : idx)}
                  className="w-full p-4 text-left flex items-center justify-between gap-4 font-semibold text-xs sm:text-sm text-neutral-900 hover:bg-neutral-50"
                >
                  <span>{faq.q}</span>
                  <ChevronDown className={`w-4 h-4 text-neutral-500 transition-transform ${openFaqIndex === idx ? 'rotate-180' : ''}`} />
                </button>
                {openFaqIndex === idx && (
                  <div className="p-4 pt-0 text-xs text-neutral-600 leading-relaxed bg-neutral-50/50 border-t border-neutral-100">
                    {faq.a}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Concierge Contact Form */}
        <div className="bg-white rounded-3xl p-6 sm:p-10 border border-neutral-200 shadow-xs space-y-6">
          <div className="space-y-1">
            <h2 className="font-serif text-2xl font-bold text-neutral-900">
              Contact Fragrance Concierge
            </h2>
            <p className="text-xs text-neutral-500">
              Need personalized gifting advice or corporate orders? Our sommeliers are here to assist.
            </p>
          </div>

          {contactSubmitted ? (
            <div className="p-6 rounded-2xl bg-green-50 border border-green-200 text-center space-y-2">
              <CheckCircle2 className="w-8 h-8 text-semantic-success mx-auto" />
              <h3 className="font-serif text-lg font-bold text-neutral-900">Message Dispatched</h3>
              <p className="text-xs text-neutral-600">
                A fragrance specialist will respond to your inquiry within 4 business hours.
              </p>
            </div>
          ) : (
            <form onSubmit={handleContactSubmit} className="space-y-4 text-xs">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">Your Name</label>
                  <input
                    type="text"
                    required
                    value={contactName}
                    onChange={e => setContactName(e.target.value)}
                    placeholder="Omkar Patil"
                    className="w-full px-3 py-2.5 rounded-xl border border-neutral-300 focus:outline-none focus:border-brand-plum-900"
                  />
                </div>
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">Email Address</label>
                  <input
                    type="email"
                    required
                    value={contactEmail}
                    onChange={e => setContactEmail(e.target.value)}
                    placeholder="omkar@example.com"
                    className="w-full px-3 py-2.5 rounded-xl border border-neutral-300 focus:outline-none focus:border-brand-plum-900"
                  />
                </div>
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Inquiry Subject</label>
                <select
                  value={contactSubject}
                  onChange={e => setContactSubject(e.target.value)}
                  className="w-full px-3 py-2.5 rounded-xl border border-neutral-300 focus:outline-none focus:border-brand-plum-900"
                >
                  <option value="Order Tracking">Order & Shipment Assistance</option>
                  <option value="Scent Recommendation">Bespoke Fragrance Consultation</option>
                  <option value="Authenticity">Authenticity & Batch Verification</option>
                  <option value="Corporate Gifting">Luxury Corporate & Wedding Gifting</option>
                </select>
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Message</label>
                <textarea
                  rows={4}
                  required
                  value={contactMessage}
                  onChange={e => setContactMessage(e.target.value)}
                  placeholder="Tell us how we can curate your experience..."
                  className="w-full px-3 py-2.5 rounded-xl border border-neutral-300 focus:outline-none focus:border-brand-plum-900"
                />
              </div>

              <button
                type="submit"
                className="px-8 py-3.5 rounded-2xl bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold uppercase tracking-wider flex items-center gap-2 shadow-md transition-all active:scale-98"
              >
                <Send className="w-3.5 h-3.5" />
                <span>Send Message</span>
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

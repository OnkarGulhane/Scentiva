'use client';

import React, { useState } from 'react';
import { Link } from '@/components/common/Link';
import { useStore } from '../context/StoreContext';
import { Address } from '../types';
import { MapPin, Plus, Trash2, Edit2, CheckCircle2, ArrowLeft, X } from 'lucide-react';

export const AccountAddressesPage: React.FC = () => {
  const { addresses, addAddress, updateAddress, deleteAddress, showToast } = useStore();

  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);

  const [fullName, setFullName] = useState('');
  const [phone, setPhone] = useState('');
  const [line1, setLine1] = useState('');
  const [city, setCity] = useState('Pune');
  const [state, setState] = useState('Maharashtra');
  const [pincode, setPincode] = useState('411001');
  const [type, setType] = useState<'Home' | 'Office' | 'Other'>('Home');
  const [isDefault, setIsDefault] = useState(false);

  const handleOpenAdd = () => {
    setEditingId(null);
    setFullName('');
    setPhone('');
    setLine1('');
    setCity('Pune');
    setState('Maharashtra');
    setPincode('411001');
    setType('Home');
    setIsDefault(addresses.length === 0);
    setModalOpen(true);
  };

  const handleOpenEdit = (addr: Address) => {
    setEditingId(addr.id);
    setFullName(addr.fullName);
    setPhone(addr.phoneNumber);
    setLine1(addr.addressLine1);
    setCity(addr.city);
    setState(addr.state);
    setPincode(addr.pincode);
    setType(addr.type);
    setIsDefault(addr.isDefault);
    setModalOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim() || !phone.trim() || !line1.trim() || !pincode.trim()) {
      showToast('Please fill all required address fields', 'warning');
      return;
    }

    if (editingId) {
      updateAddress(editingId, {
        fullName,
        phoneNumber: phone,
        addressLine1: line1,
        city,
        state,
        pincode,
        type,
        isDefault
      });
    } else {
      addAddress({
        fullName,
        phoneNumber: phone,
        addressLine1: line1,
        city,
        state,
        pincode,
        type,
        isDefault
      });
    }
    setModalOpen(false);
  };

  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 space-y-6">
        {/* Navigation Breadcrumb */}
        <div className="flex items-center gap-2 text-xs text-neutral-500">
          <Link to="/account" className="hover:text-brand-plum-900 flex items-center gap-1 font-medium">
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Account Dashboard</span>
          </Link>
          <span>/</span>
          <span className="text-neutral-900 font-semibold">Address Book</span>
        </div>

        {/* Page Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-neutral-200">
          <div>
            <h1 className="font-serif text-3xl font-bold text-brand-plum-950">
              Saved Shipping Addresses
            </h1>
            <p className="text-xs text-neutral-500">
              Manage delivery destinations for fast, seamless one-click luxury checkout.
            </p>
          </div>
          <button
            onClick={handleOpenAdd}
            className="px-5 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold hover:bg-brand-plum-800 transition-colors shadow-sm flex items-center gap-1.5 self-start sm:self-auto"
          >
            <Plus className="w-4 h-4" />
            <span>Add New Address</span>
          </button>
        </div>

        {/* Addresses Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {addresses.map(addr => (
            <div
              key={addr.id}
              className={`bg-white rounded-3xl p-6 border-2 transition-all space-y-4 ${
                addr.isDefault
                  ? 'border-brand-plum-900 shadow-card'
                  : 'border-neutral-200 hover:border-neutral-300'
              }`}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold text-brand-plum-950 uppercase tracking-wider bg-brand-blush-100/60 px-3 py-1 rounded-full border border-brand-blush-300/40">
                    {addr.type}
                  </span>
                  {addr.isDefault && (
                    <span className="text-[10px] font-bold text-semantic-success bg-semantic-success/10 px-2.5 py-0.5 rounded-full">
                      DEFAULT DESTINATION
                    </span>
                  )}
                </div>

                <div className="flex items-center gap-1">
                  <button
                    onClick={() => handleOpenEdit(addr)}
                    className="p-1.5 text-neutral-400 hover:text-brand-plum-900 rounded-lg hover:bg-neutral-100"
                    title="Edit Address"
                  >
                    <Edit2 className="w-4 h-4" />
                  </button>
                  {addresses.length > 1 && (
                    <button
                      onClick={() => deleteAddress(addr.id)}
                      className="p-1.5 text-neutral-400 hover:text-semantic-error rounded-lg hover:bg-neutral-100"
                      title="Delete Address"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  )}
                </div>
              </div>

              <div>
                <h4 className="text-sm font-bold text-neutral-900">{addr.fullName}</h4>
                <p className="text-xs text-neutral-600 mt-1 leading-relaxed">
                  {addr.addressLine1}{addr.addressLine2 ? `, ${addr.addressLine2}` : ''}<br />
                  {addr.city}, {addr.state} - {addr.pincode}
                </p>
                <div className="text-xs text-neutral-500 mt-2 font-medium">
                  Mobile: {addr.phoneNumber}
                </div>
              </div>

              {!addr.isDefault && (
                <div className="pt-2 border-t border-neutral-100">
                  <button
                    onClick={() => updateAddress(addr.id, { isDefault: true })}
                    className="text-xs font-semibold text-brand-plum-900 hover:underline"
                  >
                    Set as Default Address
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Address Edit/Add Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-neutral-950/70 backdrop-blur-sm">
          <div className="bg-white rounded-3xl p-6 sm:p-8 max-w-md w-full shadow-modal border border-neutral-200 space-y-4">
            <div className="flex items-center justify-between pb-2 border-b border-neutral-100">
              <h3 className="font-serif text-xl font-bold text-neutral-900">
                {editingId ? 'Edit Address' : 'Add New Address'}
              </h3>
              <button onClick={() => setModalOpen(false)} className="p-1 text-neutral-500 hover:bg-neutral-100 rounded-full">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Full Recipient Name</label>
                <input
                  type="text"
                  required
                  value={fullName}
                  onChange={e => setFullName(e.target.value)}
                  placeholder="e.g. Olivia Vane"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Phone Number</label>
                <input
                  type="tel"
                  required
                  value={phone}
                  onChange={e => setPhone(e.target.value)}
                  placeholder="+91 98765 43210"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Street Address</label>
                <input
                  type="text"
                  required
                  value={line1}
                  onChange={e => setLine1(e.target.value)}
                  placeholder="Villa 14, Royal Palm Residences"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">City</label>
                  <input
                    type="text"
                    required
                    value={city}
                    onChange={e => setCity(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">PIN Code</label>
                  <input
                    type="text"
                    required
                    value={pincode}
                    onChange={e => setPincode(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                  />
                </div>
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Address Type</label>
                <div className="flex gap-2">
                  {(['Home', 'Office', 'Other'] as const).map(t => (
                    <button
                      key={t}
                      type="button"
                      onClick={() => setType(t)}
                      className={`flex-1 py-2 rounded-xl border text-xs font-semibold ${
                        type === t
                          ? 'border-brand-plum-900 bg-brand-plum-900 text-white'
                          : 'border-neutral-300 text-neutral-700'
                      }`}
                    >
                      {t}
                    </button>
                  ))}
                </div>
              </div>

              <label className="flex items-center gap-2 pt-2 cursor-pointer">
                <input
                  type="checkbox"
                  checked={isDefault}
                  onChange={e => setIsDefault(e.target.checked)}
                  className="rounded border-neutral-300 text-brand-plum-900 focus:ring-brand-plum-700"
                />
                <span className="text-neutral-700 font-medium">Make this my default shipping address</span>
              </label>

              <div className="pt-3 flex justify-end gap-2 border-t border-neutral-100">
                <button
                  type="button"
                  onClick={() => setModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-neutral-600 font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-6 py-2 rounded-xl bg-brand-plum-900 text-white font-semibold"
                >
                  Save Address
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

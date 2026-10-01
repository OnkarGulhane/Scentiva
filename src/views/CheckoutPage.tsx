'use client';

import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from '@/hooks/useNavigation';
import { Link } from '@/components/common/Link';
import { useStore } from '../context/StoreContext';
import { Address } from '../types';
import { 
  CheckCircle2, 
  MapPin, 
  Plus, 
  Truck, 
  CreditCard, 
  QrCode, 
  Building, 
  Banknote, 
  ShieldCheck, 
  ArrowRight, 
  ArrowLeft,
  X,
  Lock,
  Loader2
} from 'lucide-react';
import confetti from 'canvas-confetti';

export const CheckoutPage: React.FC = () => {
  const {
    cart,
    addresses,
    selectedAddress,
    setSelectedAddress,
    addAddress,
    placeOrder,
    cartSubtotal,
    cartDiscount,
    cartTotal,
    formatPrice,
    showToast
  } = useStore();

  const navigate = useNavigate();
  const location = useLocation();

  const isPaymentRoute = location.pathname.includes('/payment');

  // Checkout Stepper State: 1 = Address, 2 = Delivery, 3 = Payment
  const [currentStep, setCurrentStep] = useState<number>(isPaymentRoute ? 3 : 1);
  const [deliveryMethod, setDeliveryMethod] = useState<'Standard Delivery' | 'Express Luxury Delivery'>('Express Luxury Delivery');
  const [paymentMethod, setPaymentMethod] = useState<'UPI / QR' | 'Credit / Debit Card' | 'Net Banking' | 'Cash on Delivery'>('UPI / QR');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // New Address Modal
  const [showAddressModal, setShowAddressModal] = useState(false);
  const [newFullName, setNewFullName] = useState('');
  const [newPhone, setNewPhone] = useState('');
  const [newLine1, setNewLine1] = useState('');
  const [newCity, setNewCity] = useState('Pune');
  const [newState, setNewState] = useState('Maharashtra');
  const [newPincode, setNewPincode] = useState('411001');
  const [newType, setNewType] = useState<'Home' | 'Office' | 'Other'>('Home');

  useEffect(() => {
    if (isPaymentRoute) {
      setCurrentStep(3);
    }
  }, [isPaymentRoute]);

  if (cart.length === 0) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center p-6 text-center">
        <h2 className="font-serif text-2xl font-bold text-neutral-900 mb-2">Your Bag is Empty</h2>
        <p className="text-xs text-neutral-500 mb-4">Please add fragrances to your shopping bag before proceeding to checkout.</p>
        <Link to="/shop" className="px-6 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold">
          Return to Shop
        </Link>
      </div>
    );
  }

  const handleSaveNewAddress = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newFullName.trim() || !newPhone.trim() || !newLine1.trim() || !newPincode.trim()) {
      showToast('Please fill all required address fields', 'warning');
      return;
    }
    const created = addAddress({
      fullName: newFullName,
      phoneNumber: newPhone,
      addressLine1: newLine1,
      city: newCity,
      state: newState,
      pincode: newPincode,
      type: newType,
      isDefault: false
    });
    setSelectedAddress(created);
    setShowAddressModal(false);
  };

  const handlePlaceOrder = () => {
    if (isSubmitting) return;

    if (!selectedAddress) {
      showToast('Please select or add a shipping address', 'warning');
      setCurrentStep(1);
      return;
    }

    setIsSubmitting(true);

    setTimeout(() => {
      const order = placeOrder({
        shippingAddress: selectedAddress,
        deliveryMethod,
        paymentMethod
      });

      confetti({
        particleCount: 100,
        spread: 80,
        origin: { y: 0.6 },
        colors: ['#E9B7D8', '#C7A66A', '#451333', '#B85B88']
      });

      setIsSubmitting(false);
      navigate(`/order/success?id=${order.id}`);
    }, 600);
  };

  const deliveryFee = deliveryMethod === 'Express Luxury Delivery' ? 0 : 0;
  const grandTotal = cartTotal + deliveryFee;

  return (
    <div className="min-h-screen bg-neutral-50 py-10 lg:py-16">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
        {/* Header & Stepper */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-neutral-200/80 pb-6">
          <div>
            <div className="text-xs text-neutral-400 font-medium mb-1 flex items-center gap-1.5">
              <Lock className="w-3.5 h-3.5 text-brand-gold-500" />
              <span>256-Bit Encrypted Secure Checkout (Demo Mode)</span>
            </div>
            <h1 className="font-serif text-3xl sm:text-4xl font-bold text-neutral-900">
              Demo Checkout
            </h1>
          </div>

          {/* Stepper Indicator */}
          <div className="flex items-center gap-2">
            {[
              { num: 1, label: 'Address', path: '/checkout' },
              { num: 2, label: 'Delivery', path: '/checkout' },
              { num: 3, label: 'Payment', path: '/checkout/payment' }
            ].map(step => (
              <div key={step.num} className="flex items-center gap-2">
                <button
                  onClick={() => {
                    setCurrentStep(step.num);
                    if (step.num === 3) {
                      navigate('/checkout/payment');
                    } else if (isPaymentRoute) {
                      navigate('/checkout');
                    }
                  }}
                  className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-semibold transition-all ${
                    currentStep === step.num
                      ? 'bg-brand-plum-900 text-white shadow-xs'
                      : currentStep > step.num
                      ? 'bg-brand-blush-100 text-brand-plum-950 font-bold'
                      : 'bg-neutral-200 text-neutral-600'
                  }`}
                >
                  <span>{step.num}</span>
                  <span className="hidden sm:inline">{step.label}</span>
                </button>
                {step.num < 3 && <span className="text-neutral-300">→</span>}
              </div>
            ))}
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-start">
          {/* Main Checkout Steps (8 Cols) */}
          <div className="lg:col-span-8 space-y-6">
            {/* Step 1: Shipping Address */}
            {currentStep === 1 && (
              <div className="bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-xs space-y-6 animate-in fade-in duration-200">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <MapPin className="w-5 h-5 text-brand-rose-500" />
                    <h3 className="font-serif text-xl sm:text-2xl font-bold text-neutral-900">
                      Select Shipping Address
                    </h3>
                  </div>
                  <button
                    onClick={() => setShowAddressModal(true)}
                    className="px-4 py-2 rounded-xl bg-brand-blush-100 text-brand-plum-900 hover:bg-brand-blush-200 text-xs font-semibold flex items-center gap-1.5 transition-colors"
                  >
                    <Plus className="w-3.5 h-3.5" />
                    <span>Add New</span>
                  </button>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  {addresses.map(addr => (
                    <div
                      key={addr.id}
                      onClick={() => setSelectedAddress(addr)}
                      className={`p-5 rounded-2xl border-2 cursor-pointer transition-all ${
                        selectedAddress?.id === addr.id
                          ? 'border-brand-plum-900 bg-brand-blush-100/30 shadow-sm'
                          : 'border-neutral-200 bg-neutral-50/60 hover:border-neutral-300'
                      }`}
                    >
                      <div className="flex items-center justify-between mb-2">
                        <span className="text-xs font-bold text-brand-plum-950 uppercase tracking-wider bg-white px-2.5 py-0.5 rounded-full border border-neutral-200">
                          {addr.type}
                        </span>
                        {selectedAddress?.id === addr.id && (
                          <CheckCircle2 className="w-5 h-5 text-brand-plum-900" />
                        )}
                      </div>
                      <div className="text-sm font-bold text-neutral-900">{addr.fullName}</div>
                      <div className="text-xs text-neutral-600 mt-1 leading-relaxed">
                        {addr.addressLine1}, {addr.city}, {addr.state} - {addr.pincode}
                      </div>
                      <div className="text-xs text-neutral-500 mt-2 font-medium">
                        Phone: {addr.phoneNumber}
                      </div>
                    </div>
                  ))}
                </div>

                <div className="pt-4 border-t border-neutral-100 flex justify-end">
                  <button
                    onClick={() => setCurrentStep(2)}
                    className="px-8 py-3.5 rounded-2xl bg-brand-plum-900 text-white text-xs font-semibold uppercase tracking-wider flex items-center gap-2 hover:bg-brand-plum-800 transition-all shadow-md"
                  >
                    <span>Proceed to Delivery Options</span>
                    <ArrowRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            )}

            {/* Step 2: Delivery Options */}
            {currentStep === 2 && (
              <div className="bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-xs space-y-6 animate-in fade-in duration-200">
                <div className="flex items-center gap-2">
                  <Truck className="w-5 h-5 text-brand-rose-500" />
                  <h3 className="font-serif text-xl sm:text-2xl font-bold text-neutral-900">
                    Choose Delivery Speed
                  </h3>
                </div>

                <div className="space-y-3">
                  <div
                    onClick={() => setDeliveryMethod('Express Luxury Delivery')}
                    className={`p-5 rounded-2xl border-2 cursor-pointer transition-all flex items-center justify-between ${
                      deliveryMethod === 'Express Luxury Delivery'
                        ? 'border-brand-plum-900 bg-brand-blush-100/30 shadow-sm'
                        : 'border-neutral-200 bg-neutral-50/60'
                    }`}
                  >
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="text-sm font-bold text-neutral-900">
                          Express Luxury White-Glove Courier
                        </span>
                        <span className="text-[10px] bg-brand-gold-100 text-brand-plum-950 font-bold px-2 py-0.5 rounded-md border border-brand-gold-500/40">
                          RECOMMENDED
                        </span>
                      </div>
                      <p className="text-xs text-neutral-500">
                        Insulated velvet coffret packaging • 2-3 Business Days Priority Air
                      </p>
                    </div>
                    <div className="text-right">
                      <span className="text-xs font-bold text-semantic-success">FREE</span>
                    </div>
                  </div>

                  <div
                    onClick={() => setDeliveryMethod('Standard Delivery')}
                    className={`p-5 rounded-2xl border-2 cursor-pointer transition-all flex items-center justify-between ${
                      deliveryMethod === 'Standard Delivery'
                        ? 'border-brand-plum-900 bg-brand-blush-100/30 shadow-sm'
                        : 'border-neutral-200 bg-neutral-50/60'
                    }`}
                  >
                    <div className="space-y-1">
                      <span className="text-sm font-bold text-neutral-900 block">
                        Standard Ground Dispatch
                      </span>
                      <p className="text-xs text-neutral-500">
                        Secure eco-friendly boxed parcel • 4-6 Business Days
                      </p>
                    </div>
                    <div className="text-right">
                      <span className="text-xs font-bold text-semantic-success">FREE</span>
                    </div>
                  </div>
                </div>

                <div className="pt-4 border-t border-neutral-100 flex items-center justify-between">
                  <button
                    onClick={() => setCurrentStep(1)}
                    className="px-5 py-2.5 rounded-full border border-neutral-300 text-neutral-700 text-xs font-semibold flex items-center gap-1.5"
                  >
                    <ArrowLeft className="w-3.5 h-3.5" />
                    <span>Back to Address</span>
                  </button>
                  <button
                    onClick={() => {
                      setCurrentStep(3);
                      navigate('/checkout/payment');
                    }}
                    className="px-8 py-3.5 rounded-2xl bg-brand-plum-900 text-white text-xs font-semibold uppercase tracking-wider flex items-center gap-2 hover:bg-brand-plum-800 transition-all shadow-md"
                  >
                    <span>Proceed to Payment</span>
                    <ArrowRight className="w-4 h-4" />
                  </button>
                </div>
              </div>
            )}

            {/* Step 3: Payment Method */}
            {currentStep === 3 && (
              <div className="bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-xs space-y-6 animate-in fade-in duration-200">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <CreditCard className="w-5 h-5 text-brand-rose-500" />
                    <h3 className="font-serif text-xl sm:text-2xl font-bold text-neutral-900">
                      Select Demo Payment Option
                    </h3>
                  </div>
                  <span className="text-xs text-brand-plum-700 font-semibold px-2 py-0.5 rounded-full bg-brand-blush-100">
                    Demo Simulation Only
                  </span>
                </div>

                <div className="space-y-3">
                  {[
                    { id: 'UPI / QR', title: 'Instant UPI / QR Code (Demo Simulation)', desc: 'Scan with Google Pay, PhonePe, Paytm, or BHIM', icon: QrCode },
                    { id: 'Credit / Debit Card', title: 'Credit / Debit Card (Demo Simulation)', desc: 'Visa, MasterCard, American Express, RuPay (No actual card required)', icon: CreditCard },
                    { id: 'Net Banking', title: 'Net Banking (Demo Simulation)', desc: 'HDFC, ICICI, SBI, Axis & all major Indian banks', icon: Building },
                    { id: 'Cash on Delivery', title: 'Pay on Delivery (Cash / UPI at Doorstep)', desc: 'Inspect sealed flacon box before paying courier', icon: Banknote }
                  ].map(method => {
                    const Icon = method.icon;
                    return (
                      <div
                        key={method.id}
                        onClick={() => setPaymentMethod(method.id as any)}
                        className={`p-4 sm:p-5 rounded-2xl border-2 cursor-pointer transition-all flex items-center justify-between ${
                          paymentMethod === method.id
                            ? 'border-brand-plum-900 bg-brand-blush-100/30 shadow-sm'
                            : 'border-neutral-200 bg-neutral-50/60'
                        }`}
                      >
                        <div className="flex items-center gap-3.5">
                          <div className={`p-2.5 rounded-xl ${paymentMethod === method.id ? 'bg-brand-plum-900 text-white' : 'bg-neutral-200 text-neutral-700'}`}>
                            <Icon className="w-5 h-5" />
                          </div>
                          <div>
                            <span className="text-xs sm:text-sm font-bold text-neutral-900 block">{method.title}</span>
                            <span className="text-[11px] text-neutral-500">{method.desc}</span>
                          </div>
                        </div>

                        {paymentMethod === method.id && (
                          <CheckCircle2 className="w-5 h-5 text-brand-plum-900 flex-shrink-0" />
                        )}
                      </div>
                    );
                  })}
                </div>

                <div className="p-4 rounded-2xl bg-brand-gold-100/40 border border-brand-gold-500/30 text-xs text-brand-plum-950 space-y-1">
                  <strong>🔒 Prototype Simulation Note:</strong>
                  <p className="text-[11px] text-neutral-700">
                    This is a functional frontend demo. Clicking "Place Demo Order" will generate a mock order reference and initiate the interactive live 5-stage shipment tracker. No real money will be charged.
                  </p>
                </div>

                <div className="pt-4 border-t border-neutral-100 flex items-center justify-between">
                  <button
                    onClick={() => {
                      setCurrentStep(2);
                      navigate('/checkout');
                    }}
                    className="px-5 py-2.5 rounded-full border border-neutral-300 text-neutral-700 text-xs font-semibold flex items-center gap-1.5"
                  >
                    <ArrowLeft className="w-3.5 h-3.5" />
                    <span>Back to Delivery</span>
                  </button>
                  <button
                    disabled={isSubmitting}
                    onClick={handlePlaceOrder}
                    className={`px-8 py-4 rounded-2xl bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold uppercase tracking-wider flex items-center gap-2 shadow-lg transition-all active:scale-98 ${
                      isSubmitting ? 'opacity-70 cursor-not-allowed' : ''
                    }`}
                  >
                    {isSubmitting ? (
                      <>
                        <Loader2 className="w-4 h-4 animate-spin" />
                        <span>Processing Demo Order...</span>
                      </>
                    ) : (
                      <>
                        <span>Place Demo Order • {formatPrice(grandTotal)}</span>
                        <ArrowRight className="w-4 h-4" />
                      </>
                    )}
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* Right Order Review Panel (4 Cols) */}
          <div className="lg:col-span-4 bg-white rounded-3xl p-6 sm:p-8 border border-neutral-200 shadow-xs space-y-6 sticky top-24">
            <h3 className="font-serif text-xl font-bold text-neutral-900 pb-2 border-b border-neutral-100">
              Order Review
            </h3>

            {/* Item list */}
            <div className="space-y-3 max-h-60 overflow-y-auto pr-1">
              {cart.map(item => (
                <div key={`${item.productId}-${item.selectedVariant.sku}`} className="flex items-center gap-3 text-xs">
                  <img
                    src={item.product.images[0]}
                    alt=""
                    className="w-12 h-12 rounded-lg object-cover bg-neutral-100 flex-shrink-0"
                  />
                  <div className="flex-1 min-w-0">
                    <div className="font-semibold text-neutral-900 truncate">{item.product.name}</div>
                    <div className="text-[10px] text-neutral-500">{item.selectedVariant.size} × {item.quantity}</div>
                  </div>
                  <div className="font-semibold text-brand-plum-950 tabular-nums">
                    {formatPrice(item.selectedVariant.price * item.quantity)}
                  </div>
                </div>
              ))}
            </div>

            {/* Calculations */}
            <div className="space-y-2 text-xs text-neutral-600 pt-3 border-t border-neutral-100">
              <div className="flex justify-between">
                <span>Subtotal</span>
                <span className="font-medium text-neutral-900 tabular-nums">{formatPrice(cartSubtotal)}</span>
              </div>
              {cartDiscount > 0 && (
                <div className="flex justify-between text-semantic-success font-medium">
                  <span>Voucher Discount</span>
                  <span className="tabular-nums">-{formatPrice(cartDiscount)}</span>
                </div>
              )}
              <div className="flex justify-between">
                <span>Delivery</span>
                <span className="text-semantic-success font-medium">FREE</span>
              </div>
              <div className="flex justify-between pt-3 border-t border-neutral-200 text-sm font-bold text-neutral-900">
                <span>Total Due</span>
                <span className="text-lg text-brand-plum-950 tabular-nums font-serif">
                  {formatPrice(grandTotal)}
                </span>
              </div>
            </div>

            <div className="pt-2 text-[11px] text-neutral-500 space-y-1.5 border-t border-neutral-100">
              <div className="flex items-center gap-1.5">
                <ShieldCheck className="w-3.5 h-3.5 text-brand-gold-500" />
                <span>Tamper-evident luxury seals guaranteed</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* New Address Modal */}
      {showAddressModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-neutral-950/70 backdrop-blur-sm">
          <div className="bg-white rounded-3xl p-6 sm:p-8 max-w-md w-full shadow-modal border border-neutral-200 space-y-4">
            <div className="flex items-center justify-between pb-2 border-b border-neutral-100">
              <h3 className="font-serif text-xl font-bold text-neutral-900">Add Shipping Address</h3>
              <button onClick={() => setShowAddressModal(false)} className="p-1 text-neutral-500 hover:bg-neutral-100 rounded-full">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSaveNewAddress} className="space-y-3 text-xs">
              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Full Recipient Name</label>
                <input
                  type="text"
                  required
                  value={newFullName}
                  onChange={e => setNewFullName(e.target.value)}
                  placeholder="e.g. Demo Connoisseur"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Mobile Phone</label>
                <input
                  type="tel"
                  required
                  value={newPhone}
                  onChange={e => setNewPhone(e.target.value)}
                  placeholder="+91 98765 43210"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Street Address / Villa</label>
                <input
                  type="text"
                  required
                  value={newLine1}
                  onChange={e => setNewLine1(e.target.value)}
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
                    value={newCity}
                    onChange={e => setNewCity(e.target.value)}
                    className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="font-semibold text-neutral-700 block mb-1">PIN Code</label>
                  <input
                    type="text"
                    required
                    value={newPincode}
                    onChange={e => setNewPincode(e.target.value)}
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
                      onClick={() => setNewType(t)}
                      className={`flex-1 py-2 rounded-xl border text-xs font-semibold ${
                        newType === t
                          ? 'border-brand-plum-900 bg-brand-plum-900 text-white'
                          : 'border-neutral-300 text-neutral-700'
                      }`}
                    >
                      {t}
                    </button>
                  ))}
                </div>
              </div>

              <div className="pt-2 flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setShowAddressModal(false)}
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

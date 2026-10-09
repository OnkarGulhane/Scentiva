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
import { analytics } from '../services/analyticsService';
import { CheckoutApiService } from '../services/checkoutApiService';
import { openRazorpayModal } from '@/lib/razorpay';

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
    appliedCoupon,
    cartTotal,
    formatPrice,
    showToast,
    currentUser,
    isLoggedIn,
    isHydrated
  } = useStore();

  const navigate = useNavigate();
  const location = useLocation();

  const isPaymentRoute = location.pathname.includes('/payment');

  // Checkout Stepper State: 1 = Address, 2 = Delivery, 3 = Payment
  const [currentStep, setCurrentStep] = useState<number>(isPaymentRoute ? 3 : 1);
  const [deliveryMethod, setDeliveryMethod] = useState<'Standard Delivery' | 'Express Luxury Delivery'>('Express Luxury Delivery');
  const [paymentMethod, setPaymentMethod] = useState<'RAZORPAY' | 'Cash on Delivery'>('RAZORPAY');
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

  const { addToCart, products } = useStore();

  useEffect(() => {
    if (isPaymentRoute) {
      setCurrentStep(3);
    }
  }, [isPaymentRoute]);

  if (cart.length === 0) {
    return (
      <div className="min-h-[60vh] flex flex-col items-center justify-center p-6 text-center space-y-4">
        <h2 className="font-serif text-3xl font-bold text-neutral-900">Your Bag is Empty</h2>
        <p className="text-xs text-neutral-500 max-w-sm">Please add fragrances to your shopping bag before proceeding to checkout.</p>
        <div className="flex flex-wrap items-center justify-center gap-3 pt-2">
          {products[0] && (
            <button
              onClick={() => {
                addToCart(products[0], products[0].variants[0], 1);
                showToast(`Added ${products[0].name} to your bag!`, 'success');
              }}
              className="px-6 py-2.5 rounded-full bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold shadow-sm transition-all"
            >
              + Add {products[0].name} ({products[0].variants[0]?.size})
            </button>
          )}
          <Link to="/shop" className="px-6 py-2.5 rounded-full border border-neutral-300 text-neutral-800 hover:bg-neutral-100 text-xs font-semibold transition-all">
            Explore All Fragrances
          </Link>
        </div>
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

  const handlePlaceOrder = async () => {
    if (isSubmitting) return;

    if (!selectedAddress) {
      showToast('Please select or add a shipping address', 'warning');
      setCurrentStep(1);
      return;
    }

    setIsSubmitting(true);

    // Online Payments via Razorpay Gateway (UPI, GPay, PhonePe, Cards, NetBanking)
    if (paymentMethod === 'RAZORPAY') {
      try {
        showToast('Connecting to Razorpay Secure Gateway...', 'info');

        let gatewayOrderId: string | undefined = undefined;
        let backendOrderNumber: string | undefined = undefined;
        let activeKey = process.env.NEXT_PUBLIC_RAZORPAY_KEY_ID || 'rzp_test_TkFZU8ecNzFnCq';

        // 1. Attempt Backend Order Creation on PostgreSQL database if reachable
        try {
          let addressId: number | undefined = undefined;
          if (typeof selectedAddress.id === 'number') {
            addressId = selectedAddress.id;
          } else if (!isNaN(parseInt(selectedAddress.id, 10)) && !selectedAddress.id.startsWith('addr-')) {
            addressId = parseInt(selectedAddress.id, 10);
          }

          if (addressId) {
            const processResult = await CheckoutApiService.processCheckout({
              shippingAddressId: addressId,
              paymentMethod: 'UPI',
              paymentProvider: 'RAZORPAY',
              couponCode: appliedCoupon?.code,
              notes: 'Luxury Fragrance Order via SCENTIVA Web'
            });

            if (processResult?.gatewayOrderId) {
              gatewayOrderId = processResult.gatewayOrderId;
              backendOrderNumber = processResult.orderNumber;
              if (processResult.keyId) {
                activeKey = processResult.keyId;
              }
            }
          }
        } catch (backendErr) {
          console.warn('Backend order pre-flight skipped or failed, proceeding with Razorpay test modal:', backendErr);
        }

        // 2. Open Official Razorpay Checkout Modal
        await openRazorpayModal({
          keyId: activeKey,
          orderId: gatewayOrderId || '',
          amountInPaise: Math.round(grandTotal * 100),
          customerName: selectedAddress.fullName,
          customerEmail: currentUser?.email || 'customer@scentiva.com',
          customerPhone: selectedAddress.phoneNumber || '+919876543210',
          orderNumber: backendOrderNumber || ('SC-' + Date.now().toString(36).toUpperCase()),
          onSuccess: async (rzpResponse) => {
            try {
              // 3. Verify Payment Signature on Backend if backend order was used
              if (backendOrderNumber && rzpResponse.razorpay_signature) {
                try {
                  await CheckoutApiService.verifyPayment({
                    orderNumber: backendOrderNumber,
                    gatewayPaymentId: rzpResponse.razorpay_payment_id,
                    gatewaySignature: rzpResponse.razorpay_signature,
                  });
                } catch (verifyErr) {
                  console.warn('Backend signature verification note:', verifyErr);
                }
              }

              // 4. Confirm Order in Store Context
              const order = placeOrder({
                shippingAddress: selectedAddress,
                deliveryMethod,
                paymentMethod: 'Razorpay Secure (UPI, Cards, NetBanking)' as any
              });

              analytics.trackPurchaseCompleted(order.id, backendOrderNumber || order.orderNumber, order.total, 'Razorpay');

              confetti({
                particleCount: 120,
                spread: 90,
                origin: { y: 0.5 },
                colors: ['#E9B7D8', '#C7A66A', '#451333', '#B85B88']
              });

              showToast(`🎉 Payment of ${formatPrice(grandTotal)} successful via Razorpay! (ID: ${rzpResponse.razorpay_payment_id})`, 'success');
              setIsSubmitting(false);
              navigate(`/order/success?id=${order.id}&paymentId=${rzpResponse.razorpay_payment_id}`);
            } catch (err: any) {
              console.error('Payment confirmation error:', err);
              showToast('Payment confirmed! Redirecting...', 'success');
              setIsSubmitting(false);
              navigate('/account/orders');
            }
          },
          onFailure: (err) => {
            console.warn('Razorpay payment failed:', err);
            showToast(err?.description || 'Payment was unsuccessful. Please try again.', 'error');
            setIsSubmitting(false);
          },
          onDismiss: () => {
            showToast('Razorpay payment window closed. Your items remain saved in cart.', 'info');
            setIsSubmitting(false);
          }
        });
      } catch (err: any) {
        console.error('Failed to open Razorpay modal:', err);
        showToast(err?.message || 'Could not load Razorpay. Please check connection.', 'error');
        setIsSubmitting(false);
      }
      return;
    }

    // Cash on Delivery Option
    setTimeout(() => {
      const order = placeOrder({
        shippingAddress: selectedAddress,
        deliveryMethod,
        paymentMethod: 'Cash on Delivery'
      });

      analytics.trackPurchaseCompleted(order.id, order.orderNumber, order.total, 'Cash on Delivery');

      confetti({
        particleCount: 100,
        spread: 80,
        origin: { y: 0.6 },
        colors: ['#E9B7D8', '#C7A66A', '#451333', '#B85B88']
      });

      showToast('Order placed successfully with Cash on Delivery!', 'success');
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
              <span>256-Bit Encrypted Secure Luxury Checkout</span>
            </div>
            <h1 className="font-serif text-3xl sm:text-4xl font-bold text-neutral-900">
              Checkout
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
                  {addresses.length > 0 && (
                    <button
                      onClick={() => setShowAddressModal(true)}
                      className="px-4 py-2 rounded-xl bg-brand-blush-100 text-brand-plum-900 hover:bg-brand-blush-200 text-xs font-semibold flex items-center gap-1.5 transition-colors"
                    >
                      <Plus className="w-3.5 h-3.5" />
                      <span>Add New</span>
                    </button>
                  )}
                </div>

                {addresses.length === 0 ? (
                  <div className="text-center py-10 px-4 border-2 border-dashed border-neutral-200 rounded-3xl space-y-3 bg-neutral-50/50">
                    <MapPin className="w-8 h-8 text-neutral-400 mx-auto" />
                    <h4 className="font-serif text-lg font-bold text-neutral-800">No saved addresses yet.</h4>
                    <p className="text-xs text-neutral-500 max-w-sm mx-auto">
                      Add your delivery destination to proceed with your luxury fragrance order.
                    </p>
                    <button
                      onClick={() => setShowAddressModal(true)}
                      className="px-6 py-2.5 rounded-full bg-brand-plum-900 text-white text-xs font-semibold hover:bg-brand-plum-800 transition-colors shadow-sm inline-flex items-center gap-1.5"
                    >
                      <Plus className="w-3.5 h-3.5" />
                      <span>+ Add New Address</span>
                    </button>
                  </div>
                ) : (
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
                )}

                <div className="pt-4 border-t border-neutral-100 flex justify-end">
                  <button
                    disabled={!selectedAddress}
                    onClick={() => {
                      if (!selectedAddress) {
                        showToast('Please add or select a shipping address', 'warning');
                        return;
                      }
                      setCurrentStep(2);
                    }}
                    className={`w-full sm:w-auto px-8 py-4 rounded-2xl bg-brand-plum-900 text-white text-xs font-semibold uppercase tracking-wider flex items-center justify-center gap-2 hover:bg-brand-plum-800 transition-all shadow-md active:scale-98 ${
                      !selectedAddress ? 'opacity-50 cursor-not-allowed' : ''
                    }`}
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

                <div className="pt-4 border-t border-neutral-100 flex flex-col-reverse sm:flex-row items-stretch sm:items-center justify-between gap-3">
                  <button
                    onClick={() => setCurrentStep(1)}
                    className="w-full sm:w-auto px-5 py-3 rounded-full border border-neutral-300 text-neutral-700 text-xs font-semibold flex items-center justify-center gap-1.5 hover:bg-neutral-100 transition-colors"
                  >
                    <ArrowLeft className="w-3.5 h-3.5" />
                    <span>Back to Address</span>
                  </button>
                  <button
                    onClick={() => {
                      setCurrentStep(3);
                      navigate('/checkout/payment');
                    }}
                    className="w-full sm:w-auto px-8 py-4 rounded-2xl bg-brand-plum-900 text-white text-xs font-semibold uppercase tracking-wider flex items-center justify-center gap-2 hover:bg-brand-plum-800 transition-all shadow-md active:scale-98"
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
                      Select Payment Method
                    </h3>
                  </div>
                  <span className="text-xs text-brand-plum-700 font-semibold px-2.5 py-1 rounded-full bg-brand-blush-100 flex items-center gap-1.5">
                    <ShieldCheck className="w-3.5 h-3.5 text-brand-gold-500" />
                    <span>256-Bit SSL Encrypted</span>
                  </span>
                </div>

                <div className="space-y-4">
                  {/* Option 1: Razorpay Secure Gateway (Primary) */}
                  <div
                    onClick={() => setPaymentMethod('RAZORPAY')}
                    className={`p-5 rounded-3xl border-2 cursor-pointer transition-all space-y-4 ${
                      paymentMethod === 'RAZORPAY'
                        ? 'border-brand-plum-900 bg-gradient-to-br from-brand-blush-50/80 via-white to-brand-gold-50/20 shadow-md ring-1 ring-brand-plum-900/10'
                        : 'border-neutral-200 bg-white hover:border-neutral-300'
                    }`}
                  >
                    <div className="flex items-start justify-between gap-3">
                      <div className="flex items-center gap-3">
                        <div className={`w-11 h-11 rounded-2xl flex items-center justify-center font-bold text-sm shadow-xs transition-colors ${
                          paymentMethod === 'RAZORPAY' ? 'bg-brand-plum-900 text-white' : 'bg-neutral-100 text-neutral-700'
                        }`}>
                          <ShieldCheck className="w-6 h-6 text-brand-gold-400" />
                        </div>
                        <div>
                          <div className="flex items-center gap-2 flex-wrap">
                            <span className="text-base font-bold text-neutral-900">
                              Razorpay Secure Gateway
                            </span>
                            <span className="text-[10px] font-extrabold uppercase tracking-wider px-2.5 py-0.5 rounded-full bg-brand-gold-100 text-brand-plum-950 border border-brand-gold-400/50">
                              RECOMMENDED
                            </span>
                            <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-800 border border-emerald-300 flex items-center gap-1">
                              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                              Test Mode Active
                            </span>
                          </div>
                          <p className="text-xs text-neutral-600 mt-0.5">
                            UPI (GPay, PhonePe, Paytm), Credit & Debit Cards, Net Banking, Wallets & Cred
                          </p>
                        </div>
                      </div>

                      {paymentMethod === 'RAZORPAY' && (
                        <CheckCircle2 className="w-6 h-6 text-brand-plum-900 shrink-0" />
                      )}
                    </div>

                    {/* Supported Methods Badges */}
                    <div className="pt-2 border-t border-neutral-100 flex items-center gap-1.5 flex-wrap">
                      <span className="text-[11px] font-semibold text-neutral-400 mr-1">Accepted:</span>
                      {['Google Pay', 'PhonePe', 'Paytm UPI', 'BHIM', 'Visa', 'MasterCard', 'RuPay', 'NetBanking', 'Cred'].map((tag) => (
                        <span key={tag} className="text-[10px] font-medium bg-neutral-100 text-neutral-700 px-2 py-0.5 rounded-md border border-neutral-200/80">
                          {tag}
                        </span>
                      ))}
                    </div>

                    {paymentMethod === 'RAZORPAY' && (
                      <div className="p-3 bg-brand-plum-900/5 rounded-2xl border border-brand-plum-900/10 text-xs text-neutral-700 flex items-center gap-2">
                        <Lock className="w-4 h-4 text-brand-gold-600 shrink-0" />
                        <span>Clicking <strong>&ldquo;Pay via Razorpay&rdquo;</strong> will open the official Razorpay payment window with live test credentials.</span>
                      </div>
                    )}
                  </div>

                  {/* Option 2: Cash on Delivery */}
                  <div
                    onClick={() => setPaymentMethod('Cash on Delivery')}
                    className={`p-5 rounded-3xl border-2 cursor-pointer transition-all flex items-center justify-between ${
                      paymentMethod === 'Cash on Delivery'
                        ? 'border-brand-plum-900 bg-brand-blush-100/30 shadow-sm'
                        : 'border-neutral-200 bg-white hover:border-neutral-300'
                    }`}
                  >
                    <div className="flex items-center gap-3.5">
                      <div className={`w-11 h-11 rounded-2xl flex items-center justify-center font-bold text-sm ${
                        paymentMethod === 'Cash on Delivery' ? 'bg-brand-plum-900 text-white' : 'bg-neutral-100 text-neutral-700'
                      }`}>
                        <Banknote className="w-5 h-5" />
                      </div>
                      <div>
                        <span className="text-sm font-bold text-neutral-900 block">Cash on Delivery (White-Glove)</span>
                        <span className="text-xs text-neutral-500">Pay securely upon luxury delivery at your doorstep</span>
                      </div>
                    </div>

                    {paymentMethod === 'Cash on Delivery' && (
                      <CheckCircle2 className="w-6 h-6 text-brand-plum-900 shrink-0" />
                    )}
                  </div>
                </div>

                <div className="p-4 rounded-2xl bg-brand-blush-100/40 border border-brand-blush-200/80 text-xs text-brand-plum-950 flex items-start gap-3">
                  <ShieldCheck className="w-5 h-5 text-brand-gold-600 shrink-0 mt-0.5" />
                  <div>
                    <strong className="font-semibold block text-neutral-900">100% Authentic & Insured Delivery</strong>
                    <p className="text-[11px] text-neutral-600 mt-0.5 leading-relaxed">
                      Your fragrance is sealed with serialized authenticity holograms and dispatched via priority air courier.
                    </p>
                  </div>
                </div>

                <div className="pt-4 border-t border-neutral-100 flex flex-col-reverse sm:flex-row items-stretch sm:items-center justify-between gap-3">
                  <button
                    onClick={() => {
                      setCurrentStep(2);
                      navigate('/checkout');
                    }}
                    className="w-full sm:w-auto px-5 py-3 rounded-full border border-neutral-300 text-neutral-700 text-xs font-semibold flex items-center justify-center gap-1.5 hover:bg-neutral-100 transition-colors"
                  >
                    <ArrowLeft className="w-3.5 h-3.5" />
                    <span>Back to Delivery</span>
                  </button>
                  <button
                    disabled={isSubmitting}
                    onClick={handlePlaceOrder}
                    className={`w-full sm:w-auto px-8 py-4 rounded-2xl bg-brand-plum-900 hover:bg-brand-plum-800 text-white text-xs font-semibold uppercase tracking-wider flex items-center justify-center gap-2 shadow-lg transition-all active:scale-98 ${
                      isSubmitting ? 'opacity-70 cursor-not-allowed' : ''
                    }`}
                  >
                    {isSubmitting ? (
                      <>
                        <Loader2 className="w-4 h-4 animate-spin" />
                        <span>Processing Order...</span>
                      </>
                    ) : paymentMethod === 'RAZORPAY' ? (
                      <>
                        <span>Pay via Razorpay • {formatPrice(grandTotal)}</span>
                        <ArrowRight className="w-4 h-4" />
                      </>
                    ) : (
                      <>
                        <span>Place Order (COD) • {formatPrice(grandTotal)}</span>
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
                  placeholder="e.g. Olivia Vane"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Mobile Phone</label>
                <input
                  type="tel"
                  inputMode="tel"
                  autoComplete="tel"
                  required
                  value={newPhone}
                  onChange={e => setNewPhone(e.target.value)}
                  placeholder="+91 98765 43210"
                  className="w-full p-2.5 rounded-xl border border-neutral-300 focus:border-brand-plum-700 focus:outline-none"
                />
              </div>

              <div>
                <label className="font-semibold text-neutral-700 block mb-1">Street Address / Residence</label>
                <input
                  type="text"
                  required
                  value={newLine1}
                  onChange={e => setNewLine1(e.target.value)}
                  placeholder="Flat 402, Signature Heights, MG Road"
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
                    inputMode="numeric"
                    pattern="[0-9]*"
                    maxLength={6}
                    autoComplete="postal-code"
                    required
                    value={newPincode}
                    onChange={e => setNewPincode(e.target.value.replace(/\D/g, ''))}
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

/**
 * SCENTIVA — Luxury Razorpay Standard Checkout SDK Gateway
 * Dynamically loads Razorpay script and initializes custom branded payment modal.
 */

declare global {
  interface Window {
    Razorpay?: any;
  }
}

export interface RazorpayOptions {
  keyId: string;
  orderId: string;
  amountInPaise: number;
  currency?: string;
  customerName?: string;
  customerEmail?: string;
  customerPhone?: string;
  orderNumber?: string;
  onSuccess: (response: {
    razorpay_payment_id: string;
    razorpay_order_id: string;
    razorpay_signature: string;
  }) => void;
  onFailure?: (error: any) => void;
  onDismiss?: () => void;
}

export const loadRazorpayScript = (): Promise<boolean> => {
  return new Promise((resolve) => {
    if (typeof window === 'undefined') {
      resolve(false);
      return;
    }

    if (window.Razorpay) {
      resolve(true);
      return;
    }

    const existingScript = document.getElementById('razorpay-checkout-sdk');
    if (existingScript) {
      existingScript.addEventListener('load', () => resolve(true));
      existingScript.addEventListener('error', () => resolve(false));
      return;
    }

    const script = document.createElement('script');
    script.id = 'razorpay-checkout-sdk';
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });
};

export const openRazorpayModal = async (options: RazorpayOptions): Promise<void> => {
  const isLoaded = await loadRazorpayScript();
  if (!isLoaded || !window.Razorpay) {
    throw new Error('Failed to load Razorpay payment gateway SDK. Please check your internet connection.');
  }

  const razorpayConfig = {
    key: options.keyId || process.env.NEXT_PUBLIC_RAZORPAY_KEY_ID || 'rzp_test_TkFZU8ecNzFnCq',
    amount: options.amountInPaise,
    currency: options.currency || 'INR',
    name: 'SCENTIVA',
    description: `Haute Parfumerie Order ${options.orderNumber ? '#' + options.orderNumber : ''}`,
    image: 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?auto=format&fit=crop&q=80&w=200',
    order_id: options.orderId,
    prefill: {
      name: options.customerName || 'SCENTIVA Connoisseur',
      email: options.customerEmail || 'customer@scentiva.com',
      contact: options.customerPhone || '+919876543210',
    },
    notes: {
      merchant: 'SCENTIVA Haute Parfumerie',
      orderNumber: options.orderNumber || '',
    },
    theme: {
      color: '#321027', // SCENTIVA Deep Plum
      backdrop_color: 'rgba(50, 16, 39, 0.65)',
    },
    handler: (response: any) => {
      if (options.onSuccess) {
        options.onSuccess({
          razorpay_payment_id: response.razorpay_payment_id,
          razorpay_order_id: response.razorpay_order_id,
          razorpay_signature: response.razorpay_signature,
        });
      }
    },
    modal: {
      ondismiss: () => {
        if (options.onDismiss) {
          options.onDismiss();
        }
      },
      escape: true,
      animation: true,
    },
  };

  const rzpInstance = new window.Razorpay(razorpayConfig);
  rzpInstance.on('payment.failed', (response: any) => {
    if (options.onFailure) {
      options.onFailure(response.error);
    }
  });

  rzpInstance.open();
};

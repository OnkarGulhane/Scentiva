import React from 'react';
import { useStore } from '../../context/StoreContext';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';

export const ToastContainer: React.FC = () => {
  const { toasts, removeToast } = useStore();

  if (toasts.length === 0) return null;

  return (
    <div className="fixed bottom-20 sm:bottom-6 right-4 sm:right-6 z-50 flex flex-col gap-2 max-w-sm w-full pointer-events-none">
      {toasts.map(toast => (
        <div
          key={toast.id}
          className={`pointer-events-auto flex items-center gap-3 p-4 rounded-xl shadow-modal border backdrop-blur-md transition-all duration-300 transform translate-y-0 animate-in fade-in slide-in-from-bottom-4 ${
            toast.type === 'success'
              ? 'bg-neutral-950/95 text-white border-brand-rose-500/30'
              : toast.type === 'error'
              ? 'bg-neutral-950/95 text-white border-semantic-error/40'
              : toast.type === 'warning'
              ? 'bg-neutral-950/95 text-white border-brand-gold-500/40'
              : 'bg-brand-plum-950/95 text-white border-brand-blush-300/20'
          }`}
        >
          {toast.type === 'success' && <CheckCircle2 className="w-5 h-5 text-brand-blush-300 flex-shrink-0" />}
          {toast.type === 'error' && <AlertCircle className="w-5 h-5 text-semantic-error flex-shrink-0" />}
          {toast.type === 'warning' && <AlertCircle className="w-5 h-5 text-brand-gold-500 flex-shrink-0" />}
          {toast.type === 'info' && <Info className="w-5 h-5 text-brand-blush-200 flex-shrink-0" />}
          
          <p className="text-sm font-medium flex-1 leading-snug">{toast.message}</p>
          
          <button
            onClick={() => removeToast(toast.id)}
            className="text-neutral-400 hover:text-white p-1 rounded-md transition-colors"
            aria-label="Close notification"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      ))}
    </div>
  );
};

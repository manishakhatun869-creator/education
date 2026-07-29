import React, { useState } from 'react';
import { Plus, Search, Bookmark, MessageCircle, ArrowUp, Bot, Phone, HelpCircle } from 'lucide-react';
import { useTheme } from '../../context/ThemeContext';

interface FloatingButtonProps {
  onOpenSearch: () => void;
  onOpenSaved: () => void;
  onOpenAiChat: () => void;
}

export const FloatingButton: React.FC<FloatingButtonProps> = ({ onOpenSearch, onOpenSaved, onOpenAiChat }) => {
  const [isOpen, setIsOpen] = useState(false);
  const { settings } = useTheme();

  const rawPhone = settings?.contactPhone ? String(settings.contactPhone) : '9609881733';
  const rawWhatsapp = settings?.whatsappNumber ? String(settings.whatsappNumber) : '9609881733';
  const phoneNum = rawPhone.includes('9876543210') ? '9609881733' : rawPhone;
  const whatsappNum = rawWhatsapp.includes('9876543210') ? '9609881733' : rawWhatsapp;

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
    setIsOpen(false);
  };

  const openPhoneCall = () => {
    window.location.href = `tel:${phoneNum}`;
    setIsOpen(false);
  };

  const openWhatsApp = () => {
    const cleanNum = whatsappNum.replace(/\D/g, '');
    const formattedNum = cleanNum.length === 10 ? `91${cleanNum}` : cleanNum;
    window.open(`https://wa.me/${formattedNum}?text=Hello%20Towfik%20Edutips,%20I%20have%20a%20query%20regarding%20Madhyamik%20Study%20Material.`, '_blank');
    setIsOpen(false);
  };

  return (
    <div className="fixed bottom-20 right-4 z-40 max-w-md mx-auto flex flex-col items-end gap-2">
      {/* Direct AI Tutor Quick FAB Bubble */}
      <button
        onClick={onOpenAiChat}
        className="flex items-center gap-1.5 bg-gradient-to-r from-emerald-600 to-teal-600 text-white px-3.5 py-2 rounded-full shadow-lg text-xs font-bold active:scale-95 transition hover:shadow-xl border border-emerald-400/40"
      >
        <Bot size={18} className="animate-pulse" />
        <span>Ask AI Tutor</span>
      </button>

      {/* Speed Dial Menu */}
      {isOpen && (
        <div className="flex flex-col items-end gap-2.5 mb-1 animate-in fade-in slide-in-from-bottom-5 duration-200">
          {/* Help Helpline Call Button */}
          <button
            onClick={openPhoneCall}
            className="flex items-center gap-2 bg-amber-500 hover:bg-amber-600 text-slate-950 px-3.5 py-2 rounded-full shadow-lg text-xs font-extrabold active:scale-95 transition border border-amber-300"
          >
            <span>Help: Call {phoneNum}</span>
            <div className="w-7 h-7 rounded-full bg-slate-900 flex items-center justify-center text-amber-400">
              <Phone size={15} />
            </div>
          </button>

          {/* Help WhatsApp Chat Button */}
          <button
            onClick={openWhatsApp}
            className="flex items-center gap-2 bg-emerald-500 hover:bg-emerald-600 text-white px-3.5 py-2 rounded-full shadow-lg text-xs font-extrabold active:scale-95 transition border border-emerald-400"
          >
            <span>Help / WhatsApp ({whatsappNum})</span>
            <div className="w-7 h-7 rounded-full bg-emerald-700 flex items-center justify-center text-white">
              <MessageCircle size={15} />
            </div>
          </button>

          <button
            onClick={() => {
              onOpenAiChat();
              setIsOpen(false);
            }}
            className="flex items-center gap-2 bg-teal-600 text-white px-3 py-2 rounded-full shadow-lg text-xs font-semibold active:scale-95 transition"
          >
            <span>AI Study Partner</span>
            <div className="w-7 h-7 rounded-full bg-teal-700 flex items-center justify-center text-white">
              <Bot size={15} />
            </div>
          </button>

          <button
            onClick={() => {
              onOpenSaved();
              setIsOpen(false);
            }}
            className="flex items-center gap-2 bg-slate-700 text-white px-3 py-2 rounded-full shadow-lg text-xs font-semibold active:scale-95 transition"
          >
            <span>Saved Notes</span>
            <div className="w-7 h-7 rounded-full bg-slate-800 flex items-center justify-center text-white">
              <Bookmark size={15} />
            </div>
          </button>

          <button
            onClick={() => {
              onOpenSearch();
              setIsOpen(false);
            }}
            className="flex items-center gap-2 bg-blue-600 text-white px-3 py-2 rounded-full shadow-lg text-xs font-semibold active:scale-95 transition"
          >
            <span>Instant Search</span>
            <div className="w-7 h-7 rounded-full bg-blue-700 flex items-center justify-center text-white">
              <Search size={15} />
            </div>
          </button>

          <button
            onClick={scrollToTop}
            className="flex items-center gap-2 bg-white dark:bg-slate-800 text-slate-800 dark:text-slate-100 px-3 py-2 rounded-full shadow-lg border border-slate-200 dark:border-slate-700 text-xs font-semibold active:scale-95 transition"
          >
            <span>Scroll Top</span>
            <div className="w-7 h-7 rounded-full bg-slate-100 dark:bg-slate-700 flex items-center justify-center text-slate-700 dark:text-slate-200">
              <ArrowUp size={15} />
            </div>
          </button>
        </div>
      )}

      {/* Main Floating Trigger Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className={`w-12 h-12 rounded-full flex items-center justify-center text-white shadow-xl transition-transform duration-200 active:scale-90 ${
          isOpen ? 'bg-slate-800 dark:bg-slate-700 rotate-45' : 'bg-emerald-600 dark:bg-emerald-500 hover:bg-emerald-700'
        }`}
        aria-label="Quick Actions Floating Button"
      >
        <Plus size={24} className="transition-transform duration-200" />
      </button>
    </div>
  );
};

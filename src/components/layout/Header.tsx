import React from 'react';
import { useTheme } from '../../context/ThemeContext';
import { Moon, Sun, Lock, ShieldCheck, Sparkles } from 'lucide-react';

interface HeaderProps {
  onOpenAdminModal: () => void;
  onOpenSearch: () => void;
  isAdminLoggedIn: boolean;
  onAdminLogout: () => void;
}

export const Header: React.FC<HeaderProps> = ({
  onOpenAdminModal,
  onOpenSearch,
  isAdminLoggedIn,
  onAdminLogout
}) => {
  const { isDarkMode, toggleDarkMode, settings } = useTheme();

  return (
    <header className="sticky top-0 z-40 bg-emerald-600 dark:bg-slate-900 text-white shadow-md transition-colors duration-200">
      {/* Notice Banner Bar */}
      {settings.noticeBanner && (
        <div className="bg-amber-400 dark:bg-amber-600 text-slate-900 dark:text-white px-3 py-1 text-xs font-medium flex items-center justify-between overflow-hidden">
          <div className="flex items-center gap-1.5 animate-pulse whitespace-nowrap overflow-hidden text-ellipsis">
            <Sparkles size={13} className="shrink-0 text-amber-900 dark:text-amber-200" />
            <span className="truncate">{settings.noticeBanner}</span>
          </div>
        </div>
      )}

      {/* Main Top App Bar */}
      <div className="max-w-md mx-auto px-4 py-2.5 flex items-center justify-between gap-2">
        <div className="flex items-center gap-2.5">
          <div className="w-10 h-10 rounded-xl bg-white/10 dark:bg-white/5 border border-white/20 overflow-hidden flex items-center justify-center shrink-0 shadow-inner">
            {settings.logoUrl ? (
              <img
                src={settings.logoUrl}
                alt={settings.appName}
                className="w-full h-full object-cover"
                referrerPolicy="no-referrer"
              />
            ) : (
              <span className="text-xl font-black tracking-wider text-amber-300">TE</span>
            )}
          </div>

          <div>
            <h1 className="font-bold text-lg leading-tight tracking-tight text-white flex items-center gap-1.5">
              {settings.appName || 'Towfik Edutips'}
            </h1>
            <p className="text-[10px] text-emerald-100 dark:text-slate-400 font-medium tracking-wide">
              WBBSE MADHYAMIK STUDY PORTAL
            </p>
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex items-center gap-1">
          <button
            onClick={toggleDarkMode}
            className="p-2 rounded-full hover:bg-white/10 active:scale-95 transition"
            title={isDarkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
            aria-label="Toggle Theme"
          >
            {isDarkMode ? <Sun size={20} className="text-amber-300" /> : <Moon size={20} />}
          </button>

          {isAdminLoggedIn ? (
            <button
              onClick={onAdminLogout}
              className="flex items-center gap-1 text-xs font-semibold px-2.5 py-1.5 rounded-lg bg-emerald-700 dark:bg-emerald-800 text-emerald-100 hover:bg-emerald-800 active:scale-95 transition"
              title="Admin Logged In (Click to exit admin view)"
            >
              <ShieldCheck size={16} className="text-amber-300" />
              <span>Admin</span>
            </button>
          ) : (
            <button
              onClick={onOpenAdminModal}
              className="p-2 rounded-full hover:bg-white/10 active:scale-95 transition text-emerald-100 hover:text-white"
              title="Admin Panel Login"
              aria-label="Admin Login"
            >
              <Lock size={19} />
            </button>
          )}
        </div>
      </div>
    </header>
  );
};

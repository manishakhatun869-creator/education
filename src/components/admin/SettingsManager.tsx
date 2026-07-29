import React, { useState, useEffect } from 'react';
import { useTheme } from '../../context/ThemeContext';
import { getAdminCredentials, updateAdminCredentials } from '../../services/db';
import { Settings as SettingsIcon, ShieldCheck, Check, AlertCircle } from 'lucide-react';

export const SettingsManager: React.FC = () => {
  const { settings, updateSettings } = useTheme();

  const [appName, setAppName] = useState(settings.appName || '');
  const [logoUrl, setLogoUrl] = useState(settings.logoUrl || '');
  const [noticeBanner, setNoticeBanner] = useState(settings.noticeBanner || '');
  const [contactEmail, setContactEmail] = useState(settings.contactEmail || '');
  const [contactPhone, setContactPhone] = useState(settings.contactPhone || '');
  const [whatsappNumber, setWhatsappNumber] = useState(settings.whatsappNumber || '');
  const [aboutText, setAboutText] = useState(settings.aboutText || '');

  // Admin Credentials form
  const [adminUsername, setAdminUsername] = useState('');
  const [adminPassword, setAdminPassword] = useState('');
  const [adminId, setAdminId] = useState<string | null>(null);

  const [statusMsg, setStatusMsg] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setAppName(settings.appName || '');
    setLogoUrl(settings.logoUrl || '');
    setNoticeBanner(settings.noticeBanner || '');
    setContactEmail(settings.contactEmail || '');
    setContactPhone(settings.contactPhone || '');
    setWhatsappNumber(settings.whatsappNumber || '');
    setAboutText(settings.aboutText || '');

    getAdminCredentials().then(res => {
      setAdminUsername(res.username);
      setAdminPassword(res.password);
      if (res.id) setAdminId(res.id);
    });
  }, [settings]);

  const handleSaveSettings = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setStatusMsg('');

    try {
      await updateSettings({
        appName,
        logoUrl,
        noticeBanner,
        contactEmail,
        contactPhone,
        whatsappNumber,
        aboutText
      });

      if (adminId) {
        await updateAdminCredentials(adminId, {
          username: adminUsername,
          password: adminPassword
        });
      }

      setStatusMsg('Settings & Admin Credentials updated successfully in Firestore!');
      setTimeout(() => setStatusMsg(''), 3000);
    } catch (err) {
      setStatusMsg('Failed to update settings.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSaveSettings} className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="font-extrabold text-sm text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
          <SettingsIcon size={18} className="text-emerald-600" />
          <span>App & Contact Settings</span>
        </h3>
      </div>

      {statusMsg && (
        <div className="p-3 bg-emerald-50 dark:bg-emerald-950/60 border border-emerald-200 dark:border-emerald-800 text-emerald-800 dark:text-emerald-200 rounded-xl text-xs flex items-center gap-2">
          <Check size={16} />
          <span>{statusMsg}</span>
        </div>
      )}

      {/* General Settings */}
      <div className="p-4 bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 space-y-3">
        <h4 className="text-xs font-bold text-slate-700 dark:text-slate-300">General Branding</h4>

        <div>
          <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Application Name</label>
          <input
            type="text"
            required
            value={appName}
            onChange={e => setAppName(e.target.value)}
            className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
          />
        </div>

        <div>
          <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">App Logo Image URL</label>
          <input
            type="url"
            value={logoUrl}
            onChange={e => setLogoUrl(e.target.value)}
            className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
          />
        </div>

        <div>
          <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Header Announcement / Notice Bar</label>
          <input
            type="text"
            value={noticeBanner}
            onChange={e => setNoticeBanner(e.target.value)}
            placeholder="e.g. WBBSE Madhyamik 2026 Suggestions Released!"
            className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
          />
        </div>
      </div>

      {/* Contact Info */}
      <div className="p-4 bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 space-y-3">
        <h4 className="text-xs font-bold text-slate-700 dark:text-slate-300">Contact & Support Information</h4>

        <div className="grid grid-cols-2 gap-2">
          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Contact Phone</label>
            <input
              type="text"
              value={contactPhone}
              onChange={e => setContactPhone(e.target.value)}
              className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">WhatsApp Number</label>
            <input
              type="text"
              value={whatsappNumber}
              onChange={e => setWhatsappNumber(e.target.value)}
              placeholder="919876543210"
              className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>
        </div>

        <div>
          <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Contact Email</label>
          <input
            type="email"
            value={contactEmail}
            onChange={e => setContactEmail(e.target.value)}
            className="w-full px-3 py-2 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
          />
        </div>
      </div>

      {/* Admin Security */}
      <div className="p-4 bg-amber-50 dark:bg-amber-950/40 border border-amber-200 dark:border-amber-900/50 rounded-2xl space-y-3">
        <h4 className="text-xs font-bold text-amber-900 dark:text-amber-200 flex items-center gap-1.5">
          <ShieldCheck size={16} />
          <span>Admin Credentials (Stored in Firestore)</span>
        </h4>

        <div className="grid grid-cols-2 gap-2">
          <div>
            <label className="block text-[11px] font-bold text-amber-900 dark:text-amber-300 mb-1">Admin Username</label>
            <input
              type="text"
              required
              value={adminUsername}
              onChange={e => setAdminUsername(e.target.value)}
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-amber-200 dark:border-amber-800 rounded-xl text-xs font-bold"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-amber-900 dark:text-amber-300 mb-1">Admin Password</label>
            <input
              type="text"
              required
              value={adminPassword}
              onChange={e => setAdminPassword(e.target.value)}
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-amber-200 dark:border-amber-800 rounded-xl text-xs font-bold"
            />
          </div>
        </div>
      </div>

      <button
        type="submit"
        disabled={loading}
        className="w-full py-3 rounded-2xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-md active:scale-95 transition"
      >
        {loading ? 'Saving Changes...' : 'Save All Settings & Security'}
      </button>
    </form>
  );
};

import React, { createContext, useContext, useEffect, useState } from 'react';
import { AppSettings } from '../types';
import { DEFAULT_SETTINGS, getAppSettings, updateAppSettings as updateSettingsInDb } from '../services/db';

interface ThemeContextType {
  isDarkMode: boolean;
  toggleDarkMode: () => void;
  settings: AppSettings;
  refreshSettings: () => Promise<void>;
  updateSettings: (newSettings: Partial<AppSettings>) => Promise<void>;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isDarkMode, setIsDarkMode] = useState<boolean>(() => {
    const local = localStorage.getItem('towfik_dark_mode');
    if (local !== null) return local === 'true';
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  });

  const [settings, setSettings] = useState<AppSettings>(DEFAULT_SETTINGS);

  const fetchSettings = async () => {
    const res = await getAppSettings();
    setSettings(res);
  };

  useEffect(() => {
    fetchSettings();
  }, []);

  useEffect(() => {
    if (isDarkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
    localStorage.setItem('towfik_dark_mode', String(isDarkMode));
  }, [isDarkMode]);

  const toggleDarkMode = () => {
    setIsDarkMode(prev => !prev);
  };

  const updateSettings = async (newSettings: Partial<AppSettings>) => {
    if (settings.id) {
      await updateSettingsInDb(settings.id, newSettings);
      setSettings(prev => ({ ...prev, ...newSettings }));
    }
  };

  return (
    <ThemeContext.Provider
      value={{
        isDarkMode,
        toggleDarkMode,
        settings,
        refreshSettings: fetchSettings,
        updateSettings
      }}
    >
      {children}
    </ThemeContext.Provider>
  );
};

export const useTheme = () => {
  const ctx = useContext(ThemeContext);
  if (!ctx) throw new Error('useTheme must be used within ThemeProvider');
  return ctx;
};

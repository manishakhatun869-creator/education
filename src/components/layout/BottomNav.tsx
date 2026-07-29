import React from 'react';
import { Home, BookOpen, Award, Bookmark, Search } from 'lucide-react';

export type NavTab = 'home' | 'subjects' | 'suggestions' | 'saved' | 'search';

interface BottomNavProps {
  activeTab: NavTab;
  onChangeTab: (tab: NavTab) => void;
  savedCount: number;
}

export const BottomNav: React.FC<BottomNavProps> = ({ activeTab, onChangeTab, savedCount }) => {
  const tabs = [
    { id: 'home' as NavTab, label: 'Home', icon: Home },
    { id: 'subjects' as NavTab, label: 'Subjects', icon: BookOpen },
    { id: 'suggestions' as NavTab, label: 'Suggestions', icon: Award },
    { id: 'search' as NavTab, label: 'Search', icon: Search },
    { id: 'saved' as NavTab, label: 'Saved', icon: Bookmark, badge: savedCount }
  ];

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 bg-white dark:bg-slate-900 border-t border-slate-200 dark:border-slate-800 shadow-lg transition-colors">
      <div className="max-w-md mx-auto flex items-center justify-around px-2 py-1.5">
        {tabs.map(tab => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => onChangeTab(tab.id)}
              className={`relative flex flex-col items-center justify-center w-full py-1 rounded-xl transition-all duration-200 ${
                isActive
                  ? 'text-emerald-600 dark:text-emerald-400 font-bold'
                  : 'text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
              }`}
            >
              <div
                className={`p-1 rounded-full transition-all ${
                  isActive ? 'bg-emerald-50 dark:bg-emerald-950/60 scale-110' : ''
                }`}
              >
                <Icon size={20} />
              </div>
              <span className="text-[11px] mt-0.5 leading-tight">{tab.label}</span>

              {tab.badge !== undefined && tab.badge > 0 && (
                <span className="absolute top-0 right-3 flex items-center justify-center min-w-[16px] h-[16px] text-[10px] font-extrabold text-white bg-rose-500 rounded-full px-1 shadow-sm">
                  {tab.badge > 99 ? '99+' : tab.badge}
                </span>
              )}
            </button>
          );
        })}
      </div>
    </nav>
  );
};

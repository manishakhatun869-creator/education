import React from 'react';
import { useSaved } from '../../context/SavedContext';
import { Bookmark, Trash2, ChevronRight, BookOpen, FileText, Download } from 'lucide-react';

interface SavedViewProps {
  onSelectChapter: (chapterId: string) => void;
}

export const SavedView: React.FC<SavedViewProps> = ({ onSelectChapter }) => {
  const { savedItems, removeSaved } = useSaved();

  return (
    <div className="space-y-4 pb-16 animate-in fade-in duration-300">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-extrabold text-slate-900 dark:text-slate-100 flex items-center gap-2">
            <Bookmark size={22} className="text-amber-500" />
            <span>Saved Study Notes ({savedItems.length})</span>
          </h2>
          <p className="text-xs text-slate-500 dark:text-slate-400">
            Quick offline access to your saved chapters, formulas, and notes.
          </p>
        </div>
      </div>

      {savedItems.length === 0 ? (
        <div className="p-10 text-center bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 space-y-2">
          <Bookmark size={36} className="mx-auto text-slate-300 dark:text-slate-600" />
          <h3 className="font-bold text-sm text-slate-700 dark:text-slate-300">No Saved Items Yet</h3>
          <p className="text-xs text-slate-500 dark:text-slate-400 max-w-xs mx-auto">
            Tap the "Save" bookmark button on any chapter or note to keep it handy for quick revision.
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {savedItems.map(item => (
            <div
              key={item.id}
              className="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm flex items-center justify-between gap-3 hover:border-amber-400 transition"
            >
              <div
                className="flex-1 cursor-pointer"
                onClick={() => {
                  if (item.chapterId) onSelectChapter(item.chapterId);
                }}
              >
                <span className="text-[10px] font-extrabold px-2 py-0.5 bg-amber-100 dark:bg-amber-950 text-amber-800 dark:text-amber-300 rounded uppercase">
                  {item.itemType}
                </span>
                <h3 className="font-bold text-sm text-slate-900 dark:text-slate-100 mt-1">
                  {item.title}
                </h3>
                {item.subtitle && (
                  <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                    {item.subtitle}
                  </p>
                )}
              </div>

              <div className="flex items-center gap-2">
                {item.chapterId && (
                  <button
                    onClick={() => onSelectChapter(item.chapterId!)}
                    className="p-2 rounded-xl bg-emerald-50 dark:bg-emerald-950 text-emerald-600 dark:text-emerald-400 hover:bg-emerald-100 transition"
                    title="Open Chapter"
                  >
                    <ChevronRight size={18} />
                  </button>
                )}

                <button
                  onClick={() => removeSaved(item.itemId)}
                  className="p-2 rounded-xl bg-rose-50 dark:bg-rose-950 text-rose-600 dark:text-rose-400 hover:bg-rose-100 transition"
                  title="Remove from Saved"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

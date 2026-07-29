import React, { useState } from 'react';
import { Banner } from '../../types';
import { createBanner, updateBanner, deleteBanner } from '../../services/db';
import { Plus, Edit2, Trash2, Eye, EyeOff, Image as ImageIcon, Check, X } from 'lucide-react';

interface BannerManagerProps {
  banners: Banner[];
  onRefresh: () => void;
}

export const BannerManager: React.FC<BannerManagerProps> = ({ banners, onRefresh }) => {
  const [editingBanner, setEditingBanner] = useState<Partial<Banner> | null>(null);
  const [confirmDeleteId, setConfirmDeleteId] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingBanner?.title || !editingBanner?.imageUrl) return;

    setLoading(true);
    try {
      if (editingBanner.id) {
        await updateBanner(editingBanner.id, editingBanner);
      } else {
        await createBanner({
          title: editingBanner.title,
          imageUrl: editingBanner.imageUrl,
          targetUrl: editingBanner.targetUrl || '',
          isVisible: editingBanner.isVisible ?? true,
          order: editingBanner.order || banners.length + 1,
          createdAt: new Date().toISOString()
        });
      }
      setEditingBanner(null);
      onRefresh();
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmDelete = async (id: string) => {
    setDeletingId(id);
    try {
      await deleteBanner(id);
      setConfirmDeleteId(null);
      onRefresh();
    } catch (err) {
      console.error('Delete banner error:', err);
    } finally {
      setDeletingId(null);
    }
  };

  const handleToggleVisibility = async (banner: Banner) => {
    await updateBanner(banner.id, { isVisible: !banner.isVisible });
    onRefresh();
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="font-extrabold text-sm text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
          <ImageIcon size={18} className="text-emerald-600" />
          <span>Home Banner Management ({banners.length})</span>
        </h3>
        <button
          onClick={() => setEditingBanner({ isVisible: true, order: banners.length + 1 })}
          className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-bold text-xs flex items-center gap-1 shadow transition"
        >
          <Plus size={15} />
          <span>Add Banner</span>
        </button>
      </div>

      {/* Editor Modal / Form */}
      {editingBanner && (
        <form onSubmit={handleSave} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 space-y-3">
          <div className="flex items-center justify-between font-bold text-xs">
            <span>{editingBanner.id ? 'Edit Banner' : 'Add New Banner'}</span>
            <button type="button" onClick={() => setEditingBanner(null)} className="text-slate-400 hover:text-slate-600">
              <X size={16} />
            </button>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Banner Title</label>
            <input
              type="text"
              required
              value={editingBanner.title || ''}
              onChange={e => setEditingBanner({ ...editingBanner, title: e.target.value })}
              placeholder="e.g., Madhyamik 2026 Special Suggestions"
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Image URL</label>
            <input
              type="url"
              required
              value={editingBanner.imageUrl || ''}
              onChange={e => setEditingBanner({ ...editingBanner, imageUrl: e.target.value })}
              placeholder="https://images.unsplash.com/..."
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Target Link / Action (Optional)</label>
            <input
              type="text"
              value={editingBanner.targetUrl || ''}
              onChange={e => setEditingBanner({ ...editingBanner, targetUrl: e.target.value })}
              placeholder="#suggestions or URL"
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div className="flex items-center justify-between pt-1">
            <label className="flex items-center gap-2 text-xs font-semibold cursor-pointer">
              <input
                type="checkbox"
                checked={editingBanner.isVisible ?? true}
                onChange={e => setEditingBanner({ ...editingBanner, isVisible: e.target.checked })}
                className="w-4 h-4 rounded text-emerald-600 focus:ring-emerald-500"
              />
              <span>Visible on Home Slider</span>
            </label>

            <button
              type="submit"
              disabled={loading}
              className="px-4 py-1.5 bg-emerald-600 text-white rounded-xl font-bold text-xs shadow hover:bg-emerald-700"
            >
              {loading ? 'Saving...' : 'Save Banner'}
            </button>
          </div>
        </form>
      )}

      {/* List */}
      <div className="space-y-2">
        {banners.map(b => (
          <div
            key={b.id}
            className="p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm flex items-center justify-between gap-3"
          >
            <img src={b.imageUrl} alt={b.title} className="w-16 h-10 rounded-lg object-cover shrink-0" referrerPolicy="no-referrer" />
            <div className="flex-1 min-w-0">
              <h4 className="font-bold text-xs text-slate-900 dark:text-slate-100 truncate">{b.title}</h4>
              <span className={`text-[10px] font-extrabold ${b.isVisible ? 'text-emerald-600' : 'text-slate-400'}`}>
                {b.isVisible ? '● Active' : '○ Hidden'}
              </span>
            </div>

            {confirmDeleteId === b.id ? (
              <div className="flex items-center gap-1 bg-rose-50 dark:bg-rose-950/60 p-1.5 rounded-xl border border-rose-200 dark:border-rose-800">
                <span className="text-[10px] font-bold text-rose-700 dark:text-rose-300 mr-1">Delete Banner?</span>
                <button
                  onClick={() => handleConfirmDelete(b.id)}
                  disabled={deletingId === b.id}
                  className="px-2.5 py-1 bg-rose-600 hover:bg-rose-700 text-white font-bold text-[10px] rounded-lg shadow"
                >
                  {deletingId === b.id ? 'Deleting...' : 'Yes, Delete'}
                </button>
                <button
                  onClick={() => setConfirmDeleteId(null)}
                  className="px-2 py-1 bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-200 font-semibold text-[10px] rounded-lg"
                >
                  Cancel
                </button>
              </div>
            ) : (
              <div className="flex items-center gap-1">
                <button
                  onClick={() => handleToggleVisibility(b)}
                  className="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800"
                  title={b.isVisible ? 'Hide Banner' : 'Show Banner'}
                >
                  {b.isVisible ? <Eye size={16} className="text-emerald-600" /> : <EyeOff size={16} />}
                </button>
                <button
                  onClick={() => setEditingBanner(b)}
                  className="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800"
                  title="Edit Banner"
                >
                  <Edit2 size={16} />
                </button>
                <button
                  onClick={() => setConfirmDeleteId(b.id)}
                  className="p-1.5 rounded-lg text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950"
                  title="Delete Banner"
                >
                  <Trash2 size={16} />
                </button>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

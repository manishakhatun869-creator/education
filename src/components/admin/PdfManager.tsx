import React, { useState } from 'react';
import { Chapter, PdfLink } from '../../types';
import { createPdfLink, updatePdfLink, deletePdfLink } from '../../services/db';
import { Plus, Edit2, Trash2, Download, X } from 'lucide-react';

interface PdfManagerProps {
  chapters: Chapter[];
  pdfLinks: PdfLink[];
  onRefresh: () => void;
}

export const PdfManager: React.FC<PdfManagerProps> = ({ chapters, pdfLinks, onRefresh }) => {
  const [editingPdf, setEditingPdf] = useState<Partial<PdfLink> | null>(null);
  const [confirmDeleteId, setConfirmDeleteId] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingPdf?.title || !editingPdf?.url || !editingPdf?.chapterId) return;

    const ch = chapters.find(c => c.id === editingPdf.chapterId);

    setLoading(true);
    try {
      if (editingPdf.id) {
        await updatePdfLink(editingPdf.id, {
          ...editingPdf,
          subjectId: ch?.subjectId
        });
      } else {
        await createPdfLink({
          chapterId: editingPdf.chapterId,
          subjectId: ch?.subjectId || '',
          title: editingPdf.title,
          url: editingPdf.url,
          size: editingPdf.size || '2.5 MB',
          description: editingPdf.description || '',
          createdAt: new Date().toISOString()
        });
      }
      setEditingPdf(null);
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
      await deletePdfLink(id);
      setConfirmDeleteId(null);
      onRefresh();
    } catch (err) {
      console.error('Delete pdf link error:', err);
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="font-extrabold text-sm text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
          <Download size={18} className="text-emerald-600" />
          <span>PDF Links Management ({pdfLinks.length})</span>
        </h3>
        <button
          onClick={() => setEditingPdf({ chapterId: chapters[0]?.id || '' })}
          className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-bold text-xs flex items-center gap-1 shadow transition"
        >
          <Plus size={15} />
          <span>Add PDF Link</span>
        </button>
      </div>

      {editingPdf && (
        <form onSubmit={handleSave} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 space-y-3">
          <div className="flex items-center justify-between font-bold text-xs">
            <span>{editingPdf.id ? 'Edit PDF Link' : 'Add New PDF Link'}</span>
            <button type="button" onClick={() => setEditingPdf(null)} className="text-slate-400 hover:text-slate-600">
              <X size={16} />
            </button>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Select Chapter</label>
            <select
              required
              value={editingPdf.chapterId || ''}
              onChange={e => setEditingPdf({ ...editingPdf, chapterId: e.target.value })}
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            >
              <option value="" disabled>-- Select Chapter --</option>
              {chapters.map(c => (
                <option key={c.id} value={c.id}>{c.subjectName} - {c.chapterName}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">PDF Title</label>
            <input
              type="text"
              required
              value={editingPdf.title || ''}
              onChange={e => setEditingPdf({ ...editingPdf, title: e.target.value })}
              placeholder="e.g., Gyan Chokkhu Complete Solved PDF"
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Direct PDF URL</label>
            <input
              type="url"
              required
              value={editingPdf.url || ''}
              onChange={e => setEditingPdf({ ...editingPdf, url: e.target.value })}
              placeholder="https://example.com/file.pdf"
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Estimated Size (Optional)</label>
            <input
              type="text"
              value={editingPdf.size || ''}
              onChange={e => setEditingPdf({ ...editingPdf, size: e.target.value })}
              placeholder="e.g. 3.2 MB"
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div className="flex justify-end pt-2">
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-1.5 bg-emerald-600 text-white rounded-xl font-bold text-xs shadow hover:bg-emerald-700"
            >
              {loading ? 'Saving...' : 'Save PDF Link'}
            </button>
          </div>
        </form>
      )}

      <div className="space-y-2">
        {pdfLinks.map(pdf => (
          <div
            key={pdf.id}
            className="p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm flex items-center justify-between gap-3"
          >
            <div className="flex-1 min-w-0">
              <h4 className="font-bold text-xs text-slate-900 dark:text-slate-100 truncate">{pdf.title}</h4>
              <p className="text-[10px] text-slate-400 truncate">{pdf.url}</p>
            </div>

            {confirmDeleteId === pdf.id ? (
              <div className="flex items-center gap-1 bg-rose-50 dark:bg-rose-950/60 p-1.5 rounded-xl border border-rose-200 dark:border-rose-800">
                <span className="text-[10px] font-bold text-rose-700 dark:text-rose-300 mr-1">Delete PDF?</span>
                <button
                  onClick={() => handleConfirmDelete(pdf.id)}
                  disabled={deletingId === pdf.id}
                  className="px-2.5 py-1 bg-rose-600 hover:bg-rose-700 text-white font-bold text-[10px] rounded-lg shadow"
                >
                  {deletingId === pdf.id ? 'Deleting...' : 'Yes, Delete'}
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
                  onClick={() => setEditingPdf(pdf)}
                  className="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800"
                  title="Edit PDF Link"
                >
                  <Edit2 size={16} />
                </button>
                <button
                  onClick={() => setConfirmDeleteId(pdf.id)}
                  className="p-1.5 rounded-lg text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950"
                  title="Delete PDF Link"
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

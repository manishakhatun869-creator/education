import React, { useState } from 'react';
import { Subject, Chapter } from '../../types';
import { createChapter, updateChapter, deleteChapter } from '../../services/db';
import { Plus, Edit2, Trash2, FileText, X } from 'lucide-react';

interface ChapterManagerProps {
  subjects: Subject[];
  chapters: Chapter[];
  onRefresh: () => void;
}

export const ChapterManager: React.FC<ChapterManagerProps> = ({ subjects, chapters, onRefresh }) => {
  const [editingChapter, setEditingChapter] = useState<Partial<Chapter> | null>(null);
  const [confirmDeleteId, setConfirmDeleteId] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingChapter?.chapterName || !editingChapter?.subjectId) return;

    const parentSubject = subjects.find(s => s.id === editingChapter.subjectId);

    setLoading(true);
    try {
      if (editingChapter.id) {
        await updateChapter(editingChapter.id, {
          ...editingChapter,
          subjectName: parentSubject?.name || editingChapter.subjectName
        });
      } else {
        await createChapter({
          subjectId: editingChapter.subjectId,
          subjectName: parentSubject?.name || '',
          chapterName: editingChapter.chapterName,
          imageUrl: editingChapter.imageUrl || 'https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&q=80&w=600',
          description: editingChapter.description || '',
          order: editingChapter.order || chapters.length + 1,
          pdfUrl: editingChapter.pdfUrl || '',
          pdfTitle: editingChapter.pdfTitle || '',
          createdAt: new Date().toISOString()
        });
      }
      setEditingChapter(null);
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
      await deleteChapter(id);
      setConfirmDeleteId(null);
      onRefresh();
    } catch (err) {
      console.error('Delete chapter error:', err);
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="font-extrabold text-sm text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
          <FileText size={18} className="text-emerald-600" />
          <span>Chapter Management ({chapters.length})</span>
        </h3>
        <button
          onClick={() => setEditingChapter({ subjectId: subjects[0]?.id || '', order: chapters.length + 1 })}
          className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-bold text-xs flex items-center gap-1 shadow transition"
        >
          <Plus size={15} />
          <span>Add Chapter</span>
        </button>
      </div>

      {editingChapter && (
        <form onSubmit={handleSave} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 space-y-3">
          <div className="flex items-center justify-between font-bold text-xs">
            <span>{editingChapter.id ? 'Edit Chapter' : 'Add New Chapter'}</span>
            <button type="button" onClick={() => setEditingChapter(null)} className="text-slate-400 hover:text-slate-600">
              <X size={16} />
            </button>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Select Subject</label>
            <select
              required
              value={editingChapter.subjectId || ''}
              onChange={e => setEditingChapter({ ...editingChapter, subjectId: e.target.value })}
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            >
              <option value="" disabled>-- Select Subject --</option>
              {subjects.map(s => (
                <option key={s.id} value={s.id}>{s.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Chapter Name</label>
            <input
              type="text"
              required
              value={editingChapter.chapterName || ''}
              onChange={e => setEditingChapter({ ...editingChapter, chapterName: e.target.value })}
              placeholder="e.g., Chapter 1: Gyan Chokkhu"
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Image Banner URL</label>
            <input
              type="url"
              value={editingChapter.imageUrl || ''}
              onChange={e => setEditingChapter({ ...editingChapter, imageUrl: e.target.value })}
              placeholder="https://images.unsplash.com/..."
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Full Chapter PDF Link URL</label>
            <input
              type="url"
              value={editingChapter.pdfUrl || ''}
              onChange={e => setEditingChapter({ ...editingChapter, pdfUrl: e.target.value })}
              placeholder="https://example.com/chapter-notes.pdf"
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Chapter Description</label>
            <textarea
              rows={2}
              value={editingChapter.description || ''}
              onChange={e => setEditingChapter({ ...editingChapter, description: e.target.value })}
              placeholder="Summary of what this chapter covers..."
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div className="flex justify-end pt-2">
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-1.5 bg-emerald-600 text-white rounded-xl font-bold text-xs shadow hover:bg-emerald-700"
            >
              {loading ? 'Saving...' : 'Save Chapter'}
            </button>
          </div>
        </form>
      )}

      <div className="space-y-2">
        {chapters.map(ch => (
          <div
            key={ch.id}
            className="p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm flex items-center justify-between gap-3"
          >
            <div className="flex-1 min-w-0">
              <span className="text-[9px] font-bold text-emerald-600 dark:text-emerald-400 uppercase">
                {ch.subjectName || 'Subject'}
              </span>
              <h4 className="font-bold text-xs text-slate-900 dark:text-slate-100 truncate">{ch.chapterName}</h4>
            </div>

            {confirmDeleteId === ch.id ? (
              <div className="flex items-center gap-1 bg-rose-50 dark:bg-rose-950/60 p-1.5 rounded-xl border border-rose-200 dark:border-rose-800">
                <span className="text-[10px] font-bold text-rose-700 dark:text-rose-300 mr-1">Delete Chapter?</span>
                <button
                  onClick={() => handleConfirmDelete(ch.id)}
                  disabled={deletingId === ch.id}
                  className="px-2.5 py-1 bg-rose-600 hover:bg-rose-700 text-white font-bold text-[10px] rounded-lg shadow"
                >
                  {deletingId === ch.id ? 'Deleting...' : 'Yes, Delete'}
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
                  onClick={() => setEditingChapter(ch)}
                  className="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800"
                  title="Edit Chapter"
                >
                  <Edit2 size={16} />
                </button>
                <button
                  onClick={() => setConfirmDeleteId(ch.id)}
                  className="p-1.5 rounded-lg text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950"
                  title="Delete Chapter"
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

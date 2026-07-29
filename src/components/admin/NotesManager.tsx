import React, { useState } from 'react';
import { Chapter, Note } from '../../types';
import { createNote, updateNote, deleteNote } from '../../services/db';
import { Plus, Edit2, Trash2, BookOpen, X } from 'lucide-react';

interface NotesManagerProps {
  chapters: Chapter[];
  notes: Note[];
  onRefresh: () => void;
}

export const NotesManager: React.FC<NotesManagerProps> = ({ chapters, notes, onRefresh }) => {
  const [editingNote, setEditingNote] = useState<Partial<Note> | null>(null);
  const [confirmDeleteId, setConfirmDeleteId] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingNote?.title || !editingNote?.content || !editingNote?.chapterId) return;

    const ch = chapters.find(c => c.id === editingNote.chapterId);

    setLoading(true);
    try {
      if (editingNote.id) {
        await updateNote(editingNote.id, {
          ...editingNote,
          subjectId: ch?.subjectId
        });
      } else {
        await createNote({
          chapterId: editingNote.chapterId,
          subjectId: ch?.subjectId || '',
          title: editingNote.title,
          content: editingNote.content,
          type: editingNote.type || 'summary',
          order: editingNote.order || notes.length + 1,
          createdAt: new Date().toISOString()
        });
      }
      setEditingNote(null);
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
      await deleteNote(id);
      setConfirmDeleteId(null);
      onRefresh();
    } catch (err) {
      console.error('Delete note error:', err);
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="font-extrabold text-sm text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
          <BookOpen size={18} className="text-emerald-600" />
          <span>Notes Management ({notes.length})</span>
        </h3>
        <button
          onClick={() => setEditingNote({ chapterId: chapters[0]?.id || '', type: 'summary', order: notes.length + 1 })}
          className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-bold text-xs flex items-center gap-1 shadow transition"
        >
          <Plus size={15} />
          <span>Add Note</span>
        </button>
      </div>

      {editingNote && (
        <form onSubmit={handleSave} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 space-y-3">
          <div className="flex items-center justify-between font-bold text-xs">
            <span>{editingNote.id ? 'Edit Note' : 'Add New Note'}</span>
            <button type="button" onClick={() => setEditingNote(null)} className="text-slate-400 hover:text-slate-600">
              <X size={16} />
            </button>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Select Chapter</label>
            <select
              required
              value={editingNote.chapterId || ''}
              onChange={e => setEditingNote({ ...editingNote, chapterId: e.target.value })}
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            >
              <option value="" disabled>-- Select Chapter --</option>
              {chapters.map(c => (
                <option key={c.id} value={c.id}>{c.subjectName} - {c.chapterName}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Note Title</label>
            <input
              type="text"
              required
              value={editingNote.title || ''}
              onChange={e => setEditingNote({ ...editingNote, title: e.target.value })}
              placeholder="e.g., Key Formula Sheet or Summary"
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Type Tag</label>
            <select
              value={editingNote.type || 'summary'}
              onChange={e => setEditingNote({ ...editingNote, type: e.target.value as any })}
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            >
              <option value="summary">Summary</option>
              <option value="formula">Formula Sheet</option>
              <option value="concept">Concept Breakdown</option>
              <option value="keypoint">Key Point</option>
            </select>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Note Content</label>
            <textarea
              rows={4}
              required
              value={editingNote.content || ''}
              onChange={e => setEditingNote({ ...editingNote, content: e.target.value })}
              placeholder="Enter comprehensive note content..."
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div className="flex justify-end pt-2">
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-1.5 bg-emerald-600 text-white rounded-xl font-bold text-xs shadow hover:bg-emerald-700"
            >
              {loading ? 'Saving...' : 'Save Note'}
            </button>
          </div>
        </form>
      )}

      <div className="space-y-2">
        {notes.map(n => {
          const parentChapter = chapters.find(c => c.id === n.chapterId);
          return (
            <div
              key={n.id}
              className="p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm flex items-center justify-between gap-3"
            >
              <div className="flex-1 min-w-0">
                <span className="text-[9px] font-bold text-emerald-600 dark:text-emerald-400 uppercase">
                  {parentChapter?.chapterName || 'Chapter Note'}
                </span>
                <h4 className="font-bold text-xs text-slate-900 dark:text-slate-100 truncate">{n.title}</h4>
              </div>

              {confirmDeleteId === n.id ? (
                <div className="flex items-center gap-1 bg-rose-50 dark:bg-rose-950/60 p-1.5 rounded-xl border border-rose-200 dark:border-rose-800">
                  <span className="text-[10px] font-bold text-rose-700 dark:text-rose-300 mr-1">Delete Note?</span>
                  <button
                    onClick={() => handleConfirmDelete(n.id)}
                    disabled={deletingId === n.id}
                    className="px-2.5 py-1 bg-rose-600 hover:bg-rose-700 text-white font-bold text-[10px] rounded-lg shadow"
                  >
                    {deletingId === n.id ? 'Deleting...' : 'Yes, Delete'}
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
                    onClick={() => setEditingNote(n)}
                    className="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800"
                    title="Edit Note"
                  >
                    <Edit2 size={16} />
                  </button>
                  <button
                    onClick={() => setConfirmDeleteId(n.id)}
                    className="p-1.5 rounded-lg text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950"
                    title="Delete Note"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};

import React, { useState } from 'react';
import { Subject } from '../../types';
import { createSubject, updateSubject, deleteSubject } from '../../services/db';
import { Plus, Edit2, Trash2, BookOpen, X } from 'lucide-react';

interface SubjectManagerProps {
  subjects: Subject[];
  onRefresh: () => void;
}

const COLOR_OPTIONS = ['#E53935', '#1E88E5', '#43A047', '#FB8C00', '#00ACC1', '#8E24AA', '#D81B60', '#3F51B5'];
const ICON_OPTIONS = ['BookOpen', 'Languages', 'Calculator', 'Atom', 'Dna', 'Landmark', 'Globe'];

export const SubjectManager: React.FC<SubjectManagerProps> = ({ subjects, onRefresh }) => {
  const [editingSubject, setEditingSubject] = useState<Partial<Subject> | null>(null);
  const [confirmDeleteId, setConfirmDeleteId] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingSubject?.name) return;

    setLoading(true);
    try {
      if (editingSubject.id) {
        await updateSubject(editingSubject.id, editingSubject);
      } else {
        await createSubject({
          name: editingSubject.name,
          code: editingSubject.code || editingSubject.name.substring(0, 3).toUpperCase(),
          icon: editingSubject.icon || 'BookOpen',
          color: editingSubject.color || '#10B981',
          description: editingSubject.description || '',
          order: editingSubject.order || subjects.length + 1
        });
      }
      setEditingSubject(null);
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
      await deleteSubject(id);
      setConfirmDeleteId(null);
      onRefresh();
    } catch (err) {
      console.error('Delete subject error:', err);
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="font-extrabold text-sm text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
          <BookOpen size={18} className="text-emerald-600" />
          <span>Subject Management ({subjects.length})</span>
        </h3>
        <button
          onClick={() => setEditingSubject({ color: '#10B981', icon: 'BookOpen', order: subjects.length + 1 })}
          className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-bold text-xs flex items-center gap-1 shadow transition"
        >
          <Plus size={15} />
          <span>Add Subject</span>
        </button>
      </div>

      {editingSubject && (
        <form onSubmit={handleSave} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 space-y-3">
          <div className="flex items-center justify-between font-bold text-xs">
            <span>{editingSubject.id ? 'Edit Subject' : 'Add New Subject'}</span>
            <button type="button" onClick={() => setEditingSubject(null)} className="text-slate-400 hover:text-slate-600">
              <X size={16} />
            </button>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Subject Name</label>
            <input
              type="text"
              required
              value={editingSubject.name || ''}
              onChange={e => setEditingSubject({ ...editingSubject, name: e.target.value })}
              placeholder="e.g., Computer Application"
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">
              Subject Logo Image URL (Optional - Custom Image Logo)
            </label>
            <div className="flex items-center gap-2">
              <input
                type="url"
                value={editingSubject.logoUrl || ''}
                onChange={e => setEditingSubject({ ...editingSubject, logoUrl: e.target.value })}
                placeholder="https://example.com/subject-logo.png"
                className="flex-1 px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
              />
              {editingSubject.logoUrl && (
                <img
                  src={editingSubject.logoUrl}
                  alt="Subject logo preview"
                  className="w-8 h-8 rounded-lg object-cover border border-slate-300 dark:border-slate-700 shrink-0"
                  referrerPolicy="no-referrer"
                />
              )}
            </div>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Description</label>
            <input
              type="text"
              value={editingSubject.description || ''}
              onChange={e => setEditingSubject({ ...editingSubject, description: e.target.value })}
              placeholder="e.g., Madhyamik Computer Syllabus"
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div className="grid grid-cols-2 gap-2">
            <div>
              <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Color Accent</label>
              <div className="flex items-center gap-1.5 flex-wrap">
                {COLOR_OPTIONS.map(c => (
                  <button
                    key={c}
                    type="button"
                    onClick={() => setEditingSubject({ ...editingSubject, color: c })}
                    className={`w-6 h-6 rounded-full border-2 transition ${
                      editingSubject.color === c ? 'border-slate-900 scale-110' : 'border-transparent'
                    }`}
                    style={{ backgroundColor: c }}
                  />
                ))}
              </div>
            </div>

            <div>
              <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Icon</label>
              <select
                value={editingSubject.icon || 'BookOpen'}
                onChange={e => setEditingSubject({ ...editingSubject, icon: e.target.value })}
                className="w-full px-2 py-1.5 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
              >
                {ICON_OPTIONS.map(i => (
                  <option key={i} value={i}>{i}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="flex justify-end pt-2">
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-1.5 bg-emerald-600 text-white rounded-xl font-bold text-xs shadow hover:bg-emerald-700"
            >
              {loading ? 'Saving...' : 'Save Subject'}
            </button>
          </div>
        </form>
      )}

      <div className="space-y-2">
        {subjects.map(s => (
          <div
            key={s.id}
            className="p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm flex items-center justify-between"
          >
            <div className="flex items-center gap-3">
              {s.logoUrl ? (
                <img
                  src={s.logoUrl}
                  alt={s.name}
                  className="w-8 h-8 rounded-lg object-cover border border-slate-200 dark:border-slate-700 shrink-0"
                  referrerPolicy="no-referrer"
                />
              ) : (
                <div className="w-8 h-8 rounded-lg flex items-center justify-center text-white shrink-0" style={{ backgroundColor: s.color }}>
                  <BookOpen size={16} />
                </div>
              )}
              <div>
                <h4 className="font-bold text-xs text-slate-900 dark:text-slate-100">{s.name}</h4>
                <p className="text-[10px] text-slate-400">{s.description || 'No description'}</p>
              </div>
            </div>

            {confirmDeleteId === s.id ? (
              <div className="flex items-center gap-1 bg-rose-50 dark:bg-rose-950/60 p-1.5 rounded-xl border border-rose-200 dark:border-rose-800">
                <span className="text-[10px] font-bold text-rose-700 dark:text-rose-300 mr-1">Delete Subject?</span>
                <button
                  onClick={() => handleConfirmDelete(s.id)}
                  disabled={deletingId === s.id}
                  className="px-2.5 py-1 bg-rose-600 hover:bg-rose-700 text-white font-bold text-[10px] rounded-lg shadow"
                >
                  {deletingId === s.id ? 'Deleting...' : 'Yes, Delete'}
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
                  onClick={() => setEditingSubject(s)}
                  className="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800"
                  title="Edit Subject"
                >
                  <Edit2 size={16} />
                </button>
                <button
                  onClick={() => setConfirmDeleteId(s.id)}
                  className="p-1.5 rounded-lg text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950"
                  title="Delete Subject"
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

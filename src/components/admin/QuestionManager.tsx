import React, { useState } from 'react';
import { Chapter, Question, QuestionCategory } from '../../types';
import { createQuestion, updateQuestion, deleteQuestion } from '../../services/db';
import { Plus, Edit2, Trash2, HelpCircle, X } from 'lucide-react';

interface QuestionManagerProps {
  chapters: Chapter[];
  questions: Question[];
  onRefresh: () => void;
}

export const QuestionManager: React.FC<QuestionManagerProps> = ({ chapters, questions, onRefresh }) => {
  const [editingQuestion, setEditingQuestion] = useState<Partial<Question> | null>(null);
  const [confirmDeleteId, setConfirmDeleteId] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  // MCQ options state
  const [opt0, setOpt0] = useState('');
  const [opt1, setOpt1] = useState('');
  const [opt2, setOpt2] = useState('');
  const [opt3, setOpt3] = useState('');

  const handleOpenEdit = (q?: Question) => {
    if (q) {
      setEditingQuestion(q);
      const opts = q.options || ['', '', '', ''];
      setOpt0(opts[0] || '');
      setOpt1(opts[1] || '');
      setOpt2(opts[2] || '');
      setOpt3(opts[3] || '');
    } else {
      setEditingQuestion({
        chapterId: chapters[0]?.id || '',
        category: 'short',
        marks: 2,
        order: questions.length + 1
      });
      setOpt0('');
      setOpt1('');
      setOpt2('');
      setOpt3('');
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingQuestion?.questionText || !editingQuestion?.chapterId || !editingQuestion?.category) return;

    const ch = chapters.find(c => c.id === editingQuestion.chapterId);

    let finalOptions: string[] | undefined = undefined;
    if (editingQuestion.category === 'mcq') {
      finalOptions = [opt0, opt1, opt2, opt3].filter(o => o.trim() !== '');
    }

    setLoading(true);
    try {
      if (editingQuestion.id) {
        await updateQuestion(editingQuestion.id, {
          ...editingQuestion,
          subjectId: ch?.subjectId,
          options: finalOptions
        });
      } else {
        await createQuestion({
          chapterId: editingQuestion.chapterId,
          subjectId: ch?.subjectId || '',
          questionText: editingQuestion.questionText,
          answerText: editingQuestion.answerText || '',
          category: editingQuestion.category as QuestionCategory,
          options: finalOptions,
          correctOptionIndex: editingQuestion.correctOptionIndex ?? 0,
          marks: editingQuestion.marks || 1,
          year: editingQuestion.year || '',
          order: editingQuestion.order || questions.length + 1,
          createdAt: new Date().toISOString()
        });
      }
      setEditingQuestion(null);
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
      await deleteQuestion(id);
      setConfirmDeleteId(null);
      onRefresh();
    } catch (err) {
      console.error('Delete question error:', err);
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="font-extrabold text-sm text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
          <HelpCircle size={18} className="text-emerald-600" />
          <span>Question Bank Management ({questions.length})</span>
        </h3>
        <button
          onClick={() => handleOpenEdit()}
          className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-bold text-xs flex items-center gap-1 shadow transition"
        >
          <Plus size={15} />
          <span>Add Question</span>
        </button>
      </div>

      {editingQuestion && (
        <form onSubmit={handleSave} className="p-4 rounded-2xl bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 space-y-3">
          <div className="flex items-center justify-between font-bold text-xs">
            <span>{editingQuestion.id ? 'Edit Question' : 'Add New Question'}</span>
            <button type="button" onClick={() => setEditingQuestion(null)} className="text-slate-400 hover:text-slate-600">
              <X size={16} />
            </button>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Select Chapter</label>
            <select
              required
              value={editingQuestion.chapterId || ''}
              onChange={e => setEditingQuestion({ ...editingQuestion, chapterId: e.target.value })}
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            >
              <option value="" disabled>-- Select Chapter --</option>
              {chapters.map(c => (
                <option key={c.id} value={c.id}>{c.subjectName} - {c.chapterName}</option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-2 gap-2">
            <div>
              <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Category</label>
              <select
                value={editingQuestion.category || 'short'}
                onChange={e => setEditingQuestion({ ...editingQuestion, category: e.target.value as any })}
                className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-semibold"
              >
                <option value="mcq">MCQ (Multiple Choice)</option>
                <option value="short">Short Question (1-2 Marks)</option>
                <option value="long">Long Question (3-5 Marks)</option>
                <option value="important">Important Question</option>
                <option value="pyq">Previous Year Question (PYQ)</option>
                <option value="madhyamik_suggestion">🏆 Madhyamik Suggestion</option>
              </select>
            </div>

            <div>
              <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Marks Weightage</label>
              <input
                type="number"
                min={1}
                max={10}
                value={editingQuestion.marks || 1}
                onChange={e => setEditingQuestion({ ...editingQuestion, marks: parseInt(e.target.value) || 1 })}
                className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
              />
            </div>
          </div>

          {editingQuestion.category === 'pyq' && (
            <div>
              <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Year Tag (e.g. 2024)</label>
              <input
                type="text"
                value={editingQuestion.year || ''}
                onChange={e => setEditingQuestion({ ...editingQuestion, year: e.target.value })}
                placeholder="e.g. 2024 or 2023"
                className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
              />
            </div>
          )}

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Question Text</label>
            <textarea
              rows={2}
              required
              value={editingQuestion.questionText || ''}
              onChange={e => setEditingQuestion({ ...editingQuestion, questionText: e.target.value })}
              placeholder="Enter question statement..."
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          {/* MCQ Options Fields */}
          {editingQuestion.category === 'mcq' && (
            <div className="p-3 bg-white dark:bg-slate-900 rounded-xl border border-slate-200 dark:border-slate-700 space-y-2">
              <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400">MCQ Choices & Correct Key</label>
              <div className="space-y-1.5">
                {[
                  { val: opt0, set: setOpt0, idx: 0, label: 'Option A' },
                  { val: opt1, set: setOpt1, idx: 1, label: 'Option B' },
                  { val: opt2, set: setOpt2, idx: 2, label: 'Option C' },
                  { val: opt3, set: setOpt3, idx: 3, label: 'Option D' }
                ].map(item => (
                  <div key={item.idx} className="flex items-center gap-2">
                    <input
                      type="radio"
                      name="correctOpt"
                      checked={editingQuestion.correctOptionIndex === item.idx}
                      onChange={() => setEditingQuestion({ ...editingQuestion, correctOptionIndex: item.idx })}
                      title="Set as correct answer"
                    />
                    <input
                      type="text"
                      value={item.val}
                      onChange={e => item.set(e.target.value)}
                      placeholder={item.label}
                      className="flex-1 px-2.5 py-1 bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg text-xs"
                    />
                  </div>
                ))}
              </div>
            </div>
          )}

          <div>
            <label className="block text-[11px] font-bold text-slate-600 dark:text-slate-400 mb-1">Answer Text / Solution / Key</label>
            <textarea
              rows={3}
              value={editingQuestion.answerText || ''}
              onChange={e => setEditingQuestion({ ...editingQuestion, answerText: e.target.value })}
              placeholder="Provide complete answer or explanation..."
              className="w-full px-3 py-2 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-xs"
            />
          </div>

          <div className="flex justify-end pt-2">
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-1.5 bg-emerald-600 text-white rounded-xl font-bold text-xs shadow hover:bg-emerald-700"
            >
              {loading ? 'Saving...' : 'Save Question'}
            </button>
          </div>
        </form>
      )}

      <div className="space-y-2">
        {questions.map(q => (
          <div
            key={q.id}
            className="p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm flex items-center justify-between gap-3"
          >
            <div className="flex-1 min-w-0">
              <span className="text-[9px] font-bold text-emerald-600 dark:text-emerald-400 uppercase">
                {q.category}
              </span>
              <h4 className="font-bold text-xs text-slate-900 dark:text-slate-100 truncate">{q.questionText}</h4>
            </div>

            {confirmDeleteId === q.id ? (
              <div className="flex items-center gap-1 bg-rose-50 dark:bg-rose-950/60 p-1.5 rounded-xl border border-rose-200 dark:border-rose-800">
                <span className="text-[10px] font-bold text-rose-700 dark:text-rose-300 mr-1">Delete Question?</span>
                <button
                  onClick={() => handleConfirmDelete(q.id)}
                  disabled={deletingId === q.id}
                  className="px-2.5 py-1 bg-rose-600 hover:bg-rose-700 text-white font-bold text-[10px] rounded-lg shadow"
                >
                  {deletingId === q.id ? 'Deleting...' : 'Yes, Delete'}
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
                  onClick={() => handleOpenEdit(q)}
                  className="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800"
                  title="Edit Question"
                >
                  <Edit2 size={16} />
                </button>
                <button
                  onClick={() => setConfirmDeleteId(q.id)}
                  className="p-1.5 rounded-lg text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950"
                  title="Delete Question"
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

import React, { useState } from 'react';
import { Subject, Chapter, Question } from '../../types';
import { Award, Sparkles, HelpCircle, ChevronRight, BookOpen, Download, FileText } from 'lucide-react';
import { generateSuggestionsPdfHtml } from '../../utils/pdfGenerator';
import { PdfViewerModal } from '../common/PdfViewerModal';

interface SuggestionsViewProps {
  subjects: Subject[];
  chapters: Chapter[];
  questions: Question[];
  onSelectChapter: (chapterId: string) => void;
}

export const SuggestionsView: React.FC<SuggestionsViewProps> = ({
  subjects,
  chapters,
  questions,
  onSelectChapter
}) => {
  const [selectedSubjectId, setSelectedSubjectId] = useState<string>('all');
  const [selectedCategory, setSelectedCategory] = useState<'all' | 'madhyamik_suggestion' | 'pyq' | 'important'>('all');
  const [pdfModalOpen, setPdfModalOpen] = useState(false);
  const [pdfModalData, setPdfModalData] = useState<{ title: string; subtitle?: string; htmlContent?: string }>({ title: '' });

  let filtered = questions.filter(
    q => q.category === 'madhyamik_suggestion' || q.category === 'pyq' || q.category === 'important'
  );

  if (selectedSubjectId !== 'all') {
    const subjectChapters = chapters.filter(c => c.subjectId === selectedSubjectId).map(c => c.id);
    filtered = filtered.filter(q => subjectChapters.includes(q.chapterId));
  }

  if (selectedCategory !== 'all') {
    filtered = filtered.filter(q => q.category === selectedCategory);
  }

  const handleOpenPdfModal = (qsToPdf: Question[], customTitle?: string) => {
    const html = generateSuggestionsPdfHtml(qsToPdf, chapters, subjects);
    const subName = selectedSubjectId !== 'all' ? subjects.find(s => s.id === selectedSubjectId)?.name : 'All Subjects';
    setPdfModalData({
      title: customTitle || `Madhyamik 2026 Suggestions PDF (${subName})`,
      subtitle: `Total ${qsToPdf.length} Solved Questions & Exam Predictions`,
      htmlContent: html
    });
    setPdfModalOpen(true);
  };

  return (
    <div className="space-y-4 pb-16 animate-in fade-in duration-300">
      <PdfViewerModal
        isOpen={pdfModalOpen}
        title={pdfModalData.title}
        subtitle={pdfModalData.subtitle}
        htmlContent={pdfModalData.htmlContent}
        onClose={() => setPdfModalOpen(false)}
      />

      <div className="p-4 rounded-2xl bg-gradient-to-r from-amber-500 to-orange-600 text-white shadow-md">
        <div className="flex items-center justify-between gap-2 mb-1">
          <div className="flex items-center gap-2">
            <Award size={24} />
            <h2 className="text-lg font-extrabold">WBBSE Madhyamik Suggestions</h2>
          </div>
          <button
            onClick={() => handleOpenPdfModal(filtered)}
            className="px-3 py-1.5 bg-white text-amber-900 rounded-xl font-bold text-xs flex items-center gap-1.5 shadow hover:bg-amber-50 active:scale-95 transition"
          >
            <Download size={14} className="text-amber-700" />
            <span>Download PDF</span>
          </button>
        </div>
        <p className="text-xs text-amber-100">
          Handpicked high-yield questions, past paper solved answers, and 2026 examination predictions.
        </p>
      </div>

      {/* Filters */}
      <div className="space-y-2">
        {/* Subject Filter Chips */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 no-scrollbar text-xs">
          <button
            onClick={() => setSelectedSubjectId('all')}
            className={`px-3 py-1.5 rounded-full font-bold whitespace-nowrap transition ${
              selectedSubjectId === 'all'
                ? 'bg-amber-500 text-white shadow-sm'
                : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300'
            }`}
          >
            All Subjects
          </button>
          {subjects.map(s => (
            <button
              key={s.id}
              onClick={() => setSelectedSubjectId(s.id)}
              className={`px-3 py-1.5 rounded-full font-bold whitespace-nowrap transition ${
                selectedSubjectId === s.id
                  ? 'bg-amber-500 text-white shadow-sm'
                  : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300'
              }`}
            >
              {s.name}
            </button>
          ))}
        </div>

        {/* Category Filter Chips */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 no-scrollbar text-xs">
          {[
            { id: 'all', label: 'All Types' },
            { id: 'madhyamik_suggestion', label: '🏆 Madhyamik 2026 Suggestions' },
            { id: 'pyq', label: '📜 Previous Year Qs (PYQ)' },
            { id: 'important', label: '⭐ Important Questions' }
          ].map(cat => (
            <button
              key={cat.id}
              onClick={() => setSelectedCategory(cat.id as any)}
              className={`px-3 py-1 rounded-full font-semibold whitespace-nowrap transition ${
                selectedCategory === cat.id
                  ? 'bg-emerald-600 text-white'
                  : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300'
              }`}
            >
              {cat.label}
            </button>
          ))}
        </div>
      </div>

      {/* Results List */}
      <div className="space-y-3">
        {filtered.length === 0 ? (
          <div className="p-8 text-center bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 text-xs text-slate-500">
            No suggestions match the selected criteria yet. Select another subject or category!
          </div>
        ) : (
          filtered.map(q => {
            const ch = chapters.find(c => c.id === q.chapterId);
            return (
              <div
                key={q.id}
                onClick={() => onSelectChapter(q.chapterId)}
                className="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm hover:shadow-md cursor-pointer transition space-y-2"
              >
                <div className="flex items-center gap-2">
                  <span className="text-[10px] font-extrabold px-2 py-0.5 bg-amber-500 text-white rounded uppercase">
                    {q.category === 'madhyamik_suggestion' ? 'Suggestion 2026' : q.category.toUpperCase()}
                  </span>
                  {q.marks && (
                    <span className="text-[10px] font-bold text-slate-500">
                      {q.marks} Marks
                    </span>
                  )}
                  {q.year && (
                    <span className="text-[10px] font-bold px-1.5 py-0.5 bg-amber-100 text-amber-900 rounded">
                      {q.year}
                    </span>
                  )}
                  {ch && (
                    <span className="text-[10px] text-slate-400 font-medium ml-auto truncate">
                      {ch.subjectName} • {ch.chapterName}
                    </span>
                  )}
                </div>

                <h3 className="font-bold text-xs text-slate-900 dark:text-slate-100 leading-snug">
                  {q.questionText}
                </h3>

                <p className="text-xs text-slate-600 dark:text-slate-300 line-clamp-3 bg-slate-50 dark:bg-slate-800 p-2.5 rounded-xl border border-slate-100 dark:border-slate-750">
                  <strong className="text-emerald-600 dark:text-emerald-400 block mb-0.5">Answer:</strong>
                  {q.answerText}
                </p>

                <div className="pt-2 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between text-xs">
                  <button
                    onClick={e => {
                      e.stopPropagation();
                      handleOpenPdfModal([q], `Suggestion: ${q.questionText.substring(0, 30)}...`);
                    }}
                    className="px-2.5 py-1 rounded-lg bg-rose-50 dark:bg-rose-950/50 text-rose-600 dark:text-rose-400 font-semibold flex items-center gap-1 hover:bg-rose-100 transition"
                  >
                    <Download size={13} />
                    <span>PDF</span>
                  </button>

                  <div className="flex items-center gap-1 font-bold text-emerald-600 dark:text-emerald-400">
                    <span>Open Full Chapter</span>
                    <ChevronRight size={14} />
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};

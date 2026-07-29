import React, { useState } from 'react';
import { Chapter, Note, Question, PdfLink } from '../../types';
import { useSaved } from '../../context/SavedContext';
import {
  ArrowLeft,
  Download,
  Bookmark,
  BookmarkCheck,
  Share2,
  BookOpen,
  HelpCircle,
  Award,
  CheckCircle,
  XCircle,
  FileText,
  Copy,
  Check,
  Printer
} from 'lucide-react';
import { generateChapterPdfHtml } from '../../utils/pdfGenerator';
import { PdfViewerModal } from '../common/PdfViewerModal';

interface ChapterDetailViewProps {
  chapter: Chapter;
  notes: Note[];
  questions: Question[];
  pdfLinks: PdfLink[];
  onBack: () => void;
}

type ContentTab = 'notes' | 'mcq' | 'qa' | 'short' | 'long' | 'pyq' | 'suggestions';

export const ChapterDetailView: React.FC<ChapterDetailViewProps> = ({
  chapter,
  notes,
  questions,
  pdfLinks,
  onBack
}) => {
  const [activeTab, setActiveTab] = useState<ContentTab>('notes');
  const [copiedShare, setCopiedShare] = useState(false);
  const [pdfModalOpen, setPdfModalOpen] = useState(false);
  const [pdfModalData, setPdfModalData] = useState<{ title: string; subtitle?: string; htmlContent?: string; pdfUrl?: string }>({ title: '' });
  const { isSaved, toggleSave } = useSaved();

  // MCQ state: selected answers map questionId -> optionIndex
  const [userMcqAnswers, setUserMcqAnswers] = useState<Record<string, number>>({});
  const [mcqSubmitted, setMcqSubmitted] = useState(false);

  const chapterSaved = isSaved(chapter.id);

  const mcqQuestions = questions.filter(q => q.category === 'mcq');
  const shortQuestions = questions.filter(q => q.category === 'short');
  const longQuestions = questions.filter(q => q.category === 'long');
  const importantQuestions = questions.filter(q => q.category === 'important');
  const pyqQuestions = questions.filter(q => q.category === 'pyq');
  const suggestionQuestions = questions.filter(q => q.category === 'madhyamik_suggestion');
  const generalQa = questions.filter(q => q.category === 'short' || q.category === 'long' || q.category === 'important');

  const handleOpenChapterPdf = () => {
    // Always generate full chapter PDF containing notes & all non-MCQ questions serially & mark-division wise
    const html = generateChapterPdfHtml(chapter, notes, questions);
    setPdfModalData({
      title: `${chapter.chapterName} — Complete Chapter Study Material`,
      subtitle: `${chapter.subjectName || 'WBBSE Madhyamik'} • Notes, Suggestions & Solved Q&A PDF`,
      htmlContent: html
    });
    setPdfModalOpen(true);
  };

  const handleShare = async () => {
    const shareData = {
      title: `${chapter.chapterName} - Towfik Edutips`,
      text: `Read complete notes and solved questions for ${chapter.chapterName} on Towfik Edutips!`,
      url: window.location.href
    };

    if (navigator.share) {
      try {
        await navigator.share(shareData);
      } catch {
        // Fallback to clipboard
        navigator.clipboard.writeText(window.location.href);
        setCopiedShare(true);
        setTimeout(() => setCopiedShare(false), 2000);
      }
    } else {
      navigator.clipboard.writeText(window.location.href);
      setCopiedShare(true);
      setTimeout(() => setCopiedShare(false), 2000);
    }
  };

  const handleToggleSaveChapter = () => {
    toggleSave({
      itemId: chapter.id,
      itemType: 'chapter',
      title: chapter.chapterName,
      subtitle: chapter.subjectName || 'Chapter Study Material',
      chapterId: chapter.id,
      subjectId: chapter.subjectId
    });
  };

  const handleSelectOption = (qId: string, optionIndex: number) => {
    setUserMcqAnswers(prev => ({ ...prev, [qId]: optionIndex }));
  };

  // Calculate score for MCQs
  let mcqScore = 0;
  mcqQuestions.forEach(q => {
    if (userMcqAnswers[q.id] === q.correctOptionIndex) {
      mcqScore += 1;
    }
  });

  return (
    <div className="space-y-4 pb-16 animate-in fade-in duration-300">
      <PdfViewerModal
        isOpen={pdfModalOpen}
        title={pdfModalData.title}
        subtitle={pdfModalData.subtitle}
        htmlContent={pdfModalData.htmlContent}
        pdfUrl={pdfModalData.pdfUrl}
        onClose={() => setPdfModalOpen(false)}
      />

      {/* Back Button */}
      <button
        onClick={onBack}
        className="flex items-center gap-1.5 text-xs font-bold text-emerald-600 dark:text-emerald-400 hover:underline active:scale-95 transition"
      >
        <ArrowLeft size={16} />
        <span>Back</span>
      </button>

      {/* Chapter Banner & Title Card */}
      <div className="bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 shadow-md overflow-hidden">
        {chapter.imageUrl && (
          <div className="w-full h-44 relative overflow-hidden">
            <img
              src={chapter.imageUrl}
              alt={chapter.chapterName}
              className="w-full h-full object-cover"
              referrerPolicy="no-referrer"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-transparent to-transparent" />
            <span className="absolute bottom-3 left-3 px-2.5 py-0.5 text-[10px] font-extrabold uppercase bg-emerald-500 text-white rounded-md">
              {chapter.subjectName || 'Subject Chapter'}
            </span>
          </div>
        )}

        <div className="p-4 space-y-3">
          <h1 className="text-xl font-extrabold text-slate-900 dark:text-slate-100 leading-tight">
            {chapter.chapterName}
          </h1>

          {chapter.description && (
            <p className="text-xs text-slate-600 dark:text-slate-300 leading-relaxed">
              {chapter.description}
            </p>
          )}

          {/* Action Bar */}
          <div className="flex items-center gap-2 pt-2 border-t border-slate-100 dark:border-slate-800">
            {/* Full Chapter PDF Download & View Button */}
            <button
              onClick={handleOpenChapterPdf}
              className="flex-1 py-2 px-3 rounded-xl bg-rose-600 text-white font-bold text-xs flex items-center justify-center gap-1.5 shadow hover:bg-rose-700 active:scale-95 transition"
            >
              <Download size={15} />
              <span>Full Chapter PDF</span>
            </button>

            {/* Save Chapter Button */}
            <button
              onClick={handleToggleSaveChapter}
              className={`py-2 px-3 rounded-xl font-bold text-xs flex items-center justify-center gap-1.5 border transition active:scale-95 ${
                chapterSaved
                  ? 'bg-amber-100 dark:bg-amber-950/60 border-amber-300 text-amber-700 dark:text-amber-300'
                  : 'bg-slate-100 dark:bg-slate-800 border-slate-200 dark:border-slate-700 text-slate-700 dark:text-slate-300 hover:bg-slate-200'
              }`}
            >
              {chapterSaved ? <BookmarkCheck size={16} /> : <Bookmark size={16} />}
              <span>{chapterSaved ? 'Saved' : 'Save'}</span>
            </button>

            {/* Share Chapter Button */}
            <button
              onClick={handleShare}
              className="py-2 px-3 rounded-xl bg-emerald-50 dark:bg-emerald-950/50 border border-emerald-200 dark:border-emerald-800 text-emerald-700 dark:text-emerald-300 font-bold text-xs flex items-center justify-center gap-1.5 hover:bg-emerald-100 active:scale-95 transition"
            >
              {copiedShare ? <Check size={16} className="text-emerald-500" /> : <Share2 size={16} />}
              <span>{copiedShare ? 'Copied' : 'Share'}</span>
            </button>
          </div>
        </div>
      </div>

      {/* Tabs Navigation */}
      <div className="flex items-center gap-1.5 overflow-x-auto pb-1 no-scrollbar">
        {[
          { id: 'notes' as ContentTab, label: `Notes (${notes.length})`, icon: BookOpen },
          { id: 'mcq' as ContentTab, label: `MCQ (${mcqQuestions.length})`, icon: HelpCircle },
          { id: 'qa' as ContentTab, label: `Q & A (${generalQa.length})`, icon: FileText },
          { id: 'short' as ContentTab, label: `Short Qs (${shortQuestions.length})`, icon: FileText },
          { id: 'long' as ContentTab, label: `Long Qs (${longQuestions.length})`, icon: FileText },
          { id: 'pyq' as ContentTab, label: `PYQ (${pyqQuestions.length})`, icon: Award },
          { id: 'suggestions' as ContentTab, label: `Suggestions (${suggestionQuestions.length})`, icon: Award }
        ].map(tab => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center gap-1 px-3 py-1.5 rounded-full text-xs font-semibold whitespace-nowrap transition ${
                isActive
                  ? 'bg-emerald-600 text-white shadow-sm'
                  : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700'
              }`}
            >
              <Icon size={13} />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* Tab Content Panels */}
      <div className="space-y-3">
        {/* Notes Tab */}
        {activeTab === 'notes' && (
          <div className="space-y-3">
            {notes.length === 0 ? (
              <div className="p-6 text-center text-xs text-slate-500 bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800">
                No extra notes added for this chapter yet.
              </div>
            ) : (
              notes.map(note => (
                <div
                  key={note.id}
                  className="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm space-y-2"
                >
                  <div className="flex items-center justify-between">
                    <h3 className="font-bold text-sm text-slate-900 dark:text-slate-100">
                      {note.title}
                    </h3>
                    {note.type && (
                      <span className="text-[9px] font-extrabold px-2 py-0.5 bg-emerald-100 dark:bg-emerald-900 text-emerald-700 dark:text-emerald-300 rounded uppercase">
                        {note.type}
                      </span>
                    )}
                  </div>
                  <div className="text-xs text-slate-700 dark:text-slate-300 whitespace-pre-line leading-relaxed">
                    {note.content}
                  </div>
                </div>
              ))
            )}
          </div>
        )}

        {/* MCQ Tab */}
        {activeTab === 'mcq' && (
          <div className="space-y-3">
            {mcqQuestions.length === 0 ? (
              <div className="p-6 text-center text-xs text-slate-500 bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800">
                No MCQ questions available for this chapter yet.
              </div>
            ) : (
              <>
                <div className="p-3 bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800/60 rounded-2xl flex items-center justify-between text-xs font-bold text-emerald-800 dark:text-emerald-300">
                  <span>Practice MCQ Quiz ({mcqQuestions.length} Questions)</span>
                  {mcqSubmitted && (
                    <span className="px-2.5 py-1 bg-emerald-600 text-white rounded-lg">
                      Score: {mcqScore} / {mcqQuestions.length}
                    </span>
                  )}
                </div>

                {mcqQuestions.map((q, idx) => {
                  const selectedOption = userMcqAnswers[q.id];
                  const options = q.options || ['Option A', 'Option B', 'Option C', 'Option D'];

                  return (
                    <div
                      key={q.id}
                      className="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm space-y-3"
                    >
                      <div className="flex items-start gap-2">
                        <span className="w-6 h-6 rounded-full bg-emerald-100 dark:bg-emerald-900 text-emerald-800 dark:text-emerald-200 text-xs font-bold flex items-center justify-center shrink-0">
                          {idx + 1}
                        </span>
                        <p className="font-bold text-xs text-slate-900 dark:text-slate-100 pt-0.5">
                          {q.questionText}
                        </p>
                      </div>

                      <div className="space-y-2 pl-8">
                        {options.map((opt, optIdx) => {
                          const isSelected = selectedOption === optIdx;
                          const isCorrect = q.correctOptionIndex === optIdx;

                          let btnStyle = 'bg-slate-50 dark:bg-slate-800 border-slate-200 dark:border-slate-700 text-slate-700 dark:text-slate-200';
                          if (mcqSubmitted) {
                            if (isCorrect) {
                              btnStyle = 'bg-emerald-100 dark:bg-emerald-900/60 border-emerald-500 text-emerald-900 dark:text-emerald-100 font-bold';
                            } else if (isSelected && !isCorrect) {
                              btnStyle = 'bg-rose-100 dark:bg-rose-950/60 border-rose-500 text-rose-900 dark:text-rose-100 font-bold';
                            }
                          } else if (isSelected) {
                            btnStyle = 'bg-emerald-50 dark:bg-emerald-950 border-emerald-500 text-emerald-800 dark:text-emerald-200 font-bold';
                          }

                          return (
                            <button
                              key={optIdx}
                              onClick={() => handleSelectOption(q.id, optIdx)}
                              className={`w-full p-2.5 rounded-xl border text-left text-xs transition flex items-center justify-between ${btnStyle}`}
                            >
                              <span>{opt}</span>
                              {mcqSubmitted && isCorrect && <CheckCircle size={16} className="text-emerald-600" />}
                              {mcqSubmitted && isSelected && !isCorrect && <XCircle size={16} className="text-rose-600" />}
                            </button>
                          );
                        })}
                      </div>

                      {mcqSubmitted && q.answerText && (
                        <div className="ml-8 p-2.5 bg-amber-50 dark:bg-amber-950/40 rounded-xl text-[11px] text-amber-900 dark:text-amber-200 border border-amber-200 dark:border-amber-900">
                          <strong>Explanation / Key:</strong> {q.answerText}
                        </div>
                      )}
                    </div>
                  );
                })}

                <button
                  onClick={() => setMcqSubmitted(true)}
                  className="w-full py-3 rounded-2xl bg-emerald-600 text-white font-bold text-xs shadow-md hover:bg-emerald-700 transition active:scale-95"
                >
                  {mcqSubmitted ? 'Re-check Result & Answers' : 'Submit Answers & View Score'}
                </button>
              </>
            )}
          </div>
        )}

        {/* Q & A / Short / Long / PYQ / Suggestions Tabs */}
        {(activeTab === 'qa' ||
          activeTab === 'short' ||
          activeTab === 'long' ||
          activeTab === 'pyq' ||
          activeTab === 'suggestions') && (
          <div className="space-y-3">
            {(() => {
              let targetList = generalQa;
              if (activeTab === 'short') targetList = shortQuestions;
              if (activeTab === 'long') targetList = longQuestions;
              if (activeTab === 'pyq') targetList = pyqQuestions;
              if (activeTab === 'suggestions') targetList = suggestionQuestions;

              if (targetList.length === 0) {
                return (
                  <div className="p-6 text-center text-xs text-slate-500 bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800">
                    No questions in this category yet.
                  </div>
                );
              }

              return targetList.map((q, i) => (
                <div
                  key={q.id}
                  className="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm space-y-2"
                >
                  <div className="flex items-center gap-2">
                    <span className="text-[10px] font-extrabold px-2 py-0.5 bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300 rounded uppercase">
                      Q{i + 1} • {q.category}
                    </span>
                    {q.marks && (
                      <span className="text-[10px] font-bold text-slate-500">
                        {q.marks} {q.marks === 1 ? 'Mark' : 'Marks'}
                      </span>
                    )}
                    {q.year && (
                      <span className="text-[10px] font-bold px-1.5 py-0.5 bg-amber-100 text-amber-800 rounded ml-auto">
                        Year {q.year}
                      </span>
                    )}
                  </div>

                  <h3 className="font-bold text-xs text-slate-900 dark:text-slate-100 leading-snug">
                    {q.questionText}
                  </h3>

                  <div className="p-3 bg-slate-50 dark:bg-slate-800/80 rounded-xl text-xs text-slate-700 dark:text-slate-300 leading-relaxed border border-slate-100 dark:border-slate-750">
                    <strong className="text-emerald-600 dark:text-emerald-400 block mb-1">Answer:</strong>
                    {q.answerText}
                  </div>
                </div>
              ));
            })()}
          </div>
        )}
      </div>
    </div>
  );
};

import React, { useState } from 'react';
import { Subject, Chapter, PdfLink } from '../../types';
import { BookOpen, Languages, Calculator, Atom, Dna, Landmark, Globe, ChevronRight, FileText, Download, ArrowLeft } from 'lucide-react';

const ICON_MAP: Record<string, any> = {
  BookOpen,
  Languages,
  Calculator,
  Atom,
  Dna,
  Landmark,
  Globe
};

interface SubjectsViewProps {
  subjects: Subject[];
  chapters: Chapter[];
  pdfLinks: PdfLink[];
  selectedSubjectId?: string | null;
  onSelectSubject: (subjectId: string | null) => void;
  onSelectChapter: (chapterId: string) => void;
}

export const SubjectsView: React.FC<SubjectsViewProps> = ({
  subjects,
  chapters,
  pdfLinks,
  selectedSubjectId,
  onSelectSubject,
  onSelectChapter
}) => {
  const currentSubject = subjects.find(s => s.id === selectedSubjectId);
  const currentChapters = chapters.filter(c => c.subjectId === selectedSubjectId);

  return (
    <div className="space-y-4 pb-12 animate-in fade-in duration-300">
      {/* Header if a subject is selected */}
      {selectedSubjectId && currentSubject ? (
        <div className="space-y-3">
          <button
            onClick={() => onSelectSubject(null)}
            className="flex items-center gap-1.5 text-xs font-bold text-emerald-600 dark:text-emerald-400 hover:underline active:scale-95 transition"
          >
            <ArrowLeft size={16} />
            <span>Back to All Subjects</span>
          </button>

          <div
            className="p-4 rounded-2xl text-white shadow-md flex items-center gap-3"
            style={{ backgroundColor: currentSubject.color || '#10B981' }}
          >
            {currentSubject.logoUrl ? (
              <img
                src={currentSubject.logoUrl}
                alt={currentSubject.name}
                className="w-12 h-12 rounded-xl object-cover bg-white/20 shrink-0 border border-white/30 shadow"
                referrerPolicy="no-referrer"
              />
            ) : (
              <div className="w-12 h-12 rounded-xl bg-white/20 flex items-center justify-center shrink-0">
                {React.createElement(ICON_MAP[currentSubject.icon] || BookOpen, { size: 26 })}
              </div>
            )}
            <div>
              <h2 className="text-lg font-extrabold">{currentSubject.name}</h2>
              <p className="text-xs text-white/90">{currentSubject.description || 'Full Chapter Notes & Question Bank'}</p>
            </div>
          </div>
        </div>
      ) : (
        <div>
          <h2 className="text-lg font-extrabold text-slate-900 dark:text-slate-100 mb-1">
            WBBSE Madhyamik Subjects
          </h2>
          <p className="text-xs text-slate-500 dark:text-slate-400">
            Select a subject to access complete chapter notes, MCQs, and PDFs.
          </p>
        </div>
      )}

      {/* Subject List View */}
      {!selectedSubjectId && (
        <div className="space-y-3">
          {subjects.map(subject => {
            const IconComp = ICON_MAP[subject.icon] || BookOpen;
            const subChapters = chapters.filter(c => c.subjectId === subject.id);
            return (
              <div
                key={subject.id}
                onClick={() => onSelectSubject(subject.id)}
                className="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm hover:shadow-md hover:border-emerald-400 cursor-pointer transition flex items-center justify-between"
              >
                <div className="flex items-center gap-3.5">
                  {subject.logoUrl ? (
                    <img
                      src={subject.logoUrl}
                      alt={subject.name}
                      className="w-12 h-12 rounded-2xl object-cover border border-slate-200 dark:border-slate-800 shrink-0 shadow-sm"
                      referrerPolicy="no-referrer"
                    />
                  ) : (
                    <div
                      className="w-12 h-12 rounded-2xl flex items-center justify-center text-white shrink-0 shadow-sm"
                      style={{ backgroundColor: subject.color || '#10B981' }}
                    >
                      <IconComp size={24} />
                    </div>
                  )}
                  <div>
                    <h3 className="font-bold text-base text-slate-900 dark:text-slate-100">
                      {subject.name}
                    </h3>
                    <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                      {subChapters.length} Chapters Available
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-1 text-slate-400">
                  <span className="text-xs font-semibold text-emerald-600 dark:text-emerald-400">View</span>
                  <ChevronRight size={18} className="text-emerald-600 dark:text-emerald-400" />
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Chapters under selected subject */}
      {selectedSubjectId && (
        <div className="space-y-3">
          <h3 className="text-sm font-bold uppercase tracking-wider text-slate-400">
            Chapters List ({currentChapters.length})
          </h3>

          {currentChapters.length === 0 ? (
            <div className="p-8 text-center bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 text-slate-500 text-xs">
              No chapters added under this subject yet. Check back soon or request admin to add!
            </div>
          ) : (
            currentChapters.map(ch => {
              const chPdfs = pdfLinks.filter(p => p.chapterId === ch.id);
              return (
                <div
                  key={ch.id}
                  onClick={() => onSelectChapter(ch.id)}
                  className="p-3.5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm hover:shadow-md cursor-pointer transition flex flex-col gap-3"
                >
                  <div className="flex items-start gap-3">
                    {ch.imageUrl ? (
                      <img
                        src={ch.imageUrl}
                        alt={ch.chapterName}
                        className="w-20 h-20 rounded-xl object-cover shrink-0 border border-slate-200 dark:border-slate-800"
                        referrerPolicy="no-referrer"
                      />
                    ) : (
                      <div className="w-20 h-20 rounded-xl bg-emerald-50 dark:bg-emerald-950 flex items-center justify-center text-emerald-600 shrink-0">
                        <FileText size={28} />
                      </div>
                    )}

                    <div className="flex-1">
                      <h4 className="font-bold text-sm text-slate-900 dark:text-slate-100 leading-snug">
                        {ch.chapterName}
                      </h4>
                      <p className="text-xs text-slate-500 dark:text-slate-400 line-clamp-2 mt-1">
                        {ch.description || 'Includes Notes, MCQs, Question & Answers, PYQs and Suggestions.'}
                      </p>
                    </div>
                  </div>

                  {/* Actions & PDF Links */}
                  <div className="pt-2 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between text-xs">
                    <span className="font-bold text-emerald-600 dark:text-emerald-400 flex items-center gap-1">
                      Read Complete Chapter
                      <ChevronRight size={14} />
                    </span>

                    {(ch.pdfUrl || chPdfs.length > 0) && (
                      <a
                        href={ch.pdfUrl || chPdfs[0]?.url}
                        target="_blank"
                        rel="noreferrer"
                        onClick={e => e.stopPropagation()}
                        className="px-2.5 py-1 rounded-lg bg-rose-50 dark:bg-rose-950/50 text-rose-600 dark:text-rose-400 font-semibold flex items-center gap-1 hover:bg-rose-100 transition"
                      >
                        <Download size={13} />
                        <span>PDF</span>
                      </a>
                    )}
                  </div>
                </div>
              );
            })
          )}
        </div>
      )}
    </div>
  );
};

import React, { useState, useEffect } from 'react';
import { Search, X, BookOpen, FileText, HelpCircle, Download, ChevronRight } from 'lucide-react';
import { Subject, Chapter, Note, Question, PdfLink } from '../../types';

interface SearchBarProps {
  onSearchQueryChange?: (query: string) => void;
  subjects: Subject[];
  chapters: Chapter[];
  notes: Note[];
  questions: Question[];
  pdfLinks: PdfLink[];
  onSelectChapter: (chapterId: string) => void;
  onSelectSubject?: (subjectId: string) => void;
}

export const SearchBar: React.FC<SearchBarProps> = ({
  subjects,
  chapters,
  notes,
  questions,
  pdfLinks,
  onSelectChapter
}) => {
  const [query, setQuery] = useState('');
  const [filterType, setFilterType] = useState<'all' | 'chapters' | 'notes' | 'questions' | 'pdfs'>('all');

  const filteredChapters = chapters.filter(c =>
    c.chapterName.toLowerCase().includes(query.toLowerCase()) ||
    c.description.toLowerCase().includes(query.toLowerCase())
  );

  const filteredNotes = notes.filter(n =>
    n.title.toLowerCase().includes(query.toLowerCase()) ||
    n.content.toLowerCase().includes(query.toLowerCase())
  );

  const filteredQuestions = questions.filter(q =>
    q.questionText.toLowerCase().includes(query.toLowerCase()) ||
    q.answerText.toLowerCase().includes(query.toLowerCase())
  );

  const filteredPdfs = pdfLinks.filter(p =>
    p.title.toLowerCase().includes(query.toLowerCase())
  );

  const totalResults =
    (filterType === 'all' || filterType === 'chapters' ? filteredChapters.length : 0) +
    (filterType === 'all' || filterType === 'notes' ? filteredNotes.length : 0) +
    (filterType === 'all' || filterType === 'questions' ? filteredQuestions.length : 0) +
    (filterType === 'all' || filterType === 'pdfs' ? filteredPdfs.length : 0);

  return (
    <div className="w-full space-y-3">
      {/* Input Box */}
      <div className="relative">
        <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
          <Search size={18} />
        </div>
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search subjects, chapters, notes, MCQs..."
          className="w-full pl-10 pr-10 py-2.5 bg-slate-100 dark:bg-slate-800 text-slate-900 dark:text-slate-100 text-sm rounded-xl border border-slate-200 dark:border-slate-700 focus:outline-none focus:ring-2 focus:ring-emerald-500 transition"
        />
        {query && (
          <button
            onClick={() => setQuery('')}
            className="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
          >
            <X size={18} />
          </button>
        )}
      </div>

      {/* Filter Chips */}
      {query && (
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 text-xs no-scrollbar">
          {(['all', 'chapters', 'notes', 'questions', 'pdfs'] as const).map(type => (
            <button
              key={type}
              onClick={() => setFilterType(type)}
              className={`px-3 py-1 rounded-full font-medium whitespace-nowrap transition ${
                filterType === type
                  ? 'bg-emerald-600 text-white shadow-sm'
                  : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700'
              }`}
            >
              {type === 'all' && 'All Results'}
              {type === 'chapters' && `Chapters (${filteredChapters.length})`}
              {type === 'notes' && `Notes (${filteredNotes.length})`}
              {type === 'questions' && `Questions (${filteredQuestions.length})`}
              {type === 'pdfs' && `PDFs (${filteredPdfs.length})`}
            </button>
          ))}
        </div>
      )}

      {/* Live Results Dropdown / Container */}
      {query && (
        <div className="bg-white dark:bg-slate-900 rounded-2xl border border-slate-200 dark:border-slate-800 p-3 shadow-lg space-y-3 max-h-[60vh] overflow-y-auto">
          {totalResults === 0 ? (
            <div className="text-center py-6 text-slate-500 dark:text-slate-400 text-xs">
              No matching notes or questions found for "{query}".
            </div>
          ) : (
            <>
              {/* Chapters */}
              {(filterType === 'all' || filterType === 'chapters') && filteredChapters.length > 0 && (
                <div>
                  <h4 className="text-[11px] font-extrabold uppercase text-slate-400 mb-1.5 flex items-center gap-1">
                    <BookOpen size={12} /> Chapters
                  </h4>
                  <div className="space-y-1.5">
                    {filteredChapters.map(ch => (
                      <div
                        key={ch.id}
                        onClick={() => onSelectChapter(ch.id)}
                        className="p-2 rounded-xl bg-slate-50 dark:bg-slate-800/60 hover:bg-emerald-50 dark:hover:bg-emerald-950/30 cursor-pointer flex items-center justify-between transition"
                      >
                        <div>
                          <div className="text-xs font-bold text-slate-900 dark:text-slate-100">{ch.chapterName}</div>
                          <div className="text-[10px] text-slate-500 dark:text-slate-400">{ch.subjectName}</div>
                        </div>
                        <ChevronRight size={16} className="text-slate-400" />
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Notes */}
              {(filterType === 'all' || filterType === 'notes') && filteredNotes.length > 0 && (
                <div>
                  <h4 className="text-[11px] font-extrabold uppercase text-slate-400 mb-1.5 flex items-center gap-1">
                    <FileText size={12} /> Notes & Summaries
                  </h4>
                  <div className="space-y-1.5">
                    {filteredNotes.map(n => (
                      <div
                        key={n.id}
                        onClick={() => onSelectChapter(n.chapterId)}
                        className="p-2 rounded-xl bg-slate-50 dark:bg-slate-800/60 hover:bg-emerald-50 dark:hover:bg-emerald-950/30 cursor-pointer transition"
                      >
                        <div className="text-xs font-semibold text-slate-900 dark:text-slate-100">{n.title}</div>
                        <p className="text-[10px] text-slate-500 dark:text-slate-400 line-clamp-2 mt-0.5">{n.content}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Questions */}
              {(filterType === 'all' || filterType === 'questions') && filteredQuestions.length > 0 && (
                <div>
                  <h4 className="text-[11px] font-extrabold uppercase text-slate-400 mb-1.5 flex items-center gap-1">
                    <HelpCircle size={12} /> Questions & MCQs
                  </h4>
                  <div className="space-y-1.5">
                    {filteredQuestions.map(q => (
                      <div
                        key={q.id}
                        onClick={() => onSelectChapter(q.chapterId)}
                        className="p-2 rounded-xl bg-slate-50 dark:bg-slate-800/60 hover:bg-emerald-50 dark:hover:bg-emerald-950/30 cursor-pointer transition"
                      >
                        <div className="flex items-center gap-1.5 mb-0.5">
                          <span className="text-[9px] font-bold px-1.5 py-0.5 bg-emerald-100 dark:bg-emerald-900 text-emerald-700 dark:text-emerald-300 rounded uppercase">
                            {q.category}
                          </span>
                        </div>
                        <div className="text-xs font-semibold text-slate-900 dark:text-slate-100">{q.questionText}</div>
                        <div className="text-[10px] text-emerald-600 dark:text-emerald-400 mt-0.5 line-clamp-1 font-medium">
                          Ans: {q.answerText}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* PDFs */}
              {(filterType === 'all' || filterType === 'pdfs') && filteredPdfs.length > 0 && (
                <div>
                  <h4 className="text-[11px] font-extrabold uppercase text-slate-400 mb-1.5 flex items-center gap-1">
                    <Download size={12} /> Downloadable PDFs
                  </h4>
                  <div className="space-y-1.5">
                    {filteredPdfs.map(pdf => (
                      <a
                        key={pdf.id}
                        href={pdf.url}
                        target="_blank"
                        rel="noreferrer"
                        className="p-2 rounded-xl bg-slate-50 dark:bg-slate-800/60 hover:bg-rose-50 dark:hover:bg-rose-950/30 text-rose-600 dark:text-rose-400 font-medium text-xs flex items-center justify-between transition"
                      >
                        <span>{pdf.title}</span>
                        <Download size={14} />
                      </a>
                    ))}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
};

import React from 'react';
import { Subject, Chapter, Banner, Note, Question, PdfLink } from '../../types';
import { BannerSlider } from '../common/BannerSlider';
import { SearchBar } from '../common/SearchBar';
import {
  BookOpen,
  Languages,
  Calculator,
  Atom,
  Dna,
  Landmark,
  Globe,
  Sparkles,
  ChevronRight,
  Award,
  Bookmark,
  FileCheck,
  CheckCircle2,
  HelpCircle,
  Clock,
  Phone,
  MessageSquare
} from 'lucide-react';
import { useSaved } from '../../context/SavedContext';

const ICON_MAP: Record<string, any> = {
  BookOpen,
  Languages,
  Calculator,
  Atom,
  Dna,
  Landmark,
  Globe
};

interface HomeViewProps {
  banners: Banner[];
  subjects: Subject[];
  chapters: Chapter[];
  notes: Note[];
  questions: Question[];
  pdfLinks: PdfLink[];
  onSelectSubject: (subjectId: string) => void;
  onSelectChapter: (chapterId: string) => void;
  onNavigateTab: (tab: any) => void;
}

export const HomeView: React.FC<HomeViewProps> = ({
  banners,
  subjects,
  chapters,
  notes,
  questions,
  pdfLinks,
  onSelectSubject,
  onSelectChapter,
  onNavigateTab
}) => {
  const { savedItems } = useSaved();

  const madhyamikSuggestions = questions.filter(
    q => q.category === 'madhyamik_suggestion' || q.category === 'pyq'
  ).slice(0, 4);

  return (
    <div className="space-y-5 pb-8 animate-in fade-in duration-300">
      {/* Banner Slider */}
      {banners.length > 0 && (
        <BannerSlider banners={banners} />
      )}

      {/* Quick Search Bar */}
      <SearchBar
        subjects={subjects}
        chapters={chapters}
        notes={notes}
        questions={questions}
        pdfLinks={pdfLinks}
        onSelectChapter={onSelectChapter}
      />

      {/* Quick Access Badges / Navigation */}
      <div className="grid grid-cols-4 gap-2">
        <button
          onClick={() => onNavigateTab('subjects')}
          className="flex flex-col items-center justify-center p-3 rounded-2xl bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-100 dark:border-emerald-900/50 hover:bg-emerald-100 transition active:scale-95 text-emerald-700 dark:text-emerald-300"
        >
          <BookOpen size={22} className="mb-1" />
          <span className="text-[11px] font-bold">Subjects</span>
        </button>

        <button
          onClick={() => onNavigateTab('suggestions')}
          className="flex flex-col items-center justify-center p-3 rounded-2xl bg-amber-50 dark:bg-amber-950/40 border border-amber-100 dark:border-amber-900/50 hover:bg-amber-100 transition active:scale-95 text-amber-700 dark:text-amber-300"
        >
          <Award size={22} className="mb-1" />
          <span className="text-[11px] font-bold">Suggestions</span>
        </button>

        <button
          onClick={() => onNavigateTab('saved')}
          className="flex flex-col items-center justify-center p-3 rounded-2xl bg-blue-50 dark:bg-blue-950/40 border border-blue-100 dark:border-blue-900/50 hover:bg-blue-100 transition active:scale-95 text-blue-700 dark:text-blue-300"
        >
          <Bookmark size={22} className="mb-1" />
          <span className="text-[11px] font-bold">Saved</span>
        </button>

        <button
          onClick={() => onNavigateTab('search')}
          className="flex flex-col items-center justify-center p-3 rounded-2xl bg-purple-50 dark:bg-purple-950/40 border border-purple-100 dark:border-purple-900/50 hover:bg-purple-100 transition active:scale-95 text-purple-700 dark:text-purple-300"
        >
          <FileCheck size={22} className="mb-1" />
          <span className="text-[11px] font-bold">MCQs & PYQ</span>
        </button>
      </div>

      {/* All Subjects Grid */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <h2 className="text-base font-extrabold text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
            <BookOpen size={18} className="text-emerald-600 dark:text-emerald-400" />
            <span>Madhyamik Subjects</span>
          </h2>
          <button
            onClick={() => onNavigateTab('subjects')}
            className="text-xs font-bold text-emerald-600 dark:text-emerald-400 flex items-center hover:underline"
          >
            <span>View All</span>
            <ChevronRight size={14} />
          </button>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
          {subjects.map(subject => {
            const IconComp = ICON_MAP[subject.icon] || BookOpen;
            const subChapters = chapters.filter(c => c.subjectId === subject.id);
            return (
              <div
                key={subject.id}
                onClick={() => onSelectSubject(subject.id)}
                className="p-3.5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm hover:shadow-md hover:border-emerald-300 dark:hover:border-emerald-700 cursor-pointer transition flex flex-col justify-between"
              >
                <div>
                  {subject.logoUrl ? (
                    <img
                      src={subject.logoUrl}
                      alt={subject.name}
                      className="w-10 h-10 rounded-xl object-cover mb-2 border border-slate-200 dark:border-slate-800 shadow-sm"
                      referrerPolicy="no-referrer"
                    />
                  ) : (
                    <div
                      className="w-10 h-10 rounded-xl flex items-center justify-center text-white mb-2 shadow-sm"
                      style={{ backgroundColor: subject.color || '#10B981' }}
                    >
                      <IconComp size={20} />
                    </div>
                  )}
                  <h3 className="font-bold text-sm text-slate-900 dark:text-slate-100 leading-snug">
                    {subject.name}
                  </h3>
                  <p className="text-[11px] text-slate-500 dark:text-slate-400 line-clamp-1 mt-0.5">
                    {subject.description || 'Full Syllabus & Notes'}
                  </p>
                </div>

                <div className="mt-3 pt-2 border-t border-slate-100 dark:border-slate-800/80 flex items-center justify-between text-[11px] font-semibold text-slate-600 dark:text-slate-400">
                  <span>{subChapters.length} Chapters</span>
                  <ChevronRight size={14} className="text-slate-400" />
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Madhyamik 2026 Special Suggestions Card Section */}
      {madhyamikSuggestions.length > 0 && (
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-extrabold text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
              <Sparkles size={18} className="text-amber-500" />
              <span>Madhyamik 2026 Special Suggestions</span>
            </h2>
            <button
              onClick={() => onNavigateTab('suggestions')}
              className="text-xs font-bold text-amber-600 dark:text-amber-400 flex items-center hover:underline"
            >
              <span>Explore All</span>
              <ChevronRight size={14} />
            </button>
          </div>

          <div className="space-y-2">
            {madhyamikSuggestions.map(q => {
              const chapterObj = chapters.find(c => c.id === q.chapterId);
              return (
                <div
                  key={q.id}
                  onClick={() => onSelectChapter(q.chapterId)}
                  className="p-3.5 rounded-2xl bg-gradient-to-r from-amber-50 to-orange-50 dark:from-slate-900 dark:to-slate-850 border border-amber-200 dark:border-amber-900/40 shadow-sm cursor-pointer hover:border-amber-400 transition"
                >
                  <div className="flex items-center gap-2 mb-1.5">
                    <span className="text-[10px] font-extrabold px-2 py-0.5 bg-amber-500 text-white rounded-md uppercase tracking-wider">
                      {q.category === 'madhyamik_suggestion' ? 'Suggestion 2026' : 'PYQ Solved'}
                    </span>
                    {q.marks && (
                      <span className="text-[10px] font-bold text-amber-800 dark:text-amber-300">
                        {q.marks} Marks
                      </span>
                    )}
                    {chapterObj && (
                      <span className="text-[10px] text-slate-500 dark:text-slate-400 truncate ml-auto">
                        {chapterObj.subjectName}
                      </span>
                    )}
                  </div>

                  <h4 className="font-bold text-xs text-slate-900 dark:text-slate-100 leading-snug">
                    {q.questionText}
                  </h4>
                  <p className="text-[11px] text-slate-600 dark:text-slate-400 line-clamp-2 mt-1 font-normal">
                    Ans: {q.answerText}
                  </p>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Saved Notes Quick Drawer Preview if user saved items */}
      {savedItems.length > 0 && (
        <div className="space-y-3 pt-2">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-extrabold text-slate-900 dark:text-slate-100 flex items-center gap-1.5">
              <Bookmark size={18} className="text-blue-500" />
              <span>Your Saved Study Notes ({savedItems.length})</span>
            </h2>
            <button
              onClick={() => onNavigateTab('saved')}
              className="text-xs font-bold text-blue-600 dark:text-blue-400 flex items-center hover:underline"
            >
              <span>View Saved</span>
              <ChevronRight size={14} />
            </button>
          </div>

          <div className="flex gap-2.5 overflow-x-auto pb-2 no-scrollbar">
            {savedItems.slice(0, 5).map(item => (
              <div
                key={item.id}
                onClick={() => {
                  if (item.chapterId) onSelectChapter(item.chapterId);
                  else onNavigateTab('saved');
                }}
                className="shrink-0 w-48 p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm cursor-pointer hover:border-blue-400 transition"
              >
                <div className="text-[10px] font-bold text-blue-600 dark:text-blue-400 uppercase tracking-wider mb-1">
                  {item.itemType}
                </div>
                <h4 className="font-bold text-xs text-slate-900 dark:text-slate-100 line-clamp-2">
                  {item.title}
                </h4>
                {item.subtitle && (
                  <p className="text-[10px] text-slate-500 dark:text-slate-400 line-clamp-1 mt-1">
                    {item.subtitle}
                  </p>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

    </div>
  );
};

import React, { useState } from 'react';
import { Subject, Chapter, Note, Question, PdfLink, Banner } from '../../types';
import { BannerManager } from './BannerManager';
import { SubjectManager } from './SubjectManager';
import { ChapterManager } from './ChapterManager';
import { NotesManager } from './NotesManager';
import { QuestionManager } from './QuestionManager';
import { PdfManager } from './PdfManager';
import { SettingsManager } from './SettingsManager';
import {
  ShieldCheck,
  Image as ImageIcon,
  BookOpen,
  FileText,
  HelpCircle,
  Download,
  Settings,
  LogOut,
  Layers,
  LayoutDashboard
} from 'lucide-react';

interface AdminDashboardProps {
  banners: Banner[];
  subjects: Subject[];
  chapters: Chapter[];
  notes: Note[];
  questions: Question[];
  pdfLinks: PdfLink[];
  onRefreshData: () => void;
  onLogoutAdmin: () => void;
}

type AdminTab = 'dashboard' | 'banners' | 'subjects' | 'chapters' | 'notes' | 'questions' | 'pdfs' | 'settings';

export const AdminDashboard: React.FC<AdminDashboardProps> = ({
  banners,
  subjects,
  chapters,
  notes,
  questions,
  pdfLinks,
  onRefreshData,
  onLogoutAdmin
}) => {
  const [activeTab, setActiveTab] = useState<AdminTab>('dashboard');

  return (
    <div className="space-y-4 pb-16 animate-in fade-in duration-300">
      {/* Top Admin Header Bar */}
      <div className="p-4 rounded-2xl bg-slate-900 text-white shadow-md flex items-center justify-between">
        <div className="flex items-center gap-2.5">
          <div className="w-10 h-10 rounded-xl bg-emerald-500 text-white flex items-center justify-center font-bold">
            <ShieldCheck size={22} />
          </div>
          <div>
            <h2 className="text-base font-extrabold leading-snug">Admin Control Center</h2>
            <p className="text-[10px] text-emerald-400 font-medium">Firestore Live Synchronization</p>
          </div>
        </div>

        <button
          onClick={onLogoutAdmin}
          className="px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-bold flex items-center gap-1 active:scale-95 transition"
        >
          <LogOut size={14} />
          <span>Exit</span>
        </button>
      </div>

      {/* Admin Tab Navigation Grid */}
      <div className="flex items-center gap-1.5 overflow-x-auto pb-1 no-scrollbar text-xs font-bold">
        {[
          { id: 'dashboard' as AdminTab, label: 'Overview', icon: LayoutDashboard },
          { id: 'banners' as AdminTab, label: `Banners (${banners.length})`, icon: ImageIcon },
          { id: 'subjects' as AdminTab, label: `Subjects (${subjects.length})`, icon: BookOpen },
          { id: 'chapters' as AdminTab, label: `Chapters (${chapters.length})`, icon: Layers },
          { id: 'notes' as AdminTab, label: `Notes (${notes.length})`, icon: FileText },
          { id: 'questions' as AdminTab, label: `Questions (${questions.length})`, icon: HelpCircle },
          { id: 'pdfs' as AdminTab, label: `PDFs (${pdfLinks.length})`, icon: Download },
          { id: 'settings' as AdminTab, label: 'Settings', icon: Settings }
        ].map(tab => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center gap-1 px-3 py-2 rounded-xl whitespace-nowrap transition ${
                isActive
                  ? 'bg-emerald-600 text-white shadow'
                  : 'bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 text-slate-700 dark:text-slate-300 hover:bg-slate-100'
              }`}
            >
              <Icon size={14} />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* Overview Panel */}
      {activeTab === 'dashboard' && (
        <div className="space-y-4">
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
            <div
              onClick={() => setActiveTab('subjects')}
              className="p-3.5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm cursor-pointer hover:border-emerald-500 transition"
            >
              <div className="text-[10px] font-bold text-slate-400 uppercase">Subjects</div>
              <div className="text-2xl font-black text-emerald-600 dark:text-emerald-400 mt-1">{subjects.length}</div>
            </div>

            <div
              onClick={() => setActiveTab('chapters')}
              className="p-3.5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm cursor-pointer hover:border-emerald-500 transition"
            >
              <div className="text-[10px] font-bold text-slate-400 uppercase">Chapters</div>
              <div className="text-2xl font-black text-blue-600 dark:text-blue-400 mt-1">{chapters.length}</div>
            </div>

            <div
              onClick={() => setActiveTab('questions')}
              className="p-3.5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm cursor-pointer hover:border-emerald-500 transition"
            >
              <div className="text-[10px] font-bold text-slate-400 uppercase">Questions & MCQs</div>
              <div className="text-2xl font-black text-amber-600 dark:text-amber-400 mt-1">{questions.length}</div>
            </div>

            <div
              onClick={() => setActiveTab('notes')}
              className="p-3.5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm cursor-pointer hover:border-emerald-500 transition"
            >
              <div className="text-[10px] font-bold text-slate-400 uppercase">Notes & Summaries</div>
              <div className="text-2xl font-black text-purple-600 dark:text-purple-400 mt-1">{notes.length}</div>
            </div>

            <div
              onClick={() => setActiveTab('banners')}
              className="p-3.5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm cursor-pointer hover:border-emerald-500 transition"
            >
              <div className="text-[10px] font-bold text-slate-400 uppercase">Active Banners</div>
              <div className="text-2xl font-black text-rose-600 dark:text-rose-400 mt-1">{banners.length}</div>
            </div>

            <div
              onClick={() => setActiveTab('pdfs')}
              className="p-3.5 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm cursor-pointer hover:border-emerald-500 transition"
            >
              <div className="text-[10px] font-bold text-slate-400 uppercase">PDF Links</div>
              <div className="text-2xl font-black text-teal-600 dark:text-teal-400 mt-1">{pdfLinks.length}</div>
            </div>
          </div>

          <div className="p-4 rounded-2xl bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800/60 text-xs text-emerald-800 dark:text-emerald-200 space-y-1">
            <h4 className="font-bold flex items-center gap-1.5">
              <span>🚀 Real-Time Firestore Control</span>
            </h4>
            <p className="leading-relaxed">
              All changes made inside Banners, Subjects, Chapters, Notes, Questions, PDFs, or Settings are written directly to Firebase Firestore. Students immediately see updated content on their mobile devices without needing code deployments!
            </p>
          </div>
        </div>
      )}

      {/* Managers */}
      {activeTab === 'banners' && <BannerManager banners={banners} onRefresh={onRefreshData} />}
      {activeTab === 'subjects' && <SubjectManager subjects={subjects} onRefresh={onRefreshData} />}
      {activeTab === 'chapters' && <ChapterManager subjects={subjects} chapters={chapters} onRefresh={onRefreshData} />}
      {activeTab === 'notes' && <NotesManager chapters={chapters} notes={notes} onRefresh={onRefreshData} />}
      {activeTab === 'questions' && <QuestionManager chapters={chapters} questions={questions} onRefresh={onRefreshData} />}
      {activeTab === 'pdfs' && <PdfManager chapters={chapters} pdfLinks={pdfLinks} onRefresh={onRefreshData} />}
      {activeTab === 'settings' && <SettingsManager />}
    </div>
  );
};

import React, { useState, useEffect } from 'react';
import { ThemeProvider } from './context/ThemeContext';
import { SavedProvider, useSaved } from './context/SavedContext';
import { Header } from './components/layout/Header';
import { BottomNav, NavTab } from './components/layout/BottomNav';
import { FloatingButton } from './components/layout/FloatingButton';
import { HomeView } from './components/views/HomeView';
import { SubjectsView } from './components/views/SubjectsView';
import { ChapterDetailView } from './components/views/ChapterDetailView';
import { SuggestionsView } from './components/views/SuggestionsView';
import { SavedView } from './components/views/SavedView';
import { AdminLoginModal } from './components/admin/AdminLoginModal';
import { AdminDashboard } from './components/admin/AdminDashboard';
import { AiChatModal } from './components/common/AiChatModal';
import {
  Subject,
  Chapter,
  Note,
  Question,
  PdfLink,
  Banner
} from './types';
import {
  seedInitialDatabase,
  getBanners,
  getSubjects,
  getChapters,
  getNotes,
  getQuestions,
  getPdfLinks
} from './services/db';
import { Loader2 } from 'lucide-react';

function AppContent() {
  const [activeTab, setActiveTab] = useState<NavTab | 'admin'>('home');
  const [selectedSubjectId, setSelectedSubjectId] = useState<string | null>(null);
  const [selectedChapterId, setSelectedChapterId] = useState<string | null>(null);

  // Data states
  const [banners, setBanners] = useState<Banner[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [chapters, setChapters] = useState<Chapter[]>([]);
  const [notes, setNotes] = useState<Note[]>([]);
  const [questions, setQuestions] = useState<Question[]>([]);
  const [pdfLinks, setPdfLinks] = useState<PdfLink[]>([]);
  const [initialLoading, setInitialLoading] = useState(true);

  // Admin state
  const [adminModalOpen, setAdminModalOpen] = useState(false);
  const [aiChatOpen, setAiChatOpen] = useState(false);
  const [isAdminLoggedIn, setIsAdminLoggedIn] = useState<boolean>(() => {
    return sessionStorage.getItem('towfik_admin_logged') === 'true';
  });

  const { savedItems } = useSaved();

  const loadData = async () => {
    try {
      await seedInitialDatabase();

      const [bRes, sRes, cRes, nRes, qRes, pRes] = await Promise.all([
        getBanners(false),
        getSubjects(),
        getChapters(),
        getNotes(),
        getQuestions(),
        getPdfLinks()
      ]);

      setBanners(bRes);
      setSubjects(sRes);
      setChapters(cRes);
      setNotes(nRes);
      setQuestions(qRes);
      setPdfLinks(pRes);
    } catch (err) {
      console.error('Error loading Firestore data:', err);
    } finally {
      setInitialLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleAdminLoginSuccess = () => {
    setIsAdminLoggedIn(true);
    sessionStorage.setItem('towfik_admin_logged', 'true');
    setActiveTab('admin');
  };

  const handleAdminLogout = () => {
    setIsAdminLoggedIn(false);
    sessionStorage.removeItem('towfik_admin_logged');
    if (activeTab === 'admin') setActiveTab('home');
  };

  const handleSelectSubject = (subjId: string | null) => {
    setSelectedSubjectId(subjId);
    setSelectedChapterId(null);
    setActiveTab('subjects');
  };

  const handleSelectChapter = (chId: string) => {
    setSelectedChapterId(chId);
  };

  const currentChapter = chapters.find(c => c.id === selectedChapterId);
  const currentChapterNotes = notes.filter(n => n.chapterId === selectedChapterId);
  const currentChapterQuestions = questions.filter(q => q.chapterId === selectedChapterId);
  const currentChapterPdfs = pdfLinks.filter(p => p.chapterId === selectedChapterId);

  return (
    <div className="min-h-screen bg-slate-100 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-sans transition-colors duration-200">
      {/* Container Frame mimicking modern Android App layout */}
      <div className="max-w-md mx-auto min-h-screen bg-slate-50 dark:bg-slate-900 shadow-2xl flex flex-col relative border-x border-slate-200 dark:border-slate-800">
        {/* Header */}
        <Header
          onOpenAdminModal={() => {
            if (isAdminLoggedIn) setActiveTab('admin');
            else setAdminModalOpen(true);
          }}
          onOpenSearch={() => {
            setSelectedChapterId(null);
            setActiveTab('search');
          }}
          isAdminLoggedIn={isAdminLoggedIn}
          onAdminLogout={handleAdminLogout}
        />

        {/* Main Body */}
        <main className="flex-1 px-4 pt-4 pb-20 overflow-y-auto">
          {initialLoading ? (
            <div className="flex flex-col items-center justify-center py-20 text-slate-500 dark:text-slate-400 space-y-3">
              <Loader2 size={32} className="animate-spin text-emerald-600 dark:text-emerald-400" />
              <p className="text-xs font-bold tracking-wide">Connecting to Firestore Database...</p>
            </div>
          ) : selectedChapterId && currentChapter ? (
            <ChapterDetailView
              chapter={currentChapter}
              notes={currentChapterNotes}
              questions={currentChapterQuestions}
              pdfLinks={currentChapterPdfs}
              onBack={() => setSelectedChapterId(null)}
            />
          ) : activeTab === 'home' || activeTab === 'search' ? (
            <HomeView
              banners={banners.filter(b => b.isVisible)}
              subjects={subjects}
              chapters={chapters}
              notes={notes}
              questions={questions}
              pdfLinks={pdfLinks}
              onSelectSubject={handleSelectSubject}
              onSelectChapter={handleSelectChapter}
              onNavigateTab={(tab) => {
                setSelectedChapterId(null);
                setActiveTab(tab);
              }}
            />
          ) : activeTab === 'subjects' ? (
            <SubjectsView
              subjects={subjects}
              chapters={chapters}
              pdfLinks={pdfLinks}
              selectedSubjectId={selectedSubjectId}
              onSelectSubject={handleSelectSubject}
              onSelectChapter={handleSelectChapter}
            />
          ) : activeTab === 'suggestions' ? (
            <SuggestionsView
              subjects={subjects}
              chapters={chapters}
              questions={questions}
              onSelectChapter={handleSelectChapter}
            />
          ) : activeTab === 'saved' ? (
            <SavedView onSelectChapter={handleSelectChapter} />
          ) : activeTab === 'admin' ? (
            isAdminLoggedIn ? (
              <AdminDashboard
                banners={banners}
                subjects={subjects}
                chapters={chapters}
                notes={notes}
                questions={questions}
                pdfLinks={pdfLinks}
                onRefreshData={loadData}
                onLogoutAdmin={handleAdminLogout}
              />
            ) : (
              <div className="p-8 text-center text-xs space-y-3">
                <p>Admin authentication required.</p>
                <button
                  onClick={() => setAdminModalOpen(true)}
                  className="px-4 py-2 bg-emerald-600 text-white font-bold rounded-xl"
                >
                  Click to Login as Admin
                </button>
              </div>
            )
          ) : null}
        </main>

        {/* Floating Corner FAB Button */}
        <FloatingButton
          onOpenSearch={() => {
            setSelectedChapterId(null);
            setActiveTab('home');
            window.scrollTo({ top: 0, behavior: 'smooth' });
          }}
          onOpenSaved={() => {
            setSelectedChapterId(null);
            setActiveTab('saved');
          }}
          onOpenAiChat={() => setAiChatOpen(true)}
        />

        {/* Android Bottom Navigation */}
        <BottomNav
          activeTab={activeTab === 'admin' ? 'home' : activeTab}
          onChangeTab={(tab) => {
            setSelectedChapterId(null);
            setActiveTab(tab);
          }}
          savedCount={savedItems.length}
        />

        {/* AI Tutor Chatbot Modal */}
        <AiChatModal
          isOpen={aiChatOpen}
          onClose={() => setAiChatOpen(false)}
        />

        {/* Admin Login Modal */}
        <AdminLoginModal
          isOpen={adminModalOpen}
          onClose={() => setAdminModalOpen(false)}
          onLoginSuccess={handleAdminLoginSuccess}
        />
      </div>
    </div>
  );
}

export default function App() {
  return (
    <ThemeProvider>
      <SavedProvider>
        <AppContent />
      </SavedProvider>
    </ThemeProvider>
  );
}

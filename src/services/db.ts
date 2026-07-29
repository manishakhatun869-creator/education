import {
  collection,
  doc,
  getDocs,
  getDoc,
  addDoc,
  updateDoc,
  deleteDoc,
  setDoc,
  query,
  where,
  orderBy
} from 'firebase/firestore';
import { db } from '../lib/firebase';
import {
  Subject,
  Chapter,
  Note,
  Question,
  PdfLink,
  Banner,
  AppSettings,
  AdminUser,
  SavedItem
} from '../types';

// Collections names
export const COLLECTIONS = {
  ADMIN: 'admin',
  BANNERS: 'banners',
  SUBJECTS: 'subjects',
  CHAPTERS: 'chapters',
  NOTES: 'notes',
  QUESTIONS: 'questions',
  PDF_LINKS: 'pdfLinks',
  SAVED: 'saved',
  SETTINGS: 'settings'
};

// Default App Settings
export const DEFAULT_SETTINGS: AppSettings = {
  appName: 'Towfik Edutips',
  logoUrl: 'https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&q=80&w=200',
  contactEmail: 'support@towfikedutips.com',
  contactPhone: '9609881733',
  whatsappNumber: '9609881733',
  noticeBanner: '🔥 WBBSE Madhyamik 2026 Special Suggestions & Important Notes are now available!',
  theme: 'light',
  aboutText: 'Towfik Edutips is West Bengal\'s premier mobile-first educational portal providing top-quality study notes, MCQs, suggestions, and PDF downloads for WBBSE Madhyamik students.',
  footerText: '© 2026 Towfik Edutips. Dedicated to Madhyamik Excellence.'
};

// Default Admin
export const DEFAULT_ADMIN: AdminUser = {
  username: 'admin',
  password: 'towfik2026'
};

// Initial Seed Data for WBBSE Madhyamik
const SEED_SUBJECTS: Omit<Subject, 'id'>[] = [
  { name: 'Bengali (বাংলা)', code: 'BEN', icon: 'BookOpen', color: '#E53935', description: 'সাহিত্য সঞ্চয়ন, ব্যাকরণ ও কোনি', order: 1, chapterCount: 3 },
  { name: 'English', code: 'ENG', icon: 'Languages', color: '#1E88E5', description: 'Bliss textbook, Grammar, Writing skills', order: 2, chapterCount: 3 },
  { name: 'Mathematics (গণিত)', code: 'MATH', icon: 'Calculator', color: '#43A047', description: 'পাটিগণিত, বীজগণিত, জ্যামিতি ও পরিমিতি', order: 3, chapterCount: 3 },
  { name: 'Physical Science (ভৌতবিজ্ঞান)', code: 'PSC', icon: 'Atom', color: '#FB8C00', description: 'পদার্থবিদ্যা ও রসায়নবিদ্যা', order: 4, chapterCount: 3 },
  { name: 'Life Science (জীবনবিজ্ঞান)', code: 'LSC', icon: 'Dna', color: '#00ACC1', description: 'জীববিজ্ঞান ও পরিবেশ', order: 5, chapterCount: 3 },
  { name: 'History (ইতিহাস)', code: 'HIST', icon: 'Landmark', color: '#8E24AA', description: 'ভারতের ইতিহাস ও জাতীয় আন্দোলন', order: 6, chapterCount: 3 },
  { name: 'Geography (ভূগোল)', code: 'GEO', icon: 'Globe', color: '#D81B60', description: 'প্রাকৃতিক ও আঞ্চলিক ভূগোল', order: 7, chapterCount: 3 }
];

const SEED_BANNERS: Omit<Banner, 'id'>[] = [
  {
    title: 'Madhyamik 2026 Final Suggestion Set',
    imageUrl: 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&q=80&w=1000',
    targetUrl: '#suggestions',
    isVisible: true,
    order: 1,
    createdAt: new Date().toISOString()
  },
  {
    title: 'Complete Chapter PDFs & Solved Model Papers',
    imageUrl: 'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&q=80&w=1000',
    targetUrl: '#pdfs',
    isVisible: true,
    order: 2,
    createdAt: new Date().toISOString()
  },
  {
    title: 'Subjectwise MCQ Test & Rapid Revision',
    imageUrl: 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&q=80&w=1000',
    targetUrl: '#mcq',
    isVisible: true,
    order: 3,
    createdAt: new Date().toISOString()
  }
];

// Helper to check and seed initial Firestore database
export async function seedInitialDatabase() {
  try {
    // 1. Settings check/seed
    const settingsSnap = await getDocs(collection(db, COLLECTIONS.SETTINGS));
    if (settingsSnap.empty) {
      await addDoc(collection(db, COLLECTIONS.SETTINGS), DEFAULT_SETTINGS);
    }

    // 2. Admin check/seed
    const adminSnap = await getDocs(collection(db, COLLECTIONS.ADMIN));
    if (adminSnap.empty) {
      await addDoc(collection(db, COLLECTIONS.ADMIN), DEFAULT_ADMIN);
    }

    // 3. Banners check/seed
    const bannerSnap = await getDocs(collection(db, COLLECTIONS.BANNERS));
    if (bannerSnap.empty) {
      for (const b of SEED_BANNERS) {
        await addDoc(collection(db, COLLECTIONS.BANNERS), b);
      }
    }

    // 4. Subjects & Chapters check/seed
    const subjectSnap = await getDocs(collection(db, COLLECTIONS.SUBJECTS));
    if (subjectSnap.empty) {
      for (const subjData of SEED_SUBJECTS) {
        const docRef = await addDoc(collection(db, COLLECTIONS.SUBJECTS), subjData);
        const subjId = docRef.id;

        // Create sample chapters & notes for each subject
        if (subjData.code === 'BEN') {
          const ch1 = await addDoc(collection(db, COLLECTIONS.CHAPTERS), {
            subjectId: subjId,
            subjectName: subjData.name,
            chapterName: 'জ্ঞানচক্ষু (Gyan Chokkhu)',
            imageUrl: 'https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&q=80&w=600',
            description: 'আশাপূর্ণা দেবীর রচিত কালজয়ী ছোটগল্প "জ্ঞানচক্ষু" এর সম্পূর্ণ বিশ্লেষণ ও প্রশ্নোত্তর।',
            order: 1,
            pdfUrl: 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
            pdfTitle: 'জ্ঞানচক্ষু সম্পূর্ণ নোটস ও প্রশ্নোত্তরের PDF',
            createdAt: new Date().toISOString()
          });

          await addDoc(collection(db, COLLECTIONS.NOTES), {
            chapterId: ch1.id,
            subjectId: subjId,
            title: 'জ্ঞানচক্ষু গল্পের বিষয়সংক্ষেপ',
            content: 'তপনের লেখালিখির আগ্রহ এবং তার মেসোমশায়ের পত্রিকা "সন্ধ্যাতারা"-য় গল্প ছাপানোর অভিজ্ঞতা দিয়ে গল্পটির সূচনা। মেসোমশায় গল্পটি একটু আধটু "কারেকশন" করে ছাপিয়ে দেবেন প্রতিশ্রুতি দেন। পত্রিকা হাতে পেয়ে তপন দেখে পুরো গল্পটাই মেসোমশায় বদলে দিয়েছেন। এই ঘটনার মধ্য দিয়েই তপনের প্রকৃত "জ্ঞানচক্ষু" উন্মীলিত হয়।',
            type: 'summary',
            order: 1,
            createdAt: new Date().toISOString()
          });

          await addDoc(collection(db, COLLECTIONS.QUESTIONS), {
            chapterId: ch1.id,
            subjectId: subjId,
            questionText: '"তপনের মনে হয় আজ যেন তার জীবনের সবচেয়ে দুঃখের দিন।" — তপনের কেন এমন মনে হয়েছিল?',
            answerText: 'সন্ধ্যাতারা পত্রিকায় তপনের ছাপানো গল্পটি পড়তে গিয়ে সে দেখে গল্পটির একটি বাক্যও তার নিজের নয়, সবই তার নতুন মেসোমশাই কারেকশন করার নামে নতুন করে লিখে দিয়েছেন। নিজের নাম সত্ত্বেও অন্য কারোর লেখা গল্প পড়ার লজ্জায় ও অপমানে তপনের মনে হয়েছিল আজই তার জীবনের সবচেয়ে দুঃখের দিন।',
            category: 'short',
            marks: 3,
            order: 1,
            createdAt: new Date().toISOString()
          });

          await addDoc(collection(db, COLLECTIONS.QUESTIONS), {
            chapterId: ch1.id,
            subjectId: subjId,
            questionText: 'তপনের লেখা গল্পের নাম কী ছিল?',
            answerText: 'প্রথম দিন',
            category: 'mcq',
            options: ['প্রথম দিন', 'সন্ধ্যাতারা', 'নতুন মেসো', 'জ্ঞানচক্ষু'],
            correctOptionIndex: 0,
            marks: 1,
            order: 2,
            createdAt: new Date().toISOString()
          });

          await addDoc(collection(db, COLLECTIONS.QUESTIONS), {
            chapterId: ch1.id,
            subjectId: subjId,
            questionText: 'মাধ্যমিক ২০২৬ সাজেশন: "জ্ঞানচক্ষু" গল্পে তপনের চরিত্রটির বিবর্তন আলোচনা করো।',
            answerText: 'তপন একটি সাধারণ বালক যে নতুন মেসোমশাইকে দেখে লেখক হওয়ার স্বপ্ন দেখে। কিন্তু নিজের লেখা গল্প মেসোমশাইয়ের হাতে সম্পূর্ণ পরিবর্তিত হতে দেখে সে বুঝতে পারে যে পরনির্ভরশীল সাফল্য ক্ষণস্থায়ী এবং আত্মমর্যাদাহীন। এরপর সে নিজে না ছাপাতে পারলেও নিজের ভাষায় লেখার সংকল্প গ্রহণ করে।',
            category: 'madhyamik_suggestion',
            marks: 5,
            year: '2026',
            order: 3,
            createdAt: new Date().toISOString()
          });
        } else if (subjData.code === 'ENG') {
          const ch1 = await addDoc(collection(db, COLLECTIONS.CHAPTERS), {
            subjectId: subjId,
            subjectName: subjData.name,
            chapterName: "Father's Help",
            imageUrl: 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&q=80&w=600',
            description: "Detailed analysis, vocabulary and Q&A for R. K. Narayan's story Father's Help.",
            order: 1,
            pdfUrl: 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
            pdfTitle: "Father's Help Complete Notes PDF",
            createdAt: new Date().toISOString()
          });

          await addDoc(collection(db, COLLECTIONS.NOTES), {
            chapterId: ch1.id,
            subjectId: subjId,
            title: "Father's Help - Story Overview",
            content: "Swami did not want to go to school on Monday morning pretending to have a headache. His strict father compelled him to go and write a letter to the headmaster complaining against his teacher Samuel. Later Swami feels guilty about making up false allegations about Samuel.",
            type: 'summary',
            order: 1,
            createdAt: new Date().toISOString()
          });

          await addDoc(collection(db, COLLECTIONS.QUESTIONS), {
            chapterId: ch1.id,
            subjectId: subjId,
            questionText: 'Swami complained of a headache at:',
            answerText: '9:00 AM',
            category: 'mcq',
            options: ['9:00 AM', '9:30 AM', '10:00 AM', '8:00 AM'],
            correctOptionIndex: 0,
            marks: 1,
            order: 1,
            createdAt: new Date().toISOString()
          });

          await addDoc(collection(db, COLLECTIONS.QUESTIONS), {
            chapterId: ch1.id,
            subjectId: subjId,
            questionText: 'Why did Swami feel grieved when he met Samuel in class?',
            answerText: 'Swami felt grieved because Samuel appeared very kind, gentle, and inspected homework leniently, which made Swami feel immense guilt for inventing stories about Samuel to his father.',
            category: 'short',
            marks: 2,
            year: '2023',
            order: 2,
            createdAt: new Date().toISOString()
          });
        } else if (subjData.code === 'MATH') {
          const ch1 = await addDoc(collection(db, COLLECTIONS.CHAPTERS), {
            subjectId: subjId,
            subjectName: subjData.name,
            chapterName: 'একচল বিশিষ্ট দ্বিঘাত সমীকরণ (Quadratic Equations)',
            imageUrl: 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb?auto=format&fit=crop&q=80&w=600',
            description: 'দ্বিঘাত সমীকরণের সাধারণ রূপ ax² + bx + c = 0, শ্রীধর আচার্যের সূত্র ও বীজদ্বয়ের প্রকৃতি।',
            order: 1,
            pdfUrl: 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
            pdfTitle: 'একচল বিশিষ্ট দ্বিঘাত সমীকরণ সম্পূর্ণ সমাধান PDF',
            createdAt: new Date().toISOString()
          });

          await addDoc(collection(db, COLLECTIONS.NOTES), {
            chapterId: ch1.id,
            subjectId: subjId,
            title: 'গুরুত্বপূর্ণ সূত্রসমূহ (Important Formulas)',
            content: '১. একচল বিশিষ্ট দ্বিঘাত সমীকরণের সাধারণ রূপ: ax² + bx + c = 0 (যেখানে a ≠ 0)।\n২. শ্রীধর আচার্যের সূত্র: x = (-b ± √(b² - 4ac)) / (2a)।\n৩. নিরূপক (Discriminant): D = b² - 4ac।\n   - যদি D > 0 হয়, তবে বীজদ্বয় বাস্তব ও অসমান।\n   - যদি D = 0 হয়, তবে বীজদ্বয় বাস্তব ও সমান।\n   - যদি D < 0 হয়, তবে কোনো বাস্তব বীজ নেই।',
            type: 'formula',
            order: 1,
            createdAt: new Date().toISOString()
          });

          await addDoc(collection(db, COLLECTIONS.QUESTIONS), {
            chapterId: ch1.id,
            subjectId: subjId,
            questionText: 'ax² + bx + c = 0 সমীকরণের বীজদ্বয় সমান হওয়ার শর্ত কী?',
            answerText: 'b² - 4ac = 0',
            category: 'mcq',
            options: ['b² - 4ac = 0', 'b² - 4ac > 0', 'b² - 4ac < 0', 'b² + 4ac = 0'],
            correctOptionIndex: 0,
            marks: 1,
            order: 1,
            createdAt: new Date().toISOString()
          });
        } else {
          // Default fallback chapter for other subjects
          const ch = await addDoc(collection(db, COLLECTIONS.CHAPTERS), {
            subjectId: subjId,
            subjectName: subjData.name,
            chapterName: `প্রথম অধ্যায়: ${subjData.name} পরিচিতি`,
            imageUrl: 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&q=80&w=600',
            description: `মাধ্যমিক ২০২৬ এর জন্য ${subjData.name} এর প্রথম অধ্যায়ের গুরুত্বপূর্ণ প্রশ্ন ও নোটস।`,
            order: 1,
            pdfUrl: 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
            pdfTitle: `${subjData.name} Chapter 1 Notes PDF`,
            createdAt: new Date().toISOString()
          });

          await addDoc(collection(db, COLLECTIONS.NOTES), {
            chapterId: ch.id,
            subjectId: subjId,
            title: `${subjData.name} প্রথম অধ্যায়ের সারসংক্ষেপ`,
            content: `${subjData.name} বিষয়ে ভালো নম্বর পেতে অধ্যায়টির প্রতিটি ধারণা গভীরভাবে বোঝা জরুরি। মাধ্যমিক পরীক্ষায় সরাসরি ও ব্যাখ্যাভিত্তিক উভয় ধরনের প্রশ্ন থাকে।`,
            type: 'summary',
            order: 1,
            createdAt: new Date().toISOString()
          });

          await addDoc(collection(db, COLLECTIONS.QUESTIONS), {
            chapterId: ch.id,
            subjectId: subjId,
            questionText: `মাধ্যমিক ২০২৬ এর জন্য ${subjData.name} এর সম্ভাব্য মোস্ট ইম্পরট্যান্ট প্রশ্ন।`,
            answerText: 'বিশদ উত্তর পেতে নোটস অধ্যায়টি পড়ুন এবং নিয়ম মেনে অনুশীলন করুন।',
            category: 'madhyamik_suggestion',
            marks: 5,
            year: '2026',
            order: 1,
            createdAt: new Date().toISOString()
          });
        }
      }
    }
  } catch (err) {
    console.error('Error seeding database:', err);
  }
}

// ========================
// FIRESTORE API SERVICES
// ========================

// Settings
export async function getAppSettings(): Promise<AppSettings> {
  try {
    const snap = await getDocs(collection(db, COLLECTIONS.SETTINGS));
    if (!snap.empty) {
      const docData = snap.docs[0];
      return { id: docData.id, ...docData.data() } as AppSettings;
    }
    return DEFAULT_SETTINGS;
  } catch {
    return DEFAULT_SETTINGS;
  }
}

export async function updateAppSettings(settingsId: string, data: Partial<AppSettings>): Promise<void> {
  const docRef = doc(db, COLLECTIONS.SETTINGS, settingsId);
  await updateDoc(docRef, data);
}

// Admin Auth
export async function getAdminCredentials(): Promise<AdminUser> {
  try {
    const snap = await getDocs(collection(db, COLLECTIONS.ADMIN));
    if (!snap.empty) {
      const docData = snap.docs[0];
      return { id: docData.id, ...docData.data() } as AdminUser;
    }
    return DEFAULT_ADMIN;
  } catch {
    return DEFAULT_ADMIN;
  }
}

export async function updateAdminCredentials(adminId: string, data: Partial<AdminUser>): Promise<void> {
  const docRef = doc(db, COLLECTIONS.ADMIN, adminId);
  await updateDoc(docRef, data);
}

// Banners
export async function getBanners(onlyVisible = true): Promise<Banner[]> {
  try {
    const snap = await getDocs(collection(db, COLLECTIONS.BANNERS));
    let list = snap.docs.map(d => ({ id: d.id, ...d.data() } as Banner));
    if (onlyVisible) {
      list = list.filter(b => b.isVisible);
    }
    return list.sort((a, b) => (a.order || 0) - (b.order || 0));
  } catch {
    return [];
  }
}

export async function createBanner(banner: Omit<Banner, 'id'>): Promise<string> {
  const docRef = await addDoc(collection(db, COLLECTIONS.BANNERS), banner);
  return docRef.id;
}

export async function updateBanner(id: string, banner: Partial<Banner>): Promise<void> {
  await updateDoc(doc(db, COLLECTIONS.BANNERS, id), banner);
}

export async function deleteBanner(id: string): Promise<void> {
  await deleteDoc(doc(db, COLLECTIONS.BANNERS, id));
}

// Subjects
export async function getSubjects(): Promise<Subject[]> {
  try {
    const snap = await getDocs(collection(db, COLLECTIONS.SUBJECTS));
    const list = snap.docs.map(d => ({ id: d.id, ...d.data() } as Subject));
    return list.sort((a, b) => (a.order || 0) - (b.order || 0));
  } catch {
    return [];
  }
}

export async function createSubject(subject: Omit<Subject, 'id'>): Promise<string> {
  const docRef = await addDoc(collection(db, COLLECTIONS.SUBJECTS), subject);
  return docRef.id;
}

export async function updateSubject(id: string, subject: Partial<Subject>): Promise<void> {
  await updateDoc(doc(db, COLLECTIONS.SUBJECTS, id), subject);
}

export async function deleteSubject(id: string): Promise<void> {
  await deleteDoc(doc(db, COLLECTIONS.SUBJECTS, id));
}

// Chapters
export async function getChapters(subjectId?: string): Promise<Chapter[]> {
  try {
    const snap = await getDocs(collection(db, COLLECTIONS.CHAPTERS));
    let list = snap.docs.map(d => ({ id: d.id, ...d.data() } as Chapter));
    if (subjectId) {
      list = list.filter(c => c.subjectId === subjectId);
    }
    return list.sort((a, b) => (a.order || 0) - (b.order || 0));
  } catch {
    return [];
  }
}

export async function getChapterById(id: string): Promise<Chapter | null> {
  try {
    const d = await getDoc(doc(db, COLLECTIONS.CHAPTERS, id));
    if (d.exists()) {
      return { id: d.id, ...d.data() } as Chapter;
    }
    return null;
  } catch {
    return null;
  }
}

export async function createChapter(chapter: Omit<Chapter, 'id'>): Promise<string> {
  const docRef = await addDoc(collection(db, COLLECTIONS.CHAPTERS), chapter);
  return docRef.id;
}

export async function updateChapter(id: string, chapter: Partial<Chapter>): Promise<void> {
  await updateDoc(doc(db, COLLECTIONS.CHAPTERS, id), chapter);
}

export async function deleteChapter(id: string): Promise<void> {
  await deleteDoc(doc(db, COLLECTIONS.CHAPTERS, id));
}

// Notes
export async function getNotes(chapterId?: string): Promise<Note[]> {
  try {
    const snap = await getDocs(collection(db, COLLECTIONS.NOTES));
    let list = snap.docs.map(d => ({ id: d.id, ...d.data() } as Note));
    if (chapterId) {
      list = list.filter(n => n.chapterId === chapterId);
    }
    return list.sort((a, b) => (a.order || 0) - (b.order || 0));
  } catch {
    return [];
  }
}

export async function createNote(note: Omit<Note, 'id'>): Promise<string> {
  const docRef = await addDoc(collection(db, COLLECTIONS.NOTES), note);
  return docRef.id;
}

export async function updateNote(id: string, note: Partial<Note>): Promise<void> {
  await updateDoc(doc(db, COLLECTIONS.NOTES, id), note);
}

export async function deleteNote(id: string): Promise<void> {
  await deleteDoc(doc(db, COLLECTIONS.NOTES, id));
}

// Questions
export async function getQuestions(chapterId?: string, category?: string): Promise<Question[]> {
  try {
    const snap = await getDocs(collection(db, COLLECTIONS.QUESTIONS));
    let list = snap.docs.map(d => ({ id: d.id, ...d.data() } as Question));
    if (chapterId) {
      list = list.filter(q => q.chapterId === chapterId);
    }
    if (category) {
      list = list.filter(q => q.category === category);
    }
    return list.sort((a, b) => (a.order || 0) - (b.order || 0));
  } catch {
    return [];
  }
}

export async function createQuestion(q: Omit<Question, 'id'>): Promise<string> {
  const docRef = await addDoc(collection(db, COLLECTIONS.QUESTIONS), q);
  return docRef.id;
}

export async function updateQuestion(id: string, q: Partial<Question>): Promise<void> {
  await updateDoc(doc(db, COLLECTIONS.QUESTIONS, id), q);
}

export async function deleteQuestion(id: string): Promise<void> {
  await deleteDoc(doc(db, COLLECTIONS.QUESTIONS, id));
}

// PDF Links
export async function getPdfLinks(chapterId?: string): Promise<PdfLink[]> {
  try {
    const snap = await getDocs(collection(db, COLLECTIONS.PDF_LINKS));
    let list = snap.docs.map(d => ({ id: d.id, ...d.data() } as PdfLink));
    if (chapterId) {
      list = list.filter(p => p.chapterId === chapterId);
    }
    return list;
  } catch {
    return [];
  }
}

export async function createPdfLink(pdf: Omit<PdfLink, 'id'>): Promise<string> {
  const docRef = await addDoc(collection(db, COLLECTIONS.PDF_LINKS), pdf);
  return docRef.id;
}

export async function updatePdfLink(id: string, pdf: Partial<PdfLink>): Promise<void> {
  await updateDoc(doc(db, COLLECTIONS.PDF_LINKS, id), pdf);
}

export async function deletePdfLink(id: string): Promise<void> {
  await deleteDoc(doc(db, COLLECTIONS.PDF_LINKS, id));
}

// Saved Items (Firestore collection + Local Storage sync)
export async function getSavedItems(): Promise<SavedItem[]> {
  try {
    const snap = await getDocs(collection(db, COLLECTIONS.SAVED));
    return snap.docs.map(d => ({ id: d.id, ...d.data() } as SavedItem));
  } catch {
    return [];
  }
}

export async function saveItemToFirestore(item: Omit<SavedItem, 'id'>): Promise<string> {
  const docRef = await addDoc(collection(db, COLLECTIONS.SAVED), item);
  return docRef.id;
}

export async function removeSavedItemFromFirestore(id: string): Promise<void> {
  await deleteDoc(doc(db, COLLECTIONS.SAVED, id));
}

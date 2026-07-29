export interface Subject {
  id: string;
  name: string;
  code: string;
  icon: string;
  logoUrl?: string;
  color: string;
  description: string;
  order: number;
  chapterCount?: number;
}

export interface Chapter {
  id: string;
  subjectId: string;
  subjectName?: string;
  chapterName: string;
  imageUrl: string;
  description: string;
  order: number;
  pdfUrl?: string;
  pdfTitle?: string;
  createdAt?: string;
}

export interface Note {
  id: string;
  chapterId: string;
  subjectId?: string;
  title: string;
  content: string;
  type?: 'summary' | 'concept' | 'keypoint' | 'formula';
  order: number;
  createdAt?: string;
}

export type QuestionCategory =
  | 'mcq'
  | 'short'
  | 'long'
  | 'important'
  | 'pyq'
  | 'madhyamik_suggestion';

export interface Question {
  id: string;
  chapterId: string;
  subjectId?: string;
  questionText: string;
  answerText: string;
  category: QuestionCategory;
  options?: string[];
  correctOptionIndex?: number;
  marks?: number;
  year?: string;
  order: number;
  createdAt?: string;
}

export interface PdfLink {
  id: string;
  chapterId: string;
  subjectId?: string;
  title: string;
  url: string;
  size?: string;
  description?: string;
  createdAt?: string;
}

export interface Banner {
  id: string;
  title: string;
  imageUrl: string;
  targetUrl?: string;
  isVisible: boolean;
  order: number;
  createdAt?: string;
}

export interface SavedItem {
  id: string;
  itemId: string;
  itemType: 'chapter' | 'note' | 'question' | 'pdf';
  title: string;
  subtitle?: string;
  chapterId?: string;
  subjectId?: string;
  savedAt: string;
  payload?: any;
}

export interface AppSettings {
  id?: string;
  appName: string;
  logoUrl: string;
  contactEmail: string;
  contactPhone: string;
  whatsappNumber: string;
  noticeBanner: string;
  theme: 'light' | 'dark' | 'system';
  aboutText: string;
  footerText: string;
}

export interface AdminUser {
  id?: string;
  username: string;
  password: string;
  lastLogin?: string;
}

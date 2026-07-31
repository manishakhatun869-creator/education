package com.towfikedutips.app.model

data class Subject(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val icon: String = "BookOpen",
    val logoUrl: String? = null,
    val color: String = "#10B981",
    val description: String = "",
    val order: Int = 0,
    val chapterCount: Int? = null
)

data class Chapter(
    val id: String = "",
    val subjectId: String = "",
    val subjectName: String? = null,
    val chapterName: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val order: Int = 0,
    val pdfUrl: String? = null,
    val pdfTitle: String? = null,
    val createdAt: String = ""
)

data class Note(
    val id: String = "",
    val chapterId: String = "",
    val subjectId: String? = null,
    val title: String = "",
    val content: String = "",
    val type: String? = "summary",
    val order: Int = 0,
    val createdAt: String = ""
)

data class Question(
    val id: String = "",
    val chapterId: String = "",
    val subjectId: String? = null,
    val questionText: String = "",
    val answerText: String = "",
    val category: String = "mcq", // mcq, short, long, important, pyq, madhyamik_suggestion
    val options: List<String>? = null,
    val correctOptionIndex: Int? = null,
    val marks: Int? = null,
    val year: String? = null,
    val order: Int = 0,
    val createdAt: String = ""
)

data class SavedItem(
    val id: String = "",
    val itemId: String = "",
    val itemType: String = "", // chapter, note, question, pdf
    val title: String = "",
    val subtitle: String? = null,
    val chapterId: String? = null,
    val subjectId: String? = null,
    val savedAt: String = ""
)

data class Banner(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val targetUrl: String? = null,
    val isVisible: Boolean = true,
    val order: Int = 0,
    val createdAt: String = ""
)

data class AppSettings(
    val appName: String = "Towfik Edutips",
    val logoUrl: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val whatsappNumber: String = "",
    val noticeBanner: String = "",
    val theme: String = "light",
    val aboutText: String = "",
    val footerText: String = ""
)

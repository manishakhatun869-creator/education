package com.towfikedutips.app.data

import com.towfikedutips.app.model.*

object SeedData {
    val banners = listOf(
        Banner(
            id = "banner_1",
            title = "Madhyamik 2026 Final Suggestion Set",
            imageUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&q=80&w=1000",
            targetUrl = "#suggestions",
            isVisible = true,
            order = 1
        ),
        Banner(
            id = "banner_2",
            title = "Complete Chapter PDFs & Solved Model Papers",
            imageUrl = "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&q=80&w=1000",
            targetUrl = "#pdfs",
            isVisible = true,
            order = 2
        ),
        Banner(
            id = "banner_3",
            title = "Subjectwise MCQ Test & Rapid Revision",
            imageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&q=80&w=1000",
            targetUrl = "#mcq",
            isVisible = true,
            order = 3
        )
    )

    val subjects = listOf(
        Subject("sub_1", "Bengali (বাংলা)", "BEN", "BookOpen", null, "#E53935", "সাহিত্য সঞ্চয়ন, ব্যাকরণ ও কোনি", 1, 3),
        Subject("sub_2", "English", "ENG", "Languages", null, "#1E88E5", "Bliss textbook, Grammar, Writing skills", 2, 3),
        Subject("sub_3", "Mathematics (গণিত)", "MATH", "Calculator", null, "#43A047", "পাটিগণিত, বীজগণিত, জ্যামিতি ও পরিমিতি", 3, 3),
        Subject("sub_4", "Physical Science (ভৌতবিজ্ঞান)", "PSC", "Atom", null, "#FB8C00", "পদার্থবিদ্যা ও রসায়নবিদ্যা", 4, 3),
        Subject("sub_5", "Life Science (জীবনবিজ্ঞান)", "LSC", "Dna", null, "#00ACC1", "জীববিজ্ঞান ও পরিবেশ", 5, 3),
        Subject("sub_6", "History (ইতিহাস)", "HIST", "Landmark", null, "#8E24AA", "ভারতের ইতিহাস ও জাতীয় আন্দোলন", 6, 3),
        Subject("sub_7", "Geography (ভূগোল)", "GEO", "Globe", null, "#D81B60", "প্রাকৃতিক ও আঞ্চলিক ভূগোল", 7, 3)
    )

    val chapters = listOf(
        Chapter(
            id = "ch_ben_1",
            subjectId = "sub_1",
            subjectName = "Bengali (বাংলা)",
            chapterName = "জ্ঞানচক্ষু (Gyan Chokkhu)",
            imageUrl = "https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&q=80&w=600",
            description = "আশাপূর্ণা দেবীর রচিত কালজয়ী ছোটগল্প \"জ্ঞানচক্ষু\" এর সম্পূর্ণ বিশ্লেষণ ও প্রশ্নোত্তর।",
            order = 1,
            pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            pdfTitle = "জ্ঞানচক্ষু সম্পূর্ণ নোটস ও প্রশ্নোত্তরের PDF"
        ),
        Chapter(
            id = "ch_eng_1",
            subjectId = "sub_2",
            subjectName = "English",
            chapterName = "Father's Help",
            imageUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&q=80&w=600",
            description = "Detailed analysis, vocabulary and Q&A for R. K. Narayan's story Father's Help.",
            order = 1,
            pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            pdfTitle = "Father's Help Complete Notes PDF"
        ),
        Chapter(
            id = "ch_math_1",
            subjectId = "sub_3",
            subjectName = "Mathematics (গণিত)",
            chapterName = "একচল বিশিষ্ট দ্বিঘাত সমীকরণ (Quadratic Equations)",
            imageUrl = "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?auto=format&fit=crop&q=80&w=600",
            description = "দ্বিঘাত সমীকরণের সাধারণ রূপ ax² + bx + c = 0, শ্রীধর আচার্যের সূত্র ও বীজদ্বয়ের প্রকৃতি।",
            order = 1,
            pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            pdfTitle = "একচল বিশিষ্ট দ্বিঘাত সমীকরণ সম্পূর্ণ সমাধান PDF"
        )
    )

    val notes = listOf(
        Note(
            id = "note_ben_1",
            chapterId = "ch_ben_1",
            subjectId = "sub_1",
            title = "জ্ঞানচক্ষু গল্পের বিষয়সংক্ষেপ",
            content = "তপনের লেখালিখির আগ্রহ এবং তার মেসোমশায়ের পত্রিকা \"সন্ধ্যাতারা\"-য় গল্প ছাপানোর অভিজ্ঞতা দিয়ে গল্পটির সূচনা। মেসোমশায় গল্পটি একটু আধটু \"কারেকশন\" করে ছাপিয়ে দেবেন প্রতিশ্রুতি দেন। পত্রিকা হাতে পেয়ে তপন দেখে পুরো গল্পটাই মেসোমশায় বদলে দিয়েছেন। এই ঘটনার মধ্য দিয়েই তপনের প্রকৃত \"জ্ঞানচক্ষু\" উন্মীলিত হয়।",
            type = "summary",
            order = 1
        ),
        Note(
            id = "note_eng_1",
            chapterId = "ch_eng_1",
            subjectId = "sub_2",
            title = "Father's Help - Story Overview",
            content = "Swami did not want to go to school on Monday morning pretending to have a headache. His strict father compelled him to go and write a letter to the headmaster complaining against his teacher Samuel. Later Swami feels guilty about making up false allegations about Samuel.",
            type = "summary",
            order = 1
        ),
        Note(
            id = "note_math_1",
            chapterId = "ch_math_1",
            subjectId = "sub_3",
            title = "গুরুত্বপূর্ণ সূত্রসমূহ (Important Formulas)",
            content = "১. একচল বিশিষ্ট দ্বিঘাত সমীকরণের সাধারণ রূপ: ax² + bx + c = 0 (যেখানে a ≠ 0)।\n২. শ্রীধর আচার্যের সূত্র: x = (-b ± √(b² - 4ac)) / (2a)।\n৩. নিরূপক (Discriminant): D = b² - 4ac।\n   - যদি D > 0 হয়, তবে বীজদ্বয় বাস্তব ও অসমান।\n   - যদি D = 0 হয়, তবে বীজদ্বয় বাস্তব ও সমান।\n   - যদি D < 0 হয়, তবে কোনো বাস্তব বীজ নেই।",
            type = "formula",
            order = 1
        )
    )

    val questions = listOf(
        Question(
            id = "q_ben_1",
            chapterId = "ch_ben_1",
            subjectId = "sub_1",
            questionText = "\"তপনের মনে হয় আজ যেন তার জীবনের সবচেয়ে দুঃখের দিন।\" — তপনের কেন এমন মনে হয়েছিল?",
            answerText = "সন্ধ্যাতারা পত্রিকায় তপনের ছাপানো গল্পটি পড়তে গিয়ে সে দেখে গল্পটির একটি বাক্যও তার নিজের নয়, সবই তার নতুন মেসোমশাই কারেকশন করার নামে নতুন করে লিখে দিয়েছেন। নিজের নাম সত্ত্বেও অন্য কারোর লেখা গল্প পড়ার লজ্জায় ও অপমানে তপনের মনে হয়েছিল আজই তার জীবনের সবচেয়ে দুঃখের দিন।",
            category = "short",
            marks = 3,
            order = 1
        ),
        Question(
            id = "q_ben_2",
            chapterId = "ch_ben_1",
            subjectId = "sub_1",
            questionText = "তপনের লেখা গল্পের নাম কী ছিল?",
            answerText = "প্রথম দিন",
            category = "mcq",
            options = listOf("প্রথম দিন", "সন্ধ্যাতারা", "নতুন মেসো", "জ্ঞানচক্ষু"),
            correctOptionIndex = 0,
            marks = 1,
            order = 2
        ),
        Question(
            id = "q_ben_3",
            chapterId = "ch_ben_1",
            subjectId = "sub_1",
            questionText = "মাধ্যমিক ২০২৬ সাজেশন: \"জ্ঞানচক্ষু\" গল্পে তপনের চরিত্রটির বিবর্তন আলোচনা করো।",
            answerText = "তপন একটি সাধারণ বালক যে নতুন মেসোমশাইকে দেখে লেখক হওয়ার স্বপ্ন দেখে। কিন্তু নিজের লেখা গল্প মেসোমশাইয়ের হাতে সম্পূর্ণ পরিবর্তিত হতে দেখে সে বুঝতে পারে যে পরনির্ভরশীল সাফল্য ক্ষণস্থায়ী এবং আত্মমর্যাদাহীন। এরপর সে নিজে না ছাপাতে পারলেও নিজের ভাষায় লেখার সংকল্প গ্রহণ করে।",
            category = "madhyamik_suggestion",
            marks = 5,
            year = "2026",
            order = 3
        ),
        Question(
            id = "q_eng_1",
            chapterId = "ch_eng_1",
            subjectId = "sub_2",
            questionText = "Swami complained of a headache at:",
            answerText = "9:00 AM",
            category = "mcq",
            options = listOf("9:00 AM", "9:30 AM", "10:00 AM", "8:00 AM"),
            correctOptionIndex = 0,
            marks = 1,
            order = 1
        ),
        Question(
            id = "q_eng_2",
            chapterId = "ch_eng_1",
            subjectId = "sub_2",
            questionText = "Why did Swami feel grieved when he met Samuel in class?",
            answerText = "Swami felt grieved because Samuel appeared very kind, gentle, and inspected homework leniently, which made Swami feel immense guilt for inventing stories about Samuel to his father.",
            category = "short",
            marks = 2,
            year = "2023",
            order = 2
        ),
        Question(
            id = "q_math_1",
            chapterId = "ch_math_1",
            subjectId = "sub_3",
            questionText = "ax² + bx + c = 0 সমীকরণের বীজদ্বয় সমান হওয়ার শর্ত কী?",
            answerText = "b² - 4ac = 0",
            category = "mcq",
            options = listOf("b² - 4ac = 0", "b² - 4ac > 0", "b² - 4ac < 0", "b² + 4ac = 0"),
            correctOptionIndex = 0,
            marks = 1,
            order = 1
        )
    )
}

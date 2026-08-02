package com.towfikedutips.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.towfikedutips.app.model.Banner
import com.towfikedutips.app.model.Chapter
import com.towfikedutips.app.model.Note
import com.towfikedutips.app.model.Question
import com.towfikedutips.app.model.Subject
import com.towfikedutips.app.model.AppSettings

enum class AdminTab(val title: String) {
    SUBJECTS("Subjects"),
    CHAPTERS("Chapters"),
    NOTES("Notes"),
    QUESTIONS("Questions"),
    BANNERS("Banners"),
    SETTINGS("Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(AdminTab.SUBJECTS) }

    val firestore = remember { com.towfikedutips.app.data.FirestoreProvider.getFirestore(context) }

    // Live state lists
    val subjectsList = remember { mutableStateListOf<Subject>() }
    val chaptersList = remember { mutableStateListOf<Chapter>() }
    val notesList = remember { mutableStateListOf<Note>() }
    val questionsList = remember { mutableStateListOf<Question>() }
    val bannersList = remember { mutableStateListOf<Banner>() }
    var appSettingsState by remember { mutableStateOf(AppSettings()) }
    var appSettingsDocId by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    // Dialog control states
    var showAddEditSubjectDialog by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<Subject?>(null) }

    var showAddEditChapterDialog by remember { mutableStateOf(false) }
    var editingChapter by remember { mutableStateOf<Chapter?>(null) }

    var showAddEditNoteDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }

    var showAddEditQuestionDialog by remember { mutableStateOf(false) }
    var editingQuestion by remember { mutableStateOf<Question?>(null) }

    var showAddEditBannerDialog by remember { mutableStateOf(false) }
    var editingBanner by remember { mutableStateOf<Banner?>(null) }

    // Functions to fetch records from Firestore
    fun fetchAllData() {
        isLoading = true

        // Fetch Subjects
        firestore.collection("subjects").get().addOnSuccessListener { querySnapshot ->
            subjectsList.clear()
            val items = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Subject::class.java)?.copy(id = doc.id)
            }.sortedBy { it.order }
            subjectsList.addAll(items)
        }

        // Fetch Chapters
        firestore.collection("chapters").get().addOnSuccessListener { querySnapshot ->
            chaptersList.clear()
            val items = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Chapter::class.java)?.copy(id = doc.id)
            }.sortedBy { it.order }
            chaptersList.addAll(items)
        }

        // Fetch Notes
        firestore.collection("notes").get().addOnSuccessListener { querySnapshot ->
            notesList.clear()
            val items = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Note::class.java)?.copy(id = doc.id)
            }.sortedBy { it.order }
            notesList.addAll(items)
        }

        // Fetch Questions
        firestore.collection("questions").get().addOnSuccessListener { querySnapshot ->
            questionsList.clear()
            val items = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Question::class.java)?.copy(id = doc.id)
            }.sortedBy { it.order }
            questionsList.addAll(items)
        }

        // Fetch Banners
        firestore.collection("banners").get().addOnSuccessListener { querySnapshot ->
            bannersList.clear()
            val items = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Banner::class.java)?.copy(id = doc.id)
            }.sortedBy { it.order }
            bannersList.addAll(items)
        }

        // Fetch Settings
        firestore.collection("settings").get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents[0]
                appSettingsDocId = doc.id
                appSettingsState = doc.toObject(AppSettings::class.java) ?: AppSettings()
            }
            isLoading = false
        }.addOnFailureListener {
            isLoading = false
            Toast.makeText(context, "Error loading data from Firestore", Toast.LENGTH_SHORT).show()
        }
    }

    // Load initial data
    LaunchedEffect(Unit) {
        fetchAllData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel Dashboard", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { fetchAllData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            // Settings doesn't need a float add button as it's a static panel configuration
            if (selectedTab != AdminTab.SETTINGS) {
                FloatingActionButton(
                    onClick = {
                        when (selectedTab) {
                            AdminTab.SUBJECTS -> {
                                editingSubject = null
                                showAddEditSubjectDialog = true
                            }
                            AdminTab.CHAPTERS -> {
                                editingChapter = null
                                showAddEditChapterDialog = true
                            }
                            AdminTab.NOTES -> {
                                editingNote = null
                                showAddEditNoteDialog = true
                            }
                            AdminTab.QUESTIONS -> {
                                editingQuestion = null
                                showAddEditQuestionDialog = true
                            }
                            AdminTab.BANNERS -> {
                                editingBanner = null
                                showAddEditBannerDialog = true
                            }
                            else -> {}
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add New Item")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Horizontal Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                AdminTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (selectedTab) {
                        AdminTab.SUBJECTS -> {
                            if (subjectsList.isEmpty()) {
                                item { Text("No subjects found.", color = Color.Gray, fontSize = 12.sp) }
                            }
                            items(subjectsList) { subject ->
                                SubjectAdminRow(
                                    subject = subject,
                                    onEdit = {
                                        editingSubject = subject
                                        showAddEditSubjectDialog = true
                                    },
                                    onDelete = {
                                        firestore.collection("subjects").document(subject.id).delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Subject deleted", Toast.LENGTH_SHORT).show()
                                                fetchAllData()
                                            }
                                    }
                                )
                            }
                        }
                        AdminTab.CHAPTERS -> {
                            if (chaptersList.isEmpty()) {
                                item { Text("No chapters found. Add one first.", color = Color.Gray, fontSize = 12.sp) }
                            }
                            items(chaptersList) { chapter ->
                                ChapterAdminRow(
                                    chapter = chapter,
                                    onEdit = {
                                        editingChapter = chapter
                                        showAddEditChapterDialog = true
                                    },
                                    onDelete = {
                                        firestore.collection("chapters").document(chapter.id).delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Chapter deleted", Toast.LENGTH_SHORT).show()
                                                fetchAllData()
                                            }
                                    }
                                )
                            }
                        }
                        AdminTab.NOTES -> {
                            if (notesList.isEmpty()) {
                                item { Text("No notes found. Add one first.", color = Color.Gray, fontSize = 12.sp) }
                            }
                            items(notesList) { note ->
                                NoteAdminRow(
                                    note = note,
                                    onEdit = {
                                        editingNote = note
                                        showAddEditNoteDialog = true
                                    },
                                    onDelete = {
                                        firestore.collection("notes").document(note.id).delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
                                                fetchAllData()
                                            }
                                    }
                                )
                            }
                        }
                        AdminTab.QUESTIONS -> {
                            if (questionsList.isEmpty()) {
                                item { Text("No questions found. Add one first.", color = Color.Gray, fontSize = 12.sp) }
                            }
                            items(questionsList) { question ->
                                QuestionAdminRow(
                                    question = question,
                                    onEdit = {
                                        editingQuestion = question
                                        showAddEditQuestionDialog = true
                                    },
                                    onDelete = {
                                        firestore.collection("questions").document(question.id).delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Question deleted", Toast.LENGTH_SHORT).show()
                                                fetchAllData()
                                            }
                                    }
                                )
                            }
                        }
                        AdminTab.BANNERS -> {
                            if (bannersList.isEmpty()) {
                                item { Text("No banners found.", color = Color.Gray, fontSize = 12.sp) }
                            }
                            items(bannersList) { banner ->
                                BannerAdminRow(
                                    banner = banner,
                                    onEdit = {
                                        editingBanner = banner
                                        showAddEditBannerDialog = true
                                    },
                                    onDelete = {
                                        firestore.collection("banners").document(banner.id).delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Banner deleted", Toast.LENGTH_SHORT).show()
                                                fetchAllData()
                                            }
                                    }
                                )
                            }
                        }
                        AdminTab.SETTINGS -> {
                            item {
                                AppSettingsPanel(
                                    settings = appSettingsState,
                                    onSave = { updatedSettings ->
                                        val data = mapOf(
                                            "appName" to updatedSettings.appName,
                                            "logoUrl" to updatedSettings.logoUrl,
                                            "contactEmail" to updatedSettings.contactEmail,
                                            "contactPhone" to updatedSettings.contactPhone,
                                            "whatsappNumber" to updatedSettings.whatsappNumber,
                                            "noticeBanner" to updatedSettings.noticeBanner,
                                            "theme" to updatedSettings.theme,
                                            "aboutText" to updatedSettings.aboutText,
                                            "footerText" to updatedSettings.footerText
                                        )

                                        val docId = appSettingsDocId
                                        val task = if (docId != null) {
                                            firestore.collection("settings").document(docId).set(data)
                                        } else {
                                            firestore.collection("settings").add(data)
                                        }

                                        task.addOnSuccessListener {
                                            Toast.makeText(context, "App settings updated successfully", Toast.LENGTH_SHORT).show()
                                            fetchAllData()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // ===================================
        // ADD / EDIT SUBJECT DIALOG
        // ===================================
        if (showAddEditSubjectDialog) {
            var name by remember { mutableStateOf(editingSubject?.name ?: "") }
            var code by remember { mutableStateOf(editingSubject?.code ?: "") }
            var color by remember { mutableStateOf(editingSubject?.color ?: "#10B981") }
            var description by remember { mutableStateOf(editingSubject?.description ?: "") }
            var order by remember { mutableStateOf(editingSubject?.order?.toString() ?: "0") }

            AlertDialog(
                onDismissRequest = { showAddEditSubjectDialog = false },
                title = { Text(if (editingSubject == null) "Add Subject" else "Edit Subject") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Subject Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Code (e.g. BEN, ENG)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color HEX (e.g. #10B981)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = order, onValueChange = { order = it }, label = { Text("Order") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val o = order.toIntOrNull() ?: 0
                        val data = mapOf(
                            "name" to name,
                            "code" to code,
                            "color" to color,
                            "description" to description,
                            "order" to o
                        )
                        val task = if (editingSubject == null) {
                            firestore.collection("subjects").add(data)
                        } else {
                            firestore.collection("subjects").document(editingSubject!!.id).set(data)
                        }
                        task.addOnSuccessListener {
                            Toast.makeText(context, "Subject saved", Toast.LENGTH_SHORT).show()
                            showAddEditSubjectDialog = false
                            fetchAllData()
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddEditSubjectDialog = false }) { Text("Cancel") }
                }
            )
        }

        // ===================================
        // ADD / EDIT CHAPTER DIALOG
        // ===================================
        if (showAddEditChapterDialog) {
            var selectedSubject by remember { mutableStateOf(subjectsList.find { it.id == (editingChapter?.subjectId ?: "") } ?: subjectsList.firstOrNull()) }
            var name by remember { mutableStateOf(editingChapter?.chapterName ?: "") }
            var desc by remember { mutableStateOf(editingChapter?.description ?: "") }
            var imageUrl by remember { mutableStateOf(editingChapter?.imageUrl ?: "") }
            var pdfUrl by remember { mutableStateOf(editingChapter?.pdfUrl ?: "") }
            var pdfTitle by remember { mutableStateOf(editingChapter?.pdfTitle ?: "") }
            var order by remember { mutableStateOf(editingChapter?.order?.toString() ?: "0") }

            AlertDialog(
                onDismissRequest = { showAddEditChapterDialog = false },
                title = { Text(if (editingChapter == null) "Add Chapter" else "Edit Chapter") },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 350.dp)) {
                        item {
                            Text("Select Subject:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            subjectsList.forEach { subj ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedSubject = subj }
                                        .background(if (selectedSubject?.id == subj.id) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = selectedSubject?.id == subj.id, onClick = { selectedSubject = subj })
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(subj.name, fontSize = 12.sp)
                                }
                            }
                        }
                        item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Chapter Name") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = pdfUrl, onValueChange = { pdfUrl = it }, label = { Text("PDF Download URL") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = pdfTitle, onValueChange = { pdfTitle = it }, label = { Text("PDF Title") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = order, onValueChange = { order = it }, label = { Text("Order") }, modifier = Modifier.fillMaxWidth()) }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val sub = selectedSubject
                        if (sub == null) {
                            Toast.makeText(context, "Please select a subject first.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val o = order.toIntOrNull() ?: 0
                        val data = mapOf(
                            "subjectId" to sub.id,
                            "subjectName" to sub.name,
                            "chapterName" to name,
                            "description" to desc,
                            "imageUrl" to imageUrl,
                            "pdfUrl" to pdfUrl.ifBlank { null },
                            "pdfTitle" to pdfTitle.ifBlank { null },
                            "order" to o,
                            "createdAt" to (editingChapter?.createdAt ?: java.util.Date().toString())
                        )
                        val task = if (editingChapter == null) {
                            firestore.collection("chapters").add(data)
                        } else {
                            firestore.collection("chapters").document(editingChapter!!.id).set(data)
                        }
                        task.addOnSuccessListener {
                            Toast.makeText(context, "Chapter saved", Toast.LENGTH_SHORT).show()
                            showAddEditChapterDialog = false
                            fetchAllData()
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddEditChapterDialog = false }) { Text("Cancel") }
                }
            )
        }

        // ===================================
        // ADD / EDIT NOTE DIALOG
        // ===================================
        if (showAddEditNoteDialog) {
            var selectedChapter by remember { mutableStateOf(chaptersList.find { it.id == (editingNote?.chapterId ?: "") } ?: chaptersList.firstOrNull()) }
            var title by remember { mutableStateOf(editingNote?.title ?: "") }
            var content by remember { mutableStateOf(editingNote?.content ?: "") }
            var type by remember { mutableStateOf(editingNote?.type ?: "summary") }
            var order by remember { mutableStateOf(editingNote?.order?.toString() ?: "0") }

            AlertDialog(
                onDismissRequest = { showAddEditNoteDialog = false },
                title = { Text(if (editingNote == null) "Add Note" else "Edit Note") },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 350.dp)) {
                        item {
                            Text("Select Chapter:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            chaptersList.forEach { ch ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedChapter = ch }
                                        .background(if (selectedChapter?.id == ch.id) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = selectedChapter?.id == ch.id, onClick = { selectedChapter = ch })
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(ch.chapterName, fontSize = 12.sp)
                                }
                            }
                        }
                        item { OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Note Title") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content Body") }, modifier = Modifier.fillMaxWidth().height(100.dp)) }
                        item { OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (summary/formula)") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = order, onValueChange = { order = it }, label = { Text("Order") }, modifier = Modifier.fillMaxWidth()) }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val ch = selectedChapter
                        if (ch == null) {
                            Toast.makeText(context, "Please select a chapter first.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val o = order.toIntOrNull() ?: 0
                        val data = mapOf(
                            "chapterId" to ch.id,
                            "subjectId" to ch.subjectId,
                            "title" to title,
                            "content" to content,
                            "type" to type,
                            "order" to o,
                            "createdAt" to (editingNote?.createdAt ?: java.util.Date().toString())
                        )
                        val task = if (editingNote == null) {
                            firestore.collection("notes").add(data)
                        } else {
                            firestore.collection("notes").document(editingNote!!.id).set(data)
                        }
                        task.addOnSuccessListener {
                            Toast.makeText(context, "Note saved", Toast.LENGTH_SHORT).show()
                            showAddEditNoteDialog = false
                            fetchAllData()
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddEditNoteDialog = false }) { Text("Cancel") }
                }
            )
        }

        // ===================================
        // ADD / EDIT QUESTION DIALOG
        // ===================================
        if (showAddEditQuestionDialog) {
            var selectedChapter by remember { mutableStateOf(chaptersList.find { it.id == (editingQuestion?.chapterId ?: "") } ?: chaptersList.firstOrNull()) }
            var questionText by remember { mutableStateOf(editingQuestion?.questionText ?: "") }
            var answerText by remember { mutableStateOf(editingQuestion?.answerText ?: "") }
            var category by remember { mutableStateOf(editingQuestion?.category ?: "mcq") }
            var marks by remember { mutableStateOf(editingQuestion?.marks?.toString() ?: "1") }
            var order by remember { mutableStateOf(editingQuestion?.order?.toString() ?: "0") }

            AlertDialog(
                onDismissRequest = { showAddEditQuestionDialog = false },
                title = { Text(if (editingQuestion == null) "Add Question" else "Edit Question") },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 350.dp)) {
                        item {
                            Text("Select Chapter:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            chaptersList.forEach { ch ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedChapter = ch }
                                        .background(if (selectedChapter?.id == ch.id) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = selectedChapter?.id == ch.id, onClick = { selectedChapter = ch })
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(ch.chapterName, fontSize = 12.sp)
                                }
                            }
                        }
                        item { OutlinedTextField(value = questionText, onValueChange = { questionText = it }, label = { Text("Question Text") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = answerText, onValueChange = { answerText = it }, label = { Text("Answer Text") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (mcq/short/long/madhyamik_suggestion)") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = marks, onValueChange = { marks = it }, label = { Text("Marks") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = order, onValueChange = { order = it }, label = { Text("Order") }, modifier = Modifier.fillMaxWidth()) }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val ch = selectedChapter
                        if (ch == null) {
                            Toast.makeText(context, "Please select a chapter first.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val o = order.toIntOrNull() ?: 0
                        val m = marks.toIntOrNull() ?: 1
                        val data = mapOf(
                            "chapterId" to ch.id,
                            "subjectId" to ch.subjectId,
                            "questionText" to questionText,
                            "answerText" to answerText,
                            "category" to category,
                            "marks" to m,
                            "order" to o,
                            "createdAt" to (editingQuestion?.createdAt ?: java.util.Date().toString())
                        )
                        val task = if (editingQuestion == null) {
                            firestore.collection("questions").add(data)
                        } else {
                            firestore.collection("questions").document(editingQuestion!!.id).set(data)
                        }
                        task.addOnSuccessListener {
                            Toast.makeText(context, "Question saved", Toast.LENGTH_SHORT).show()
                            showAddEditQuestionDialog = false
                            fetchAllData()
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddEditQuestionDialog = false }) { Text("Cancel") }
                }
            )
        }

        // ===================================
        // ADD / EDIT BANNERS DIALOG
        // ===================================
        if (showAddEditBannerDialog) {
            var title by remember { mutableStateOf(editingBanner?.title ?: "") }
            var imageUrl by remember { mutableStateOf(editingBanner?.imageUrl ?: "") }
            var targetUrl by remember { mutableStateOf(editingBanner?.targetUrl ?: "") }
            var isVisible by remember { mutableStateOf(editingBanner?.isVisible ?: true) }
            var order by remember { mutableStateOf(editingBanner?.order?.toString() ?: "0") }

            AlertDialog(
                onDismissRequest = { showAddEditBannerDialog = false },
                title = { Text(if (editingBanner == null) "Add Banner" else "Edit Banner") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Banner Title") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = targetUrl, onValueChange = { targetUrl = it }, label = { Text("Target Screen Route") }, modifier = Modifier.fillMaxWidth())
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isVisible, onCheckedChange = { isVisible = it })
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Visible in App", fontSize = 12.sp)
                        }
                        OutlinedTextField(value = order, onValueChange = { order = it }, label = { Text("Order") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val o = order.toIntOrNull() ?: 0
                        val data = mapOf(
                            "title" to title,
                            "imageUrl" to imageUrl,
                            "targetUrl" to targetUrl,
                            "isVisible" to isVisible,
                            "order" to o,
                            "createdAt" to (editingBanner?.createdAt ?: java.util.Date().toString())
                        )
                        val task = if (editingBanner == null) {
                            firestore.collection("banners").add(data)
                        } else {
                            firestore.collection("banners").document(editingBanner!!.id).set(data)
                        }
                        task.addOnSuccessListener {
                            Toast.makeText(context, "Banner saved", Toast.LENGTH_SHORT).show()
                            showAddEditBannerDialog = false
                            fetchAllData()
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddEditBannerDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun BannerAdminRow(banner: Banner, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(banner.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Visible: ${banner.isVisible} | Order: ${banner.order}", color = Color.Gray, fontSize = 11.sp)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun AppSettingsPanel(settings: AppSettings, onSave: (AppSettings) -> Unit) {
    var appName by remember { mutableStateOf(settings.appName) }
    var logoUrl by remember { mutableStateOf(settings.logoUrl) }
    var contactEmail by remember { mutableStateOf(settings.contactEmail) }
    var contactPhone by remember { mutableStateOf(settings.contactPhone) }
    var whatsappNumber by remember { mutableStateOf(settings.whatsappNumber) }
    var noticeBanner by remember { mutableStateOf(settings.noticeBanner) }
    var theme by remember { mutableStateOf(settings.theme) }
    var aboutText by remember { mutableStateOf(settings.aboutText) }
    var footerText by remember { mutableStateOf(settings.footerText) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("General App Settings", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(value = appName, onValueChange = { appName = it }, label = { Text("App Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = logoUrl, onValueChange = { logoUrl = it }, label = { Text("Logo URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = noticeBanner, onValueChange = { noticeBanner = it }, label = { Text("Notice Banner Bar") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = contactEmail, onValueChange = { contactEmail = it }, label = { Text("Contact Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = contactPhone, onValueChange = { contactPhone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = whatsappNumber, onValueChange = { whatsappNumber = it }, label = { Text("WhatsApp Number") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = aboutText, onValueChange = { aboutText = it }, label = { Text("About Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            OutlinedTextField(value = footerText, onValueChange = { footerText = it }, label = { Text("Footer Text") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    onSave(
                        AppSettings(
                            appName = appName,
                            logoUrl = logoUrl,
                            contactEmail = contactEmail,
                            contactPhone = contactPhone,
                            whatsappNumber = whatsappNumber,
                            noticeBanner = noticeBanner,
                            theme = theme,
                            aboutText = aboutText,
                            footerText = footerText
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Settings Changes", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SubjectAdminRow(subject: Subject, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(subject.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Code: ${subject.code} | Order: ${subject.order}", color = Color.Gray, fontSize = 11.sp)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun ChapterAdminRow(chapter: Chapter, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(chapter.chapterName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Subject: ${chapter.subjectName ?: "None"} | Order: ${chapter.order}", color = Color.Gray, fontSize = 11.sp)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun NoteAdminRow(note: Note, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(note.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Type: ${note.type ?: "summary"} | Order: ${note.order}", color = Color.Gray, fontSize = 11.sp)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun QuestionAdminRow(question: Question, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(question.questionText, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Cat: ${question.category} | Marks: ${question.marks ?: 1}", color = Color.Gray, fontSize = 11.sp)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}

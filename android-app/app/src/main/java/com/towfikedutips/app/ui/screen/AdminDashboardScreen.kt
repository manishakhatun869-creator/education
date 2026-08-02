package com.towfikedutips.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.towfikedutips.app.model.Chapter
import com.towfikedutips.app.model.Note
import com.towfikedutips.app.model.Question
import com.towfikedutips.app.model.Subject

enum class AdminTab(val title: String) {
    SUBJECTS("Subjects"),
    CHAPTERS("Chapters"),
    NOTES("Notes"),
    QUESTIONS("Questions")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(AdminTab.SUBJECTS) }

    val firestore = remember { FirebaseFirestore.getInstance() }

    // Live state lists
    val subjectsList = remember { mutableStateListOf<Subject>() }
    val chaptersList = remember { mutableStateListOf<Chapter>() }
    val notesList = remember { mutableStateListOf<Note>() }
    val questionsList = remember { mutableStateListOf<Question>() }

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
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Item")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Horizontal Tab Row
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                AdminTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
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

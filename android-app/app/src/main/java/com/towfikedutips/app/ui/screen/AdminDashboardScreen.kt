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
    CONTENT("Content Wizard"),
    BANNERS("Banners"),
    SETTINGS("Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(AdminTab.CONTENT) }

    val firestore = remember { com.towfikedutips.app.data.FirestoreProvider.getFirestore(context) }

    // Navigation state in content wizard
    var activeSubject by remember { mutableStateOf<Subject?>(null) }
    var activeChapter by remember { mutableStateOf<Chapter?>(null) }

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
                title = { Text("Admin Panel Dashboard", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { fetchAllData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            // Context-based FAB creation logic for nested content management
            if (selectedTab == AdminTab.CONTENT) {
                if (activeSubject == null) {
                    FloatingActionButton(
                        onClick = {
                            editingSubject = null
                            showAddEditSubjectDialog = true
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Subject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (activeChapter == null) {
                    FloatingActionButton(
                        onClick = {
                            editingChapter = null
                            showAddEditChapterDialog = true
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Chapter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (selectedTab == AdminTab.BANNERS) {
                FloatingActionButton(
                    onClick = {
                        editingBanner = null
                        showAddEditBannerDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Banner")
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
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
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
                        AdminTab.CONTENT -> {
                            if (activeSubject == null) {
                                // LEVEL 1: Subjects List
                                item {
                                    Text("Explore Subjects", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                if (subjectsList.isEmpty()) {
                                    item { Text("No subjects found. Create one to begin.", color = Color.Gray, fontSize = 12.sp) }
                                }
                                items(subjectsList) { subject ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { activeSubject = subject },
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(subject.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text("Code: ${subject.code} | Order: ${subject.order}", color = Color.Gray, fontSize = 11.sp)
                                                }
                                            }
                                            Row {
                                                IconButton(onClick = {
                                                    editingSubject = subject
                                                    showAddEditSubjectDialog = true
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                                }
                                                IconButton(onClick = {
                                                    firestore.collection("subjects").document(subject.id).delete()
                                                        .addOnSuccessListener {
                                                            Toast.makeText(context, "Subject deleted", Toast.LENGTH_SHORT).show()
                                                            fetchAllData()
                                                        }
                                                }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (activeChapter == null) {
                                // LEVEL 2: Chapters List of Active Subject
                                val subj = activeSubject!!
                                val subjChapters = chaptersList.filter { it.subjectId == subj.id }

                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { activeSubject = null },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.3f), contentColor = Color.DarkGray),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("All Subjects", fontSize = 11.sp)
                                            }
                                        }
                                        Text(subj.name, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                if (subjChapters.isEmpty()) {
                                    item { Text("No chapters found. Add a chapter using the + button.", color = Color.Gray, fontSize = 12.sp) }
                                }
                                items(subjChapters) { chapter ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { activeChapter = chapter },
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Icon(Icons.Default.Book, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(chapter.chapterName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text("Order: ${chapter.order}", color = Color.Gray, fontSize = 11.sp)
                                                }
                                            }
                                            Row {
                                                IconButton(onClick = {
                                                    editingChapter = chapter
                                                    showAddEditChapterDialog = true
                                                }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                                }
                                                IconButton(onClick = {
                                                    firestore.collection("chapters").document(chapter.id).delete()
                                                        .addOnSuccessListener {
                                                            Toast.makeText(context, "Chapter deleted", Toast.LENGTH_SHORT).show()
                                                            fetchAllData()
                                                        }
                                                }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // LEVEL 3: Notes & Questions List of Active Chapter
                                val ch = activeChapter!!
                                val chNotes = notesList.filter { it.chapterId == ch.id }
                                val chQuestions = questionsList.filter { it.chapterId == ch.id }

                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { activeChapter = null },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.3f), contentColor = Color.DarkGray),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("All Chapters", fontSize = 11.sp)
                                            }
                                        }
                                        Text(ch.chapterName, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f).padding(start = 12.dp))
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                }

                                // 3A. Notes Section Header
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("📚 Notes & Summaries (${chNotes.size})", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                        Button(
                                            onClick = {
                                                editingNote = null
                                                showAddEditNoteDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("+ Add Note", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (chNotes.isEmpty()) {
                                    item { Text("No notes in this chapter yet.", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 4.dp)) }
                                }

                                items(chNotes) { note ->
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

                                // 3B. Questions Section Header
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("❓ Questions & Answers (${chQuestions.size})", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                        Button(
                                            onClick = {
                                                editingQuestion = null
                                                showAddEditQuestionDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("+ Add Q&A", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (chQuestions.isEmpty()) {
                                    item { Text("No questions in this chapter yet.", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 4.dp)) }
                                }

                                items(chQuestions) { question ->
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
                                            "footerText" to updatedSettings.footerText,
                                            "backendUrl" to updatedSettings.backendUrl
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
            var color by remember { mutableStateOf(editingSubject?.color ?: "#6B4EFF") }
            var description by remember { mutableStateOf(editingSubject?.description ?: "") }
            var order by remember { mutableStateOf(editingSubject?.order?.toString() ?: "0") }

            AlertDialog(
                onDismissRequest = { showAddEditSubjectDialog = false },
                title = { Text(if (editingSubject == null) "Add Subject" else "Edit Subject") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Subject Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Code (e.g. BEN, ENG)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color HEX (e.g. #6B4EFF)") }, modifier = Modifier.fillMaxWidth())
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
            val sub = activeSubject!!
            var name by remember { mutableStateOf(editingChapter?.chapterName ?: "") }
            var desc by remember { mutableStateOf(editingChapter?.description ?: "") }
            var imageUrl by remember { mutableStateOf(editingChapter?.imageUrl ?: "") }
            var pdfUrl by remember { mutableStateOf(editingChapter?.pdfUrl ?: "") }
            var pdfTitle by remember { mutableStateOf(editingChapter?.pdfTitle ?: "") }
            var order by remember { mutableStateOf(editingChapter?.order?.toString() ?: "0") }

            AlertDialog(
                onDismissRequest = { showAddEditChapterDialog = false },
                title = { Text(if (editingChapter == null) "Add Chapter to ${sub.name}" else "Edit Chapter") },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 350.dp)) {
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
            val ch = activeChapter!!
            var title by remember { mutableStateOf(editingNote?.title ?: "") }
            var content by remember { mutableStateOf(editingNote?.content ?: "") }
            var type by remember { mutableStateOf(editingNote?.type ?: "summary") }

            AlertDialog(
                onDismissRequest = { showAddEditNoteDialog = false },
                title = { Text(if (editingNote == null) "Add Note to ${ch.chapterName}" else "Edit Note") },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 350.dp)) {
                        item { OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Note Title") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content Body") }, modifier = Modifier.fillMaxWidth().height(120.dp)) }
                        item {
                            Text("Note Type:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            val noteTypes = listOf("summary", "formula", "important", "revision")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                noteTypes.forEach { t ->
                                    val isSelected = type == t
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { type = t }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = t.uppercase(),
                                            color = if (isSelected) Color.White else Color.DarkGray,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        // Sequential ordering logic: calculate next order number
                        val nextOrder = if (editingNote == null) {
                            val notesOfCh = notesList.filter { it.chapterId == ch.id }
                            (notesOfCh.maxOfOrNull { it.order } ?: 0) + 1
                        } else {
                            editingNote!!.order
                        }

                        val data = mapOf(
                            "chapterId" to ch.id,
                            "subjectId" to ch.subjectId,
                            "title" to title,
                            "content" to content,
                            "type" to type,
                            "order" to nextOrder,
                            "createdAt" to (editingNote?.createdAt ?: java.util.Date().toString())
                        )
                        val task = if (editingNote == null) {
                            firestore.collection("notes").add(data)
                        } else {
                            firestore.collection("notes").document(editingNote!!.id).set(data)
                        }
                        task.addOnSuccessListener {
                            Toast.makeText(context, "Note saved sequentially", Toast.LENGTH_SHORT).show()
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
            val ch = activeChapter!!
            var questionText by remember { mutableStateOf(editingQuestion?.questionText ?: "") }
            var answerText by remember { mutableStateOf(editingQuestion?.answerText ?: "") }
            var category by remember { mutableStateOf(editingQuestion?.category ?: "mcq") }
            var marks by remember { mutableStateOf(editingQuestion?.marks?.toString() ?: "1") }

            // MCQ Specific States
            var optA by remember { mutableStateOf(editingQuestion?.options?.getOrNull(0) ?: "") }
            var optB by remember { mutableStateOf(editingQuestion?.options?.getOrNull(1) ?: "") }
            var optC by remember { mutableStateOf(editingQuestion?.options?.getOrNull(2) ?: "") }
            var optD by remember { mutableStateOf(editingQuestion?.options?.getOrNull(3) ?: "") }
            var selectedCorrectIndex by remember { mutableStateOf(editingQuestion?.correctOptionIndex ?: 0) }

            AlertDialog(
                onDismissRequest = { showAddEditQuestionDialog = false },
                title = { Text(if (editingQuestion == null) "Add Question to ${ch.chapterName}" else "Edit Question") },
                text = {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 350.dp)) {
                        item { OutlinedTextField(value = questionText, onValueChange = { questionText = it }, label = { Text("Question Text") }, modifier = Modifier.fillMaxWidth()) }
                        item { OutlinedTextField(value = answerText, onValueChange = { answerText = it }, label = { Text("Answer Text") }, modifier = Modifier.fillMaxWidth()) }
                        item {
                            Text("Question Category:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            val categories = listOf("mcq", "short", "long", "madhyamik_suggestion", "pyq", "important")
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(categories) { cat ->
                                    val isSelected = category == cat
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { category = cat }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = cat.uppercase().replace("_", " "),
                                            color = if (isSelected) Color.White else Color.DarkGray,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // If the category is MCQ, we securely render 4 options inputs
                        if (category == "mcq") {
                            item {
                                Text("MCQ Options Details:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
                            }
                            item { OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("Option A") }, modifier = Modifier.fillMaxWidth()) }
                            item { OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth()) }
                            item { OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth()) }
                            item { OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth()) }
                            item {
                                Text("Correct Answer Index (A:0, B:1, C:2, D:3):", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    listOf("A", "B", "C", "D").forEachIndexed { index, label ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.clickable { selectedCorrectIndex = index }
                                        ) {
                                            RadioButton(selected = selectedCorrectIndex == index, onClick = { selectedCorrectIndex = index })
                                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        item { OutlinedTextField(value = marks, onValueChange = { marks = it }, label = { Text("Marks") }, modifier = Modifier.fillMaxWidth()) }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        // Sequential ordering logic: calculate next order number for Question
                        val nextOrder = if (editingQuestion == null) {
                            val questionsOfCh = questionsList.filter { it.chapterId == ch.id }
                            (questionsOfCh.maxOfOrNull { it.order } ?: 0) + 1
                        } else {
                            editingQuestion!!.order
                        }

                        val optionsList = if (category == "mcq") listOf(optA, optB, optC, optD) else null
                        val correctIdx = if (category == "mcq") selectedCorrectIndex else null

                        val m = marks.toIntOrNull() ?: 1
                        val data = mapOf(
                            "chapterId" to ch.id,
                            "subjectId" to ch.subjectId,
                            "questionText" to questionText,
                            "answerText" to answerText,
                            "category" to category,
                            "options" to optionsList,
                            "correctOptionIndex" to correctIdx,
                            "marks" to m,
                            "order" to nextOrder,
                            "createdAt" to (editingQuestion?.createdAt ?: java.util.Date().toString())
                        )
                        val task = if (editingQuestion == null) {
                            firestore.collection("questions").add(data)
                        } else {
                            firestore.collection("questions").document(editingQuestion!!.id).set(data)
                        }
                        task.addOnSuccessListener {
                            Toast.makeText(context, "Question saved sequentially", Toast.LENGTH_SHORT).show()
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
    var backendUrl by remember { mutableStateOf(settings.backendUrl) }

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
            OutlinedTextField(value = backendUrl, onValueChange = { backendUrl = it }, label = { Text("Backend API Base URL") }, modifier = Modifier.fillMaxWidth())
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
                            footerText = footerText,
                            backendUrl = backendUrl
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
                Text(note.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Type: ${note.type ?: "summary"} | Order: ${note.order}", color = Color.Gray, fontSize = 10.sp)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
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
                Text(question.questionText, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Cat: ${question.category} | Marks: ${question.marks ?: 1} | Order: ${question.order}", color = Color.Gray, fontSize = 10.sp)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

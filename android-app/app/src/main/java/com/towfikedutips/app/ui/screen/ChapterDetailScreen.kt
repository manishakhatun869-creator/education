package com.towfikedutips.app.ui.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.towfikedutips.app.data.LocalStorageManager
import com.towfikedutips.app.model.Chapter
import com.towfikedutips.app.model.Note
import com.towfikedutips.app.model.Question
import com.towfikedutips.app.model.SavedItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterDetailScreen(
    navController: NavController,
    localStorageManager: LocalStorageManager,
    chapter: Chapter,
    notes: List<Note>,
    questions: List<Question>
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf("notes") }
    var isChapterSaved by remember { mutableStateOf(localStorageManager.isSaved(chapter.id)) }

    // MCQ Practice state: mapping questionId -> selectedOptionIndex
    var userMcqAnswers by remember { mutableStateOf(mapOf<String, Int>()) }
    var mcqSubmitted by remember { mutableStateOf(false) }

    val mcqQuestions = remember(questions) { questions.filter { it.category == "mcq" } }
    val generalQa = remember(questions) { questions.filter { it.category != "mcq" } }

    val tabs = listOf(
        "notes" to "Notes (${notes.size})",
        "mcq" to "MCQ Practice (${mcqQuestions.size})",
        "qa" to "Q & A (${generalQa.size})"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = chapter.chapterName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Study ${chapter.chapterName} with Towfik Edutips! Notes, MCQs and exam suggestions."
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share with"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                    }

                    IconButton(onClick = {
                        if (isChapterSaved) {
                            localStorageManager.removeItem(chapter.id)
                            isChapterSaved = false
                            Toast.makeText(context, "Removed from Saved", Toast.LENGTH_SHORT).show()
                        } else {
                            val savedItem = SavedItem(
                                id = "local_${System.currentTimeMillis()}",
                                itemId = chapter.id,
                                itemType = "chapter",
                                title = chapter.chapterName,
                                subtitle = chapter.subjectName ?: "Chapter Study Material",
                                chapterId = chapter.id,
                                subjectId = chapter.subjectId,
                                savedAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
                            )
                            localStorageManager.saveItem(savedItem)
                            isChapterSaved = true
                            Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = if (isChapterSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save",
                            tint = if (isChapterSaved) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Chapter Title & Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = chapter.subjectName ?: "WBBSE",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = chapter.chapterName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = chapter.description.ifBlank { "Full syllabus notes & solved model suggestions." },
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        // Simulation of View/Download complete Chapter PDF
                        Button(
                            onClick = {
                                val downloadUrl = chapter.pdfUrl ?: "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
                                val downloadTitle = chapter.pdfTitle ?: "${chapter.chapterName} - Full Notes & Q&A"
                                localStorageManager.saveDownloadedPdf(
                                    com.towfikedutips.app.model.DownloadedPdf(
                                        id = chapter.id,
                                        title = downloadTitle,
                                        pdfUrl = downloadUrl,
                                        downloadedAt = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
                                    )
                                )
                                Toast.makeText(
                                    context,
                                    "Successfully downloaded & saved in App offline downloads list!",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Get Full Chapter PDF (Offline)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Screen Tab list layout selection
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tabs) { (id, label) ->
                        val isSelected = activeTab == id
                        FilterChip(
                            selected = isSelected,
                            onClick = { activeTab = id },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Tab contents rendering
            when (activeTab) {
                "notes" -> {
                    if (notes.isEmpty()) {
                        item {
                            Text(
                                text = "No extra study notes available for this chapter yet.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        items(notes) { note ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = note.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        note.type?.let {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = it.uppercase(),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = note.content,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }
                    }
                }

                "mcq" -> {
                    if (mcqQuestions.isEmpty()) {
                        item {
                            Text(
                                text = "No MCQ practice questions added for this chapter yet.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Interactive Practice Quiz",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (mcqSubmitted) {
                                        val correctCount = mcqQuestions.count { q ->
                                            userMcqAnswers[q.id] == q.correctOptionIndex
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(MaterialTheme.colorScheme.primary)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "Score: $correctCount / ${mcqQuestions.size}",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        itemsIndexed(mcqQuestions) { idx, q ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${idx + 1}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = q.questionText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    val options = q.options ?: listOf("Option A", "Option B", "Option C", "Option D")
                                    options.forEachIndexed { optIdx, opt ->
                                        val isSelected = userMcqAnswers[q.id] == optIdx
                                        val isCorrect = q.correctOptionIndex == optIdx

                                        val (containerColor, borderTint) = when {
                                            mcqSubmitted && isCorrect -> Color(0xFFD1FAE5) to Color(0xFF10B981)
                                            mcqSubmitted && isSelected && !isCorrect -> Color(0xFFFEE2E2) to Color(0xFFEF4444)
                                            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) to MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.background to Color.LightGray.copy(alpha = 0.5f)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(containerColor)
                                                .clickable(enabled = !mcqSubmitted) {
                                                    userMcqAnswers = userMcqAnswers.toMutableMap().apply {
                                                        put(q.id, optIdx)
                                                    }
                                                }
                                                .padding(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = opt, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                if (mcqSubmitted && isCorrect) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                                } else if (mcqSubmitted && isSelected && !isCorrect) {
                                                    Icon(Icons.Default.Close, contentDescription = "Incorrect", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }

                                    if (mcqSubmitted && q.answerText.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFFEF3C7))
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = "Explanation: ${q.answerText}",
                                                fontSize = 10.sp,
                                                color = Color(0xFF78350F),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = { mcqSubmitted = !mcqSubmitted },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (mcqSubmitted) "Reset Practice Quiz" else "Submit and Check Results",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                "qa" -> {
                    if (generalQa.isEmpty()) {
                        item {
                            Text(
                                text = "No Question & Answers added for this chapter yet.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        items(generalQa) { q ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFFEF3C7))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = q.category.uppercase(),
                                                color = Color(0xFFB45309),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        q.marks?.let {
                                            Text(
                                                text = "$it Marks",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = q.questionText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Answer:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = q.answerText,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

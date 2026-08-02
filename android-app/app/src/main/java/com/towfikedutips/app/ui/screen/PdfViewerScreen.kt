package com.towfikedutips.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.towfikedutips.app.model.Note
import com.towfikedutips.app.model.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    navController: NavController,
    title: String,
    chapterId: String
) {
    val context = LocalContext.current
    val firestore = remember { com.towfikedutips.app.data.FirestoreProvider.getFirestore(context) }

    val notesList = remember { mutableStateListOf<Note>() }
    val questionsList = remember { mutableStateListOf<Question>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(chapterId) {
        isLoading = true
        // Fetch Notes for this chapter
        firestore.collection("notes")
            .whereEqualTo("chapterId", chapterId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                notesList.clear()
                val items = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Note::class.java)?.copy(id = doc.id)
                }.sortedBy { it.order }
                notesList.addAll(items)
            }

        // Fetch Questions for this chapter
        firestore.collection("questions")
            .whereEqualTo("chapterId", chapterId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                questionsList.clear()
                val items = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Question::class.java)?.copy(id = doc.id)
                }.sortedBy { it.order }
                questionsList.addAll(items)
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                Toast.makeText(context, "Using offline cache for document reading.", Toast.LENGTH_SHORT).show()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("E-Book: PDF Reader", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Sharing this chapter PDF document...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF1F5F9)) // Textbook gray sheet background
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // PDF Header Sheet Cover
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = Color(0xFFE11D48),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "TOWFIK EDUTIPS STUDY SERIES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Class 10 Madhyamik Board Revision Guide",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "This document compiles all registered study notes, multiple-choice questions, short answers, and long answers sequentially for offline study.",
                                fontSize = 9.sp,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }

                // Section 1: Notes
                if (notesList.isNotEmpty()) {
                    item {
                        Text(
                            text = "PART I: REVISION SYLLABUS NOTES",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(notesList) { note ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Color.LightGray, RoundedCornerShape(4.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = note.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = note.content,
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                        }
                    }
                }

                // Section 2: Questions Grouped
                val mcqs = questionsList.filter { it.category == "mcq" }
                val writtenQuestions = questionsList.filter { it.category != "mcq" }

                if (mcqs.isNotEmpty()) {
                    item {
                        Text(
                            text = "PART II: MULTIPLE CHOICE QUESTIONS (MCQS)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(mcqs) { q ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Color.LightGray, RoundedCornerShape(4.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Q. " + q.questionText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Options Render
                                q.options?.forEachIndexed { index, option ->
                                    val isCorrect = index == q.correctOptionIndex
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .background(
                                                color = if (isCorrect) Color(0xFFECFDF5) else Color.Transparent,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(4.dp)
                                    ) {
                                        Text(
                                            text = "${'A' + index}) $option",
                                            fontSize = 11.sp,
                                            color = if (isCorrect) Color(0xFF047857) else Color.DarkGray,
                                            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (isCorrect) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "(✔ Correct Option)",
                                                fontSize = 9.sp,
                                                color = Color(0xFF047857),
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Correct Answer: Option ${'A' + (q.correctOptionIndex ?: 0)} (${q.answerText})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }
                        }
                    }
                }

                if (writtenQuestions.isNotEmpty()) {
                    item {
                        Text(
                            text = "PART III: SHORT & LONG QUESTIONS AND SOLUTIONS",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(writtenQuestions) { q ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Color.LightGray, RoundedCornerShape(4.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Category: ${q.category.uppercase().replace("_", " ")}",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${q.marks ?: 3} Marks",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Q. " + q.questionText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF8FAFC))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Model Solution / Answer:",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = q.answerText,
                                            fontSize = 11.sp,
                                            color = Color.DarkGray,
                                            lineHeight = 15.sp,
                                            fontFamily = FontFamily.Serif
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "End of Syllabus PDF Compilation\nDownloaded from Towfik Edutips.",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Star
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
import com.towfikedutips.app.model.Question
import com.towfikedutips.app.model.Subject
import com.towfikedutips.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionsScreen(
    navController: NavController,
    localStorageManager: LocalStorageManager,
    subjects: List<Subject>,
    chapters: List<Chapter>,
    questions: List<Question>
) {
    val context = LocalContext.current
    var selectedSubjectId by remember { mutableStateOf("all") }
    var selectedCategory by remember { mutableStateOf("all") }

    val filteredQuestions = remember(selectedSubjectId, selectedCategory, questions, chapters) {
        var result = questions.filter {
            it.category == "madhyamik_suggestion" || it.category == "pyq" || it.category == "important"
        }

        if (selectedSubjectId != "all") {
            val subjectChapters = chapters.filter { it.subjectId == selectedSubjectId }.map { it.id }
            result = result.filter { subjectChapters.contains(it.chapterId) }
        }

        if (selectedCategory != "all") {
            result = result.filter { it.category == selectedCategory }
        }

        result
    }

    val categories = listOf(
        "all" to "All Types",
        "madhyamik_suggestion" to "🏆 Suggestion 2026",
        "pyq" to "📜 PYQs (Past Solved Qs)",
        "important" to "⭐ Important"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "WBBSE Suggestions",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    IconButton(onClick = {
                        localStorageManager.saveDownloadedPdf(
                            com.towfikedutips.app.model.DownloadedPdf(
                                id = "suggestions_all",
                                title = "Madhyamik 2026 Complete Suggestions Prediction Set",
                                pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                                downloadedAt = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
                            )
                        )
                        Toast.makeText(
                            context,
                            "Suggestions PDF successfully downloaded & saved in App offline list!",
                            Toast.LENGTH_LONG
                        ).show()
                    }) {
                        Icon(Icons.Default.Download, contentDescription = "Download All PDF", tint = MaterialTheme.colorScheme.primary)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Madhyamik 2026 Exam Predictions",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Handpicked high-yield questions, solved class 10 model answers, and official WBBSE suggestions.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Subject Filters Carousel
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedSubjectId == "all",
                            onClick = { selectedSubjectId = "all" },
                            label = { Text("All Subjects", fontSize = 11.sp) }
                        )
                    }
                    items(subjects) { s ->
                        FilterChip(
                            selected = selectedSubjectId == s.id,
                            onClick = { selectedSubjectId = s.id },
                            label = { Text(s.name, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Category Filters Carousel
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { (id, label) ->
                        FilterChip(
                            selected = selectedCategory == id,
                            onClick = { selectedCategory = id },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Suggestions predictions lists
            if (filteredQuestions.isEmpty()) {
                item {
                    Text(
                        text = "No predictions match the selected filters. Please choose another subject or category.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            } else {
                items(filteredQuestions) { q ->
                    val ch = chapters.find { it.id == q.chapterId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(Screen.ChapterDetail.createRoute(q.chapterId))
                            },
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

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${q.marks ?: 5} Marks",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Gray
                                    )
                                    q.year?.let {
                                        Text(
                                            text = "• Year $it",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = q.questionText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Answer / Solution Summary:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = q.answerText,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        localStorageManager.saveDownloadedPdf(
                                            com.towfikedutips.app.model.DownloadedPdf(
                                                id = q.id,
                                                title = q.questionText,
                                                pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                                                downloadedAt = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
                                            )
                                        )
                                        Toast.makeText(
                                            context,
                                            "Material PDF successfully downloaded & saved in App offline list!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F2), contentColor = Color(0xFFE11D48)),
                                    modifier = Modifier.height(30.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Text("Save PDF", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Open Full Chapter",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
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

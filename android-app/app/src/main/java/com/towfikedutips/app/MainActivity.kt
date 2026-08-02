package com.towfikedutips.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.FirebaseFirestore
import com.towfikedutips.app.data.LocalStorageManager
import com.towfikedutips.app.data.SeedData
import com.towfikedutips.app.model.Chapter
import com.towfikedutips.app.model.Note
import com.towfikedutips.app.model.Question
import com.towfikedutips.app.model.Subject
import com.towfikedutips.app.ui.navigation.Screen
import com.towfikedutips.app.ui.screen.AiChatScreen
import com.towfikedutips.app.ui.screen.ChapterDetailScreen
import com.towfikedutips.app.ui.screen.HomeScreen
import com.towfikedutips.app.ui.screen.SavedScreen
import com.towfikedutips.app.ui.screen.SubjectsScreen
import com.towfikedutips.app.ui.screen.SuggestionsScreen
import com.towfikedutips.app.ui.screen.DownloadsScreen
import com.towfikedutips.app.ui.screen.PdfViewerScreen
import com.towfikedutips.app.ui.screen.AdminLoginScreen
import com.towfikedutips.app.ui.screen.AdminDashboardScreen
import com.towfikedutips.app.ui.theme.TowfikEdutipsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request push notification permission for Android 13+ (API 33+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        val localStorageManager = LocalStorageManager(applicationContext)

        setContent {
            TowfikEdutipsTheme {
                MainAppLayout(localStorageManager)
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem(Screen.Home.route, "Home", Icons.Default.Home)
    object Subjects : BottomNavItem(Screen.Subjects.route, "Subjects", Icons.Default.Book)
    object Suggestions : BottomNavItem(Screen.Suggestions.route, "Suggestions", Icons.Default.Star)
    object Downloads : BottomNavItem(Screen.Downloads.route, "Downloads", Icons.Default.Download)
    object Saved : BottomNavItem(Screen.Saved.route, "Saved", Icons.Default.Favorite)
}

@Composable
fun MainAppLayout(localStorageManager: LocalStorageManager) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val subjectsList = remember { mutableStateListOf<Subject>() }
    val chaptersList = remember { mutableStateListOf<Chapter>() }
    val questionsList = remember { mutableStateListOf<Question>() }
    val notesList = remember { mutableStateListOf<Note>() }

    var isLoading by remember { mutableStateOf(true) }

    // Initialize with offline seed data as immediate offline fallback
    LaunchedEffect(Unit) {
        subjectsList.addAll(SeedData.subjects)
        chaptersList.addAll(SeedData.chapters)
        questionsList.addAll(SeedData.questions)
        notesList.addAll(SeedData.notes)
        isLoading = false

        // Fetch asynchronously from Firebase Firestore
        try {
            val firestore = com.towfikedutips.app.data.FirestoreProvider.getFirestore(context)

            // Fetch Subjects
            firestore.collection("subjects").get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val remoteSubjects = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Subject::class.java)?.copy(id = doc.id)
                    }.sortedBy { it.order }

                    if (remoteSubjects.isNotEmpty()) {
                        subjectsList.clear()
                        subjectsList.addAll(remoteSubjects)
                    }
                }
            }

            // Fetch Chapters
            firestore.collection("chapters").get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val remoteChapters = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Chapter::class.java)?.copy(id = doc.id)
                    }.sortedBy { it.order }

                    if (remoteChapters.isNotEmpty()) {
                        chaptersList.clear()
                        chaptersList.addAll(remoteChapters)
                    }
                }
            }

            // Fetch Questions
            firestore.collection("questions").get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val remoteQuestions = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Question::class.java)?.copy(id = doc.id)
                    }.sortedBy { it.order }

                    if (remoteQuestions.isNotEmpty()) {
                        questionsList.clear()
                        questionsList.addAll(remoteQuestions)
                    }
                }
            }

            // Fetch Notes
            firestore.collection("notes").get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val remoteNotes = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Note::class.java)?.copy(id = doc.id)
                    }.sortedBy { it.order }

                    if (remoteNotes.isNotEmpty()) {
                        notesList.clear()
                        notesList.addAll(remoteNotes)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val bottomNavItems = listOf(
                BottomNavItem.Home,
                BottomNavItem.Subjects,
                BottomNavItem.Suggestions,
                BottomNavItem.Downloads,
                BottomNavItem.Saved
            )

            // Show BottomNavigation bar on tab screens
            val showBottomNav = bottomNavItems.any { it.route == currentRoute }

            if (showBottomNav) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                indicatorColor = Color.White
                            ),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    navController = navController,
                    localStorageManager = localStorageManager,
                    subjects = subjectsList,
                    chapters = chaptersList,
                    questions = questionsList
                )
            }

            composable(Screen.Subjects.route) {
                SubjectsScreen(
                    navController = navController,
                    subjects = subjectsList,
                    chapters = chaptersList
                )
            }

            composable(Screen.Suggestions.route) {
                SuggestionsScreen(
                    navController = navController,
                    localStorageManager = localStorageManager,
                    subjects = subjectsList,
                    chapters = chaptersList,
                    questions = questionsList
                )
            }

            composable(Screen.Saved.route) {
                SavedScreen(
                    navController = navController,
                    localStorageManager = localStorageManager
                )
            }

            composable(
                route = Screen.PdfViewer.route,
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("chapterId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val title = backStackEntry.arguments?.getString("title") ?: "Study Material"
                val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                PdfViewerScreen(
                    navController = navController,
                    title = title,
                    chapterId = chapterId
                )
            }

            composable(Screen.Downloads.route) {
                DownloadsScreen(
                    navController = navController,
                    localStorageManager = localStorageManager
                )
            }

            composable(
                route = Screen.ChapterDetail.route,
                arguments = listOf(navArgument("chapterId") { type = NavType.StringType })
            ) { backStackEntry ->
                val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                val chapter = chaptersList.find { it.id == chapterId }
                if (chapter != null) {
                    val currentNotes = notesList.filter { it.chapterId == chapterId }
                    val currentQuestions = questionsList.filter { it.chapterId == chapterId }

                    ChapterDetailScreen(
                        navController = navController,
                        localStorageManager = localStorageManager,
                        chapter = chapter,
                        notes = currentNotes,
                        questions = currentQuestions
                    )
                }
            }

            composable(Screen.Chat.route) {
                AiChatScreen(navController = navController)
            }

            composable(Screen.AdminLogin.route) {
                AdminLoginScreen(navController = navController)
            }

            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(navController = navController)
            }
        }
    }
}

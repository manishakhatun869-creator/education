package com.towfikedutips.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Subjects : Screen("subjects")
    object Suggestions : Screen("suggestions")
    object Saved : Screen("saved")
    object ChapterDetail : Screen("chapter_detail/{chapterId}") {
        fun createRoute(chapterId: String) = "chapter_detail/$chapterId"
    }
    object Chat : Screen("chat")
    object Admin : Screen("admin")
    object AdminDashboard : Screen("admin_dashboard")
}

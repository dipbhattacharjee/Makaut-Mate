package com.dev.makautmate.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Notes : Screen("notes")
    object Papers : Screen("papers")
    object Results : Screen("results")
    object Calculator : Screen("calculator")
    object Attendance : Screen("attendance")
    object Books : Screen("books")
    object Videos : Screen("videos")
    object Upload : Screen("upload")
    object UploadNote : Screen("upload_note")
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Syllabus : Screen("syllabus")
    object RateUs : Screen("rate_us")
    object Share : Screen("share")
    object Privacy : Screen("privacy")
    object Terms : Screen("terms")
    object Contact : Screen("contact")
    object Feedback : Screen("feedback")
    object Downloads : Screen("downloads")
    object AskAI : Screen("ask_ai")
    object Premium : Screen("premium")
    object Organiser : Screen("organiser")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Profile
)

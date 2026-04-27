package com.dev.makautmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dev.makautmate.ui.screens.BooksScreen
import com.dev.makautmate.ui.screens.HomeScreen
import com.dev.makautmate.ui.screens.LoginScreen
import com.dev.makautmate.ui.screens.NotesScreen
import com.dev.makautmate.ui.screens.PapersScreen
import com.dev.makautmate.ui.screens.SettingsScreen
import com.dev.makautmate.ui.screens.SignupScreen
import com.dev.makautmate.ui.screens.SplashScreen
import com.dev.makautmate.ui.screens.SyllabusScreen
import com.dev.makautmate.ui.screens.UploadNoteScreen
import com.dev.makautmate.ui.screens.UploadScreen
import com.dev.makautmate.ui.screens.VideosScreen
import com.dev.makautmate.ui.screens.DownloadsScreen
import com.dev.makautmate.ui.screens.LegalScreen
import com.dev.makautmate.ui.screens.AskAIScreen
import com.dev.makautmate.ui.screens.PremiumScreen
import com.dev.makautmate.ui.screens.OrganiserScreen
import com.dev.makautmate.ui.screens.CalculatorScreen

import com.dev.makautmate.ui.viewmodel.AuthViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNext = { isLoggedIn ->
                val destination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
                navController.navigate(destination) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Screen.Signup.route)
                }
            )
        }
        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            val userProfile by authViewModel.currentUserProfile.collectAsState()
            HomeScreen(
                onNavigateToNotes = { navController.navigate(Screen.Notes.route) },
                onNavigateToPapers = { navController.navigate(Screen.Papers.route) },
                onNavigateToBooks = { navController.navigate(Screen.Books.route) },
                onNavigateToVideos = { navController.navigate(Screen.Videos.route) },
                onNavigateToUpload = { navController.navigate(Screen.Upload.route) },
                onNavigateToSyllabus = { navController.navigate(Screen.Syllabus.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToDownloads = { navController.navigate(Screen.Downloads.route) },
                onNavigateToAskAI = { navController.navigate(Screen.AskAI.route) },
                onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                onNavigateToCalculator = { navController.navigate(Screen.Calculator.route) },
                onNavigateToOrganiser = { navController.navigate(Screen.Organiser.route) }
            )
        }
        composable(Screen.Organiser.route) {
            val userProfile by authViewModel.currentUserProfile.collectAsState()
            OrganiserScreen(
                userProfile = userProfile,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AskAI.route) {
            AskAIScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Premium.route) {
            PremiumScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Downloads.route) {
            DownloadsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Calculator.route) {
            CalculatorScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Notes.route) {
            NotesScreen(
                onBack = { navController.popBackStack() },
                onNavigateToUpload = { navController.navigate(Screen.UploadNote.route) }
            )
        }
        composable(Screen.UploadNote.route) {
            UploadNoteScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Papers.route) {
            PapersScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Books.route) {
            BooksScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Videos.route) {
            VideosScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Upload.route) {
            UploadScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Syllabus.route) {
            SyllabusScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                onNavigateToRateUs = { /* TODO */ },
                onNavigateToShare = { /* TODO */ },
                onNavigateToPrivacy = { navController.navigate(Screen.Privacy.route) },
                onNavigateToTerms = { navController.navigate(Screen.Terms.route) },
                onNavigateToContact = { /* TODO */ },
                onNavigateToFeedback = { /* TODO */ },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Privacy.route) {
            LegalScreen(
                title = "Privacy Policy",
                content = "Your privacy is important to us. This Privacy Policy explains how we collect, use, and protect your information when you use MAKAUT Mate.\n\n1. Information We Collect\nWe collect information you provide directly to us, such as when you create an account or contact support.\n\n2. How We Use Information\nWe use the information to provide, maintain, and improve our services, and communicate with you.\n\n3. Data Security\nWe implement reasonable security measures to protect your data from unauthorized access.\n\n4. Your Choices\nYou can update your account information at any time through the app settings.",
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Terms.route) {
            LegalScreen(
                title = "Terms & Conditions",
                content = "By using MAKAUT Mate, you agree to these Terms and Conditions.\n\n1. User Accounts\nYou are responsible for maintaining the confidentiality of your account credentials.\n\n2. Content Ownership\nAll materials provided on the platform are for educational purposes. Unauthorized distribution is prohibited.\n\n3. Limitation of Liability\nMAKAUT Mate is not responsible for any inaccuracies in study materials or exam results.",
                onBack = { navController.popBackStack() }
            )
        }
    }
}

package com.dev.makautmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dev.makautmate.ui.screens.BooksScreen
import com.dev.makautmate.ui.screens.HomeScreen
import com.dev.makautmate.ui.screens.LoginScreen
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
import com.dev.makautmate.ui.screens.OrganisedContentScreen

import com.dev.makautmate.ui.viewmodel.AuthViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import com.dev.makautmate.ui.screens.PortalWebViewScreen
import com.dev.makautmate.ui.screens.NoticeScreen
import com.dev.makautmate.ui.screens.MarksFormScreen
import com.dev.makautmate.ui.screens.GradeCardScreen
import com.dev.makautmate.ui.viewmodel.StudentViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNext = { isLoggedIn ->
                val destination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
                if (isLoggedIn) studentViewModel.trackActivity("login")
                navController.navigate(destination) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    studentViewModel.trackActivity("login")
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
                    studentViewModel.trackActivity("signup")
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToNotes = { 
                    studentViewModel.trackActivity("viewed_notes")
                    navController.navigate(Screen.Notes.route) 
                },
                onNavigateToPapers = { 
                    studentViewModel.trackActivity("viewed_papers")
                    navController.navigate(Screen.Notes.route) 
                },
                onNavigateToBooks = { 
                    studentViewModel.trackActivity("viewed_books")
                    navController.navigate(Screen.Books.route) 
                },
                onNavigateToVideos = { 
                    studentViewModel.trackActivity("viewed_videos")
                    navController.navigate(Screen.Videos.route) 
                },
                onNavigateToUpload = { navController.navigate(Screen.Upload.route) },
                onNavigateToSyllabus = { 
                    studentViewModel.trackActivity("viewed_syllabus")
                    navController.navigate(Screen.Notes.route) 
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToDownloads = { navController.navigate(Screen.Downloads.route) },
                onNavigateToAskAI = { 
                    studentViewModel.trackActivity("used_ask_ai")
                    navController.navigate(Screen.AskAI.route) 
                },
                onNavigateToPremium = { navController.navigate(Screen.Premium.route) },
                onNavigateToCalculator = { navController.navigate(Screen.Calculator.route) },
                onNavigateToOrganiser = { 
                    studentViewModel.trackActivity("viewed_organiser")
                    navController.navigate(Screen.Organiser.route) 
                },
                onNavigateToPortalUrl = { url ->
                    navController.navigate(Screen.PortalWebView.createRoute(url))
                },
                onNavigateToNotices = { navController.navigate(Screen.Notices.route) },
                onNavigateToSubmitMarks = { navController.navigate(Screen.SubmitMarks.route) },
                onNavigateToGradeCard = { navController.navigate(Screen.GradeCard.route) },
                authViewModel = authViewModel,
                studentViewModel = studentViewModel
            )
        }
        composable(Screen.Notices.route) {
            NoticeScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.SubmitMarks.route) {
            MarksFormScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.GradeCard.route) {
            GradeCardScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.PortalWebView.route,
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")?.let {
                java.net.URLDecoder.decode(it, "UTF-8")
            } ?: ""
            PortalWebViewScreen(
                url = url,
                onNavigateBack = { navController.popBackStack() }
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
            DownloadsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPortalUrl = { url ->
                    navController.navigate(Screen.PortalWebView.createRoute(url))
                }
            )
        }
        composable(Screen.Calculator.route) {
            CalculatorScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Notes.route) {
            OrganisedContentScreen(
                onBack = { navController.popBackStack() },
                onNavigateToUpload = { navController.navigate(Screen.UploadNote.route) },
                onNavigateToPortalUrl = { url ->
                    navController.navigate(Screen.PortalWebView.createRoute(url))
                }
            )
        }
        composable(Screen.UploadNote.route) {
            UploadNoteScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Papers.route) {
            PapersScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPortalUrl = { url ->
                    navController.navigate(Screen.PortalWebView.createRoute(url))
                }
            )
        }
        composable(Screen.Books.route) {
            BooksScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPortalUrl = { url ->
                    navController.navigate(Screen.PortalWebView.createRoute(url))
                }
            )
        }
        composable(Screen.Upload.route) {
            UploadScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Syllabus.route) {
            SyllabusScreen(
                onBack = { navController.popBackStack() },
                onNavigateToPortalUrl = { url ->
                    navController.navigate(Screen.PortalWebView.createRoute(url))
                }
            )
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

package com.dev.makautmate.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.dev.makautmate.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.common.api.ApiException
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.makautmate.ui.theme.BluePrimary
import com.dev.makautmate.ui.viewmodel.AuthViewModel
import com.dev.makautmate.ui.viewmodel.PortalViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    portalViewModel: PortalViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Google Sign-In Launcher
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                authViewModel.signInWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            scope.launch {
                snackbarHostState.showSnackbar("Google Sign-In failed: ${e.message}")
            }
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            onLoginSuccess()
        } else if (authState is AuthViewModel.AuthState.Error) {
            snackbarHostState.showSnackbar((authState as AuthViewModel.AuthState.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Gradient Glow at the top (Blue)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                BluePrimary.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // "Let's Study" Cursive Text
                Text(
                    text = "Let's Study",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontFamily = FontFamily.Cursive,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Abstract Book Illustration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BookLineArt()
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Main Text
                Text(
                    text = "Search Less,\nExplore More...",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Google Login Button
                Button(
                    onClick = {
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    enabled = authState !is AuthViewModel.AuthState.Loading
                ) {
                    if (authState is AuthViewModel.AuthState.Loading) {
                        CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Login,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Sign in with Google", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Login as Guest Button
                TextButton(
                    onClick = { authViewModel.loginAsGuest() }
                ) {
                    Text("Login as Guest", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Footer Links
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "By continuing, you agree to our ",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                    Row {
                        Text(
                            "Terms of Service",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("and", color = Color.Gray, fontSize = 11.sp)
                        Text(
                            " Privacy Policy",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun BookLineArt() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val path = Path().apply {
            moveTo(width * 0.05f, height * 0.7f)
            cubicTo(width * 0.2f, height * 0.8f, width * 0.4f, height * 0.5f, width * 0.5f, height * 0.7f)
            cubicTo(width * 0.6f, height * 0.9f, width * 0.8f, height * 0.6f, width * 0.95f, height * 0.7f)
            
            moveTo(width * 0.5f, height * 0.7f)
            cubicTo(width * 0.4f, height * 0.4f, width * 0.2f, height * 0.3f, width * 0.15f, height * 0.55f)
            lineTo(width * 0.15f, height * 0.75f)
            quadraticTo(width * 0.3f, height * 0.65f, width * 0.5f, height * 0.8f)
            
            moveTo(width * 0.5f, height * 0.7f)
            cubicTo(width * 0.6f, height * 0.4f, width * 0.8f, height * 0.3f, width * 0.85f, height * 0.55f)
            lineTo(width * 0.85f, height * 0.75f)
            quadraticTo(width * 0.7f, height * 0.65f, width * 0.5f, height * 0.8f)
            
            moveTo(width * 0.5f, height * 0.7f)
            lineTo(width * 0.5f, height * 0.8f)
        }
        
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.7f),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

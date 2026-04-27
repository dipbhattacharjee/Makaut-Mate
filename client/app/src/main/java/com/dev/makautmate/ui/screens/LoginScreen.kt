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
    val isPortalSyncing by portalViewModel.isSyncing.collectAsState()
    val portalError by portalViewModel.error.collectAsState(initial = null)
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var rollNumber by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            onLoginSuccess()
        } else if (authState is AuthViewModel.AuthState.Error) {
            snackbarHostState.showSnackbar((authState as AuthViewModel.AuthState.Error).message)
        }
    }

    LaunchedEffect(portalError) {
        portalError?.let {
            snackbarHostState.showSnackbar(it)
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

                // Roll Number Field
                OutlinedTextField(
                    value = rollNumber,
                    onValueChange = { rollNumber = it },
                    label = { Text("University Roll Number", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date of Birth Field
                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Date of Birth (DD-MM-YYYY)", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Example: 15-08-2005", color = Color.DarkGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                // The New Main Login Button
                Button(
                    onClick = {
                        if (rollNumber.isNotBlank() && dob.isNotBlank()) {
                            portalViewModel.loginToPortal(rollNumber, dob) {
                                onLoginSuccess()
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please enter Roll Number and DOB")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    enabled = !isPortalSyncing
                ) {
                    if (isPortalSyncing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Login to Portal", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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

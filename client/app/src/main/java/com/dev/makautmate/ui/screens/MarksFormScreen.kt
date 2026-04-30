package com.dev.makautmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.makautmate.ui.components.StandardInput
import com.dev.makautmate.ui.viewmodel.StudentViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarksFormScreen(
    onBack: () -> Unit,
    viewModel: StudentViewModel = hiltViewModel(),
) {
    var subject by remember { mutableStateOf("") }
    var marks by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.marksSubmitStatus.collectLatest { result ->
            result.onSuccess {
                snackbarHostState.showSnackbar("Marks submitted successfully!")
                subject = ""
                marks = ""
                semester = ""
            }.onFailure {
                snackbarHostState.showSnackbar("Error: ${it.message}")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Submit Marks", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F2040),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFF0F2040), Color(0xFF05080C))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "Academic Progress",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "Keep track of your subject marks here.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                StandardInput(
                    value = subject,
                    onValueChange = { subject = it },
                    label = "Subject Name",
                    icon = Icons.Rounded.Category
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                StandardInput(
                    value = marks,
                    onValueChange = { marks = it },
                    label = "Marks Obtained",
                    icon = Icons.Rounded.Percent
                )

                Spacer(modifier = Modifier.height(16.dp))

                StandardInput(
                    value = semester,
                    onValueChange = { semester = it },
                    label = "Semester (1-8)",
                    icon = Icons.Rounded.School
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        val m = marks.toIntOrNull() ?: 0
                        val s = semester.toIntOrNull() ?: 0
                        if (subject.isNotBlank() && (m > 0) && (s > 0)) {
                            viewModel.submitMarks(subject, m, s)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B8CFF)),
                    enabled = !isLoading && subject.isNotBlank() && marks.isNotBlank() && semester.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Submit Marks", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

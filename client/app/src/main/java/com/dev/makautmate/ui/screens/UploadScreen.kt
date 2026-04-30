package com.dev.makautmate.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.makautmate.ui.viewmodel.NoteViewModel
import com.dev.makautmate.ui.theme.BluePrimary
import com.dev.makautmate.ui.components.StandardInput
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    onBack: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Notes") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    
    val types = listOf("Notes", "Books", "Organizers", "PYQs")
    val isLoading by viewModel.isLoading.collectAsState()
    val uploadStatus by viewModel.uploadStatus.collectAsState(initial = null)
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fileUri = uri
        fileName = uri?.lastPathSegment ?: "File selected"
    }

    LaunchedEffect(uploadStatus) {
        uploadStatus?.onSuccess {
            snackbarHostState.showSnackbar("Upload successful! Pending approval.")
            title = ""
            course = ""
            semester = ""
            branch = ""
            fileUri = null
            fileName = ""
        }?.onFailure {
            snackbarHostState.showSnackbar("Upload failed: ${it.message}")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Contribute", fontWeight = FontWeight.Bold) },
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
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "Help the Community",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "Upload resources to help other students.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Type Selection
                Text("Select Resource Type", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    types.forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = type },
                            label = { Text(type) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF5B8CFF),
                                selectedLabelColor = Color.White,
                                containerColor = Color.White.copy(alpha = 0.05f),
                                labelColor = Color.White.copy(alpha = 0.6f)
                            ),
                            border = null,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                StandardInput(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title (e.g. Physics Module 1)",
                    icon = Icons.Rounded.Title
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                StandardInput(
                    value = course,
                    onValueChange = { course = it },
                    label = "Course (e.g. B.Tech, BCA)",
                    icon = Icons.Rounded.Category
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        StandardInput(
                            value = semester,
                            onValueChange = { semester = it },
                            label = "Semester",
                            icon = Icons.Rounded.School
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StandardInput(
                            value = branch,
                            onValueChange = { branch = it },
                            label = "Branch",
                            icon = Icons.Rounded.AccountTree
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // File Picker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                        .clickable { launcher.launch("*/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (fileUri == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Rounded.CloudUpload, contentDescription = null, tint = Color(0xFF82B1FF), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tap to Select File", color = Color.White.copy(alpha = 0.5f))
                        }
                    } else {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Description, contentDescription = null, tint = Color(0xFF22C55E))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(fileName, color = Color.White, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (fileUri != null && title.isNotBlank()) {
                            viewModel.uploadNote(
                                title = title,
                                author = userProfile?.fullName ?: "User",
                                semester = semester,
                                subject = branch,
                                course = course,
                                type = selectedType,
                                fileUri = fileUri!!
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B8CFF)),
                    enabled = !isLoading && fileUri != null && title.isNotBlank() && course.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Upload for Approval", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


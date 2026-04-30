package com.dev.makautmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.dev.makautmate.domain.model.Note
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dev.makautmate.ui.components.BookLoader
import com.dev.makautmate.ui.components.SkeletonCard
import com.dev.makautmate.ui.theme.BluePrimary
import com.dev.makautmate.ui.viewmodel.AuthViewModel
import com.dev.makautmate.ui.viewmodel.NoteViewModel

@Composable
fun NotesScreen(
    onBack: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToPortalUrl: (String) -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val notes by viewModel.cloudNotes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val semesters = listOf("All", "Sem-1", "Sem-2", "Sem-3", "Sem-4", "Sem-5", "Sem-6", "Sem-7", "Sem-8")
    var selectedSem by remember { mutableStateOf("All") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A1220), Color(0xFF05080C))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(64.dp))
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pick your subject!",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Semester Filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(semesters) { sem ->
                    val isSelected = sem == selectedSem
                    Surface(
                        onClick = { 
                            selectedSem = sem
                            viewModel.setSemester(sem)
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) BluePrimary else Color.White.copy(alpha = 0.05f),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSelected) {
                                Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = sem,
                                color = if (isSelected) Color.Black else Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BookLoader()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Searching for Notes...", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(32.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            repeat(3) {
                                SkeletonCard()
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes) { note ->
                        NoteItem(
                            note = note,
                            onNavigateToPortalUrl = onNavigateToPortalUrl
                        )
                    }
                }
            }
        }

        // Add Floating Action Button for Upload
        FloatingActionButton(
            onClick = onNavigateToUpload,
            containerColor = BluePrimary,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 24.dp)
        ) {
            Icon(Icons.Rounded.CloudUpload, contentDescription = "Upload Note")
        }

        if (!isLoading && notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BookLoader()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No notes found for this semester", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onNavigateToPortalUrl: (String) -> Unit) {
    Surface(
        onClick = {
            if (note.fileUrl.isNotBlank()) {
                onNavigateToPortalUrl(note.fileUrl)
            }
        },
        modifier = Modifier.fillMaxWidth().height(90.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://static.vecteezy.com/system/resources/previews/015/074/331/original/folder-3d-render-icon-png.png",
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = note.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Subject: ${note.subject} | ${note.semester}", color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.Rounded.NorthEast, contentDescription = null, tint = BluePrimary)
        }
    }
}



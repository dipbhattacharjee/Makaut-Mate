package com.dev.makautmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ListAlt
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.makautmate.domain.model.Note
import com.dev.makautmate.ui.components.BookLoader
import com.dev.makautmate.ui.theme.BluePrimary
import com.dev.makautmate.ui.viewmodel.NoteViewModel

@Composable
fun OrganisedContentScreen(
    onBack: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToPortalUrl: (String) -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val notes by viewModel.cloudNotes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    
    val isOrganiser = userProfile?.role == "organiser" || userProfile?.role == "admin"
    
    val courses = listOf("All", "CSE", "IT", "ECE", "EE", "ME", "CE", "BCA", "MCA")
    val semesters = listOf("All", "Sem-1", "Sem-2", "Sem-3", "Sem-4", "Sem-5", "Sem-6", "Sem-7", "Sem-8")
    val types = listOf("All", "Notes", "PYQ", "Syllabus")

    var selectedCourse by remember { mutableStateOf("All") }
    var selectedSem by remember { mutableStateOf("All") }
    var selectedType by remember { mutableStateOf("All") }

    val filteredNotes = notes.filter { note ->
        (selectedCourse == "All" || note.course == selectedCourse) &&
        (selectedSem == "All" || note.semester == selectedSem) &&
        (selectedType == "All" || note.type == selectedType)
    }

    val groupedBySubject = filteredNotes.groupBy { it.subject }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A1220), Color(0xFF05080C))))
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
                    text = "Study Materials",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filters
            FilterRow("Course", courses, selectedCourse) { selectedCourse = it }
            FilterRow("Semester", semesters, selectedSem) { 
                selectedSem = it
                viewModel.setSemester(it) 
            }
            FilterRow("Type", types, selectedType) { selectedType = it }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    BookLoader()
                }
            } else if (groupedBySubject.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.SearchOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No materials found", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    groupedBySubject.forEach { (subject, subjectNotes) ->
                        item {
                            SubjectSection(
                                subject = subject,
                                notes = subjectNotes,
                                onNavigateToPortalUrl = onNavigateToPortalUrl
                            )
                        }
                    }
                }
            }
        }

        if (isOrganiser) {
            FloatingActionButton(
                onClick = onNavigateToUpload,
                containerColor = BluePrimary,
                contentColor = Color.Black,
                modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Material")
            }
        }
    }
}

@Composable
fun FilterRow(label: String, items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                val isSelected = item == selectedItem
                FilterChip(
                    selected = isSelected,
                    onClick = { onItemSelected(item) },
                    label = { Text(item, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BluePrimary,
                        selectedLabelColor = Color.Black,
                        containerColor = Color.White.copy(alpha = 0.05f),
                        labelColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = null
                )
            }
        }
    }
}

@Composable
fun SubjectSection(
    subject: String,
    notes: List<Note>,
    onNavigateToPortalUrl: (String) -> Unit
) {
    Column {
        Text(
            text = if (subject.isBlank()) "General" else subject,
            color = BluePrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        notes.forEach { note ->
            ResourceItem(note, onNavigateToPortalUrl)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ResourceItem(note: Note, onNavigateToPortalUrl: (String) -> Unit) {
    Surface(
        onClick = { if (note.fileUrl.isNotBlank()) onNavigateToPortalUrl(note.fileUrl) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (note.type) {
                "PYQ" -> Icons.Rounded.HistoryEdu
                "Syllabus" -> Icons.AutoMirrored.Rounded.ListAlt
                else -> Icons.Rounded.Description
            }
            
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = BluePrimary.copy(alpha = 0.1f)
            ) {
                Icon(icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.padding(8.dp))
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = note.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(text = "${note.type} • ${note.author} • ${note.semester}", color = Color.Gray, fontSize = 12.sp)
            }
            
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

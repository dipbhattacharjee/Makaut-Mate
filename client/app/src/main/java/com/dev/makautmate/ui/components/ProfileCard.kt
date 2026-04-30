package com.dev.makautmate.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.makautmate.domain.model.Notice
import com.dev.makautmate.domain.model.StudentProfile

@Composable
fun ProfileCard(
    profile: StudentProfile?,
    isSyncing: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // Results Bento Card (The "Live Result" Wide Tile)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF3D8BFF), Color(0xFF8E24AA))
                    )
                )
                .clickable { expanded = !expanded }
                .padding(20.dp)
        ) {
            if (isSyncing) {
                SyncingState()
            } else if (profile != null) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current CGPA",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = profile.cgpa.toString(),
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        
                        SyncIcon(isSyncing = isSyncing)
                    }
                    
                    AnimatedVisibility(visible = expanded) {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            // Example SGPA breakdown or other details
                            Text(
                                text = "Attendance: ${profile.attendance}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                NotSyncedState()
            }
        }

        if (profile != null && !isSyncing) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "CA Marks Progress",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            )
            
            // CA Marks Component
            val subjects = profile.caMarks.keys.toList()
            Box(modifier = Modifier.heightIn(max = 400.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(subjects) { subject ->
                        SubjectCard(
                            subject = subject,
                            mark = profile.caMarks[subject] ?: 0.0
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectCard(subject: String, mark: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1C1F26))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicatorCustom(
                    progress = (mark / 25f).toFloat(),
                    color = if (mark >= 10) Color(0xFF00C853) else Color(0xFFFFAB00)
                )
                Text(
                    text = mark.toInt().toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subject,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun CircularProgressIndicatorCustom(
    progress: Float,
    color: Color
) {
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 6.dp.toPx()
        drawArc(
            color = Color.White.copy(alpha = 0.1f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
        )
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
    }
}

@Composable
fun NoticeBoardSection(
    notices: List<Notice>,
    onClick: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(notices) {
        if (notices.isNotEmpty()) {
            while (true) {
                kotlinx.coroutines.delay(3000)
                currentIndex = (currentIndex + 1) % notices.size
            }
        }
    }

    Column(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Notice Board",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.Rounded.Sync,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(16.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (notices.isEmpty()) {
                Text(
                    text = "No notices today",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            } else {
                AnimatedContent(
                    targetState = notices[currentIndex],
                    transitionSpec = {
                        (fadeIn(tween(500)) + slideInVertically { it })
                            .togetherWith(fadeOut(tween(500)) + slideOutVertically { -it })
                    },
                    label = "noticeAnimation"
                ) { notice ->
                    Text(
                        text = notice.title,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SyncIcon(isSyncing: Boolean) {
    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Icon(
        imageVector = Icons.Rounded.Sync,
        contentDescription = "Sync",
        tint = Color.White,
        modifier = Modifier
            .size(28.dp)
            .rotate(if (isSyncing) rotation else 0f)
    )
}

@Composable
fun SyncingState() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.fillMaxWidth(0.6f).height(20.dp).clip(RoundedCornerShape(4.dp)).background(Color.White.copy(alpha = alpha)))
        Box(modifier = Modifier.fillMaxWidth(0.4f).height(15.dp).clip(RoundedCornerShape(4.dp)).background(Color.White.copy(alpha = alpha)))
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth(0.8f).height(40.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = alpha)))
    }
}

@Composable
fun NotSyncedState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Portal Data Not Synced",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap to sync your academic data",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }
    }
}

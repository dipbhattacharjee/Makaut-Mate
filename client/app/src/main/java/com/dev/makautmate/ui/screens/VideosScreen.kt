package com.dev.makautmate.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// DATA MODEL
// ─────────────────────────────────────────────────────────────────────────────

data class YouTuber(
    val name: String,
    val tagline: String,           // Short tag shown as a chip
    val description: String,       // Full description
    val channelUrl: String,
    val profileImageUrl: String,
    val bannerImageUrl: String,
    val category: String,
    val subscribers: String,       // e.g. "2.6M"
    val totalVideos: String,       // e.g. "2000+"
    val accentColor: Color
)

// ─────────────────────────────────────────────────────────────────────────────
// REAL CHANNEL DATA  (profile pictures use the stable googleusercontent format)
// ─────────────────────────────────────────────────────────────────────────────

val topYouTubers = listOf(
    YouTuber(
        name = "Gate Smashers",
        tagline = "OS · DBMS · CN · DSA",
        description = "India's #1 free CS platform by Varun & Naina Singla. Covers B.Tech CSE, GATE, UGC NET, OS, DBMS, CN, TOC, Compiler Design, AI/ML, and Placement Prep with 2.6 M+ learners.",
        channelUrl = "https://www.youtube.com/@gatesmashers",
        profileImageUrl = "https://yt3.googleusercontent.com/ytc/AIdro_kCJJkHj4WXSV3n0cFsVV3gLamBYXuGNv-jmhPLHQ=s176-c-k-c0x00ffffff-no-rj",
        bannerImageUrl = "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?q=80&w=800&auto=format&fit=crop",
        category = "GATE / B.Tech",
        subscribers = "2.6M",
        totalVideos = "2000+",
        accentColor = Color(0xFF3B82F6)
    ),
    YouTuber(
        name = "Neso Academy",
        tagline = "Digital · Micro · TOC",
        description = "Premium tutorials for Digital Electronics, Microprocessors, Theory of Computation, and Signals & Systems. Crisp, structured lectures perfect for MAKAUT semester exams.",
        channelUrl = "https://www.youtube.com/@nesoacademy",
        profileImageUrl = "https://yt3.googleusercontent.com/ytc/AIdro_knl_V4i2yxcTmRALEZbFx7h5GNFKumS5RJ1YRJBQ=s176-c-k-c0x00ffffff-no-rj",
        bannerImageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?q=80&w=800&auto=format&fit=crop",
        category = "Core Electronics",
        subscribers = "3.5M",
        totalVideos = "1000+",
        accentColor = Color(0xFF8B5CF6)
    ),
    YouTuber(
        name = "Abdul Bari",
        tagline = "Algorithms · DSA · C++",
        description = "The gold standard for Algorithms and Data Structures. Prof. Abdul Bari explains recursion, dynamic programming, graph algorithms, and complexity with unmatched clarity. 1M+ subscribers trust him.",
        channelUrl = "https://www.youtube.com/@abdul_bari",
        profileImageUrl = "https://yt3.googleusercontent.com/ytc/AIdro_mj0F4c1f9yFUqBONNjAzQ7_F6FS9K2pLyUoifrZg=s176-c-k-c0x00ffffff-no-rj",
        bannerImageUrl = "https://images.unsplash.com/photo-1509228468518-180dd4864904?q=80&w=800&auto=format&fit=crop",
        category = "Algorithms",
        subscribers = "1M+",
        totalVideos = "200+",
        accentColor = Color(0xFF10B981)
    ),
    YouTuber(
        name = "CodeWithHarry",
        tagline = "Python · Web Dev · C/C++",
        description = "Harry Bhai's Hindi-medium channel is the easiest gateway to Python, JavaScript, Web Development, and C/C++ basics. Huge playlists, rapid delivery, and 6M+ subscribers make this a MAKAUT staple.",
        channelUrl = "https://www.youtube.com/@CodeWithHarry",
        profileImageUrl = "https://yt3.googleusercontent.com/ytc/AIdro_nYG7UkMLIWNBjyaU4dDFRp84-BjMH7TcOuqGEwEQ=s176-c-k-c0x00ffffff-no-rj",
        bannerImageUrl = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?q=80&w=800&auto=format&fit=crop",
        category = "Programming",
        subscribers = "6M+",
        totalVideos = "600+",
        accentColor = Color(0xFFF59E0B)
    ),
    YouTuber(
        name = "Jenny's Lectures",
        tagline = "DS · Algo · C · Java",
        description = "Dr. Naresh Joshi's channel provides structured, exam-oriented lectures on Data Structures, Algorithms, C Programming, and Java — widely recommended by MAKAUT faculties across West Bengal.",
        channelUrl = "https://www.youtube.com/@JennyslecturesCSITNE",
        profileImageUrl = "https://yt3.googleusercontent.com/ytc/AIdro_l5RQHP-YMTJL4S9C6aBHlZm5lsFOt8xGv4lMUENA=s176-c-k-c0x00ffffff-no-rj",
        bannerImageUrl = "https://images.unsplash.com/photo-1515879218367-8466d910aaa4?q=80&w=800&auto=format&fit=crop",
        category = "CS Fundamentals",
        subscribers = "1.3M",
        totalVideos = "800+",
        accentColor = Color(0xFFEF4444)
    ),
    YouTuber(
        name = "Apna College",
        tagline = "Java · DSA · Web · SQL",
        description = "Aman Dhattarwal & Shradha Khapra's channel powers lakhs of students through Java, DSA, Web Dev, and SQL in a friendly, placement-focused style. Their DSA sheet is a must-do for MAKAUT placements.",
        channelUrl = "https://www.youtube.com/@ApnaCollegeOfficial",
        profileImageUrl = "https://yt3.googleusercontent.com/ytc/AIdro_mNPlr93cWF6I-Gw77SajH1tExw6YVGJj0MkUSI7Q=s176-c-k-c0x00ffffff-no-rj",
        bannerImageUrl = "https://images.unsplash.com/photo-1555099962-4199c345e5dd?q=80&w=800&auto=format&fit=crop",
        category = "Placement Prep",
        subscribers = "4.5M",
        totalVideos = "500+",
        accentColor = Color(0xFF06B6D4)
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// CATEGORY FILTER CHIPS
// ─────────────────────────────────────────────────────────────────────────────

val allCategories = listOf("All") + topYouTubers.map { it.category }.distinct()

// ─────────────────────────────────────────────────────────────────────────────
// PREMIUM LOADER  — orbiting rings with pulsing center
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PremiumLoader() {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")

    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
        label = "outer"
    )
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "inner"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer orbiting ring
        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer { rotationZ = outerRotation }
                .drawBehind {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFF3B82F6).copy(alpha = 0f),
                                Color(0xFF3B82F6),
                                Color(0xFF8B5CF6),
                                Color(0xFF3B82F6).copy(alpha = 0f)
                            )
                        ),
                        radius = size.minDimension / 2,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                    )
                }
        )
        // Inner counter-rotating ring
        Box(
            modifier = Modifier
                .size(68.dp)
                .graphicsLayer { rotationZ = innerRotation }
                .drawBehind {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFF10B981).copy(alpha = 0f),
                                Color(0xFF10B981),
                                Color(0xFFF59E0B),
                                Color(0xFF10B981).copy(alpha = 0f)
                            )
                        ),
                        radius = size.minDimension / 2,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx())
                    )
                }
        )
        // Pulsing center dot
        Box(
            modifier = Modifier
                .size(18.dp)
                .graphicsLayer { scaleX = pulse; scaleY = pulse; this.alpha = alpha }
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF60A5FA), Color(0xFF3B82F6))
                    )
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideosScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        delay(2600)
        isLoading = false
    }

    val filteredList = remember(selectedCategory) {
        if (selectedCategory == "All") topYouTubers
        else topYouTubers.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Top Educators",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A1628),
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
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0A1628), Color(0xFF050A12))
                    )
                )
        ) {
            AnimatedContent(
                targetState = isLoading,
                transitionSpec = {
                    fadeIn(tween(600)) togetherWith fadeOut(tween(400))
                },
                label = "screenSwitch"
            ) { loading ->
                if (loading) {
                    // ── LOADING STATE ─────────────────────────────────────
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            PremiumLoader()
                            Text(
                                "Curating best educators for MAKAUT…",
                                color = Color.White.copy(alpha = 0.55f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                } else {
                    // ── CONTENT STATE ──────────────────────────────────────
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // Header
                        item {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(top = 20.dp, bottom = 8.dp)
                            ) {
                                Text(
                                    "Handpicked for MAKAUT",
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    lineHeight = 32.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "YouTube channels to ace your semester exams & placements.",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        // Category filter chips
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(allCategories) { cat ->
                                    val selected = cat == selectedCategory
                                    Surface(
                                        onClick = { selectedCategory = cat },
                                        shape = RoundedCornerShape(50),
                                        color = if (selected) Color(0xFF3B82F6) else Color.White.copy(alpha = 0.06f),
                                        border = if (!selected) androidx.compose.foundation.BorderStroke(
                                            0.5.dp, Color.White.copy(alpha = 0.12f)
                                        ) else null
                                    ) {
                                        Text(
                                            cat,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                            color = if (selected) Color.White else Color.White.copy(alpha = 0.55f),
                                            fontSize = 12.sp,
                                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // Cards with staggered entry
                        items(filteredList, key = { it.name }) { youtuber ->
                            YouTuberCard(
                                youtuber = youtuber,
                                onClick = { uriHandler.openUri(youtuber.channelUrl) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// YOUTUBER CARD  — premium glassmorphic card with banner, avatar, tags
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun YouTuberCard(youtuber: YouTuber, onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(youtuber.name) {
        delay(80)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500, easing = FastOutSlowInEasing)) { 40 }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Subtle glow behind the card matching accent color
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { translationY = 6f }
                    .clip(RoundedCornerShape(20.dp))
                    .background(youtuber.accentColor.copy(alpha = 0.08f))
            )

            Card(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF111D35)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    0.5.dp, Color.White.copy(alpha = 0.08f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    // ── BANNER ────────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    ) {
                        AsyncImage(
                            model = youtuber.bannerImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Gradient overlay so text on it stays readable
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0xFF111D35).copy(alpha = 0.7f)
                                        )
                                    )
                                )
                        )
                        // Category pill top-right
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(10.dp),
                            shape = RoundedCornerShape(50),
                            color = youtuber.accentColor.copy(alpha = 0.85f)
                        ) {
                            Text(
                                youtuber.category,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // ── AVATAR + INFO ROW ─────────────────────────────────
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .offset(y = (-22).dp),       // Pulls avatar up over banner
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Avatar with colored ring
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .border(2.dp, youtuber.accentColor, CircleShape)
                                .padding(2.dp)
                                .clip(CircleShape)
                        ) {
                            AsyncImage(
                                model = youtuber.profileImageUrl,
                                contentDescription = "${youtuber.name} profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Name + tagline (positioned to clear the avatar pull-up offset)
                        Column(modifier = Modifier.padding(bottom = 2.dp)) {
                            Text(
                                youtuber.name,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                youtuber.tagline,
                                color = youtuber.accentColor.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // YouTube play icon button
                        Icon(
                            Icons.Rounded.PlayCircleFilled,
                            contentDescription = "Open channel",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // ── DESCRIPTION ───────────────────────────────────────
                    Text(
                        youtuber.description,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── STATS ROW ─────────────────────────────────────────
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.06f),
                        thickness = 0.5.dp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(
                            icon = Icons.Rounded.Person,
                            value = youtuber.subscribers,
                            label = "Subscribers",
                            tint = youtuber.accentColor
                        )
                        StatItem(
                            icon = Icons.Rounded.VideoLibrary,
                            value = youtuber.totalVideos,
                            label = "Videos",
                            tint = youtuber.accentColor
                        )
                        // Visit button
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = youtuber.accentColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp, youtuber.accentColor.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.OpenInNew,
                                    contentDescription = null,
                                    tint = youtuber.accentColor,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    "Visit",
                                    color = youtuber.accentColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STAT ITEM  — small icon + value + label
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    tint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, contentDescription = null, tint = tint.copy(alpha = 0.7f), modifier = Modifier.size(13.dp))
        Column {
            Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp)
        }
    }
}
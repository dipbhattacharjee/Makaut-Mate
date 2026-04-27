package com.dev.makautmate.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.makautmate.ui.viewmodel.AIViewModel
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class ChatMessage(val text: String, val isUser: Boolean)

// ─────────────────────────────────────────────────────────────────────────────
// AURORA BACKGROUND  — two slow-drifting blobs that animate in the bg
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AuroraBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")

    val blob1X by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(12000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "b1x"
    )
    val blob2X by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(9000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "b2x"
    )
    val blob1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            tween(5000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "b1a"
    )

    Box(
        modifier = modifier
            .drawBehind {
                // Blob 1 — blue/teal
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = blob1Alpha),
                            Color(0xFF2DD4BF).copy(alpha = 0f)
                        ),
                        center = Offset(size.width * blob1X, size.height * 0.25f),
                        radius = size.width * 0.55f
                    ),
                    radius = size.width * 0.55f,
                    center = Offset(size.width * blob1X, size.height * 0.25f)
                )
                // Blob 2 — indigo/purple
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6366F1).copy(alpha = blob1Alpha * 0.7f),
                            Color(0xFF8B5CF6).copy(alpha = 0f)
                        ),
                        center = Offset(size.width * blob2X, size.height * 0.7f),
                        radius = size.width * 0.45f
                    ),
                    radius = size.width * 0.45f,
                    center = Offset(size.width * blob2X, size.height * 0.7f)
                )
            }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// AI AVATAR LOADER  — orbiting particles + rotating arc ring + pulse core
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AIThinkingAvatar(size: Dp = 42.dp, isThinking: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "aiAvatar")

    // Arc rotation
    val arcAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing)),
        label = "arc"
    )
    // Counter arc
    val arcAngle2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "arc2"
    )
    // Core pulse
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )
    // Particle orbit
    val orbit by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing)),
        label = "orbit"
    )

    Box(
        modifier = Modifier
            .size(size)
            .drawBehind {
                val cx = this.size.width / 2
                val cy = this.size.height / 2
                val r = this.size.minDimension / 2

                if (isThinking) {
                    // Outer spinning dashed-style arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(alpha = 0f),
                                Color(0xFF3B82F6),
                                Color(0xFF2DD4BF),
                                Color(0xFF3B82F6).copy(alpha = 0f)
                            ),
                            center = Offset(cx, cy)
                        ),
                        startAngle = arcAngle,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
                        topLeft = Offset(cx - r, cy - r),
                        size = this.size
                    )
                    // Inner counter arc
                    val r2 = r * 0.72f
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6).copy(alpha = 0f),
                                Color(0xFF8B5CF6),
                                Color(0xFFF472B6),
                                Color(0xFF8B5CF6).copy(alpha = 0f)
                            ),
                            center = Offset(cx, cy)
                        ),
                        startAngle = arcAngle2,
                        sweepAngle = 200f,
                        useCenter = false,
                        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round),
                        topLeft = Offset(cx - r2, cy - r2),
                        size = androidx.compose.ui.geometry.Size(r2 * 2, r2 * 2)
                    )
                    // Orbiting particle dot
                    val px = cx + r * 0.85f * cos(orbit)
                    val py = cy + r * 0.85f * sin(orbit)
                    drawCircle(
                        color = Color(0xFF2DD4BF),
                        radius = 3.dp.toPx(),
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = Color(0xFF2DD4BF).copy(alpha = 0.3f),
                        radius = 6.dp.toPx(),
                        center = Offset(px, py)
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Gradient filled circle core
        Box(
            modifier = Modifier
                .size(if (isThinking) size * corePulse * 0.65f else size * 0.9f)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = if (isThinking)
                            listOf(Color(0xFF3B82F6), Color(0xFF2DD4BF))
                        else
                            listOf(Color(0xFF1E3A5F), Color(0xFF162032))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(size * 0.38f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// THREE-DOT TYPING BUBBLE  — staggered wave animation
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TypingBubble() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    val offsets = (0..2).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -8f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1000
                    0f at 0
                    -8f at 200 + index * 120
                    0f at 400 + index * 120
                    0f at 1000
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "dot$index"
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(Color(0xFF1A2A4A))
                .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                offsets.forEachIndexed { i, offset ->
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .offset(y = offset.value.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF60A5FA), Color(0xFF3B82F6))
                                )
                            )
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CHAT BUBBLE  — polished with shimmer effect for AI messages
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ChatBubbleAnimated(message: ChatMessage) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(350)) +
                slideInHorizontally(
                    tween(400, easing = FastOutSlowInEasing)
                ) { if (message.isUser) 60 else -60 } +
                expandVertically(tween(300, easing = FastOutSlowInEasing))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (message.isUser) 48.dp else 0.dp,
                    end = if (message.isUser) 0.dp else 48.dp
                ),
            contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            if (message.isUser) {
                // User bubble — gradient blue
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 20.dp, topEnd = 20.dp,
                                bottomStart = 20.dp, bottomEnd = 4.dp
                            )
                        )
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = message.text,
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            } else {
                // AI bubble — glass dark with subtle left accent line
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 4.dp, topEnd = 20.dp,
                                bottomStart = 20.dp, bottomEnd = 20.dp
                            )
                        )
                        .background(Color(0xFF111D35))
                        .border(
                            0.5.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF3B82F6).copy(alpha = 0.4f),
                                    Color(0xFF2DD4BF).copy(alpha = 0.2f)
                                )
                            ),
                            RoundedCornerShape(
                                topStart = 4.dp, topEnd = 20.dp,
                                bottomStart = 20.dp, bottomEnd = 20.dp
                            )
                        )
                ) {
                    Row {
                        // Left accent stripe
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .fillMaxHeight()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF3B82F6), Color(0xFF2DD4BF))
                                    )
                                )
                        )
                        Text(
                            text = message.text,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EMPTY STATE  — shown before any messages
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmptyStateHint() {
    val suggestions = listOf(
        "📅 What's the MAKAUT exam schedule?",
        "📚 Explain the syllabus for Data Structures",
        "🎓 How to apply for MAKAUT back paper?",
        "💡 Best books for Computer Networks?"
    )

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(700, delayMillis = 300)) + slideInVertically(tween(700)) { 30 }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Central sparkle icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF1E3A5F), Color(0xFF0A1628))
                        )
                    )
                    .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(34.dp)
                )
            }

            Text(
                "Ask me anything about MAKAUT",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Powered by Gemini 2.0 — syllabus, exams,\nresults, placements and more.",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Suggestion chips
            suggestions.forEachIndexed { i, text ->
                var chipVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(500L + i * 100L)
                    chipVisible = true
                }
                AnimatedVisibility(
                    visible = chipVisible,
                    enter = fadeIn(tween(400)) + slideInHorizontally(tween(400)) { -20 }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0D1B32),
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp, Color.White.copy(alpha = 0.08f)
                        )
                    ) {
                        Text(
                            text,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SEND BUTTON  — morphs between idle and active states
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SendButton(enabled: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "sendScale"
    )
    val bgAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.35f,
        animationSpec = tween(200),
        label = "sendAlpha"
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF3B82F6).copy(alpha = bgAlpha),
                        Color(0xFF2DD4BF).copy(alpha = bgAlpha)
                    )
                )
            )
            .then(
                if (enabled) Modifier.clickableNoRipple(onClick) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.AutoMirrored.Rounded.Send,
            contentDescription = "Send",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MAIN SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskAIScreen(
    onBack: () -> Unit,
    viewModel: AIViewModel = hiltViewModel()
) {
    var message by remember { mutableStateOf("") }
    val chatMessages = viewModel.chatMessages
    val isTyping by viewModel.isTyping.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll to bottom on new message or typing indicator
    LaunchedEffect(chatMessages.size, isTyping) {
        if (chatMessages.isNotEmpty() || isTyping) {
            scope.launch {
                val target = chatMessages.size + if (isTyping) 1 else 0
                if (target > 0) listState.animateScrollToItem(target - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AIThinkingAvatar(size = 42.dp, isThinking = isTyping)
                        Column {
                            Text(
                                "MAKAUT Mate AI",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                // Live status dot
                                val dotPulse by rememberInfiniteTransition(label = "dot")
                                    .animateFloat(
                                        initialValue = 0.5f, targetValue = 1f,
                                        animationSpec = infiniteRepeatable(
                                            tween(800), RepeatMode.Reverse
                                        ),
                                        label = "dotP"
                                    )
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isTyping) Color(0xFF3B82F6).copy(alpha = dotPulse)
                                            else Color(0xFF22C55E)
                                        )
                                )
                                AnimatedContent(
                                    targetState = isTyping,
                                    transitionSpec = {
                                        fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                                    },
                                    label = "statusText"
                                ) { thinking ->
                                    Text(
                                        text = if (thinking) "Thinking…" else "Online · Gemini 2.0",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF080F1E)
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
                        listOf(Color(0xFF080F1E), Color(0xFF030609))
                    )
                )
                .imePadding()
        ) {
            // Aurora blobs in background
            AuroraBackground(modifier = Modifier.fillMaxSize())

            Column(modifier = Modifier.fillMaxSize()) {

                // ── MESSAGE LIST ───────────────────────────────────────────
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Empty state
                    if (chatMessages.isEmpty() && !isTyping) {
                        item { EmptyStateHint() }
                    }

                    items(chatMessages, key = { it.hashCode() }) { msg ->
                        ChatBubbleAnimated(msg)
                    }

                    // Typing indicator
                    if (isTyping) {
                        item {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(tween(300)) + expandVertically(tween(300))
                            ) {
                                TypingBubble()
                            }
                        }
                    }
                }

                // ── INPUT BAR ──────────────────────────────────────────────
                val inputHasFocus by remember { mutableStateOf(false) }
                val borderAlpha by animateFloatAsState(
                    targetValue = if (message.isNotBlank()) 0.4f else 0.1f,
                    animationSpec = tween(300),
                    label = "borderAlpha"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    // Glow shadow behind input when active
                    if (message.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .align(Alignment.BottomCenter)
                                .offset(y = 4.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(Color(0xFF3B82F6).copy(alpha = 0.12f))
                                .blur(12.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0D1B32),
                        shape = RoundedCornerShape(26.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF3B82F6).copy(alpha = borderAlpha),
                                    Color(0xFF2DD4BF).copy(alpha = borderAlpha * 0.6f)
                                )
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = message,
                                onValueChange = { message = it },
                                placeholder = {
                                    Text(
                                        "Ask anything about MAKAUT…",
                                        color = Color.White.copy(alpha = 0.25f),
                                        fontSize = 14.sp
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(max = 120.dp),
                                maxLines = 4,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = Color(0xFF60A5FA),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White.copy(alpha = 0.9f)
                                )
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            SendButton(
                                enabled = message.isNotBlank(),
                                onClick = {
                                    if (message.isNotBlank()) {
                                        viewModel.sendMessage(message)
                                        message = ""
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EXTENSION  — clickable without ripple (for icon buttons needing custom press)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            onClick = onClick
        )
    )
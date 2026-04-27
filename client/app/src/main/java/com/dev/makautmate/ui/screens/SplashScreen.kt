package com.dev.makautmate.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.makautmate.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// COLOUR PALETTE
// ─────────────────────────────────────────────────────────────────────────────

private val BgDark        = Color(0xFF08101E)
private val BgCard        = Color(0xFF0F1E36)
private val AccentBlue    = Color(0xFF3B82F6)
private val AccentBlueLt  = Color(0xFF60A5FA)
private val TextPrimary   = Color(0xFFF0F4FF)
private val TextMuted     = Color(0x80949DC2)   // ~50% opacity
private val TextHint      = Color(0x33FFFFFF)   // ~20% opacity
private val BorderBlue    = Color(0x403B82F6)   // subtle blue border
private val GridLine      = Color(0x0A3B82F6)   // very faint grid

// ─────────────────────────────────────────────────────────────────────────────
// SUBTLE DOT/LINE GRID BACKGROUND
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SubtleGrid(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.drawBehind {
            val step = 40.dp.toPx()
            val cols = (size.width / step).toInt() + 1
            val rows = (size.height / step).toInt() + 1

            for (c in 0..cols) {
                drawLine(
                    color = GridLine,
                    start = Offset(c * step, 0f),
                    end = Offset(c * step, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            for (r in 0..rows) {
                drawLine(
                    color = GridLine,
                    start = Offset(0f, r * step),
                    end = Offset(size.width, r * step),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// LOGO ICON  — university peak mark inside a glass card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LogoCard(alpha: Float, offsetY: Float) {
    Box(
        modifier = Modifier
            .graphicsLayer { this.alpha = alpha; translationY = offsetY }
            .size(76.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(BgCard)
            .drawBehind {
                // Border
                drawRoundRect(
                    color = BorderBlue,
                    cornerRadius = CornerRadius(22.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Custom SVG-style peak icon drawn with Canvas
        Box(
            modifier = Modifier
                .size(36.dp)
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    val stroke = 2.dp.toPx()
                    val cap = StrokeCap.Round
                    val join = StrokeJoin.Round

                    // Peak / triangle outline (academic cap / mountain motif)
                    val peakPath = Path().apply {
                        moveTo(w * 0.17f, h * 0.75f)
                        lineTo(w * 0.50f, h * 0.19f)
                        lineTo(w * 0.83f, h * 0.75f)
                    }
                    drawPath(
                        path = peakPath,
                        color = AccentBlue,
                        style = Stroke(width = stroke, cap = cap, join = join)
                    )

                    // Crossbar
                    drawLine(
                        color = AccentBlueLt,
                        start = Offset(w * 0.28f, h * 0.61f),
                        end = Offset(w * 0.72f, h * 0.61f),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = cap
                    )

                    // Apex dot
                    drawCircle(
                        color = AccentBlue,
                        radius = 2.dp.toPx(),
                        center = Offset(w * 0.50f, h * 0.19f)
                    )

                    // Base dots
                    drawCircle(
                        color = AccentBlue.copy(alpha = 0.5f),
                        radius = 2.dp.toPx(),
                        center = Offset(w * 0.17f, h * 0.75f)
                    )
                    drawCircle(
                        color = AccentBlue.copy(alpha = 0.5f),
                        radius = 2.dp.toPx(),
                        center = Offset(w * 0.83f, h * 0.75f)
                    )
                }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STEP DOTS  — 3-dot progression indicator
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StepDots(alpha: Float) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.alpha(alpha)
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(AccentBlue)
        )
        repeat(2) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.3f))
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LOADING BAR  — thin 2 dp fill bar, no shimmer
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LoadingBar(progress: Float, alpha: Float) {
    Box(
        modifier = Modifier
            .alpha(alpha)
            .width(200.dp)
            .height(2.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.07f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(
                        listOf(AccentBlue, AccentBlueLt)
                    )
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MAIN SPLASH SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SplashScreen(
    onNext: (isLoggedIn: Boolean) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // ── State
    var logoAlpha       by remember { mutableStateOf(0f) }
    var logoOffsetY     by remember { mutableStateOf(10f) }
    var textAlpha       by remember { mutableStateOf(0f) }
    var textOffsetY     by remember { mutableStateOf(8f) }
    var taglineAlpha    by remember { mutableStateOf(0f) }
    var dotsAlpha       by remember { mutableStateOf(0f) }
    var barAlpha        by remember { mutableStateOf(0f) }
    var loadingProgress by remember { mutableStateOf(0f) }

    // ── Animated values
    val animLogoAlpha   by animateFloatAsState(logoAlpha,   tween(600),              label = "la")
    val animLogoY       by animateFloatAsState(logoOffsetY, tween(600, easing = FastOutSlowInEasing), label = "ly")
    val animTextAlpha   by animateFloatAsState(textAlpha,   tween(500, delayMillis = 100), label = "ta")
    val animTextY       by animateFloatAsState(textOffsetY, tween(500, easing = FastOutSlowInEasing), label = "ty")
    val animTagline     by animateFloatAsState(taglineAlpha, tween(500, delayMillis = 200), label = "tla")
    val animDots        by animateFloatAsState(dotsAlpha,   tween(400, delayMillis = 150), label = "da")
    val animBarAlpha    by animateFloatAsState(barAlpha,    tween(400, delayMillis = 300), label = "ba")
    val animProgress    by animateFloatAsState(
        targetValue = loadingProgress,
        animationSpec = tween(2000, easing = FastOutSlowInEasing),
        label = "prog"
    )

    // ── Sequence
    LaunchedEffect(Unit) {
        delay(200)
        logoAlpha = 1f; logoOffsetY = 0f

        delay(300)
        textAlpha = 1f; textOffsetY = 0f

        delay(200)
        taglineAlpha = 1f

        delay(150)
        dotsAlpha = 1f

        delay(100)
        barAlpha = 1f
        loadingProgress = 1f

        delay(2400)
        onNext(viewModel.isUserLoggedIn())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {

        // ── Layer 1: Subtle grid
        SubtleGrid(modifier = Modifier.fillMaxSize())

        // ── Layer 2: Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {

            LogoCard(alpha = animLogoAlpha, offsetY = animLogoY)

            Spacer(modifier = Modifier.height(28.dp))

            // App name
            Text(
                text = "MAKAUT Mate",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.3).sp,
                modifier = Modifier
                    .alpha(animTextAlpha)
                    .graphicsLayer { translationY = animTextY }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Your smart university companion",
                color = TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(animTagline)
            )

            Spacer(modifier = Modifier.height(52.dp))

            StepDots(alpha = animDots)

            Spacer(modifier = Modifier.height(20.dp))

            LoadingBar(progress = animProgress, alpha = animBarAlpha)

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Preparing your experience",
                color = TextHint,
                fontSize = 11.sp,
                letterSpacing = 0.6.sp,
                modifier = Modifier.alpha(animBarAlpha)
            )
        }
    }
}
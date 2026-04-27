package com.dev.makautmate.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── 1. Pencil Loader (For Splash) ──────────────────────────────────────────
@Composable
fun PencilLoader(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pencil")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 720f,
        animationSpec = infiniteRepeatable(
            tween(3000, easing = LinearEasing),
            RepeatMode.Restart
        ), label = "rotation"
    )

    val strokeDash by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(3000, easing = LinearEasing),
            RepeatMode.Restart
        ), label = "stroke"
    )

    Box(modifier = modifier.size(100.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.5f

            // Stroke path
            val path = Path().apply {
                addOval(Rect(centerOffset, radius))
            }
            
            val pathMeasure = android.graphics.PathMeasure(path.asAndroidPath(), false)
            val length = pathMeasure.length
            val dashOffset = length * (1f - strokeDash)
            
            drawPath(
                path = path,
                color = Color(0xFF1D4ED8),
                style = Stroke(
                    width = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(length, length), dashOffset)
                )
            )

            // Pencil body
            rotate(rotation, pivot = centerOffset) {
                // Simplified Pencil drawing
                drawRoundRect(
                    color = Color(0xFF3B82F6),
                    topLeft = Offset(centerOffset.x + radius - 10f, centerOffset.y - 30f),
                    size = Size(20f, 60f),
                    cornerRadius = CornerRadius(5f, 5f)
                )
                // Pencil tip
                val tipPath = Path().apply {
                    moveTo(centerOffset.x + radius, centerOffset.y - 45f)
                    lineTo(centerOffset.x + radius + 10f, centerOffset.y - 30f)
                    lineTo(centerOffset.x + radius - 10f, centerOffset.y - 30f)
                    close()
                }
                drawPath(tipPath, Color(0xFFFDE047))
            }
        }
    }
}

// ─── 2. Book Loader (For Notes PDF) ─────────────────────────────────────────
@Composable
fun BookLoader(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "book")
    
    val pageFlip by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = FastOutSlowInEasing),
            RepeatMode.Restart
        ), label = "pageFlip"
    )

    Box(
        modifier = modifier
            .width(60.dp)
            .height(45.dp)
            .background(
                Brush.linearGradient(listOf(Color(0xFF23C4F8), Color(0xFF275EFE))),
                RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            // Left fixed part
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White.copy(alpha = 0.3f)))
            // Flipping page
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        rotationY = -180f * pageFlip
                        cameraDistance = 12f * density
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    }
                    .background(Color.White.copy(alpha = 0.5f))
            )
            // Right fixed part
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White.copy(alpha = 0.3f)))
        }
    }
}

// ─── 3. Clean Spinner (For Login) ───────────────────────────────────────────
@Composable
fun CleanSpinner(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(24.dp),
        color = Color(0xFF3B82F6),
        strokeWidth = 3.dp
    )
}

// ─── 4. Skeleton Card (For Search) ──────────────────────────────────────────
@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1200, easing = LinearEasing),
            RepeatMode.Restart
        ), label = "shimmerTranslate"
    )

    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.05f),
        Color.White.copy(alpha = 0.15f),
        Color.White.copy(alpha = 0.05f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(0.6f).height(20.dp).background(brush, RoundedCornerShape(4.dp)))
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth(0.9f).height(14.dp).background(brush, RoundedCornerShape(4.dp)))
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth(0.4f).height(14.dp).background(brush, RoundedCornerShape(4.dp)))
    }
}

// ─── 5. Progress Circle (For Downloads) ─────────────────────────────────────
@Composable
fun DownloadProgress(progress: Float, modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(40.dp)) {
        CircularProgressIndicator(
            progress = { progress },
            color = Color(0xFF22C55E),
            strokeWidth = 4.dp,
            trackColor = Color.White.copy(alpha = 0.1f),
        )
    }
}

// ─── 6. Capybara Loader (For Videos) ─────────────────────────────────────────
@Composable
fun CapybaraLoader(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "capybara")
    val bodyMove by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "bodyMove"
    )
    val legRotation by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "legRotation"
    )
    val lineMove by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -200f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "lineMove"
    )

    val color = Color(0xFFCC7D2D)
    val color2 = Color(0xFF53381C)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(140.dp, 100.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Legs
                rotate(legRotation, pivot = Offset(40f + bodyMove, 75f)) {
                    drawRoundRect(color2, Offset(35f + bodyMove, 65f), Size(15f, 30f), CornerRadius(10f))
                }
                rotate(-legRotation, pivot = Offset(100f + bodyMove, 75f)) {
                    drawRoundRect(color2, Offset(95f + bodyMove, 65f), Size(15f, 30f), CornerRadius(10f))
                }

                // Main Body
                drawOval(
                    brush = Brush.linearGradient(listOf(color, color2), start = Offset(0f, 0f), end = Offset(size.width, size.height)),
                    topLeft = Offset(20f + bodyMove, 20f),
                    size = Size(100f, 60f)
                )

                // Head
                drawOval(
                    color = color,
                    topLeft = Offset(90f + bodyMove, 15f),
                    size = Size(45f, 45f)
                )
                // Eye
                drawCircle(color2, 4f, Offset(120f + bodyMove, 30f))
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Animated Line
        Canvas(modifier = Modifier.width(100.dp).height(4.dp)) {
            val dashLength = 20f
            val gapLength = 10f
            
            drawLine(
                color = color2,
                start = Offset(lineMove, 0f),
                end = Offset(lineMove + 500f, 0f),
                strokeWidth = 4f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
            )
        }
    }
}

// ─── 7. No Internet Loader ──────────────────────────────────────────────────
@Composable
fun NoInternetLoader(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "no_internet")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "scale"
    )

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                Icons.Rounded.WifiOff,
                contentDescription = null,
                modifier = Modifier.size(80.dp).graphicsLayer(scaleX = scale, scaleY = scale),
                tint = Color.White.copy(alpha = 0.2f)
            )
            Icon(
                Icons.Rounded.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFFEF4444)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("No Internet Connection", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Please check your network and try again", color = Color.Gray, fontSize = 14.sp)
    }
}

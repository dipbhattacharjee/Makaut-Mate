package com.dev.makautmate.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.dev.makautmate.domain.model.User
import com.dev.makautmate.ui.components.NoInternetLoader
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.ui.platform.LocalContext
import com.dev.makautmate.ui.viewmodel.AuthViewModel
import com.dev.makautmate.ui.viewmodel.PortalViewModel
import com.dev.makautmate.ui.components.ProfileCard

// ─── Colour tokens ────────────────────────────────────────────────────────────
private val BgDeep      = Color(0xFF05080C)
private val BgCard1     = Color(0xFF1C222D)   // Organizers
private val BgCard2     = Color(0xFF12161F)   // Books
private val BgCard3     = Color(0xFFFFFFFF)   // Contribute
private val BgCard4     = Color(0xFF0056D2)   // Upload
private val BgCard5     = Color(0xFFFFFFFF)   // Videos
private val BgCard6     = Color(0xFF1C222D)   // Notes
private val BgSyllabus  = Color(0xFFF1F5F9)   // Syllabus
private val BgPYQ       = Color(0xFFE2E8F0)   // PYQs
private val Blue        = Color(0xFF5B8CFF)
private val BlueMid     = Color(0xFF82B1FF)

// ─── Premium PNG Assets ───────────────────────────────────────────────────────
private const val IMG_OPEN_BOOK   = "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/72x72/1f4d6.png"
private const val IMG_BOOKS       = "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/72x72/1f4da.png"
private const val IMG_NOTES_DIR   = "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/72x72/1f4d2.png"
private const val IMG_ORGANISER   = "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/72x72/1f4d6.png"
private const val IMG_UPLOAD_CLD  = "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/72x72/2601.png"
private const val IMG_SYLLABUS_BAG = "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/72x72/1f392.png"
private const val IMG_YOUTUBE_LOGO = "https://cdn-icons-png.flaticon.com/512/1384/1384060.png"
private const val IMG_GITHUB_LOGO  = "https://cdn-icons-png.flaticon.com/512/25/25231.png"
private const val IMG_CALC         = "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/72x72/1f522.png"
private const val IMG_PYQ          = "https://cdn.jsdelivr.net/gh/twitter/twemoji@14.0.2/assets/72x72/1f4dd.png"

@Composable
fun HomeScreen(
    onNavigateToNotes: () -> Unit,
    onNavigateToPapers: () -> Unit,
    onNavigateToBooks: () -> Unit,
    onNavigateToVideos: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToCalculator: () -> Unit = {},
    onNavigateToOrganiser: () -> Unit = {},
    onNavigateToAskAI: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    portalViewModel: PortalViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isOnline = checkInternet(context)
    }

    if (!isOnline) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF05080C)),
            contentAlignment = Alignment.Center
        ) {
            NoInternetLoader()
        }
    } else {
        HomeScreenContent(
            onNavigateToNotes,
            onNavigateToPapers,
            onNavigateToBooks,
            onNavigateToVideos,
            onNavigateToUpload,
            onNavigateToSyllabus,
            onNavigateToSettings,
            onNavigateToDownloads,
            onNavigateToCalculator,
            onNavigateToOrganiser,
            onNavigateToAskAI,
            onNavigateToPremium,
            authViewModel,
            portalViewModel
        )
    }
}

private fun checkInternet(context: android.content.Context): Boolean {
    val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        else -> false
    }
}

@Composable
fun HomeScreenContent(
    onNavigateToNotes: () -> Unit,
    onNavigateToPapers: () -> Unit,
    onNavigateToBooks: () -> Unit,
    onNavigateToVideos: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToSyllabus: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToCalculator: () -> Unit = {},
    onNavigateToOrganiser: () -> Unit = {},
    onNavigateToAskAI: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    authViewModel: AuthViewModel,
    portalViewModel: PortalViewModel
) {
    var visible by remember { mutableStateOf(false) }
    val userProfile by authViewModel.currentUserProfile.collectAsState()
    val portalProfile by portalViewModel.studentProfile.collectAsState()
    val isSyncing by portalViewModel.isSyncing.collectAsState()
    val uriHandler = LocalUriHandler.current
    
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F2040), BgDeep)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            // Profile & Header
            item {
                Spacer(modifier = Modifier.height(52.dp))
                HomeHeader(
                    userProfile = userProfile,
                    onSettings = onNavigateToSettings,
                    onDownloads = onNavigateToDownloads
                )
            }

            // Hero Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(700)) + slideInVertically { 20 }
                ) {
                    HeroTextSection(userName = userProfile?.fullName?.split(" ")?.firstOrNull() ?: "there")
                }
            }

            // Student Portal Profile Card
            item {
                Spacer(modifier = Modifier.height(18.dp))
                ProfileCard(
                    profile = portalProfile,
                    isSyncing = isSyncing,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { portalViewModel.syncData() }
                )
            }

            // AI Bar Refined
            item {
                Spacer(modifier = Modifier.height(18.dp))
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(700, delayMillis = 150)) +
                            slideInVertically(tween(700, delayMillis = 150)) { 20 }
                ) {
                    AskAIBar(onClick = onNavigateToAskAI)
                }
            }

            // Bento Grid Redesign
            item {
                Spacer(modifier = Modifier.height(22.dp))
                BentoGrid(
                    visible = visible,
                    onNotes = onNavigateToNotes,
                    onBooks = onNavigateToBooks,
                    onVideos = onNavigateToVideos,
                    onUpload = onNavigateToUpload,
                    onOrganiser = onNavigateToOrganiser,
                    onContribute = { uriHandler.openUri("https://github.com/dipbhattacharjee/Makaut-Mate") }
                )
            }

            // Syllabus Big Bar
            item {
                Spacer(modifier = Modifier.height(18.dp))
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(700, delayMillis = 600)) +
                            slideInVertically(tween(700, delayMillis = 600)) { 20 }
                ) {
                    SyllabusFullBar(onClick = onNavigateToSyllabus)
                }
            }

            // PYQs & Calculator Section
            item {
                Spacer(modifier = Modifier.height(18.dp))
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(700, delayMillis = 750)) +
                            slideInVertically(tween(700, delayMillis = 750)) { 20 }
                ) {
                    BottomActionsSection(
                        onPYQs = onNavigateToPapers,
                        onCalculator = onNavigateToCalculator
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeader(
    userProfile: User?,
    onSettings: () -> Unit, 
    onDownloads: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Styled Profile Section
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .clickable { /* Profile Click */ },
            contentAlignment = Alignment.Center
        ) {
            if (userProfile != null) {
                AsyncImage(
                    model = if (userProfile.profilePicUrl.isNotEmpty()) userProfile.profilePicUrl 
                           else "https://ui-avatars.com/api/?name=${userProfile.fullName}&background=random&color=fff",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            // Online dot indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E))
                    .align(Alignment.BottomEnd)
                    .offset(x = (-2).dp, y = (-2).dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        CircleIconButton(Icons.Rounded.FileDownload, onClick = onDownloads)
        Spacer(modifier = Modifier.width(10.dp))
        CircleIconButton(Icons.Rounded.Settings, onClick = onSettings)
    }
}

@Composable
fun CircleIconButton(
    icon: ImageVector, 
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.07f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun HeroTextSection(userName: String) {
    Column {
        Text(
            text = "Hey $userName, explore various contents to",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                append("Strengthen your ")
                withStyle(SpanStyle(color = BlueMid)) { append("Exams") }
            },
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            lineHeight = 36.sp
        )
    }
}

@Composable
fun AskAIBar(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "aiBorderPulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f, targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ), label = "alpha"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .drawBehind {
                drawRoundRect(
                    color = BlueMid.copy(alpha = borderAlpha),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            },
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.05f),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Blue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "ASK AI ASSISTANT",
                    color = BlueMid.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    "Clear your doubts instantly...",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Icon(
                Icons.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun BentoGrid(
    visible: Boolean,
    onNotes: () -> Unit,
    onBooks: () -> Unit,
    onVideos: () -> Unit,
    onUpload: () -> Unit,
    onOrganiser: () -> Unit,
    onContribute: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // Left Column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StaggeredItem(visible, 0) {
                BentoCard(
                    title = "Organiser",
                    bg = BgCard1,
                    titleColor = Color.White,
                    imageUrl = IMG_ORGANISER,
                    height = 200.dp,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    onClick = onOrganiser
                )
            }
            StaggeredItem(visible, 2) {
                BentoCard(
                    title = "Books",
                    bg = BgCard2,
                    titleColor = Color.White,
                    imageUrl = IMG_BOOKS,
                    height = 100.dp,
                    onClick = onBooks
                )
            }
            StaggeredItem(visible, 4) {
                BentoCard(
                    title = "Contribute",
                    bg = BgCard3,
                    titleColor = Color(0xFF0F172A),
                    imageUrl = IMG_GITHUB_LOGO,
                    imageSize = 36.dp,
                    height = 100.dp,
                    onClick = onContribute
                )
            }
        }

        // Right Column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StaggeredItem(visible, 1) {
                BentoCard(
                    title = "Upload",
                    bg = BgCard4,
                    titleColor = Color.White,
                    imageUrl = IMG_UPLOAD_CLD,
                    height = 180.dp,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    onClick = onUpload
                )
            }
            StaggeredItem(visible, 3) {
                BentoCard(
                    title = "Videos",
                    bg = BgCard5,
                    titleColor = Color(0xFF0F172A),
                    imageUrl = IMG_YOUTUBE_LOGO,
                    imageSize = 36.dp,
                    height = 100.dp,
                    onClick = onVideos
                )
            }
            StaggeredItem(visible, 5) {
                BentoCard(
                    title = "Notes",
                    bg = BgCard6,
                    titleColor = Color.White,
                    imageUrl = IMG_NOTES_DIR,
                    height = 120.dp,
                    onClick = onNotes
                )
            }
        }
    }
}

@Composable
fun StaggeredItem(visible: Boolean, index: Int, content: @Composable () -> Unit) {
    val delay = 300 + index * 90
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600, delayMillis = delay)) +
                slideInVertically(tween(600, delayMillis = delay)) { 30 }
    ) { content() }
}

@Composable
fun BentoCard(
    title: String,
    bg: Color,
    titleColor: Color,
    height: Dp,
    imageUrl: String? = null,
    imageSize: Dp = if (height > 150.dp) 110.dp else 60.dp,
    border: BorderStroke? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale),
        shape = RoundedCornerShape(26.dp),
        color = bg,
        border = border,
        shadowElevation = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = title,
                color = titleColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(imageSize)
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .padding(bottom = 12.dp, end = 12.dp)
                )
            }
        }
    }
}

@Composable
fun SyllabusFullBar(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(26.dp),
        color = BgSyllabus
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    "Syllabus",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    "Check your latest curriculum",
                    color = Color(0xFF0F172A).copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
            AsyncImage(
                model = IMG_SYLLABUS_BAG,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun BottomActionsSection(onPYQs: () -> Unit, onCalculator: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // PYQs Card
        Surface(
            onClick = onPYQs,
            modifier = Modifier.weight(1.1f),
            shape = RoundedCornerShape(26.dp),
            color = BgPYQ
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Text(
                    "PYQs",
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                AsyncImage(
                    model = IMG_PYQ,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).align(Alignment.BottomEnd)
                )
            }
        }

        // CGPA Calculator Card
        Surface(
            onClick = onCalculator,
            modifier = Modifier.weight(0.9f),
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFFFFEDD5)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Text(
                    "CGPA",
                    color = Color(0xFF9A3412),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                AsyncImage(
                    model = IMG_CALC,
                    contentDescription = null,
                    modifier = Modifier.size(45.dp).align(Alignment.BottomEnd)
                )
            }
        }
    }
}

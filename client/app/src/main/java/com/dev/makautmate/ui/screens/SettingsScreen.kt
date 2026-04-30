package com.dev.makautmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.makautmate.ui.theme.BluePrimary

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToRateUs: () -> Unit,
    onNavigateToShare: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToContact: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    onLogout: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A1220), Color(0xFF05080C))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Settings", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Premium Card
            Surface(
                onClick = onNavigateToPremium,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF1F1F1)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE0B2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Diamond, contentDescription = null, tint = Color(0xFFFB8C00))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Go Premium", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Rounded.ArrowOutward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            "Unlock Everything. Access to all AI Models. Go Ads free.",
                            color = Color.Black.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notifications
            SettingsItem(
                icon = Icons.Rounded.NotificationsNone,
                title = "Notifications",
                subtitle = "Receive latest announcements from MAKAUT",
                hasSwitch = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Group 1
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.05f)
            ) {
                Column {
                    SettingsItemSimple(icon = Icons.Rounded.StarBorder, title = "Rate Us", onClick = onNavigateToRateUs)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 24.dp))
                    SettingsItemSimple(icon = Icons.Rounded.Share, title = "Share App", onClick = onNavigateToShare)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Group 2
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.05f)
            ) {
                Column {
                    SettingsItemSimple(icon = Icons.Rounded.LockOpen, title = "Privacy Policy", onClick = onNavigateToPrivacy)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 24.dp))
                    SettingsItemSimple(icon = Icons.Rounded.Description, title = "Terms and Conditions", onClick = onNavigateToTerms)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Group 3
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.05f)
            ) {
                Column {
                    SettingsItemSimple(icon = Icons.Rounded.MailOutline, title = "Contact", onClick = onNavigateToContact)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 24.dp))
                    SettingsItemSimple(icon = Icons.Rounded.ChatBubbleOutline, title = "Feedback", onClick = onNavigateToFeedback)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF2C1A1A),
                onClick = onLogout
            ) {
                SettingsItemSimple(
                    icon = Icons.AutoMirrored.Rounded.Logout,
                    title = "Log Out",
                    tint = Color.White,
                    onClick = onLogout
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "App version 1.0.0 ",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    hasSwitch: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            if (hasSwitch) {
                var checked by remember { mutableStateOf(true) }
                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = BluePrimary,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun SettingsItemSimple(
    icon: ImageVector,
    title: String,
    tint: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = tint, fontWeight = FontWeight.SemiBold)
        }
        Icon(Icons.Rounded.ArrowOutward, contentDescription = null, tint = tint.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
    }
}

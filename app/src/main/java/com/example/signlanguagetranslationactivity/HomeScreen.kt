package com.example.signlanguagetranslationactivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHomeScreen(onNavigate: (String) -> Unit = {}) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🤟 Sign Translator",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        onNavigate("home")
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        onNavigate("settings")
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        onNavigate("help")
                    },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Help") },
                    label = { Text("Help") }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFE8EAF6), Color(0xFF7986CB))
                    )
                )
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Hero Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome back!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )
                    Text(
                        text = "Translate between sign and speech in real time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }

                GifImage(
                    rawResId = R.drawable.animation,
                    modifier = Modifier.size(90.dp)
                )
            }

            // Cards
            FeatureCardModern(
                title = "🎤 Voice to ASL",
                subtitle = "Speak and see live sign animation",
                icon = Icons.Default.Mic,
                color = Color(0xFF64B5F6)
            ) {
                onNavigate("voice_to_asl")
            }

            FeatureCardModern(
                title = "⌨️ Text to ASL",
                subtitle = "Type and convert to signs",
                icon = Icons.Default.TextFields,
                color = Color(0xFF9575CD)
            ) {
                onNavigate("text_to_asl")
            }

            FeatureCardModern(
                title = "✋ ASL to Text",
                subtitle = "Show signs, get text back",
                icon = Icons.Default.Gesture,
                color = Color(0xFF4DB6AC)
            ) {
                onNavigate("sign_to_text")
            }
        }
    }
}

@Composable
fun FeatureCardModern(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.98f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.Black)
                Text(subtitle, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewModernHomeScreen() {
    MaterialTheme {
        ModernHomeScreen()
    }
}

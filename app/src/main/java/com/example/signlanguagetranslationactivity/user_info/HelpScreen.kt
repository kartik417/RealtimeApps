package com.example.signlanguagetranslationactivity.user_info

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
fun HelpScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Help & Guide",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5))
            )
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HelpCard(
                title = "🎤 Voice to ASL",
                description = "Tap on this feature and speak clearly. Your speech will be converted into sign language using animations."
            )

            HelpCard(
                title = "⌨️ Text to ASL",
                description = "Type the text you want to convert. The system will animate the corresponding signs."
            )

            HelpCard(
                title = "✋ ASL to Text",
                description = "Hold your hand gestures in front of the camera. The app will detect signs and convert them into text."
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Need more help?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A237E)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = "Email", tint = Color(0xFF3F51B5))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "support@signai.app",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color(0xFF3F51B5))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+91 12345 67890",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun HelpCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A237E))
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHelpScreen() {
    MaterialTheme {
        HelpScreen()
    }
}

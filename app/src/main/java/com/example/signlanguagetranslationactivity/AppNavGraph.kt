package com.example.signlanguagetranslationactivity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavGraph(startDestination: String = "home") {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("home") {
            ModernHomeScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("voice_to_asl") {
            VoiceToGifScreen()  // You already have this screen
        }
        composable("text_to_asl") {
            TextToASLScreen()
        }
        composable("sign_to_text") {
          CameraPermissionAndASLScreen()
        }

        composable("settings") {
            PlaceholderScreen("Settings")
        }
        composable("help") {
            PlaceholderScreen("Help")
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Coming Soon: $title", style = MaterialTheme.typography.headlineSmall)
    }
}

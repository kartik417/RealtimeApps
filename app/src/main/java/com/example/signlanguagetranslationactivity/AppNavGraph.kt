package com.example.signlanguagetranslationactivity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.signlanguagetranslationactivity.hand_gesture_detection.CameraPermissionWrapper
import com.example.signlanguagetranslationactivity.user_info.LoginScreen
import com.example.signlanguagetranslationactivity.user_info.SignupScreen

@Composable
fun AppNavGraph(startDestination: String = Screen.Home.route) {
    val navController = rememberNavController()

//    NavHost(navController = navController, startDestination = startDestination) {
//
//        composable(Screen.Login.route) {
//            LoginScreen(
//                onLoginSuccess = { navController.navigate(Screen.Home.route) },
//                onNavigateToSignup = { navController.navigate(Screen.Signup.route) }
//            )
//        }
//
//        composable(Screen.Signup.route) {
//            SignupScreen(
//                onSignupSuccess = { navController.navigate(Screen.Home.route) },
//                onNavigateToLogin = {
//                    navController.popBackStack(Screen.Login.route, inclusive = false)
//                }
//            )
//        }
        NavHost(navController = navController,startDestination = startDestination){


        composable(Screen.Home.route) {
            ModernHomeScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable(Screen.VoiceToASL.route) { MainScreen() }
        composable(Screen.TextToASL.route) { TextToASLScreen() }
        composable(Screen.SignToText.route) { CameraPermissionWrapper() }
        composable(Screen.Settings.route) { PlaceholderScreen("Settings") }
        composable(Screen.Help.route) { PlaceholderScreen("Help") }
    }
}


@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Coming Soon: $title", style = MaterialTheme.typography.headlineSmall)
    }
}

sealed class Screen(val route: String) {
//    object Login : Screen("login")
//    object Signup : Screen("signup")
    object Home : Screen("home")
    object VoiceToASL : Screen("voice_to_asl")
    object TextToASL : Screen("text_to_asl")
    object SignToText : Screen("sign_to_text")
    object Settings : Screen("settings")
    object Help : Screen("help")
}

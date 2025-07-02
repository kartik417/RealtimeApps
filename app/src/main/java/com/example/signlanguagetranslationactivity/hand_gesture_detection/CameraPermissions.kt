package com.example.signlanguagetranslationactivity.hand_gesture_detection



import android.Manifest  // ✅ Corrected: This replaces java.util.jar.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun CameraPermissionWrapper() {
    val context = LocalContext.current
    val permissionGranted = remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionGranted.value = granted
    }

    LaunchedEffect(Unit) {
        if (!permissionGranted.value) launcher.launch(Manifest.permission.CAMERA)
    }

    if (permissionGranted.value) {
        GestureDetectionScreen()
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission required")
        }
    }
}

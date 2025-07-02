package com.example.signlanguagetranslationactivity.hand_gesture_detection

import android.Manifest
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker




@Composable
fun GestureDetectionScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var gestureText by remember { mutableStateOf("Show your gesture") }
    var useFrontCamera by remember { mutableStateOf(false) }
    var handLandmarks by remember { mutableStateOf<List<Pair<Float, Float>>>(emptyList()) }

    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // HandLandmarker initialization
    val handLandmarker by remember {
        mutableStateOf(
            HandLandmarker.createFromOptions(
                context,
                HandLandmarker.HandLandmarkerOptions.builder()
                    .setBaseOptions(
                        BaseOptions.builder()
                            .setModelAssetPath("hand_landmarker.task")
                            .build()
                    )
                    .setRunningMode(RunningMode.IMAGE)
                    .setNumHands(1)
                    .build()
            )
        )
    }

    // Rebind camera when toggle changes
    LaunchedEffect(useFrontCamera) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val analyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    HandGestureAnalyzer(
                        handLandmarker = handLandmarker,
                        shouldMirror = useFrontCamera, // ✅ Pass flag
                        onGestureRecognized = { gestureText = it },
                        onLandmarksDetected = { landmarks -> handLandmarks = landmarks }
                    )
                )

            }

        val cameraSelector = if (useFrontCamera)
            CameraSelector.DEFAULT_FRONT_CAMERA
        else
            CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analyzer)
        } catch (e: Exception) {
            Log.e("CameraX", "Camera binding failed", e)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView }
        )

        // Overlay hand landmarks
        Canvas(modifier = Modifier.fillMaxSize()) {
            handLandmarks.forEach { (x, y) ->
                drawCircle(Color.Green, radius = 10f, center = Offset(x, y))
            }
        }

        // UI controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = gestureText,
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(16.dp),
                style = MaterialTheme.typography.h5
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { useFrontCamera = !useFrontCamera }) {
                Text(if (useFrontCamera) "Switch to Rear Camera" else "Switch to Front Camera")
            }
        }
    }
}

package com.example.signlanguagetranslationactivity

import android.graphics.PointF
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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

@Composable
fun CameraPermissionAndASLScreen() {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasPermission) {
        ASLHandTrackingScreen()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Camera permission is required to use this feature.")
        }
    }
}

@Composable
fun ASLHandTrackingScreen() {
    var detectedGesture by remember { mutableStateOf("Show your hand gesture") }
    var handLandmarks by remember { mutableStateOf<List<PointF>>(emptyList()) }
    var useFrontCamera by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreviewWithAnalyzer(
            useFrontCamera = useFrontCamera,
            onGestureRecognized = { gesture ->
                detectedGesture = gesture
            },
            onHandLandmarksDetected = { landmarks ->
                handLandmarks = landmarks
            }
        )

        // Draw landmarks overlay
        HandLandmarksOverlay(landmarks = handLandmarks)

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { useFrontCamera = !useFrontCamera },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text(
                    text = if (useFrontCamera) "Switch to Back Camera" else "Switch to Front Camera",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = detectedGesture,
                style = MaterialTheme.typography.h5,
                color = Color.White,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
fun CameraPreviewWithAnalyzer(
    useFrontCamera: Boolean,
    onGestureRecognized: (String) -> Unit,
    onHandLandmarksDetected: (List<PointF>) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    androidx.compose.runtime.key(useFrontCamera) {
        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            ContextCompat.getMainExecutor(ctx),
                            HandGestureAnalyzer(onGestureRecognized, onHandLandmarksDetected)
                        )
                    }

                val cameraSelector = if (useFrontCamera)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else
                    CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (exc: Exception) {
                    // Log or handle exceptions
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun HandLandmarksOverlay(landmarks: List<PointF>) {
    if (landmarks.isEmpty()) return

    // Overlay with transparent background, draw circles for each landmark
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Assuming landmarks are in the coordinate space of the camera preview
        // You might need to adjust scaling/mirroring based on your PreviewView config

        landmarks.forEach { point ->
            // Convert PointF (which has x, y in pixels) to Offset (Compose coordinates)
            // Here, we assume the points are normalized or scaled properly
            drawCircle(
                color = Color.Green,
                radius = 10f,
                center = Offset(point.x, point.y)
            )
        }
    }
}

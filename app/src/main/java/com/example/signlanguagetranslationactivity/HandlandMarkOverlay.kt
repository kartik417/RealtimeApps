package com.example.signlanguagetranslationactivity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import android.graphics.PointF

@Composable
fun HandLandmarksOverlay(
    landmarks: List<PointF>,
    modifier: Modifier = Modifier
) {
    // This draws circles on detected landmarks and lines between them

    Canvas(modifier = modifier) {
        // Map your landmark points from image coords to Canvas coords here if needed
        // Assuming points are normalized or already scaled (you may need to scale them based on preview size)

        // For demonstration, just draw points and lines connecting first 6 landmarks:
        // In practice, you want to connect joints in meaningful way (e.g., wrist -> elbow -> shoulder)

        val sizeX = size.width
        val sizeY = size.height

        // Here assuming landmarks have coordinates in image pixels, scale accordingly
        val scaledPoints = landmarks.map {
            Offset(
                x = it.x / 480f * sizeX,  // Assuming input image width 480px — adjust as per actual
                y = it.y / 640f * sizeY   // Assuming input image height 640px
            )
        }

        // Draw points
        scaledPoints.forEach { point ->
            drawCircle(
                color = Color.Cyan,
                radius = 10f,
                center = point
            )
        }

        // Draw lines between landmarks in pairs for demo
        for (i in 0 until scaledPoints.size - 1) {
            drawLine(
                color = Color.Green,
                start = scaledPoints[i],
                end = scaledPoints[i + 1],
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
        }
    }
}

package com.example.signlanguagetranslationactivity.hand_gesture_detection

import android.annotation.SuppressLint
import android.graphics.*
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.ByteArrayOutputStream

class HandGestureAnalyzer(
    private val handLandmarker: HandLandmarker,
    private val shouldMirror: Boolean,  // ✅ Add this
    private val onGestureRecognized: (String) -> Unit,
    private val onLandmarksDetected: (List<Pair<Float, Float>>) -> Unit
) : ImageAnalysis.Analyzer {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val bitmap = imageProxy.toBitmap(rotationDegrees)

        try {
            val mpImage = BitmapImageBuilder(bitmap).build()
            val result = handLandmarker.detect(mpImage)

            if (result.landmarks().isNotEmpty()) {
                val landmarkList = result.landmarks()[0]

                // ✅ Apply mirroring to X if needed
                val landmarkPoints = landmarkList.map {
                    val x = if (shouldMirror) 1f - it.x() else it.x()
                    val y = it.y()
                    Pair(x * bitmap.width, y * bitmap.height)
                }
                onLandmarksDetected(landmarkPoints)

                // ✅ Prepare normalized input for server (63 values: x, y, z)
                val flatList = landmarkList.flatMap {
                    val x = if (shouldMirror) 1f - it.x() else it.x()
                    listOf(x, it.y(), it.z())
                }

                coroutineScope.launch {
                    try {
                        val response = RetrofitInstance.api.predictGesture(LandmarkRequest(flatList))
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                val prediction = response.body()?.prediction ?: "Unknown"
                                onGestureRecognized(prediction)
                            } else {
                                onGestureRecognized("Server Error")
                            }
                        }
                    } catch (e: HttpException) {
                        withContext(Dispatchers.Main) {
                            onGestureRecognized("HTTP Error")
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onGestureRecognized("Error: ${e.message}")
                        }
                    }
                }
            } else {
                onLandmarksDetected(emptyList())
                onGestureRecognized("No Hand")
            }
        } catch (e: Exception) {
            Log.e("Analyzer", "Detection failed", e)
            onGestureRecognized("Detection Error")
        } finally {
            imageProxy.close()
        }
    }

    private fun ImageProxy.toBitmap(rotation: Int): Bitmap {
        val yBuffer = planes[0].buffer
        val vuBuffer = planes[2].buffer
        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()
        val nv21 = ByteArray(ySize + vuSize)
        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
        val imageBytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

package com.example.signlanguagetranslationactivity

import android.annotation.SuppressLint
import android.graphics.PointF
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlin.math.abs
import kotlin.math.sqrt

class HandGestureAnalyzer(
    private val onGestureRecognized: (String) -> Unit,
    private val onHandLandmarksDetected: (List<PointF>) -> Unit  // Callback for landmarks
) : ImageAnalysis.Analyzer {

    private val poseDetector = PoseDetection.getClient(
        PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
    )

    // For simple movement tracking (e.g., detecting "Thank You" forward motion)
    private var lastRightWristX: Float? = null

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return imageProxy.close()
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        poseDetector.process(image)
            .addOnSuccessListener { pose ->
                val gesture = detectHandPose(pose)
                onGestureRecognized(gesture)

                val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
                val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
                val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
                val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
                val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
                val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)

                if (listOf(leftWrist, rightWrist, leftElbow, rightElbow, leftShoulder, rightShoulder).all { it != null }) {
                    val landmarks = listOf(
                        leftWrist!!.position,
                        rightWrist!!.position,
                        leftElbow!!.position,
                        rightElbow!!.position,
                        leftShoulder!!.position,
                        rightShoulder!!.position
                    )
                    onHandLandmarksDetected(landmarks)
                } else {
                    onHandLandmarksDetected(emptyList())
                }
            }
            .addOnFailureListener { e ->
                Log.e("HandGestureAnalyzer", "Pose detection failed", e)
                onHandLandmarksDetected(emptyList())
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun detectHandPose(pose: Pose): String {
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)

        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)

        if (leftWrist == null || rightWrist == null || leftElbow == null || rightElbow == null || leftShoulder == null || rightShoulder == null) {
            return "No hands detected"
        }

        val dx = rightWrist.position3D.x - leftWrist.position3D.x
        val dy = rightWrist.position3D.y - leftWrist.position3D.y
        val dz = rightWrist.position3D.z - leftWrist.position3D.z
        val distance = sqrt((dx * dx + dy * dy + dz * dz).toDouble())

        // Extract key coordinates for readability
        val rWristX = rightWrist.position3D.x
        val rWristY = rightWrist.position3D.y
        val rWristZ = rightWrist.position3D.z
        val rShoulderY = rightShoulder.position3D.y
        val rShoulderX = rightShoulder.position3D.x
        val lShoulderX = leftShoulder.position3D.x

        val lWristY = leftWrist.position3D.y
        val lShoulderY = leftShoulder.position3D.y

        val lElbowX = leftElbow.position3D.x
        val rElbowX = rightElbow.position3D.x

        // 1. Open Arms and Hands Close Together - your existing logic
        if (distance > 400) return "Open Arms"
        if (distance < 100) return "Hands Close Together"

        // 2. Hands Raised
        if (lWristY < lShoulderY && rWristY < rShoulderY) return "Hands Raised"

        // 3. Left or Right Hand Raised
        if (lWristY < lShoulderY && rWristY > rShoulderY) return "Left Hand Raised"
        if (rWristY < rShoulderY && lWristY > lShoulderY) return "Right Hand Raised"

        // 4. Both Hands Down
        if (lWristY > leftElbow.position3D.y && rWristY > rightElbow.position3D.y) return "Both Hands Down"

        // 5. Clapping Approximation
        if (distance < 150 && abs(leftElbow.position3D.x - rightElbow.position3D.x) > 100) return "Clapping"

        // 6. Crossed Arms
        if (leftWrist.position3D.x > rShoulderX && rightWrist.position3D.x < lShoulderX) return "Crossed Arms"

        // 7. Pointing Left or Right
        if (leftWrist.position3D.x < lElbowX - 50) return "Pointing Left"
        if (rightWrist.position3D.x > rElbowX + 50) return "Pointing Right"

        // ==== New Gestures ====

        // 8. Thank You - right hand near chin level & moving forward
        // Assume chin is near right shoulder Y level for simplicity
        val chinLevelY = rShoulderY - 150 // Adjust this based on testing
        val wristNearChin = rWristY < chinLevelY + 100 && rWristY > chinLevelY - 100
        val movedForward = lastRightWristX?.let { prevX -> (prevX - rWristX) > 20 } ?: false // Movement along X axis

        lastRightWristX = rWristX

        if (wristNearChin && movedForward) {
            return "Thank You"
        }

        // 9. Hello - right hand near forehead (above right shoulder) waving (simplified by high wrist)
        if (rWristY < rShoulderY - 150) {
            return "Hello"
        }

        // 10. Yes - fist-like approx: hand close to chest and moving up/down (simplified)
        val handNearChest = rWristY > rShoulderY && rWristY < rShoulderY + 200
        // TODO: add movement up/down detection here using frame history

        if (handNearChest) {
            // Placeholder for yes detection
            return "Yes"
        }

        // 11. No - fingers pinching approx: wrists close but elbows apart
        if (distance < 150 && abs(leftElbow.position3D.x - rightElbow.position3D.x) > 150) {
            return "No"
        }

        return "Neutral"
    }
}

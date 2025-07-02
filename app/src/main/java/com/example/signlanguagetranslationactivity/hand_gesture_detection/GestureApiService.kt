package com.example.signlanguagetranslationactivity.hand_gesture_detection

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LandmarkRequest(val landmarks: List<Float>)
data class GestureResponse(val prediction: String)

interface GestureApiService {
    @POST("/predict")
    suspend fun predictGesture(@Body request: LandmarkRequest): Response<GestureResponse>
}

package com.example.signlanguagetranslationactivity.hand_gesture_detection

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.27.163.182:5000/"


    val api: GestureApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Replace with your PC's local IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GestureApiService::class.java)
    }
}

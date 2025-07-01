package com.example.signlanguagetranslationactivity

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000") // Replace with your IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}

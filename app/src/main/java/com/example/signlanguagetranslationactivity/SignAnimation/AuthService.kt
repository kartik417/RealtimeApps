package com.example.signlanguagetranslationactivity

import com.example.signlanguagetranslationactivity.user_info.UserRequest
import com.example.signlanguagetranslationactivity.user_info.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

interface AuthService {
    @POST("/api/auth/signup")
    fun signup(@Body request: UserRequest): Call<UserResponse>

    @POST("/api/auth/login")
    fun login(@Body request: UserRequest): Call<UserResponse>
}

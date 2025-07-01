package com.example.signlanguagetranslationactivity

data class UserResponse(
    val message: String? = null,
    val token: String? = null,
    val user: UserInfo? = null,
    val error: String? = null
)

data class UserInfo(
    val name: String,
    val email: String
)

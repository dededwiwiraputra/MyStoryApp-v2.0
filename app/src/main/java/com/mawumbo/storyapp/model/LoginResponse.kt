package com.mawumbo.storyapp.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val error: Boolean,
    val message: String,

    @SerializedName("loginResult")
    val user: User?
)

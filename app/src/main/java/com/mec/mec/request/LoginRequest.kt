package com.mec.mec.request

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val email: String,
    val password: String,
    val name: String // Add any other necessary fields for sign-up
)

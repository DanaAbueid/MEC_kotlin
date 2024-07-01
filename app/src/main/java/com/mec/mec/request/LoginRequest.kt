package com.mec.mec.request

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val role: String
)

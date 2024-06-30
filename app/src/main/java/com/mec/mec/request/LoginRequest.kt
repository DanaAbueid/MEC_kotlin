package com.mec.mec.request

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val role: String
)

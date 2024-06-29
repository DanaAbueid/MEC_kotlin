package com.mec.mec.model

import android.os.Parcelable
//import kotlinx.parcelize.Parcelize

data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val user_id: Int,
    val user_role: String
)

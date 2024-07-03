package com.mec.mec

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        private const val PREFS_NAME = "user_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
    }

    fun createLoginSession(accessToken: String, email: String, password: String, userId: Long, userRole: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_ACCESS_TOKEN, accessToken)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PASSWORD, password)
        editor.putLong(KEY_USER_ID, userId)
        editor.putString(KEY_USER_ROLE, userRole)
        editor.apply()
    }

    fun logout() {
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    fun getPassword(): String? {
        return prefs.getString(KEY_PASSWORD, null)
    }

    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1)
    }

    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }
}

package com.mec.mec.viewModel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mec.mec.api.RetrofitInstance
import com.mec.mec.model.AuthResponse
import com.mec.mec.request.LoginRequest
import com.mec.mec.request.SignUpRequest
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authResponse = MutableLiveData<AuthResponse>()
    val authResponse: LiveData<AuthResponse> get() = _authResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    var accessToken: String? = null
    var refreshToken: String? = null
    private val sharedPreferencesKey = "AppPrefs"
    private val userIdKey = "user_id"
    private lateinit var sharedPrefs: SharedPreferences
    var user_id: Long = -1

    fun initSharedPreferences(context: Context) {
        sharedPrefs = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        user_id = sharedPrefs.getLong(userIdKey, -1)
    }

    fun getUserId(): Long {
        return user_id
    }

    fun setUserId(context: Context, userId: Long) {
        sharedPrefs = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        this.user_id = userId
        sharedPrefs.edit().putLong(userIdKey, userId).apply()
    }

    fun signUp(signUpRequest: SignUpRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.signUp(signUpRequest)
                _authResponse.postValue(response)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.login(loginRequest)
                _authResponse.postValue(response)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}

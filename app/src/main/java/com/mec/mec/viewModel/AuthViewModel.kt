package com.mec.mec.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mec.mec.API.RetrofitInstance
import com.mec.mec.request.LoginRequest
import com.mec.mec.request.SignUpRequest
import com.mec.mec.model.AuthResponse
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _authResponse = MutableLiveData<AuthResponse>()
    val authResponse: LiveData<AuthResponse> get() = _authResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.login(loginRequest)
                _authResponse.postValue(response)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        val signUpRequest = SignUpRequest(email, password, name)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.signUp(signUpRequest)
                _authResponse.postValue(response)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}

package com.mec.mec.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mec.mec.api.RetrofitInstance
import com.mec.mec.database.AppDatabase
import com.mec.mec.database.User
import com.mec.mec.database.UserDao
import com.mec.mec.model.AuthResponse
import com.mec.mec.request.LoginRequest
import com.mec.mec.request.SignUpRequest
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _authResponse = MutableLiveData<AuthResponse>()
    private val userDao: UserDao = AppDatabase.getDatabase(application).userDao()
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn
    val authResponse: LiveData<AuthResponse> get() = _authResponse
    private lateinit var sharedPrefs: SharedPreferences
    var user_id: Long = -1

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
    private val sharedPreferencesKey = "AppPrefs"
    private val userIdKey = "user_id"

    var accessToken: String? = null
    var refreshToken: String? = null


    init {
        viewModelScope.launch {
            _isLoggedIn.value = checkIfLoggedIn()
        }
    }

    private suspend fun checkIfLoggedIn(): Boolean {
        return userDao.getUser() != null
    }


    fun saveUser(email: String, password: String, userRole: String) {
        viewModelScope.launch {
            val user = User(email, password, userRole)
            userDao.insertUser(user)
            _isLoggedIn.postValue(true)  // Update the login status
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            userDao.getUser()?.let { userDao.deleteUser(it) }
            _isLoggedIn.postValue(false)  // Update the login status
        }
    }

    suspend fun getUserRoleRoom(): String? {
        return userDao.getUser()?.userRole
    }



    fun signUp(signUpRequest: SignUpRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.signUp(signUpRequest)
                _authResponse.postValue(response)
                response?.let {

                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
    fun setUserId(context: Context, userId: Long) {
        sharedPrefs = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        this.user_id = userId
        sharedPrefs.edit().putLong(userIdKey, userId).apply()
    }

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.login(loginRequest)
                _authResponse.postValue(response)
                response?.let {
                    saveUser(loginRequest.email, loginRequest.password, it.user_role)
                    _isLoggedIn.postValue(true)
                  //  sessionManager.createLoginSession(it.access_token, loginRequest.email, loginRequest.password, it.user_id.toLong(), it.user_role)
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }


//    fun isLoggedIn(): Boolean {
//        return sessionManager.isLoggedIn()
//    }
//
//    fun getUserRole(): String? {
//        return sessionManager.getUserRole()
//    }
    fun initSharedPreferences(context: Context) {
        sharedPrefs = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        user_id = sharedPrefs.getLong(userIdKey, -1)
    }

    fun getUserIdForEmployee(): Long {
        return user_id
    }



}

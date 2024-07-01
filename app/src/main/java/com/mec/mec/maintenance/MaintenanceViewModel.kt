package com.mec.mec.maintenance
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mec.mec.api.RetrofitInstance
import com.mec.mec.model.Task
import kotlinx.coroutines.launch

class MaintenanceViewModel : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchTasks(url: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTasks(url)
                if (response.code() == 204) {
                    _tasks.postValue(emptyList())
                    _error.postValue("No content available.")
                } else if (response.isSuccessful) {
                    response.body()?.let {
                        _tasks.postValue(it)
                    } ?: run {
                        _tasks.postValue(emptyList())
                        _error.postValue("Failed to load tasks.")
                    }
                } else {
                    _tasks.postValue(emptyList())
                    _error.postValue("Failed to load tasks.")
                }
            } catch (e: Exception) {
                _tasks.postValue(emptyList())
                _error.postValue("Network error: ${e.message}")
            }
        }
    }
}

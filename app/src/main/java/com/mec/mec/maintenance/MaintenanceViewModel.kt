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

    fun fetchTasks(url: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTasks(url)
                if (response.isSuccessful) {
                    _tasks.postValue(response.body() ?: emptyList())
                } else if (response.code() == 204) {
                    _tasks.postValue(emptyList())

                } else {
                    _tasks.postValue(emptyList())
                    // Optionally log or handle other status codes here
                }
            } catch (e: Exception) {
                _tasks.postValue(emptyList())
                // Optionally log the error here
            }
        }
    }
}

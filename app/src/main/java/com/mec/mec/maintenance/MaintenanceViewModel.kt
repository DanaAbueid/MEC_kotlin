package com.mec.mec.maintenance
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mec.mec.ui.API.RetrofitInstance
import com.mec.mec.ui.model.Task
import kotlinx.coroutines.launch

class MaintenanceViewModel : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    fun fetchTasks(url: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTasks(url)
                _tasks.postValue(response)
            } catch (e: Exception) {
                // Handle the error, e.g., log the error or show a message to the user
            }
        }
    }
}

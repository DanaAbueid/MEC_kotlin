package com.mec.mec.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mec.mec.api.RetrofitInstance
import com.mec.mec.model.NewTaskRequest
import com.mec.mec.model.UpdateApproval
import com.mec.mec.model.UpdateNotes
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskViewModel : ViewModel() {
    private val _editDoneResponse = MutableLiveData<Boolean>()
    private val _result = MutableLiveData<Boolean>()
    val result: LiveData<Boolean> get() = _result
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
    val editDoneResponse: LiveData<Boolean> get() = _editDoneResponse

    private val _editManagerNoteResponse = MutableLiveData<Boolean>()
    val editManagerNoteResponse: LiveData<Boolean> get() = _editManagerNoteResponse

    private val _editEmployeeNoteResponse = MutableLiveData<Boolean>()
    val editEmployeeNoteResponse: LiveData<Boolean> get() = _editEmployeeNoteResponse

    private val _editTaskApprovalResponse = MutableLiveData<Boolean>()
    val editTaskApprovalResponse: LiveData<Boolean> get() = _editTaskApprovalResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val apiService = RetrofitInstance.api

    fun editTaskDone(taskID: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.editDone(taskID)
                if (response.isSuccessful) {
                    _editDoneResponse.postValue(true)
                } else {
                    _editDoneResponse.postValue(false)
                    _error.postValue("Failed to mark task as done.")
                }
            } catch (e: Exception) {
                _editDoneResponse.postValue(false)
                _error.postValue("Network error: ")
            }
        }
    }

    fun editManagerNote(updateNotes: UpdateNotes) {
        viewModelScope.launch {
            try {
                val response = apiService.editManagerNote(updateNotes)
                if (response.isSuccessful) {
                    _editManagerNoteResponse.postValue(true)
                } else {
                    _editManagerNoteResponse.postValue(false)
                    _error.postValue("Failed to update manager note.")
                }
            } catch (e: Exception) {
                _editManagerNoteResponse.postValue(false)
                _error.postValue("Network error:")
            }
        }
    }

    fun editEmployeeNote(updateNotes: UpdateNotes) {
        viewModelScope.launch {
            try {
                val response = apiService.editEmployeeNote(updateNotes)
                if (response.isSuccessful) {
                    _editEmployeeNoteResponse.postValue(true)
                } else {
                    _editEmployeeNoteResponse.postValue(false)
                    _error.postValue("Failed to update employee note.")
                }
            } catch (e: Exception) {
                _editEmployeeNoteResponse.postValue(false)
                _error.postValue("Network error")
            }
        }
    }
    fun addNewTask(newTaskRequest: NewTaskRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.addNewTask(newTaskRequest)
                _result.postValue(response.isSuccessful)
            } catch (e: Exception) {
                _result.postValue(false)
            }
        }
    }
    fun editTaskApproval(updateApproval: UpdateApproval) {
        viewModelScope.launch {
            try {
                val response = apiService.editTaskApproval(updateApproval)
                if (response.isSuccessful) {
                    _editTaskApprovalResponse.postValue(true)
                } else {
                    _editTaskApprovalResponse.postValue(false)
                    _error.postValue("Failed to update task approval.")
                }
            } catch (e: Exception) {
                _editTaskApprovalResponse.postValue(false)
                _error.postValue("Network error")
            }
        }
    }
}

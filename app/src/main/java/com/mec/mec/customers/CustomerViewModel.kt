package com.mec.mec.customers
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mec.mec.ui.API.RetrofitInstance
import kotlinx.coroutines.launch
import com.mec.mec.ui.model.Customer

class CustomerViewModel : ViewModel() {

    private val _customers = MutableLiveData<List<Customer>>()
    val customers: LiveData<List<Customer>> get() = _customers

    init {
        fetchCustomers()
    }

    private fun fetchCustomers() {
        viewModelScope.launch {
            try {
                val customers = RetrofitInstance.api.getCustomers()
                _customers.value = customers
            } catch (e: Exception) {
                // Handle the exception
            }
        }
    }

    fun searchCustomers(query: String) {
        _customers.value?.let {
            val filteredList = it.filter { customer ->
                customer.customerName.contains(query, ignoreCase = true)
            }
            _customers.value = filteredList
        }
    }
}

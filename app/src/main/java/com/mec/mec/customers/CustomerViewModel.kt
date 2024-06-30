package com.mec.mec.customers
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mec.mec.api.RetrofitInstance
import kotlinx.coroutines.launch
import com.mec.mec.model.Customer
import android.util.Log


class CustomerViewModel : ViewModel() {
    private val _customers = MutableLiveData<List<Customer>>()
    private val _deleteStatus = MutableLiveData<Boolean>()
    val deleteStatus: LiveData<Boolean> get() = _deleteStatus
    val customers: LiveData<List<Customer>> get() = _customers

    init {
        fetchCustomers()
    }

    private fun fetchCustomers() {
        viewModelScope.launch {
            try {
                val customers = RetrofitInstance.api.getCustomers()
                Log.d("CustomerViewModel", "Customers fetched successfully: $customers")
                _customers.postValue(customers)
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error fetching customers", e)
                e.printStackTrace()
            }
        }
    }

    fun editCustomer(name: String, location: String, elevatorType: String, panel: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.editCustomer(name, location, elevatorType, panel)
                if (response.isSuccessful) {
                    Log.d("CustomerViewModel", "Customer updated successfully")
                    fetchCustomers() // Refresh the list after update
                } else {
                    Log.e("CustomerViewModel", "Error updating customer: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error updating customer", e)
            }
        }
    }

    fun deleteCustomer(customerId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteCustomer(customerId)
                if (response.isSuccessful) {
                    _deleteStatus.postValue(true)
                    Log.d("CustomerViewModel", "Customer deleted successfully")
                } else {
                    _deleteStatus.postValue(false)
                    Log.e("CustomerViewModel", "Failed to delete customer: ${response.message()}")
                }
            } catch (e: Exception) {
                _deleteStatus.postValue(false)
                Log.e("CustomerViewModel", "Error deleting customer", e)
            }
        }
    }

    fun addCustomer(customer: Customer) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.addCustomer(customer)
                if (response.isSuccessful) {
                    // Handle successful addition if needed
                    Log.d("CustomerViewModel", "Customer added successfully: $customer")
                } else {
                    // Handle unsuccessful addition
                    Log.e("CustomerViewModel", "Failed to add customer: ${response.errorBody()}")
                    // Optionally, post an error state to LiveData
                }
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error adding customer", e)
                // Handle network errors or exceptions
            }
        }
    }
}


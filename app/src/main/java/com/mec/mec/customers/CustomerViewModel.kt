package com.mec.mec.customers
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mec.mec.API.RetrofitInstance
import kotlinx.coroutines.launch
import com.mec.mec.model.Customer
import android.util.Log


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
                Log.d("CustomerViewModel", "Customers fetched successfully: $customers")
                _customers.postValue(customers)
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Error fetching customers", e)
                e.printStackTrace()
            }
        }
    }
}

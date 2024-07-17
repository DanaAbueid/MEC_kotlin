package com.mec.mec.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mec.mec.databinding.FragmentAddCustomerBinding
import com.mec.mec.generic.BaseFragment
import com.mec.mec.model.Customer

class AddCustomerFragment : BaseFragment() {
    private var binding: FragmentAddCustomerBinding? = null
    private lateinit var customerViewModel: CustomerViewModel

    override fun isLoggedin() = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCustomerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.toolbar?.btnSelectLanguage?.visibility = View.GONE
        binding?.toolbar?.logoutBtn?.visibility = View.GONE
        customerViewModel = ViewModelProvider(this).get(CustomerViewModel::class.java)

        binding?.let { bindingNotNull ->

            bindingNotNull.buttonDelete.setOnClickListener {
                val id = -2L
                val name = bindingNotNull.customerName.text.toString()
                val location = bindingNotNull.customerLocation.text.toString()
                val elevatorType = bindingNotNull.customerElevatorType.text.toString()
                val panel = bindingNotNull.customerPanelElevator.text.toString()

                // Create a Customer object with the entered data
                val newCustomer = Customer(id,name, location, elevatorType, panel)

                // Call ViewModel to add the new customer
                customerViewModel.addCustomer(newCustomer)

                // Optionally, handle UI updates or navigation after adding
                findNavController().popBackStack()
            }

//            bindingNotNull.buttonCancel.setOnClickListener {
//                findNavController().popBackStack()
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

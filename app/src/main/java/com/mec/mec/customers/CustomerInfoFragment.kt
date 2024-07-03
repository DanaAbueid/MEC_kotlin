package com.mec.mec.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mec.mec.databinding.FragmentCustomerInfoBinding
import com.mec.mec.generic.BaseFragment
import com.mec.mec.model.Customer

class CustomerInfoFragment : BaseFragment() {

    override fun isLoggedin() = false

    private var _binding: FragmentCustomerInfoBinding? = null
    private lateinit var customerViewModel: CustomerViewModel
    private var customer: Customer? = null // Define customer as a property

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomerInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve customer from arguments
        arguments?.getParcelable<Customer>("customer")?.let { receivedCustomer ->
            customer = receivedCustomer
            bindTaskDetails(customer!!)
        }

        // Initialize customerViewModel
        customerViewModel = ViewModelProvider(this).get(CustomerViewModel::class.java)

        // Handle delete button click
        binding.buttonDelete.setOnClickListener {
            customer?.let { customer ->
                customerViewModel.deleteCustomer(customer.customerId)
            }
        }

        // Observe the deletion status
        customerViewModel.deleteStatus.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                findNavController().popBackStack()
                Toast.makeText(requireContext(), "Customer deleted successfully", Toast.LENGTH_SHORT).show()
                // Optionally, update your UI or reload customer list after deletion
            } else {
                // Handle deletion failure, e.g., show an error message
                Toast.makeText(requireContext(), "Failed to delete customer", Toast.LENGTH_SHORT).show()
            }
        })

        // Handle edit button click
        binding.buttonEdit.setOnClickListener {
            customer?.let {
                val name = binding.name.text.toString()
                val location = binding.location.text.toString()
                val elevatorType = binding.elevator.text.toString()
                val panel = binding.panel.text.toString()

                customerViewModel.editCustomer(name, location, elevatorType, panel)
            }
        }
    }

    private fun bindTaskDetails(customer: Customer) {
        binding.name.setText(customer.customerName)
        binding.location.setText(customer.customerLocation)
        binding.elevator.setText(customer.customerElevatorType)
        binding.panel.setText(customer.customerPanelElevator)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

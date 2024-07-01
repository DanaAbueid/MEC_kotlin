package com.mec.mec.customers
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mec.mec.R

import com.mec.mec.databinding.FragmentCustomerBinding
import com.mec.mec.employee.EmployeeListFragmentDirections
import com.mec.mec.generic.BaseFragment
import com.mec.mec.model.Customer


class CustomerFragment : BaseFragment() {
    private var binding: FragmentCustomerBinding? = null
    private lateinit var customerAdapter: CustomerAdapter
    private val viewModel: CustomerViewModel by viewModels()
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun isLoggedin() = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_customer_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        customerAdapter = CustomerAdapter(emptyList()) { customer ->
            navigateToCustomer(customer)
        }
        recyclerView.adapter = customerAdapter

        // Show the progress bar and hide RecyclerView while data is being loaded
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        viewModel.customers.observe(viewLifecycleOwner) { customers ->
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            if (customers.isEmpty()) {
                Toast.makeText(requireContext(), "No customers available.", Toast.LENGTH_SHORT).show()
            }

            customerAdapter.updateCustomers(customers)
            swipeRefreshLayout.isRefreshing = false // Stop the swipe-to-refresh loading animation
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchCustomers() // Fetch the data again
        }

        binding?.let { bindingNotNull ->
            bindingNotNull.fab.setOnClickListener {
                findNavController().navigate(CustomerFragmentDirections.actionCustomerFragmentToAddCustomerFragment())
            }
        }

        // Initial data fetch
        viewModel.fetchCustomers()
    }

    private fun navigateToCustomer(customer: Customer) {
        val action = CustomerFragmentDirections.actionCustomerFragmentToCustomerDetailFragment(customer)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

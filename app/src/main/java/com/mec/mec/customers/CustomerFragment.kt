package com.mec.mec.customers
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
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
import com.mec.mec.model.Task


class CustomerFragment : BaseFragment() {
    private var binding: FragmentCustomerBinding? = null
    private lateinit var customerAdapter: CustomerAdapter
    private val viewModel: CustomerViewModel by viewModels()
    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private var currentTasks: List<Customer> = emptyList()
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
        setupSearchFunctionality()
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
    private fun setupSearchFunctionality() {
        searchButton.setOnClickListener {
            performSearch()
        }

        // Optional: Trigger search on keyboard "Done" press
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Optional: Live search as user types
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Perform search with current text after a short delay (if needed)
                // performSearch()
            }
        })
    }

    private fun performSearch() {
        val query = searchEditText.text.toString().trim()

        if (query.isNotEmpty()) {
            // Filter tasks based on the search query
            val filteredTasks = currentTasks.filter { customer ->
                customer.customerName.contains(query, ignoreCase = true)
            }

            // Update RecyclerView with filtered tasks
            customerAdapter.updateCustomers(filteredTasks)
        } else {
            // If query is empty, show all tasks
            customerAdapter.updateCustomers(currentTasks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

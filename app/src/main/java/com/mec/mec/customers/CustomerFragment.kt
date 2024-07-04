package com.mec.mec.customers
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mec.mec.R

import com.mec.mec.databinding.FragmentCustomerBinding
import com.mec.mec.employee.EmployeeListFragmentDirections
import com.mec.mec.generic.BaseFragment
import com.mec.mec.model.Customer
import com.mec.mec.viewModel.AuthViewModel


class CustomerFragment : BaseFragment() {
    private var binding: FragmentCustomerBinding? = null
    private lateinit var customerAdapter: CustomerAdapter
    private lateinit var authViewModel: AuthViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var currentTasks: List<Customer> = emptyList()
    private val viewModel: CustomerViewModel by viewModels()

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
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        binding?.logoutBtn?.setOnClickListener {
            authViewModel.deleteUser()
            findNavController().navigate(CustomerFragmentDirections.actionCustomerFragmentToLogin())
        }



        progressBar = view.findViewById(R.id.progressBar)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_customer_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        customerAdapter = CustomerAdapter(emptyList()) { customer ->
            navigateToCustomer(customer)
        }
        recyclerView.adapter = customerAdapter

        // Initialize views from binding
        searchEditText = binding?.searchEditText ?: return
        searchButton = binding?.searchButton ?: return

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
        // Ensure searchButton and searchEditText are initialized
        searchButton.setOnClickListener {
            performSearch()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else {
                false
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Optional: Live search as user types
                // performSearch()
            }
        })
    }

    private fun performSearch() {
        val query = searchEditText.text.toString().trim()

        if (query.isNotEmpty()) {
            // Filter customers based on the search query
            val filteredCustomers = currentTasks.filter { customer ->
                customer.customerName.contains(query, ignoreCase = true)
            }

            // Update RecyclerView with filtered customers
            customerAdapter.updateCustomers(filteredCustomers)
        } else {
            // If query is empty, show all customers
            customerAdapter.updateCustomers(currentTasks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

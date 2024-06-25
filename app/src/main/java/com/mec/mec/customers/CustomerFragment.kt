package com.mec.mec.customers
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mec.mec.R
import com.mec.mec.customers.CustomerFragmentDirections

import com.mec.mec.databinding.FragmentCustomerBinding
import com.mec.mec.databinding.FragmentLoginBinding
import com.mec.mec.generic.BaseFragment
import com.mec.mec.ui.model.Customer


class CustomerFragment : BaseFragment() {
    private var binding: FragmentCustomerBinding? = null
    private lateinit var customerAdapter: CustomerAdapter
    private val viewModel: CustomerViewModel by viewModels()
    override fun isLoggedin(): Boolean = true
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

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_customer_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        customerAdapter = CustomerAdapter(emptyList()) { customer ->
            val action = CustomerFragmentDirections.actionCustomerFragmentToCustomerDetailFragment(/*customer*/)
            findNavController().navigate(action)
        }
        recyclerView.adapter = customerAdapter

        val searchEditText: EditText = view.findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchCustomers(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            findNavController().navigate(R.id.action_customerFragment_to_addCustomerFragment)
        }

        viewModel.customers.observe(viewLifecycleOwner, { customers ->
            customerAdapter.updateCustomers(customers)
        })
    }
}

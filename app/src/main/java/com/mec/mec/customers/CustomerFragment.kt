package com.mec.mec.customers
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mec.mec.R

import com.mec.mec.databinding.FragmentCustomerBinding
import com.mec.mec.generic.BaseFragment


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
                // Handle item click if necessary
            }
            recyclerView.adapter = customerAdapter

            viewModel.customers.observe(viewLifecycleOwner) { customers ->
                customerAdapter.updateCustomers(customers)
            }
        }
    }

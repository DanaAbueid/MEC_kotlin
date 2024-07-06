package com.mec.mec.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mec.mec.databinding.FragmentCustomerBinding
import com.mec.mec.databinding.FragmentCustomerTaskListBinding
import com.mec.mec.generic.BaseFragment

class CustomerTaskListFragment : BaseFragment() {
    override fun isLoggedin() = false
    private var binding: FragmentCustomerTaskListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerTaskListBinding.inflate(inflater, container, false)
        return binding?.root
    }
}
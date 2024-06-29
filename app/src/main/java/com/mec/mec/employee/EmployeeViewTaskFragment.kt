package com.mec.mec.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mec.mec.auth.LoginFragmentDirections
import com.mec.mec.databinding.FragmentEmployeeViewTaskBinding
import com.mec.mec.databinding.FragmentLoginBinding
import com.mec.mec.generic.BaseFragment

class EmployeeViewTaskFragment: BaseFragment() {
    override fun isLoggedin() = false
    private var binding: FragmentEmployeeViewTaskBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmployeeViewTaskBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let { bindingNotNull ->

            bindingNotNull.buttonEditTask.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignupUpUserFragment())
            }

        }
    }
}


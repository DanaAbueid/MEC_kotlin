package com.mec.mec.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mec.mec.databinding.FragmentAddTaskForManagerBinding
import com.mec.mec.databinding.FragmentCustomerInfoBinding
import com.mec.mec.databinding.FragmentEmployeeTaskDetailsBinding
import com.mec.mec.databinding.FragmentEmployeeTasksListBinding
import com.mec.mec.databinding.FragmentForgotPasswordBinding
import com.mec.mec.generic.BaseFragment

class ForgotPasswordFragment: BaseFragment() {
    override fun isLoggedin() = false
    private var binding: FragmentForgotPasswordBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let { bindingNotNull ->

            bindingNotNull.textView2.setOnClickListener {
                findNavController().popBackStack()
            }

        }
    }
}
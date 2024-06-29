package com.mec.mec.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mec.mec.auth.LoginFragmentDirections
import com.mec.mec.databinding.FragmentAddCustomerBinding
import com.mec.mec.databinding.FragmentEmployeeViewTaskBinding
import com.mec.mec.databinding.FragmentLoginBinding
import com.mec.mec.generic.BaseFragment

class AddCustomerFragment: BaseFragment() {
    override fun isLoggedin() = false
    private var binding: FragmentAddCustomerBinding? = null
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

        binding?.let { bindingNotNull ->

            bindingNotNull.buttonDelete.setOnClickListener {
                findNavController().popBackStack()
            }

        }
    }
}

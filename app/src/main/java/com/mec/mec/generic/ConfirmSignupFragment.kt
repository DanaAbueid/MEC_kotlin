package com.mec.mec.generic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mec.mec.databinding.FragmentConfirmSignupBinding
import com.mec.mec.databinding.FragmentSettingsBinding


class ConfirmSignupFragment: BaseFragment() {
    override fun isLoggedin() = false
    private var binding: FragmentConfirmSignupBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmSignupBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let { bindingNotNull ->

            bindingNotNull.btnConfirm.setOnClickListener {
                findNavController().navigate(ConfirmSignupFragmentDirections.actionConfirmToLogin())
            }

        }
    }
}

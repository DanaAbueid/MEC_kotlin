package com.mec.mec.generic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.mec.mec.databinding.FragmentSettingsBinding
import com.mec.mec.generic.BaseFragment

class SettingsFragment: BaseFragment() {
    override fun isLoggedin() = true
    private var binding: FragmentSettingsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let { bindingNotNull ->

            bindingNotNull.button2.setOnClickListener {
                findNavController().popBackStack()
            }

        }
    }
}

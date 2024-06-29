package com.mec.mec.employee


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mec.mec.databinding.FragmentEmployeeTasksListBinding
import com.mec.mec.generic.BaseFragment

class EmployeeTasksListFragment: BaseFragment() {
    override fun isLoggedin() = false
    private var binding: FragmentEmployeeTasksListBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmployeeTasksListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let { bindingNotNull ->

            bindingNotNull.progressBar.setOnClickListener {
                findNavController().popBackStack()
            }

        }
    }
}
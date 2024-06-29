package com.mec.mec.maintenance



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mec.mec.databinding.FragmentAddTaskForManagerBinding
import com.mec.mec.databinding.FragmentEmployeeTasksListBinding
import com.mec.mec.generic.BaseFragment

class AddTaskForManagerFragment: BaseFragment() {
    override fun isLoggedin() = false
    private var binding: FragmentAddTaskForManagerBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTaskForManagerBinding.inflate(inflater, container, false)
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
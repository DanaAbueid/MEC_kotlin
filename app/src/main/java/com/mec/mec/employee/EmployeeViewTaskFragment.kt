package com.mec.mec.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mec.mec.auth.LoginFragmentDirections
import com.mec.mec.databinding.FragmentEmployeeTaskDetailsBinding
import com.mec.mec.databinding.FragmentEmployeeViewTaskBinding
import com.mec.mec.databinding.FragmentLoginBinding
import com.mec.mec.generic.BaseFragment
import com.mec.mec.model.Task

class EmployeeViewTaskFragment: BaseFragment() {

    private var _binding: FragmentEmployeeViewTaskBinding? = null
    private val binding get() = _binding!!
    override fun isLoggedin() = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmployeeViewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Task>("task")?.let { task ->
            bindTaskDetails(task)
        }

        // Handle edit button click
        binding.buttonEditTask.setOnClickListener {
            // Implement edit task functionality here
        }
    }

    private fun bindTaskDetails(task: Task) {
        val dateTimeText = "${task.taskDate}  ${task.taskTime}"
        binding.textView.text = dateTimeText
        binding.subjectEditText.setText(task.subject)
        binding.detailsEditText.setText(task.details)
        binding.customerNameEditText.setText(task.customer.customerName)
        binding.employeeFirstNameEditText.setText(task.employeeNotes)
        //   binding.employeeLastNameEditText.setText(task.employeeLastName)
        binding.managerNoteEditText.setText(task.managerNotes)
        binding.approvalSwitch.isChecked = task.approved
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

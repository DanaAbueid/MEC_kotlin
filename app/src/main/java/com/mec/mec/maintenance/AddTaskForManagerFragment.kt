package com.mec.mec.maintenance



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mec.mec.databinding.FragmentAddTaskForManagerBinding
import com.mec.mec.databinding.FragmentEmployeeTasksListBinding
import com.mec.mec.generic.BaseFragment
import com.mec.mec.model.NewTaskRequest
import com.mec.mec.viewModel.TaskViewModel

class AddTaskForManagerFragment : BaseFragment() {
    private var binding: FragmentAddTaskForManagerBinding? = null
    private val viewModel: TaskViewModel by viewModels()

    override fun isLoggedin() = false

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

            bindingNotNull.btnAdd.setOnClickListener {
                addNewTask()
            }
        }

        viewModel.result.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Task added successfully", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()

            } else {
                Toast.makeText(requireContext(), "Failed to add task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addNewTask() {
        val taskDate = viewModel.getCurrentDate()
        val taskTime = viewModel.getCurrentTime()
        val subject = binding?.etSubject?.text.toString()
        val details = binding?.etDetails?.text.toString()
        val employeeNotes = binding?.etEmployeeNotes?.text.toString()
        val managerNotes = binding?.etManagerNotes?.text.toString()
        val approved = binding?.approvalSwitch?.isChecked ?: false
        val customerName = binding?.etCustomerName?.text.toString()
        val employeeFirstName = binding?.etEmployeeFirstName?.text.toString()
        val employeeLastName = binding?.etEmployeeLastName?.text.toString()
        val done = false

        val newTaskRequest = NewTaskRequest(
            taskDate,
            taskTime,
            subject,
            details,
            employeeNotes,
            managerNotes,
            approved,
            customerName,
            employeeFirstName,
            employeeLastName,
            done
        )

        viewModel.addNewTask(newTaskRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

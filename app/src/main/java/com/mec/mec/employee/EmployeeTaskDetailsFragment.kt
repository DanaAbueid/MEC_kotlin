package com.mec.mec.employee
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.mec.mec.databinding.FragmentEmployeeTaskDetailsBinding
import com.mec.mec.generic.BaseFragment
import com.mec.mec.model.Task
import com.mec.mec.model.UpdateApproval
import com.mec.mec.model.UpdateNotes
import com.mec.mec.viewModel.TaskViewModel

class EmployeeTaskDetailsFragment : BaseFragment() {

    private var _binding: FragmentEmployeeTaskDetailsBinding? = null
    private val taskViewModel: TaskViewModel by viewModels()

    private val binding get() = _binding!!
    override fun isLoggedin() = false
    var managerNote = ""
    var note = ""
    var taskID = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmployeeTaskDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Task>("task")?.let { task ->
            bindTaskDetails(task)
        }
        note = binding.employeeFirstNameEditText.text.toString()
        managerNote = binding.managerNoteEditText.text.toString()
        binding.buttonEditTask.setOnClickListener {
            // Read current values from EditText fields
            val currentNote = binding.employeeFirstNameEditText.text.toString()
            val currentManagerNote = binding.managerNoteEditText.text.toString()

            // Update the notes using ViewModel methods
            taskViewModel.editEmployeeNote(UpdateNotes(taskID, currentNote))
            taskViewModel.editManagerNote(UpdateNotes(taskID, currentManagerNote))
            taskViewModel.editTaskApproval(UpdateApproval(taskID, binding.approvalSwitch.isChecked))
        }


        taskViewModel.editManagerNoteResponse.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Task manager note updated successfully.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to mark task as done.", Toast.LENGTH_SHORT).show()
            }
        }

        taskViewModel.editEmployeeNoteResponse.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Task employee note updated successfully.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to mark task as done.", Toast.LENGTH_SHORT).show()
            }
        }

        taskViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindTaskDetails(task: Task) {
        val dateTimeText = "${task.taskDate}  ${task.taskTime}"
        binding.tvShowDate.text = dateTimeText
        binding.subjectEditText.setText(task.subject)
        binding.detailsEditText.setText(task.details)
        binding.customerNameEditText.setText(task.customer.customerName)
        binding.employeeFirstNameEditText.setText(task.employeeNotes)
     //   binding.employeeLastNameEditText.setText(task.employeeLastName)
        binding.managerNoteEditText.setText(task.managerNotes)
        binding.approvalSwitch.isChecked = task.approved
        taskID = task.taskId

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.mec.mec.maintenance



import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mec.mec.R
import com.mec.mec.api.ApiService
import com.mec.mec.api.RetrofitInstance
import com.mec.mec.customers.CustomerViewModel
import com.mec.mec.database.AppDatabase
import com.mec.mec.database.DatabaseProvider
import com.mec.mec.database.EmployeeDao
import com.mec.mec.databinding.FragmentAddTaskForManagerBinding
import com.mec.mec.databinding.FragmentEmployeeTasksListBinding
import com.mec.mec.generic.BaseFragment
import com.mec.mec.model.Customer
import com.mec.mec.model.Employee
import com.mec.mec.model.NewTaskRequest
import com.mec.mec.viewModel.TaskViewModel
import kotlinx.coroutines.launch

class AddTaskForManagerFragment : BaseFragment() {
    private var binding: FragmentAddTaskForManagerBinding? = null
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var customerViewModel: CustomerViewModel
    private var allCustomers: List<Customer> = emptyList()
    private var allEmployees: List<Employee> = emptyList()

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

        customerViewModel = ViewModelProvider(this).get(CustomerViewModel::class.java)

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

        // Observe the LiveData from CustomerViewModel
        customerViewModel.customers.observe(viewLifecycleOwner) { customers ->
            allCustomers = customers
            setupCustomerAdapter()
        }

        // Fetch employees from the API
        fetchEmployees()

        setupTextWatchers()
    }

    private fun fetchEmployees() {
        lifecycleScope.launch {
            try {
                allEmployees = RetrofitInstance.api.getUserBasicInfo()
                setupEmployeeAdapter()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to fetch employees", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCustomerAdapter() {
        val customerNames = allCustomers.map { it.customerName }
        val customerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, customerNames)
        binding?.etCustomerName?.setAdapter(customerAdapter)
    }

    private fun setupEmployeeAdapter() {
        val employeeFirstNames = allEmployees.map { it.firstName }
        val employeeLastNames = allEmployees.map { it.lastName }

        val firstNameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, employeeFirstNames)
        val lastNameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, employeeLastNames)

        binding?.etEmployeeFirstName?.setAdapter(firstNameAdapter)
        binding?.etEmployeeLastName?.setAdapter(lastNameAdapter)
    }


    private fun setupTextWatchers() {
        binding?.etCustomerName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val customerName = s.toString().trim()
                if (customerName.isNotEmpty()) {
                    val customer = allCustomers.find { it.customerName.equals(customerName, true) }
                    if (customer != null) {
                        binding?.tilCustomerName?.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.green)
                    } else {
                        binding?.tilCustomerName?.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.red)
                    }
                } else {
                    binding?.tilCustomerName?.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.semiGray)
                }
            }
        })

        binding?.etEmployeeFirstName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val firstName = s.toString().trim()
                val lastName = binding?.etEmployeeLastName?.text.toString().trim()

                if (firstName.isNotEmpty()) {
                    val filteredLastNames = allEmployees
                        .filter { it.firstName.equals(firstName, ignoreCase = true) }
                        .map { it.lastName }
                        .distinct()

                    val lastNameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, filteredLastNames)
                    binding?.etEmployeeLastName?.setAdapter(lastNameAdapter)

                    if (lastName.isNotEmpty() && !filteredLastNames.contains(lastName)) {
                        binding?.tilEmployeeLastName?.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.red)
                    } else {
                        binding?.tilEmployeeLastName?.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.green)
                    }
                } else {
                    binding?.etEmployeeLastName?.setAdapter(null)  // Clear adapter when first name is empty
                    binding?.tilEmployeeFirstName?.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.semiGray)
                    binding?.tilEmployeeLastName?.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.semiGray)
                }
            }
        })
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


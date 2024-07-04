package com.mec.mec.employee


import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mec.mec.customers.CustomerFragmentDirections
import com.mec.mec.databinding.FragmentEmployeeTasksBinding
import com.mec.mec.databinding.FragmentEmployeeTasksListBinding
import com.mec.mec.generic.BaseFragment
import com.mec.mec.maintenance.MaintenanceViewModel
import com.mec.mec.maintenance.TaskAdapter
import com.mec.mec.model.Task
import com.mec.mec.viewModel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EmployeeTasksListFragment: BaseFragment() {
    override fun isLoggedin() = false
    private var binding: FragmentEmployeeTasksListBinding? = null
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private var userID: Long? = -1L
    private val viewModel: MaintenanceViewModel by viewModels()
    private var currentTasks: List<Task> = emptyList()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sharedPreferences: SharedPreferences


    companion object {
        private const val ARG_USER_ID = "userId"

        fun newInstance(userID: Long): EmployeeTasksFragment {
            val fragment = EmployeeTasksFragment()
            val args = Bundle()
            args.putLong(ARG_USER_ID, userID)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmployeeTasksListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        binding?.logoutBtn?.setOnClickListener {
            authViewModel.deleteUser()
            findNavController().navigate(EmployeeTasksListFragmentDirections.actionEmployeetasksFragmentToLogin())
        }

        sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        authViewModel.initSharedPreferences(requireContext())
        userID = authViewModel.getUserIdForEmployee()

        if (userID == -1L) {
            Toast.makeText(requireContext(), "User ID not provided.", Toast.LENGTH_SHORT).show()
            return
        }
        setupViews()
        setupRecyclerView()
        setupSearchFunctionality()
        setupDateButtonListener()
        setupTabLayoutListener()

        // Initial fetch based on the default selected tab
        fetchDataForTab(binding?.tabLayout2?.selectedTabPosition ?: 0)
    }

    private fun setupViews() {
        progressBar = binding?.progressBar ?: return
        searchEditText = binding?.searchEditText ?: return
        searchButton = binding?.searchButton ?: return
    }

    private fun setupRecyclerView() {
        binding?.rvMaintenanceList?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            taskAdapter = TaskAdapter(emptyList()) { task ->
                navigateToTaskDetails(task)
            }
            adapter = taskAdapter
        }
    }

    private fun setupSearchFunctionality() {
        searchButton.setOnClickListener {
            performSearch()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun setupDateButtonListener() {
        binding?.dateButton?.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun setupTabLayoutListener() {
        binding?.tabLayout2?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    fetchDataForTab(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    ///// todo check the api and pass an object to the next fragment
    private fun fetchDataForTab(tabPosition: Int) {
        val url = when (tabPosition) {
            0 -> {
                "http://34.234.65.167:8080/api/v1/employeesManagement/task/employee/$userID"
            }
            1 -> {
                "http://34.234.65.167:8080/api/v1/employee/allTodayTasks/$userID"
            }
            2 -> {
                "http://34.234.65.167:8080/api/v1/employeesManagement/task/approval/true/$userID"
            }
            3 -> {
                "http://34.234.65.167:8080/api/v1/employeesManagement/task/approval/false/$userID"
            }
            4 -> {
                "http://34.234.65.167:8080/api/v1/employee/allTasksDependsOfEmployeeDone/$userID/true"
            }
            else -> throw IllegalStateException("Unexpected tab position $tabPosition")
        }

        progressBar.visibility = View.VISIBLE
        viewModel.fetchTasks(url)

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            currentTasks = tasks
            progressBar.visibility = View.GONE
            taskAdapter.updateTasks(tasks)
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = "${year}-${month + 1}-${dayOfMonth}"
                updateUrlWithDate(selectedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun updateUrlWithDate(selectedDate: String) {
        val tabPosition = binding?.tabLayout2?.selectedTabPosition ?: 0
        val url = when (tabPosition) {
            0, 1 -> {
                "http://34.234.65.167:8080/api/v1/employeesManagement/task/date/$selectedDate/$userID"
            }
            2 -> {
                "http://34.234.65.167:8080/api/v1/employeesManagement/task/approval/true/$userID"
            }
            3 -> {
                "http://34.234.65.167:8080/api/v1/employeesManagement/task/approval/false/$userID"
            }
            4 -> {
                "http://34.234.65.167:8080/api/v1/employee/allTasksDependsOfEmployeeDoneAndDate/$userID/true/$selectedDate"
            }
            else -> throw IllegalStateException("Unexpected tab position $tabPosition")
        }

        fetchDataForTab(tabPosition)
    }

    private fun performSearch() {
        val query = searchEditText.text.toString().trim()

        if (query.isNotEmpty()) {
            val filteredTasks = currentTasks.filter { task ->
                task.subject.contains(query, ignoreCase = true) ||
                        task.details.contains(query, ignoreCase = true) ||
                        task.customer.customerName.contains(query, ignoreCase = true)
            }
            taskAdapter.updateTasks(filteredTasks)
        } else {
            taskAdapter.updateTasks(currentTasks)
        }
    }

    private fun navigateToTaskDetails(task: Task) {
        val action = EmployeeTasksListFragmentDirections.actionTasklistToItem(task)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

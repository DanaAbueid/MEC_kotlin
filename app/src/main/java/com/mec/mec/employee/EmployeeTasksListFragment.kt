package com.mec.mec.employee

import android.util.Log
import android.app.DatePickerDialog
import android.content.ContentValues
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
import com.mec.mec.R
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

class EmployeeTasksListFragment : BaseFragment() {

    override fun isLoggedin() = false

    private var binding: FragmentEmployeeTasksListBinding? = null
    private lateinit var taskAdapter: TaskAdapter
    private var progressBar: ProgressBar? = null
    private var searchEditText: EditText? = null
    private var searchButton: ImageButton? = null
    private var userID: Long? = -1L
    private val viewModel: MaintenanceViewModel by viewModels()
    private var currentTasks: List<Task> = emptyList()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var isDateSelected = false // Track if a date filter is currently active
    private var selectedDate: String? = null // Track the selected date for filtering

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
        binding?.toolbar?.btnSelectLanguage?.visibility = View.GONE
        binding?.toolbar?.logoutBtn?.visibility = View.GONE
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        binding?.button4?.setOnClickListener {
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
        progressBar = binding?.progressBar
        searchEditText = binding?.searchEditText
        searchButton = binding?.searchButton
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
        searchButton?.setOnClickListener {
            performSearch()
        }

        searchEditText?.setOnEditorActionListener { _, actionId, _ ->
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
            toggleDateFilter()
        }
    }

    private fun toggleDateFilter() {
        if (isDateSelected) {
            // Reset background image and clear selected date
            binding?.dateButton?.setBackgroundResource(R.drawable.f7__calendar_today)
            selectedDate = null
            isDateSelected = false
        } else {
            // Change background image and show date picker dialog
            binding?.dateButton?.setBackgroundResource(R.drawable.ic__twotone_refresh)
            showDatePickerDialog()
            isDateSelected = true
        }
        // Reload tasks based on tab selection
        fetchDataForTab(binding?.tabLayout2?.selectedTabPosition ?: 0)
    }

    private fun setupTabLayoutListener() {
        binding?.tabLayout2?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    if (it.position == 0 || it.position == 2 || it.position == 3 || it.position == 4) {
                        // Hide date button for certain tabs
                        binding?.dateButton?.setBackgroundResource(R.drawable.white_button)
                    } else {
                        // Show date button for other tabs
                        binding?.dateButton?.setBackgroundResource(R.drawable.f7__calendar_today)
                    }
                    fetchDataForTab(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("en"))
        return sdf.format(Date())
    }

    private fun convertArabicDateToEnglish(arabicDate: String): String {
        val arabicFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
        val englishFormat = SimpleDateFormat("yyyy-MM-dd", Locale("en"))

        val date = arabicFormat.parse(arabicDate)
        return englishFormat.format(date)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDateArabic = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                selectedDate = convertArabicDateToEnglish(selectedDateArabic)
                updateUrlWithDate(selectedDate!!)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun fetchDataForTab(tabPosition: Int) {
        taskAdapter.updateTasks(emptyList())
        binding?.rvMaintenanceList?.visibility = View.GONE

        val url = when (tabPosition) {
            0 -> {
                "http://34.234.65.167:8080/api/v1/employeesManagement/task/employeeUpdated/$userID"

            }

            1 -> {
                selectedDate?.let { date ->
                    "http://34.234.65.167:8080/api/v1/employeesManagement/task/dateUpdated/$date/$userID"
                } ?: run {
                    val currentDate = getCurrentDate()
                    "http://34.234.65.167:8080/api/v1/employeesManagement/task/dateUpdated/$currentDate/$userID"
                }
            }
            2 -> {
                "http://34.234.65.167:8080/api/v1/employeesManagement/task/approvalUpdated/true/$userID"
            }
            3 -> {
                "http://34.234.65.167:8080/api/v1/employeesManagement/task/approvalUpdated/false/$userID"
            }
            4 -> {
                "http://34.234.65.167:8080/api/v1/employee/allTasksOfEmployeeDoneUpdated/$userID/true"
            }
            else -> throw IllegalStateException("Unexpected tab position $tabPosition")
        }

        progressBar?.visibility = View.VISIBLE

        viewModel.fetchTasks(url)

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            progressBar?.visibility = View.GONE
            if (tasks.isEmpty()) {
                binding?.rvMaintenanceList?.visibility = View.GONE
                Toast.makeText(requireContext(), "No tasks available.", Toast.LENGTH_SHORT).show()
            } else {
                currentTasks = tasks
                taskAdapter.updateTasks(tasks)
                binding?.rvMaintenanceList?.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            progressBar?.visibility = View.GONE
        }
    }

    private fun updateUrlWithDate(selectedDate: String) {
        val tabPosition = binding?.tabLayout2?.selectedTabPosition ?: 0
        fetchDataForTab(tabPosition)
    }

    private fun performSearch() {
        val query = searchEditText?.text.toString().trim()

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
        searchEditText?.setOnEditorActionListener(null)
        searchEditText?.removeTextChangedListener(null)
        searchButton?.setOnClickListener(null)
        progressBar = null
        searchEditText = null
        searchButton = null
    }
}

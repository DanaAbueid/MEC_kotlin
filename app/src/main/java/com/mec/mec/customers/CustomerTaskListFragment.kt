package com.mec.mec.customers
import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mec.mec.R
import com.mec.mec.databinding.FragmentCustomerTaskListBinding
import com.mec.mec.generic.BaseFragment
import com.mec.mec.maintenance.MaintenanceViewModel
import com.mec.mec.maintenance.TaskAdapter
import com.mec.mec.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CustomerTaskListFragment : BaseFragment() {

    private var binding: FragmentCustomerTaskListBinding? = null
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private var customerID: Long = -1L
    private val viewModel: MaintenanceViewModel by viewModels()
    private var currentTasks: List<Task> = emptyList()
    private var selectedDate: String? = null

    companion object {
        private const val ARG_CUSTOMER_ID = "customerId"
        fun newInstance(customerId: Long): CustomerTaskListFragment {
            val fragment = CustomerTaskListFragment()
            val args = Bundle()
            args.putLong(ARG_CUSTOMER_ID, customerId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun isLoggedin() = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerTaskListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.toolbar?.btnSelectLanguage?.visibility = View.GONE
        binding?.toolbar?.logoutBtn?.visibility = View.GONE

        customerID = arguments?.getLong(ARG_CUSTOMER_ID) ?: -1L

        if (customerID == -1L) {
            Toast.makeText(requireContext(), "Customer ID not provided.", Toast.LENGTH_SHORT).show()
            return
        }

        setupViews()
        setupRecyclerView()
        setupTabLayout()
        setupSearchFunctionality()
        setupDateButtonListener()

        // Fetch tasks
        fetchTasks()
    }

    private fun setupViews() {
        binding?.apply {
            // Access views directly from binding
            progressBar.visibility = View.VISIBLE
            searchEditText // do something with it
            searchButton // do something with it
        }
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

    private fun setupTabLayout() {
        binding?.tabLayout2?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                filterTasksBasedOnTabAndDate(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun filterTasksBasedOnTabAndDate(tabPosition: Int) {
        var filteredTasks = currentTasks

        // Filter based on tab position
        filteredTasks = when (tabPosition) {
            0 -> filteredTasks // All tasks
            1 -> filteredTasks.filter { isToday(it.taskDate) } // Today's tasks
            2 -> filteredTasks.filter { it.approved } // Approved tasks
            3 -> filteredTasks.filter { !it.approved } // Not approved tasks
            4 -> filteredTasks.filter { it.done } // Done tasks
            else -> filteredTasks
        }

        // Further filter by selected date if present
        selectedDate?.let { date ->
            filteredTasks = filteredTasks.filter { it.taskDate == date }
        }

        taskAdapter.updateTasks(filteredTasks)
    }

    private fun fetchTasks() {
        val url = "http://34.234.65.167:8080/api/v1/maintenance/CustomerTasksUpdated?customerId=$customerID"

        progressBar.visibility = View.VISIBLE
        viewModel.fetchTasks(url)
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            currentTasks = tasks
            progressBar.visibility = View.GONE
            filterTasksBasedOnTabAndDate(binding?.tabLayout2?.selectedTabPosition ?: 0)
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
        var isDateSelected = false // Track if a date is currently selected
        binding?.dateButton?.setOnClickListener {
        if (isDateSelected) {
            // Reset background image and clear selected date
            binding?.dateButton?.setBackgroundResource(R.drawable.f7__calendar_today)
            selectedDate = null
            // Reload original tasks based on tab selection
            filterTasksBasedOnTabAndDate(binding?.tabLayout2?.selectedTabPosition ?: 0)
        } else {
            // Change background image and show date picker dialog
            binding?.dateButton?.setBackgroundResource(R.drawable.ic__twotone_refresh)
            showDatePickerDialog()
        }
        isDateSelected = !isDateSelected
    }}


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDateArabic = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                val selectedDateEnglish = convertArabicDateToEnglish(selectedDateArabic)
                this.selectedDate = selectedDateEnglish
                filterTasksBasedOnTabAndDate(binding?.tabLayout2?.selectedTabPosition ?: 0)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun convertArabicDateToEnglish(arabicDate: String): String {
        val arabicFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
        val englishFormat = SimpleDateFormat("yyyy-MM-dd", Locale("en"))

        val date = arabicFormat.parse(arabicDate)
        return englishFormat.format(date)
    }

    private fun isToday(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("en"))
        val taskDate = dateFormat.parse(dateString)
        val today = Calendar.getInstance().time
        return taskDate?.let {
            dateFormat.format(it) == dateFormat.format(today)
        } ?: false
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
            filterTasksBasedOnTabAndDate(binding?.tabLayout2?.selectedTabPosition ?: 0)
        }
    }

    private fun navigateToTaskDetails(task: Task) {
        val action = CustomerTaskListFragmentDirections.actionListToTask(task)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

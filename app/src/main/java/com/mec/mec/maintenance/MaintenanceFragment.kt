package com.mec.mec.maintenance

import com.mec.mec.generic.BaseFragment
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import java.util.*
import com.mec.mec.R
import com.mec.mec.customers.CustomerFragmentDirections
import com.mec.mec.databinding.FragmentCustomerBinding
import com.mec.mec.databinding.FragmentMaintenanceBinding
import com.mec.mec.model.Task
import java.text.SimpleDateFormat

class MaintenanceFragment : BaseFragment() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private val viewModel: MaintenanceViewModel by viewModels()
    private var binding: FragmentMaintenanceBinding? = null
    private var currentTasks: List<Task> = emptyList()

    companion object {
        private const val ARG_TYPE = "type"

        fun newInstance(type: String): MaintenanceFragment {
            val fragment = MaintenanceFragment()
            val args = Bundle()
            args.putString(ARG_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun isLoggedin()= true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMaintenanceBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and Adapter
        val recyclerView = binding?.rvMaintenanceList

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        taskAdapter = TaskAdapter(emptyList()) { task ->
            val action = MaintenanceFragmentDirections.actionMaintenanceFragmentToEmployeeTaskFragment(task)
            findNavController().navigate(action)
        }
        recyclerView?.adapter = taskAdapter

        binding?.let { bindingNotNull ->
            bindingNotNull.fab.setOnClickListener {
                findNavController().navigate(MaintenanceFragmentDirections.actionMaintenanceFragmentToAddTaskFragment())
            }}
        // Initialize ProgressBar
        progressBar = binding?.progressBar ?: throw IllegalStateException("Progress bar not found in layout.")

        // Initialize Search EditText and Search Button
        searchEditText = binding?.searchEditText ?: throw IllegalStateException("Search EditText not found in layout.")
        searchButton = binding?.searchButton ?: throw IllegalStateException("Search Button not found in layout.")
        setupSearchFunctionality()

        // Setup Date Button click listener
        binding?.dateButton?.setOnClickListener {
            showDatePickerDialog()
        }


        // Setup TabLayout listener
        binding?.tabLayout2?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    // Hide or show the date button based on the selected tab
                    if (it.position == 2 || it.position == 3) {
                        binding?.dateButton?.visibility = View.GONE
                    } else {
                        binding?.dateButton?.visibility = View.VISIBLE
                    }

                    // Fetch data for the selected tab
                    fetchDataForTab(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Initial fetch based on the default selected tab
        fetchDataForTab(binding?.tabLayout2?.selectedTabPosition ?: 0)
    }

    private fun fetchDataForTab(tabPosition: Int) {
        // Clear the current tasks in the RecyclerView
        taskAdapter.updateTasks(emptyList())

        val url = when (tabPosition) {
            0 -> {
                // Fetch tasks for selected date (current date)
                val date = getCurrentDate()
                "http://34.234.65.167:8080/api/v1/maintenance/AllTasks"
            }
            1 -> {
                // Fetch tasks for selected date (current date)
                val date = getCurrentDate()
                "http://34.234.65.167:8080/api/v1/maintenance/task/date?date=$date"
            }
            2 -> {
                // Fetch approved tasks
                "http://34.234.65.167:8080/api/v1/maintenance/task/getApprovalList?bool=true"
            }
            3 -> {
                // Fetch not approved tasks
                "http://34.234.65.167:8080/api/v1/maintenance/task/getApprovalList?bool=false"
            }
            else -> throw IllegalStateException("Unexpected tab position $tabPosition")
        }

        // Show ProgressBar while fetching data
        progressBar.visibility = View.VISIBLE

        viewModel.fetchTasks(url)

        // Observe LiveData for tasks
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            // Hide ProgressBar when data is loaded
            progressBar.visibility = View.GONE

            if (tasks.isEmpty()) {
                // If tasks list is empty, show a Toast message
                Toast.makeText(requireContext(), "No tasks available.", Toast.LENGTH_SHORT).show()
            } else {
                // Store current tasks for search filtering
                currentTasks = tasks
                // Update RecyclerView with new data
                taskAdapter.updateTasks(tasks)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            // Hide ProgressBar in case of an error
            progressBar.visibility = View.GONE
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
            R.style.CustomDatePickerDialogTheme, // Apply the custom theme here
            { _, year, month, dayOfMonth ->
                val selectedDate = "${year}-${month + 1}-${dayOfMonth}" // Adjust month +1 because DatePicker month starts from 0
                updateUrlWithDate(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun updateUrlWithDate(selectedDate: String) {
        // Update the API URL with the selected date
        val tabPosition = binding?.tabLayout2?.selectedTabPosition ?: 0

        val url = when (tabPosition) {
            0 -> {
                // Fetch tasks for selected date (current date)
                "http://34.234.65.167:8080/api/v1/maintenance/task/date?date=$selectedDate"
            }
            1 -> {
                // Fetch tasks for selected date (current date)
                "http://34.234.65.167:8080/api/v1/maintenance/task/date?date=$selectedDate"
            }
            2 -> {
                // Fetch approved tasks
                "http://34.234.65.167:8080/api/v1/maintenance/task/getApprovalList?bool=true"
            }
            3 -> {
                // Fetch not approved tasks
                "http://34.234.65.167:8080/api/v1/maintenance/task/getApprovalList?bool=false"
            }
            else -> throw IllegalStateException("Unexpected tab position $tabPosition")
        }

        // Fetch data based on updated URL
        fetchDataForTab(tabPosition)
    }

    private fun setupSearchFunctionality() {
        searchButton.setOnClickListener {
            performSearch()
        }

        // Optional: Trigger search on keyboard "Done" press
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Optional: Live search as user types
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Perform search with current text after a short delay (if needed)
                // performSearch()
            }
        })
    }

    private fun performSearch() {
        val query = searchEditText.text.toString().trim()

        if (query.isNotEmpty()) {
            // Filter tasks based on the search query
            val filteredTasks = currentTasks.filter { task ->
                task.subject.contains(query, ignoreCase = true) ||
                        task.details.contains(query, ignoreCase = true) ||
                        task.customer.customerName.contains(query, ignoreCase = true)
            }

            // Update RecyclerView with filtered tasks
            taskAdapter.updateTasks(filteredTasks)
        } else {
            // If query is empty, show all tasks
            taskAdapter.updateTasks(currentTasks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null // Clean up references to avoid memory leaks
    }
}

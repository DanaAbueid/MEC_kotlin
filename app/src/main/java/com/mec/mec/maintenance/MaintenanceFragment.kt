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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.util.*
import com.mec.mec.R
import com.mec.mec.ui.model.Task
import java.text.SimpleDateFormat

class MaintenanceFragment : BaseFragment() {
    override fun isLoggedin(): Boolean = true

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private val viewModel: MaintenanceViewModel by viewModels()

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maintenance, container, false)

        // Initialize RecyclerView and Adapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_maintenance_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskAdapter = TaskAdapter(emptyList()) { task ->
            val action = MaintenanceFragmentDirections.actionMaintenanceFragmentToEmployeeTaskFragment(task)
            findNavController().navigate(action)
        }
        recyclerView.adapter = taskAdapter

        // Initialize ProgressBar
        progressBar = view.findViewById(R.id.progressBar)

        // Initialize Search EditText and Search Button
        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)
        setupSearchFunctionality()

        // Setup Date Button click listener
        val dateButton = view.findViewById<Button>(R.id.date_button)
        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout2)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    fetchDataForTab(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Initial fetch based on the default selected tab
        fetchDataForTab(tabLayout.selectedTabPosition)
    }

    private fun fetchDataForTab(tabPosition: Int) {
        val url = when (tabPosition) {
            0 -> {
                // Fetch tasks for selected date
                val date = getCurrentDate()
                "http://34.234.65.167:8080/api/v1/maintenance/task/date?date=$date"
            }
            1 -> {
                // Fetch tasks for selected date
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
            // Store current tasks for search filtering
            currentTasks = tasks
            // Hide ProgressBar when data is loaded
            progressBar.visibility = View.GONE
            // Update RecyclerView with new data
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
                val selectedDate = "${year}-${month + 1}-${dayOfMonth}" // Adjust month +1 because DatePicker month starts from 0
                updateUrlWithDate(selectedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun updateUrlWithDate(selectedDate: String) {
        // Update the API URL with the selected date
        val tabLayout = requireView().findViewById<TabLayout>(R.id.tabLayout2)
        val tabPosition = tabLayout.selectedTabPosition

        val url = when (tabPosition) {
            0, 1 -> {
                // Fetch tasks for selected date
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
}
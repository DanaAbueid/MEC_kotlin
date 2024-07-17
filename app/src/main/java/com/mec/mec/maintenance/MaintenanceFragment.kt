package com.mec.mec.maintenance

import com.mec.mec.generic.BaseFragment
import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import java.util.*
import com.mec.mec.databinding.FragmentMaintenanceBinding
import com.mec.mec.model.Task
import com.mec.mec.viewModel.AuthViewModel
import java.text.SimpleDateFormat

class MaintenanceFragment : BaseFragment() {

    private lateinit var taskAdapter: TaskAdapter
    private var progressBar: ProgressBar? = null
    private var searchEditText: EditText? = null
    private var searchButton: ImageButton? = null
    private val viewModel: MaintenanceViewModel by viewModels()
    private var binding: FragmentMaintenanceBinding? = null
    private var currentTasks: List<Task> = emptyList()
    private lateinit var authViewModel: AuthViewModel
    private var isDateSelected = false
    private var selectedDate: String? = null



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

    override fun isLoggedin() = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMaintenanceBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.toolbar?.btnSelectLanguage?.visibility = View.GONE
        binding?.toolbar?.logoutBtn?.visibility = View.GONE

        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        binding?.button4?.setOnClickListener {
            authViewModel.deleteUser()
            findNavController().navigate(MaintenanceFragmentDirections.actionCustomerFragmentToLogin())
        }

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
            }
        }

        progressBar = binding?.progressBar ?: throw IllegalStateException("Progress bar not found in layout.")
        searchEditText = binding?.searchEditText ?: throw IllegalStateException("Search EditText not found in layout.")
        searchButton = binding?.searchButton ?: throw IllegalStateException("Search Button not found in layout.")
        setupSearchFunctionality()

    setupDateButtonListener()

        binding?.tabLayout2?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    if (it.position == 0 || it.position == 2 || it.position == 3 || it.position == 4) {
                        binding?.dateButton?.setBackgroundResource(R.drawable.white_button)
                    } else {
                        binding?.dateButton?.setBackgroundResource(R.drawable.f7__calendar_today)
                    }
                    if (it.position == 4) {
                        taskAdapter.updateTasks(currentTasks.filter { it.done }) // Done tasks

                    } else {
                        fetchDataForTab(it.position)
                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        fetchDataForTab(binding?.tabLayout2?.selectedTabPosition ?: 0)
    }

    private fun fetchDataForTab(tabPosition: Int, selectedDate: String? = null) {
        taskAdapter.updateTasks(emptyList())
        binding?.rvMaintenanceList?.visibility = View.GONE

        val url = when (tabPosition) {
            0 -> "http://34.234.65.167:8080/api/v1/maintenance/AllFilteredTasks"
            1 -> {
                val date = selectedDate ?: getCurrentDate()
                Log.d(ContentValues.TAG, "date: $date")
                "http://34.234.65.167:8080/api/v1/maintenance/taskUpdated/date?date=$date"
            }
            2 -> "http://34.234.65.167:8080/api/v1/maintenance/task/getUpdatedApprovalList?bool=true"
            3 -> "http://34.234.65.167:8080/api/v1/maintenance/task/getUpdatedApprovalList?bool=false"

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
            // Reload original tasks based on tab selection
            fetchDataForTab(binding?.tabLayout2?.selectedTabPosition ?: 0)
        } else {
            // Change background image and show date picker dialog
            binding?.dateButton?.setBackgroundResource(R.drawable.ic__twotone_refresh)
            showDatePickerDialog()
        }
        // Toggle date selection state
        isDateSelected = !isDateSelected
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
                val selectedDateEnglish = convertArabicDateToEnglish(selectedDateArabic)
                updateUrlWithDate(selectedDateEnglish)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun updateUrlWithDate(selectedDate: String) {
        val tabPosition = binding?.tabLayout2?.selectedTabPosition ?: 0
        fetchDataForTab(tabPosition, selectedDate)
    }

    private fun setupSearchFunctionality() {
        searchButton?.setOnClickListener { performSearch() }

        searchEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else {
                false
            }
        }

        searchEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { performSearch() }
        })
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

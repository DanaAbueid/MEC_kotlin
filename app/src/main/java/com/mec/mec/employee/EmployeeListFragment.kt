package com.mec.mec.employee
import com.mec.mec.generic.BaseFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mec.mec.API.ApiService
import com.mec.mec.API.RetrofitInstance
import com.mec.mec.databinding.FragmentEmployeeListBinding
import kotlinx.coroutines.launch


class EmployeeListFragment: BaseFragment(){
    override fun isLoggedin(): Boolean = true
    private var _binding: FragmentEmployeeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiService
    private lateinit var adapter: EmployeeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmployeeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        apiService = RetrofitInstance.api

        fetchEmployeeBasicInfo()
    }

    private fun setupRecyclerView() {
        adapter = EmployeeListAdapter { userId ->
            navigateToEmployeeTasks(userId)
        }
        binding.rvEmployeeList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployeeList.adapter = adapter
    }

    private fun fetchEmployeeBasicInfo() {
        lifecycleScope.launch {
            try {
                val response = apiService.getUserBasicInfo()
                adapter.submitList(response)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Failed to retrieve employee list: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToEmployeeTasks(userId: Long) {
        val action = EmployeeListFragmentDirections.actionemployeeFragmentToEmployeeTaskList(userId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
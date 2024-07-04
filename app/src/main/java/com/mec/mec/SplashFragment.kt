package com.mec.mec

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mec.mec.generic.BaseFragment
import com.mec.mec.viewModel.AuthViewModel
import kotlinx.coroutines.launch

class SplashFragment : BaseFragment() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var progressBar: ProgressBar

    override fun isLoggedin() = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        progressBar = view.findViewById(R.id.progressBar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        showProgressBar()

        // Observe the isLoggedIn LiveData to determine the next navigation step
        authViewModel.isLoggedIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val userRole = authViewModel.getUserRoleRoom()
                    if (userRole == "EMPLOYEE") {
                        findNavController().navigate(R.id.action_splashFragment_to_EmployeeFragment)
                    } else if (userRole == "MANAGER") {
                        findNavController().navigate(R.id.action_splashFragment_to_ManagerFragment)
                    }
                    hideProgressBar()
                }
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                hideProgressBar()
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isAdded || view == null) {
                return@postDelayed
            }
            // This delay is for simulation purposes, to give the appearance of a splash screen
        }, 2000) // Simulate a delay of 2 seconds
    }


    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}

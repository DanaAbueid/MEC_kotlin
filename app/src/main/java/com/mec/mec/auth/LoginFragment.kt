package com.mec.mec.auth
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mec.mec.api.ApiService
import com.mec.mec.databinding.FragmentLoginBinding
import com.mec.mec.generic.BaseFragment
import com.mec.mec.request.LoginRequest
import com.mec.mec.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : BaseFragment() {
    private var binding: FragmentLoginBinding? = null
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun isLoggedin(): Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding?.let { bindingNotNull ->
            bindingNotNull.tvCreateAccount.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignupUpUserFragment())
            }
            bindingNotNull.tvBecomePartner.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment())
            }
            bindingNotNull.buttonLogin.setOnClickListener {
                login()
            }
        }

        authViewModel.authResponse.observe(viewLifecycleOwner) { authResponse ->
            saveUserData(authResponse.user_id.toLong(), authResponse.user_role)
            navigateBasedOnRole(authResponse.user_role)
        }

        authViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, "Login failed: $error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun login() {
        val email = binding?.tilUsername?.editText?.text.toString()
        val password = binding?.tilPassword?.editText?.text.toString()

        if (email.isNotBlank() && password.isNotBlank()) {
            authViewModel.login(LoginRequest(email, password))
        } else {
            Toast.makeText(context, "Please enter valid credentials", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserData(userId: Long, userRole: String) {
        with(sharedPreferences.edit()) {
            putLong("user_id", userId)
            putString("user_role", userRole)
            apply()
        }
    }

    private fun navigateBasedOnRole(userRole: String) {
        when (userRole) {
            "EMPLOYEE" -> findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToEmployeeTasks())
            "MANAGER" -> findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToUserMyActivityFragment())
            else -> Toast.makeText(context, "Unknown user role", Toast.LENGTH_SHORT).show()
        }
    }
}

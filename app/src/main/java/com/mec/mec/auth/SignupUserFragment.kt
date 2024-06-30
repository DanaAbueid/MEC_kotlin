package com.mec.mec.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.mec.mec.generic.BaseFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mec.mec.databinding.FragmentSignupUserBinding // Update with your package name
import com.mec.mec.request.SignUpRequest
import com.mec.mec.viewModel.AuthViewModel

class SignupUserFragment: BaseFragment() {

    private var _binding: FragmentSignupUserBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    override fun isLoggedin()= false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAdd.setOnClickListener {
            val firstName = binding.tilName.editText?.text.toString()
            val lastName = binding.tilLastName.editText?.text.toString()
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()
            val phoneNumber = binding.tilPhoneNumber.editText?.text.toString()
            val role = if (binding.rbEmployee.isChecked) "EMPLOYEE" else "MANAGER"

            val signUpRequest = SignUpRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                phoneNumber = phoneNumber,
                role = role
            )

            authViewModel.signUp(signUpRequest)
        }

        authViewModel.authResponse.observe(viewLifecycleOwner, Observer {
            // Handle successful sign-up response
            // Navigate to another fragment or show a success message
            findNavController().navigate(SignupUserFragmentDirections.actionSignupFragmentToConfirm())
        })

        authViewModel.error.observe(viewLifecycleOwner, Observer {
            // Handle sign-up error
            // Show an error message
            Toast.makeText(context, "signup failed please try again later", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

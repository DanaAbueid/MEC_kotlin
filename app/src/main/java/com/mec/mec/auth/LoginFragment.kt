package com.mec.mec.auth
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mec.mec.databinding.FragmentLoginBinding
import com.mec.mec.generic.BaseFragment

class LoginFragment : BaseFragment() {
    private var binding: FragmentLoginBinding? = null
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

        binding?.let { bindingNotNull ->

            bindingNotNull.tvCreateAccount.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignupUpUserFragment())
            }
            bindingNotNull.tvBecomePartner.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment())
            }
            bindingNotNull.buttonLogin.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToUserMyActivityFragment())
            }
        }
    }
}
package com.android.firebasechatapp.presentation.authentication.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.firebasechatapp.databinding.FragmentLoginBinding
import com.android.firebasechatapp.presentation.collectLatestLifecycleFlow
import com.android.firebasechatapp.presentation.hideKeyboard
import com.android.firebasechatapp.presentation.showToast
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.asString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListenerForViews()
        observeLoginState()
    }

    private fun setUpListenerForViews() {
        binding.emailSignInButton.setOnClickListener {
            it.hideKeyboard()
            viewModel.login(
                email = binding.email.text.toString(),
                password = binding.password.text.toString()
            )
        }

        binding.register.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.forgotPassword.setOnClickListener {
            val passwordResetDialog = PasswordResetDialog()
            passwordResetDialog.show(childFragmentManager, "PasswordResetDialog")
        }

        binding.resendVerificationEmail.setOnClickListener {
            val resendVerificationDialog = ResendVerificationDialog()
            resendVerificationDialog.show(childFragmentManager, "ResendVerificationDialog")
        }
    }

    private fun observeLoginState() {
        collectLatestLifecycleFlow(viewModel.loginResultStatus) { result ->
            when (result) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(result.uiText.asString(requireContext()))
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    companion object {
        private val TAG = LoginFragment::class.simpleName.toString()
    }
}
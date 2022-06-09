package com.android.firebasechatapp.presentation.register

import android.inputmethodservice.Keyboard
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.firebasechatapp.R
import com.android.firebasechatapp.databinding.FragmentRegisterBinding
import com.android.firebasechatapp.presentation.collectLatestLifecycleFlow
import com.android.firebasechatapp.presentation.hideKeyboard
import com.android.firebasechatapp.presentation.showToast
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.asString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListenerForViews()
        observeRegisterState()
    }

    private fun setUpListenerForViews() {
        binding.btnRegister.setOnClickListener {
            it.hideKeyboard()
            viewModel.register(
                email = binding.inputEmail.text.toString(),
                password = binding.inputPassword.text.toString(),
                confirmPassword = binding.inputConfirmPassword.text.toString()
            )
        }
    }

    private fun observeRegisterState() {
        collectLatestLifecycleFlow(viewModel.registerResultStatus) { result ->
            when (result) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showToast("User is registered successfully.\nPlease check you inbox to verify your email")
                    findNavController().navigateUp()
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
        private val TAG = RegisterFragment::class.simpleName.toString()
    }
}
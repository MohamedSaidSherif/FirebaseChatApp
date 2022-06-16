package com.android.firebasechatapp.presentation.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.android.firebasechatapp.databinding.DialogResendVerificationBinding
import com.android.firebasechatapp.presentation.collectLatestLifecycleFlow
import com.android.firebasechatapp.presentation.showToast
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.asString

class ResendVerificationDialog: DialogFragment() {

    private lateinit var binding: DialogResendVerificationBinding
    private val viewModel: LoginViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /** The system calls this to get the DialogFragment's layout, regardless
    of whether it's being displayed as a dialog or an embedded fragment. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as dialog or embedded fragment
        binding = DialogResendVerificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListenerForViews()
        observeResendEmailVerificationState()
    }

    private fun setUpListenerForViews() {
        binding.dialogConfirm.setOnClickListener {
            viewModel.resendVerificationEmail(
                email = binding.confirmEmail.text.toString(),
                password = binding.confirmPassword.text.toString()
            )
        }

        binding.dialogCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun observeResendEmailVerificationState() {
        collectLatestLifecycleFlow(viewModel.resendVerificationEmailResultStatus) { result ->
            when (result) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    dismiss()
                    showToast("Email verification is sent successfully to your email.")
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
        private val TAG = ResendVerificationDialog::class.simpleName.toString()
    }
}
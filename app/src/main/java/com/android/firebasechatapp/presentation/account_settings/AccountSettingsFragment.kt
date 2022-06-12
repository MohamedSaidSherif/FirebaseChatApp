package com.android.firebasechatapp.presentation.account_settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.firebasechatapp.databinding.FragmentAccountSettingsBinding
import com.android.firebasechatapp.presentation.collectLatestLifecycleFlow
import com.android.firebasechatapp.presentation.hideKeyboard
import com.android.firebasechatapp.presentation.showToast
import com.android.firebasechatapp.resource.asString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSettingsFragment : Fragment() {

    private lateinit var binding: FragmentAccountSettingsBinding
    private val viewModel: AccountSettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListenerForViews()
        observerAccountSettingsState()
    }

    private fun setUpListenerForViews() {
        with(binding) {
            btnSave.setOnClickListener {
                it.hideKeyboard()
                viewModel.onEvent(
                    AccountSettingsEvent.SaveAction(
                        name = inputName.text.toString(),
                        phone = inputPhone.text.toString(),
                        email = inputEmail.text.toString(),
                        confirmedPassword = inputPassword.text.toString()
                    )
                )
            }

            changePassword.setOnClickListener {
                viewModel.onEvent(AccountSettingsEvent.ChangePassword)
            }
        }
    }

    private fun observerAccountSettingsState() {
        collectLatestLifecycleFlow(viewModel.accountSettingsState) {
            updateUI(it)
        }
    }

    private fun updateUI(state: AccountSettingsState) {
        with(binding) {
            progressBar.isVisible = state.isProgressBarVisible
            state.user?.let {
                inputName.setText(it.name)
                inputPhone.setText(it.phone)
                inputEmail.setText(it.email)
            }
        }
        if (state.isProfileDataUpdated) {
            showToast("Profile data is updated successfully.")
            if (state.isEmailUpdated) {
                goToLoginScreen()
            }
        } else if (state.isPasswordResetEmailSent) {
            showToast("Password reset email is sent successfully.")
            goToLoginScreen()
        } else if (state.errorUiText != null) {
            showToast(state.errorUiText.asString(requireContext()))
        }
    }

    private fun goToLoginScreen() {
        findNavController().navigate(AccountSettingsFragmentDirections.actionAccountSettingsFragmentToLoginFragment())
    }
}
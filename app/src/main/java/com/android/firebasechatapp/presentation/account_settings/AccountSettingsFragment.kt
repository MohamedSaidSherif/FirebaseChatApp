package com.android.firebasechatapp.presentation.account_settings

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.firebasechatapp.R
import com.android.firebasechatapp.databinding.FragmentAccountSettingsBinding
import com.android.firebasechatapp.domain.model.account_settings.ProfileData
import com.android.firebasechatapp.presentation.collectLatestLifecycleFlow
import com.android.firebasechatapp.presentation.hideKeyboard
import com.android.firebasechatapp.presentation.showToast
import com.android.firebasechatapp.resource.asString
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "AccountSettingsFragment"

@AndroidEntryPoint
class AccountSettingsFragment : Fragment() {

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var binding: FragmentAccountSettingsBinding
    private val viewModel: AccountSettingsViewModel by viewModels()
    private val args: AccountSettingsFragmentArgs by navArgs()
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.imageUri?.let {
            Log.d(TAG, "onViewCreated: URI: $it")
            imageUri = Uri.parse(it)
            binding.profileImage.setImageURI(imageUri)
        }
        setUpListenerForViews()
        observerStates()
    }

    private fun setUpListenerForViews() {
        with(binding) {
            profileImage.setOnClickListener {
                val changePhotoDialog = ChangePhotoDialog { uri ->
                    Log.d(TAG, "setUpListenerForViews: URI: $uri")
                    imageUri = uri
                    uri?.let { profileImage.setImageURI(it) }
                }
                changePhotoDialog.show(childFragmentManager, "changePhotoDialog")
            }

            btnSave.setOnClickListener {
                it.hideKeyboard()
                viewModel.onEvent(
                    AccountSettingsEvent.SaveAction(
                        ProfileData(
                            name = inputName.text.toString(),
                            phone = inputPhone.text.toString(),
                            imageUri = imageUri,
                            email = inputEmail.text.toString(),
                            password = inputPassword.text.toString()
                        )
                    )
                )
            }

            changePassword.setOnClickListener {
                viewModel.onEvent(AccountSettingsEvent.ChangePassword)
            }
        }
    }

    private fun observerStates() {
        observerAccountSettingsState()
        observeProgressVisibilityState()
        observeErrorState()
    }

    private fun observerAccountSettingsState() {
        collectLatestLifecycleFlow(viewModel.accountSettingsState) {
            when (it) {
                AccountSettingsState.InitialState -> {}
                is AccountSettingsState.GetUser -> {
                    with(binding) {
                        inputName.setText(it.user.name)
                        inputPhone.setText(it.user.phone)
                        inputEmail.setText(it.user.email)
                        requestManager.load(it.user.profileImage)
                            .error(R.drawable.ic_person)
                            .into(binding.profileImage)
                    }
                }
                is AccountSettingsState.ProfileUpdated -> {
                    showToast("Profile data is updated successfully.")
                    if (it.isEmailUpdated) {
                        goToLoginScreen()
                    }
                }
                AccountSettingsState.PasswordResetEmailSent -> {
                    showToast("Password reset email is sent successfully.")
                    goToLoginScreen()
                }
            }
        }
    }

    private fun observeProgressVisibilityState() {
        collectLatestLifecycleFlow(viewModel.progressVisibleState) {
            binding.progressBar.isVisible = it
        }
    }

    private fun observeErrorState() {
        collectLatestLifecycleFlow(viewModel.errorFlow) {
            showToast(it.asString(requireContext()))
        }
    }

    private fun goToLoginScreen() {
        findNavController().navigate(AccountSettingsFragmentDirections.actionAccountSettingsFragmentToLoginFragment())
    }
}
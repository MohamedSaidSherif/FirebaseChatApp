package com.android.firebasechatapp.presentation.account_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.firebasechatapp.domain.use_case.account_settings.GetUserUseCase
import com.android.firebasechatapp.domain.use_case.account_settings.UpdateProfileDataUseCase
import com.android.firebasechatapp.domain.use_case.authentication.SendPasswordResetEmailUseCase
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.UiText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    private val updateProfileDataUseCase: UpdateProfileDataUseCase
) : ViewModel() {

    private val _accountSettingsState = MutableStateFlow(AccountSettingsState())
    val accountSettingsState: StateFlow<AccountSettingsState> = _accountSettingsState.asStateFlow()

    init {
        viewModelScope.launch {
            _accountSettingsState.emit(
                accountSettingsState.value.copy(
                    isProgressBarVisible = true,
                    errorUiText = null
                )
            )
            when (val result = getUserUseCase()) {
                is Resource.Success -> {
                    _accountSettingsState.emit(
                        accountSettingsState.value.copy(
                            user = result.data,
                            isProgressBarVisible = false,
                            errorUiText = null
                        )
                    )
                }
                is Resource.Error -> {
                    _accountSettingsState.emit(
                        accountSettingsState.value.copy(
                            isProgressBarVisible = false,
                            errorUiText = result.uiText
                        )
                    )
                }
            }
        }
    }

    fun onEvent(event: AccountSettingsEvent) {
        when (event) {
            is AccountSettingsEvent.SaveAction -> {
                updateProfileData(event)
            }

            AccountSettingsEvent.ChangePassword -> {
                sendPasswordResetEmail()
            }
        }
    }

    private fun updateProfileData(event: AccountSettingsEvent.SaveAction) {
        viewModelScope.launch {
            _accountSettingsState.emit(
                accountSettingsState.value.copy(
                    isProfileDataUpdated = false,
                    isPasswordResetEmailSent = false,
                    isProgressBarVisible = true,
                    errorUiText = null
                )
            )
            when (val result = updateProfileDataUseCase(
                name = event.name,
                phone = event.phone,
                email = event.email,
                password = event.confirmedPassword
            )) {
                is Resource.Success -> {
                    _accountSettingsState.emit(
                        accountSettingsState.value.copy(
                            isProfileDataUpdated = true,
                            isEmailUpdated = result.data.isEmailUpdated,
                            isPasswordResetEmailSent = false,
                            isProgressBarVisible = false,
                            errorUiText = null
                        )
                    )
                }
                is Resource.Error -> {
                    _accountSettingsState.emit(
                        accountSettingsState.value.copy(
                            user = accountSettingsState.value.user?.copy(
                                name = event.name,
                                phone = event.phone,
                                email = event.email
                            ),
                            isProfileDataUpdated = false,
                            isPasswordResetEmailSent = false,
                            isProgressBarVisible = false,
                            errorUiText = result.uiText
                        )
                    )
                }
            }
        }
    }

    private fun sendPasswordResetEmail() {
        viewModelScope.launch {
            Firebase.auth.currentUser?.email?.let {
                _accountSettingsState.emit(
                    accountSettingsState.value.copy(
                        isProfileDataUpdated = false,
                        isPasswordResetEmailSent = false,
                        isProgressBarVisible = true,
                        errorUiText = null
                    )
                )
                when (val result = sendPasswordResetEmailUseCase(it)) {
                    is Resource.Success -> {
                        _accountSettingsState.emit(
                            accountSettingsState.value.copy(
                                isProfileDataUpdated = false,
                                isPasswordResetEmailSent = true,
                                isProgressBarVisible = false,
                                errorUiText = null
                            )
                        )
                    }
                    is Resource.Error -> {
                        _accountSettingsState.emit(
                            accountSettingsState.value.copy(
                                isProfileDataUpdated = false,
                                isPasswordResetEmailSent = false,
                                isProgressBarVisible = false,
                                errorUiText = result.uiText
                            )
                        )
                    }
                }
            } ?: kotlin.run {
                //This case NEVER should happen
                //and if it's happened, we should logout the user the redirect to login screen
                _accountSettingsState.emit(
                    accountSettingsState.value.copy(
                        isProfileDataUpdated = false,
                        isPasswordResetEmailSent = false,
                        isProgressBarVisible = false,
                        errorUiText = UiText.DynamicString("User Not Authenticated")
                    )
                )
            }
        }
    }
}
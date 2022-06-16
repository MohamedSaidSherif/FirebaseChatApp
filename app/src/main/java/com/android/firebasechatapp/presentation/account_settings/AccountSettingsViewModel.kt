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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingsViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    private val updateProfileDataUseCase: UpdateProfileDataUseCase
) : ViewModel() {

    private val _accountSettingsState = MutableStateFlow<AccountSettingsState>(AccountSettingsState.InitialState)
    val accountSettingsState: StateFlow<AccountSettingsState> = _accountSettingsState.asStateFlow()

    private val _progressVisibleState = MutableStateFlow(false)
    val progressVisibleState: StateFlow<Boolean> = _progressVisibleState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<UiText>()
    val errorFlow: SharedFlow<UiText> = _errorFlow.asSharedFlow()


    init {
        viewModelScope.launch {
            _progressVisibleState.emit(true)
            when (val result = getUserUseCase()) {
                is Resource.Success -> {
                    _progressVisibleState.emit(false)
                    _accountSettingsState.emit(AccountSettingsState.GetUser(result.data))
                }
                is Resource.Error -> {
                    _progressVisibleState.emit(false)
                    _errorFlow.emit(result.uiText)
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
            _progressVisibleState.emit(true)
            when (val result = updateProfileDataUseCase(event.profileData)) {
                is Resource.Success -> {
                    _progressVisibleState.emit(false)
                    _accountSettingsState.emit(AccountSettingsState.ProfileUpdated(result.data.isEmailUpdated))
                }
                is Resource.Error -> {
                    _progressVisibleState.emit(false)
                    _errorFlow.emit(result.uiText)
                }
            }
        }
    }

    private fun sendPasswordResetEmail() {
        viewModelScope.launch {
            Firebase.auth.currentUser?.email?.let {
                _progressVisibleState.emit(true)
                when (val result = sendPasswordResetEmailUseCase(it)) {
                    is Resource.Success -> {
                        _progressVisibleState.emit(false)
                        _accountSettingsState.emit(AccountSettingsState.PasswordResetEmailSent)
                    }
                    is Resource.Error -> {
                        _progressVisibleState.emit(false)
                        _errorFlow.emit(result.uiText)
                    }
                }
            } ?: kotlin.run {
                //This case NEVER should happen
                //and if it's happened, we should logout the user the redirect to login screen
                _errorFlow.emit(UiText.DynamicString("User Not Authenticated"))
            }
        }
    }
}
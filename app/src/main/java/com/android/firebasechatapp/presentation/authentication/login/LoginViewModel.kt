package com.android.firebasechatapp.presentation.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.firebasechatapp.domain.use_case.authentication.LoginUseCase
import com.android.firebasechatapp.domain.use_case.authentication.ResendVerificationEmailUseCase
import com.android.firebasechatapp.domain.use_case.authentication.SendPasswordResetEmailUseCase
import com.android.firebasechatapp.resource.Resource
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val resendVerificationEmailUseCase: ResendVerificationEmailUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModel() {

    private val _loginResultStatus = MutableSharedFlow<Resource<AuthResult>>()
    val loginResultStatus = _loginResultStatus.asSharedFlow()

    private val _resendVerificationEmailResultStatus = MutableSharedFlow<Resource<Unit>>()
    val resendVerificationEmailResultStatus = _resendVerificationEmailResultStatus.asSharedFlow()

    private val _sendPasswordResetEmailStatus = MutableSharedFlow<Resource<Unit>>()
    val sendPasswordResetEmailStatus = _sendPasswordResetEmailStatus.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResultStatus.emit(Resource.Loading())
            _loginResultStatus.emit(loginUseCase(email, password))
        }
    }

    fun resendVerificationEmail(email: String, password: String) {
        viewModelScope.launch {
            _resendVerificationEmailResultStatus.emit(Resource.Loading())
            _resendVerificationEmailResultStatus.emit(
                resendVerificationEmailUseCase(email, password)
            )
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _sendPasswordResetEmailStatus.emit(Resource.Loading())
            _sendPasswordResetEmailStatus.emit(
                sendPasswordResetEmailUseCase(email)
            )
        }
    }
}
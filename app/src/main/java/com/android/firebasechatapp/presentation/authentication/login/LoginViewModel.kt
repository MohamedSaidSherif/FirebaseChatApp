package com.android.firebasechatapp.presentation.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.firebasechatapp.domain.use_case.LoginUseCase
import com.android.firebasechatapp.domain.use_case.ResendVerificationEmailUseCase
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
    private val resendVerificationEmailUseCase: ResendVerificationEmailUseCase
) : ViewModel() {

    private val _loginResultStatus = MutableSharedFlow<Resource<AuthResult>>()
    val loginResultStatus = _loginResultStatus.asSharedFlow()

    private val _resendVerificationEmailResultStatus = MutableSharedFlow<Resource<Unit>>()
    val resendVerificationEmailResultStatus = _resendVerificationEmailResultStatus.asSharedFlow()

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
}
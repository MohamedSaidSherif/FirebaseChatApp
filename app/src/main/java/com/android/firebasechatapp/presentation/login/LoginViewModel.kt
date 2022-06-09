package com.android.firebasechatapp.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.firebasechatapp.domain.use_case.LoginUseCase
import com.android.firebasechatapp.resource.Resource
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginResultStatus = MutableSharedFlow<Resource<AuthResult>>()
    val loginResultStatus = _loginResultStatus.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResultStatus.emit(Resource.Loading())
            _loginResultStatus.emit(loginUseCase(email, password))
        }
    }
}
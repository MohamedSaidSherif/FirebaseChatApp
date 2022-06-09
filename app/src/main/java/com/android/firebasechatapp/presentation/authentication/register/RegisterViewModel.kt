package com.android.firebasechatapp.presentation.authentication.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.firebasechatapp.domain.use_case.authentication.RegisterUseCase
import com.android.firebasechatapp.resource.Resource
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _registerResultStatus = MutableSharedFlow<Resource<AuthResult>>()
    val registerResultStatus = _registerResultStatus.asSharedFlow()

    fun register(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _registerResultStatus.emit(Resource.Loading())
            _registerResultStatus.emit(registerUseCase(email, password, confirmPassword))
        }
    }
}
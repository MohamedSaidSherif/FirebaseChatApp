package com.android.firebasechatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.firebasechatapp.domain.model.authentication.AuthenticationState
import com.android.firebasechatapp.domain.use_case.authentication.ObserveAuthenticationStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val observeAuthenticationStateUseCase: ObserveAuthenticationStateUseCase
) : ViewModel() {

    private val _authenticationState = MutableStateFlow<AuthenticationState?>(null)
    val authenticationState = _authenticationState.asStateFlow()

    init {
        viewModelScope.launch {
            observeAuthenticationStateUseCase().collect {
                _authenticationState.emit(it)
            }
        }
    }
}
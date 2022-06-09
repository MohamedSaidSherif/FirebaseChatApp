package com.android.firebasechatapp.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.firebasechatapp.domain.use_case.authentication.IsUserSignedInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isUserSignedInUseCase: IsUserSignedInUseCase
) : ViewModel() {

    private val _isUserSignedInState = MutableStateFlow<Boolean?>(null)
    val isUserSignedInState = _isUserSignedInState.asStateFlow()

    init {
        viewModelScope.launch {
            val result = isUserSignedInUseCase()
            delay(1000)
            _isUserSignedInState.emit(result)
        }
    }
}
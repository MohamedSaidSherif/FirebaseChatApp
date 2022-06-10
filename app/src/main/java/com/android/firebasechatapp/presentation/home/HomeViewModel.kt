package com.android.firebasechatapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.firebasechatapp.domain.use_case.authentication.SignOutUseCase
import com.android.firebasechatapp.resource.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.SignOut -> {
                signOut()
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            _homeState.emit(homeState.value.copy(progressBarVisible = true))
            when (val result = signOutUseCase()) {
                is Resource.Success -> {
                    _homeState.emit(
                        homeState.value.copy(
                            progressBarVisible = false,
                            isSignedOut = true
                        )
                    )
                }
                is Resource.Error -> {
                    _homeState.emit(
                        homeState.value.copy(
                            progressBarVisible = false,
                            isSignedOut = false,
                            errorUiText = result.uiText
                        )
                    )
                }
            }
        }
    }
}
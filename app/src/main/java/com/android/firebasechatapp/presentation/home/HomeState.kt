package com.android.firebasechatapp.presentation.home

import com.android.firebasechatapp.resource.UiText

data class HomeState(
    val progressBarVisible: Boolean = false,
    val isSignedOut: Boolean = false,
    val errorUiText: UiText? = null
)
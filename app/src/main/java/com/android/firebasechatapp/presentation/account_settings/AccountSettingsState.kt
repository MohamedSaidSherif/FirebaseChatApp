package com.android.firebasechatapp.presentation.account_settings

import com.android.firebasechatapp.resource.UiText

data class AccountSettingsState(
    val isProfileDataUpdated: Boolean = false,
    val isEmailUpdated: Boolean = false,
    val isPasswordResetEmailSent: Boolean = false,
    val isProgressBarVisible: Boolean = false,
    val errorUiText: UiText? = null
)

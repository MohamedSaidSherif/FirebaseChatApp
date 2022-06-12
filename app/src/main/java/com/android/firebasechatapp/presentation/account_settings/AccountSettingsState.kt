package com.android.firebasechatapp.presentation.account_settings

import com.android.firebasechatapp.domain.model.User
import com.android.firebasechatapp.resource.UiText

data class AccountSettingsState(
    val user: User? = null,
    val isProfileDataUpdated: Boolean = false,
    val isEmailUpdated: Boolean = false,
    val isPasswordResetEmailSent: Boolean = false,
    val isProgressBarVisible: Boolean = false,
    val errorUiText: UiText? = null
)

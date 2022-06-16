package com.android.firebasechatapp.presentation.account_settings

import com.android.firebasechatapp.domain.model.User

sealed class AccountSettingsState {
    object InitialState: AccountSettingsState()
    class GetUser(val user: User): AccountSettingsState()
    class ProfileUpdated(val isEmailUpdated: Boolean): AccountSettingsState()
    object PasswordResetEmailSent: AccountSettingsState()
}

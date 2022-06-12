package com.android.firebasechatapp.presentation.account_settings

sealed class AccountSettingsEvent {
    class SaveAction(
        val name: String,
        val phone: String,
        val email: String,
        val confirmedPassword: String
    ) : AccountSettingsEvent()

    object ChangePassword : AccountSettingsEvent()
}

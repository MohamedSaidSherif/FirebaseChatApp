package com.android.firebasechatapp.presentation.account_settings

import com.android.firebasechatapp.domain.model.account_settings.ProfileData

sealed class AccountSettingsEvent {
    class SaveAction(val profileData: ProfileData) : AccountSettingsEvent()

    object ChangePassword : AccountSettingsEvent()
}

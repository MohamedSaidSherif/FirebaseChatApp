package com.android.firebasechatapp.domain.model.account_settings

import android.net.Uri

data class ProfileData(
    val name: String,
    val phone: String,
    val imageUri: Uri?,
    val email: String,
    val password: String
)

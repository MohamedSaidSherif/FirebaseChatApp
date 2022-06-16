package com.android.firebasechatapp.domain.repository.authentication

import com.android.firebasechatapp.domain.model.User
import com.android.firebasechatapp.domain.model.account_settings.ProfileData
import com.android.firebasechatapp.domain.model.account_settings.ProfileUpdateResult
import com.android.firebasechatapp.resource.Resource

interface AccountSettingRepository {
    suspend fun getUser(): Resource<User>
    suspend fun getUserById(userId: String): Resource<User>
    suspend fun updateProfileData(profileData: ProfileData): Resource<ProfileUpdateResult>
    suspend fun getSecurityLevel(): Resource<Int>
}
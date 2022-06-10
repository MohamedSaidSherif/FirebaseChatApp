package com.android.firebasechatapp.domain.use_case.account_settings

import android.util.Patterns
import com.android.firebasechatapp.domain.use_case.authentication.AuthenticationValidation
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import com.android.firebasechatapp.resource.UiText

object UpdateProfileDataValidation {

    fun validateProfileData(
        email: String,
        password: String
    ): SimpleResource {
        if (email.isEmpty()) {
            return Resource.Error(UiText.DynamicString("Email is empty"))
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.Error(UiText.DynamicString("Email not valid"))
        } else if (password.isEmpty()) {
            return Resource.Error(UiText.DynamicString("Password is empty"))
        } else if (password.length < AuthenticationValidation.MINIMUM_PASSWORD_LENGTH) {
            return Resource.Error(UiText.DynamicString("Password is less than ${AuthenticationValidation.MINIMUM_PASSWORD_LENGTH}"))
        }
        return Resource.Success(Unit)
    }
}
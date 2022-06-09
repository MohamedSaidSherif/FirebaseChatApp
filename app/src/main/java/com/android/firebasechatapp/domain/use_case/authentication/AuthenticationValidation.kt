package com.android.firebasechatapp.domain.use_case.authentication

import android.util.Patterns
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.UiText

object AuthenticationValidation {

    private const val MINIMUM_PASSWORD_LENGTH = 6

    fun validateLoginForm(email: String, password: String): Resource<Unit> {
        if (email.isEmpty()) {
            return Resource.Error(UiText.DynamicString("Email s empty"))
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.Error(UiText.DynamicString("Email not valid"))
        } else if (password.isEmpty()) {
            return Resource.Error(UiText.DynamicString("Password is empty"))
        } else if (password.length < MINIMUM_PASSWORD_LENGTH) {
            return Resource.Error(UiText.DynamicString("Password is less than $MINIMUM_PASSWORD_LENGTH"))
        }
        return Resource.Success(Unit)
    }

    fun validateRegistrationForm(email: String, password: String, confirmPassword: String): Resource<Unit> {
        if (email.isEmpty()) {
            return Resource.Error(UiText.DynamicString("Email s empty"))
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.Error(UiText.DynamicString("Email not valid"))
        } else if (password.isEmpty()) {
            return Resource.Error(UiText.DynamicString("Password is empty"))
        } else if (!password.equals(confirmPassword)) {
            return Resource.Error(UiText.DynamicString("Confirmed password not matching password"))
        } else if (password.length < MINIMUM_PASSWORD_LENGTH) {
            return Resource.Error(UiText.DynamicString("Password is less than $MINIMUM_PASSWORD_LENGTH"))
        }
        return Resource.Success(Unit)
    }
}
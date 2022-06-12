package com.android.firebasechatapp.data.model

import com.android.firebasechatapp.domain.model.User

data class RemoteUser(
    val name: String? = null,
    val phone: String? = null,
    val profileImage: String? = null,
    val securityLevel: String? = null,
    val userId: String? = null,
)

fun RemoteUser.toUser(email: String): User {
    return User(
        email = email,
        name = name ?: "",
        phone = phone ?: "",
        profileImage = profileImage ?: "",
        securityLevel = securityLevel ?: "",
        userId = userId ?: ""
    )
}
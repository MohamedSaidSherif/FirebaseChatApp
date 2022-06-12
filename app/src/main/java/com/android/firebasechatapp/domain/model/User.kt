package com.android.firebasechatapp.domain.model

data class User(
    val name: String,
    val phone: String,
    val profileImage: String,
    val securityLevel: String,
    val userId: String,
)
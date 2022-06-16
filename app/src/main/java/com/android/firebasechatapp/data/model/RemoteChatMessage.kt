package com.android.firebasechatapp.data.model

data class RemoteChatMessage(
    val message: String,
    val userId: String? = null,
    val timestamp: String,
    val profileImage: String? = null,
    val name: String? = null,
)

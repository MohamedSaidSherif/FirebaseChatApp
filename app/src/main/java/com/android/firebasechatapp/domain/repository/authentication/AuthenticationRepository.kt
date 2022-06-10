package com.android.firebasechatapp.domain.repository.authentication

import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import com.google.firebase.auth.AuthResult

interface AuthenticationRepository {
    suspend fun isUserSignedIn(): Boolean
    suspend fun login(email: String, password: String): Resource<AuthResult>
    suspend fun register(email: String, password: String): Resource<AuthResult>
    suspend fun resendVerificationEmail(email: String, password: String): SimpleResource
    suspend fun signOut(): SimpleResource
    suspend fun sendPasswordResetEmail(email: String): SimpleResource
}
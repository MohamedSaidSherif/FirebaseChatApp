package com.android.firebasechatapp.data.repository

import com.android.firebasechatapp.domain.repository.authentication.AuthenticationRepository
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import com.android.firebasechatapp.resource.UiText
import com.android.firebasechatapp.resource.safeCall
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthenticationRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val coroutineDispatcher: CoroutineDispatcher
) : AuthenticationRepository {

    override suspend fun isUserSignedIn(): Boolean = firebaseAuth.currentUser != null

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        return withContext(coroutineDispatcher) {
            safeCall {
                val loginResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                if (loginResult.user?.isEmailVerified == true) {
                    Resource.Success(loginResult)
                } else {
                    firebaseAuth.signOut()
                    Resource.Error(
                        uiText = UiText.DynamicString("Email is not Verified\nCheck your Inbox")
                    )
                }
            }
        }
    }

    override suspend fun register(email: String, password: String): Resource<AuthResult> {
        return withContext(coroutineDispatcher) {
            safeCall {
                val registrationResult: AuthResult =
                    firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                registrationResult.user?.let {
                    it.sendEmailVerification().await()
                    firebaseAuth.signOut()
                    Resource.Success(registrationResult)
                } ?: kotlin.run {
                    Resource.Error(
                        uiText = UiText.DynamicString("Unable to send verification code. please try again")
                    )
                }
            }
        }
    }

    override suspend fun resendVerificationEmail(
        email: String,
        password: String
    ): Resource<Unit> {
        return withContext(coroutineDispatcher) {
            safeCall {
                val loginResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                firebaseAuth.signOut()
                loginResult.user?.let {
                    it.sendEmailVerification().await()
                    Resource.Success(Unit)
                } ?: kotlin.run {
                    Resource.Error(
                        uiText = UiText.DynamicString("Unable to send verification code. please try again")
                    )
                }
            }
        }
    }

    override suspend fun signOut(): SimpleResource {
        return withContext(coroutineDispatcher) {
            safeCall {
                firebaseAuth.signOut()
                Resource.Success(Unit)
            }
        }
    }

    companion object {
        private val TAG = AuthenticationRepositoryImp::class.simpleName.toString()
    }
}
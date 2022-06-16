package com.android.firebasechatapp.data.repository.authentication

import android.content.Context
import com.android.firebasechatapp.R
import com.android.firebasechatapp.data.model.RemoteUser
import com.android.firebasechatapp.domain.model.authentication.AuthenticationState
import com.android.firebasechatapp.domain.repository.authentication.AuthenticationRepository
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import com.android.firebasechatapp.resource.UiText
import com.android.firebasechatapp.resource.safeCall
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthenticationRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val databaseReference: DatabaseReference,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val context: Context
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
                registrationResult.user?.let { firebaseUser ->
                    firebaseUser.sendEmailVerification().await()
                    val remoteUser = RemoteUser(
                        name = email.substring(0, email.indexOf("@")),
                        phone = "",
                        profileImage = "",
                        securityLevel = "1",
                        userId = firebaseUser.uid
                    )
                    databaseReference
                        .child(context.getString(R.string.dbnode_users))
                        .child(firebaseUser.uid)
                        .setValue(remoteUser).await()
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
                loginResult.user?.let {
                    it.sendEmailVerification().await()
                    firebaseAuth.signOut()
                    Resource.Success(Unit)
                } ?: kotlin.run {
                    firebaseAuth.signOut()
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

    override suspend fun sendPasswordResetEmail(email: String): SimpleResource {
        return withContext(coroutineDispatcher) {
            safeCall {
                firebaseAuth.sendPasswordResetEmail(email).await()
                firebaseAuth.signOut()
                Resource.Success(Unit)
            }
        }
    }

    override suspend fun observeAuthState() = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { fAuth ->
            fAuth.currentUser?.let {
                if (it.isEmailVerified) {
                    trySend(AuthenticationState.SignedIn)
                } else {
                    trySend(AuthenticationState.NotVerified)
                }
            } ?: kotlin.run {
                trySend(AuthenticationState.SignedOut)
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    companion object {
        private val TAG = AuthenticationRepositoryImp::class.simpleName.toString()
    }
}
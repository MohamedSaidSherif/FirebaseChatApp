package com.android.firebasechatapp.data.repository

import com.android.firebasechatapp.domain.repository.authentication.AccountSettingRepository
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import com.android.firebasechatapp.resource.UiText
import com.android.firebasechatapp.resource.safeCall
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountSettingRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val coroutineDispatcher: CoroutineDispatcher
) : AccountSettingRepository {

    override suspend fun updateEmail(email: String, password: String): SimpleResource {
        return withContext(coroutineDispatcher) {
            safeCall {
                firebaseAuth.currentUser?.let { firebaseUser ->
                    firebaseUser.email?.let { currentEmail ->
                        if (email == currentEmail) {
                            return@safeCall Resource.Error(uiText = UiText.DynamicString("Current email is the same as new one"))
                        }
                        val emailAuthProvider =
                            EmailAuthProvider.getCredential(currentEmail, password)
                        firebaseUser.reauthenticate(emailAuthProvider).await()
                        firebaseUser.updateEmail(email).await()
                        firebaseUser.sendEmailVerification()
                        firebaseAuth.signOut()
                        Resource.Success(Unit)
                    } ?: kotlin.run {
                        //This case NEVER should happen
                        //and if it's happened, we should logout the user the redirect to login screen
                        Resource.Error(uiText = UiText.DynamicString("User Not Authenticated. Failed to get current email"))
                    }
                } ?: kotlin.run {
                    //This case NEVER should happen
                    //and if it's happened, we should logout the user the redirect to login screen
                    Resource.Error(uiText = UiText.DynamicString("User Not Authenticated. Failed to get current user"))
                }
            }
        }
    }
}
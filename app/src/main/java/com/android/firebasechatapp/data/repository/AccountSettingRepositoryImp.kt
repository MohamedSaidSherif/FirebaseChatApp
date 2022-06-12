package com.android.firebasechatapp.data.repository

import android.content.Context
import com.android.firebasechatapp.R
import com.android.firebasechatapp.domain.model.account_settings.ProfileUpdateResult
import com.android.firebasechatapp.domain.repository.authentication.AccountSettingRepository
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.UiText
import com.android.firebasechatapp.resource.safeCall
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountSettingRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val databaseReference: DatabaseReference,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val context: Context
) : AccountSettingRepository {

    override suspend fun updateProfileData(
        name: String,
        phone: String,
        email: String,
        password: String
    ): Resource<ProfileUpdateResult> {
        return withContext(coroutineDispatcher) {
            safeCall {
                firebaseAuth.currentUser?.let { firebaseUser ->
                    firebaseUser.email?.let { currentEmail ->
                        databaseReference.child(context.getString(R.string.dbnode_users))
                            .child(firebaseUser.uid)
                            .child(context.getString(R.string.field_name))
                            .setValue(name)
                        databaseReference.child(context.getString(R.string.dbnode_users))
                            .child(firebaseUser.uid)
                            .child(context.getString(R.string.field_phone))
                            .setValue(phone)

                        if (email != currentEmail) {
                            val emailAuthProvider =
                                EmailAuthProvider.getCredential(currentEmail, password)
                            firebaseUser.reauthenticate(emailAuthProvider).await()
                            firebaseUser.updateEmail(email).await()
                            firebaseUser.sendEmailVerification()
                            firebaseAuth.signOut()
                        }
                        Resource.Success(
                            ProfileUpdateResult(
                                isEmailUpdated = email != currentEmail
                            )
                        )
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
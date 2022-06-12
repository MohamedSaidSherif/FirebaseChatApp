package com.android.firebasechatapp.data.repository

import android.content.Context
import com.android.firebasechatapp.R
import com.android.firebasechatapp.data.firebase_extension.DataResponse
import com.android.firebasechatapp.data.firebase_extension.singleValueEvent
import com.android.firebasechatapp.data.model.RemoteUser
import com.android.firebasechatapp.data.model.toUser
import com.android.firebasechatapp.domain.model.User
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

    override suspend fun getUser(): Resource<User> {
        return withContext(coroutineDispatcher) {
            safeCall {
                firebaseAuth.currentUser?.let { firebaseUser ->
                    firebaseUser.email?.let { email ->
                        val result =
                            databaseReference.child(context.getString(R.string.dbnode_users))
                                .orderByKey()
                                .equalTo(firebaseUser.uid)
                                .singleValueEvent()
                        when (result) {
                            is DataResponse.Complete -> {
                                result.data.children.forEach {
                                    val remoteUser = it.getValue(RemoteUser::class.java)
                                    remoteUser?.let { remoteUser ->
                                        return@safeCall Resource.Success(remoteUser.toUser(email))
                                    }
                                }
                                Resource.Error(
                                    uiText = UiText.DynamicString("Failed to get user info")
                                )
                            }
                            is DataResponse.Error -> {
                                result.error.printStackTrace()
                                return@safeCall Resource.Error(
                                    uiText = result.error.message?.let { UiText.DynamicString(it) }
                                        ?: UiText.unknownError()
                                )
                            }
                        }
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
                        val emailAuthProvider =
                            EmailAuthProvider.getCredential(currentEmail, password)
                        firebaseUser.reauthenticate(emailAuthProvider).await()
                        databaseReference.child(context.getString(R.string.dbnode_users))
                            .child(firebaseUser.uid)
                            .child(context.getString(R.string.field_name))
                            .setValue(name)
                        databaseReference.child(context.getString(R.string.dbnode_users))
                            .child(firebaseUser.uid)
                            .child(context.getString(R.string.field_phone))
                            .setValue(phone)

                        if (email != currentEmail) {
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
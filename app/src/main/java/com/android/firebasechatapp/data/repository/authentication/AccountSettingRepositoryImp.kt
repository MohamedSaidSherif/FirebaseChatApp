package com.android.firebasechatapp.data.repository.authentication

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.android.firebasechatapp.R
import com.android.firebasechatapp.data.firebase_extension.DataResponse
import com.android.firebasechatapp.data.firebase_extension.singleValueEvent
import com.android.firebasechatapp.data.model.FilePaths
import com.android.firebasechatapp.data.model.RemoteUser
import com.android.firebasechatapp.data.model.toUser
import com.android.firebasechatapp.domain.model.User
import com.android.firebasechatapp.domain.model.account_settings.ProfileData
import com.android.firebasechatapp.domain.model.account_settings.ProfileUpdateResult
import com.android.firebasechatapp.domain.repository.authentication.AccountSettingRepository
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.UiText
import com.android.firebasechatapp.resource.safeCall
import com.android.firebasechatapp.util.URIPathHelper
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

private const val IMAGE_NAME = "profileImage"

class AccountSettingRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val databaseReference: DatabaseReference,
    private val storageReference: StorageReference,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val context: Context
) : AccountSettingRepository {

    override suspend fun getUser(): Resource<User> {
        return firebaseAuth.currentUser?.let { firebaseUser ->
            getUserById(firebaseUser.uid)
        } ?: kotlin.run {
            //This case NEVER should happen
            //and if it's happened, we should logout the user the redirect to login screen
            Resource.Error(uiText = UiText.DynamicString("User Not Authenticated. Failed to get current user"))
        }
    }

    override suspend fun getUserById(userId: String): Resource<User> {
        return withContext(coroutineDispatcher) {
            safeCall {
                firebaseAuth.currentUser?.let { firebaseUser ->
                    firebaseUser.email?.let { email ->
                        val result =
                            databaseReference.child(context.getString(R.string.dbnode_users))
                                .orderByKey() //OR could use ->.orderByChild(getString(R.string.field_user_id))
                                .equalTo(userId)
                                .singleValueEvent()
                        when (result) {
                            is DataResponse.Complete -> {
//                                result.data.children.forEach {
//                                    val remoteUser = it.getValue(RemoteUser::class.java)
//                                    remoteUser?.let { NonNullableRemoteUser ->
//                                        return@safeCall Resource.Success(NonNullableRemoteUser.toUser(email))
//                                    }
//                                }
                                val remoteUser = result.data.children.iterator().next()
                                    .getValue(RemoteUser::class.java)
                                remoteUser?.let { NonNullableRemoteUser ->
                                    return@safeCall Resource.Success(
                                        NonNullableRemoteUser.toUser(
                                            email
                                        )
                                    )
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

    override suspend fun updateProfileData(profileData: ProfileData): Resource<ProfileUpdateResult> {
        return withContext(coroutineDispatcher) {
            safeCall {
                firebaseAuth.currentUser?.let { firebaseUser ->
                    firebaseUser.email?.let { currentEmail ->
                        val emailAuthProvider =
                            EmailAuthProvider.getCredential(currentEmail, profileData.password)
                        firebaseUser.reauthenticate(emailAuthProvider).await()
                        databaseReference.child(context.getString(R.string.dbnode_users))
                            .child(firebaseUser.uid)
                            .child(context.getString(R.string.field_name))
                            .setValue(profileData.name)
                        databaseReference.child(context.getString(R.string.dbnode_users))
                            .child(firebaseUser.uid)
                            .child(context.getString(R.string.field_phone))
                            .setValue(profileData.phone)

                        profileData.imageUri?.let {
                            val imageUrl = updateImage(firebaseUser, it)
                            databaseReference.child(context.getString(R.string.dbnode_users))
                                .child(firebaseUser.uid)
                                .child(context.getString(R.string.field_profile_image))
                                .setValue(imageUrl)
                        }

                        if (profileData.email != currentEmail) {
                            firebaseUser.updateEmail(profileData.email).await()
                            firebaseUser.sendEmailVerification()
                            firebaseAuth.signOut()
                        }
                        Resource.Success(
                            ProfileUpdateResult(
                                isEmailUpdated = profileData.email != currentEmail
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

    override suspend fun getSecurityLevel(): Resource<Int> {
        return when (val result = getUser()) {
            is Resource.Success -> {
                Resource.Success(Integer.parseInt(result.data.securityLevel))
            }
            else -> {
                val uiText = if (result is Resource.Error) {
                    result.uiText
                } else {
                    UiText.unknownError()
                }
                Resource.Error(uiText)
            }
        }
    }

    private suspend fun updateImage(firebaseUser: FirebaseUser, imageUri: Uri): String {
        val imagePath = URIPathHelper.getPath(context, imageUri)
        val compressedImage = Compressor.compress(context, File(imagePath))
        val storageMetadata = StorageMetadata.Builder()
            .setContentType("image/jpg")
            .setContentLanguage("en")
            .build()
        val storageRef = storageReference.child(
            FilePaths.FIREBASE_IMAGE_STORAGE +
                    "/" + firebaseUser.uid +
                    "/" + IMAGE_NAME
        )
        storageRef.putFile(compressedImage.toUri(), storageMetadata).await()
        return storageRef.downloadUrl.await().toString()
    }
}
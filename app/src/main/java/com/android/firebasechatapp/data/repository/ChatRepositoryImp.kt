package com.android.firebasechatapp.data.repository

import android.content.Context
import com.android.firebasechatapp.R
import com.android.firebasechatapp.data.model.RemoteChatMessage
import com.android.firebasechatapp.data.model.RemoteChatRoom
import com.android.firebasechatapp.domain.repository.authentication.ChatRepository
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import com.android.firebasechatapp.resource.UiText
import com.android.firebasechatapp.resource.safeCall
import com.android.firebasechatapp.util.getTimestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImp @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val databaseReference: DatabaseReference,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val context: Context
) : ChatRepository {

    override suspend fun createNewChatRoom(name: String, securityLevel: Int): SimpleResource {
        return withContext(coroutineDispatcher) {
            safeCall {
                firebaseAuth.currentUser?.let { firebaseUser ->
                    val chatRoomId = databaseReference
                        .child(context.getString(R.string.dbnode_chat_rooms))
                        .push().key
                        ?: return@safeCall Resource.Error(
                            UiText.DynamicString(
                                "Failed to create room. please try again"
                            )
                        )
                    val chatRoom = RemoteChatRoom(
                        chatRoomName = name,
                        creatorId = firebaseUser.uid,
                        securityLevel = securityLevel,
                        chatRoomId = chatRoomId
                    )
                    databaseReference.child(context.getString(R.string.dbnode_chat_rooms))
                        .child(chatRoomId)
                        .setValue(chatRoom)

                    //create a unique id for the message
                    val messageId = databaseReference
                        .child(context.getString(R.string.dbnode_chat_rooms))
                        .push().key
                        ?: return@safeCall Resource.Error(
                            UiText.DynamicString(
                                "Failed to create room. please try again"
                            )
                        )

                    //insert the first message into the chatroom
                    val chatMessage = RemoteChatMessage(
                        message = "Welcome to the new chatroom!",
                        timestamp = getTimestamp()
                    )
                    databaseReference
                        .child(context.getString(R.string.dbnode_chat_rooms))
                        .child(chatRoomId)
                        .child(context.getString(R.string.field_chatroom_messages))
                        .child(messageId)
                        .setValue(chatMessage)
                    Resource.Success(Unit)
                } ?: kotlin.run {
                    //This case NEVER should happen
                    //and if it's happened, we should logout the user the redirect to login screen
                    Resource.Error(
                        UiText.DynamicString("User Not Authenticated. Failed to get current user")
                    )
                }
            }
        }
    }
}
package com.android.firebasechatapp.data.repository.chat

import android.content.Context
import android.util.Log
import com.android.firebasechatapp.R
import com.android.firebasechatapp.data.firebase_extension.DataResponse
import com.android.firebasechatapp.data.firebase_extension.singleValueEvent
import com.android.firebasechatapp.data.model.RemoteChatMessage
import com.android.firebasechatapp.data.model.RemoteChatRoom
import com.android.firebasechatapp.data.model.toDomain
import com.android.firebasechatapp.domain.model.chat.ChatRoom
import com.android.firebasechatapp.domain.repository.chat.ChatRepository
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import com.android.firebasechatapp.resource.UiText
import com.android.firebasechatapp.resource.safeCall
import com.android.firebasechatapp.util.getTimestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

private val TAG = ChatRepositoryImp::class.simpleName.toString()

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

    override suspend fun getChatRooms(): Resource<List<ChatRoom>> {
        return withContext(coroutineDispatcher) {
            safeCall {
                val dataResponse =
                    databaseReference.child(context.getString(R.string.dbnode_chat_rooms))
                        .orderByKey()
                        .singleValueEvent()
                when (dataResponse) {
                    is DataResponse.Complete -> {
                        val chatRoomList = mutableListOf<ChatRoom>()
                        for (singleSnapshot: DataSnapshot in dataResponse.data.children) {
                            if (singleSnapshot.exists()) {
                                val objectMap: Map<String, Any> =
                                    singleSnapshot.value as HashMap<String, Any>

                                //get the chat room messages
                                val messagesList = mutableListOf<RemoteChatMessage>()
                                for (snapshot in singleSnapshot
                                    .child(context.getString(R.string.field_chatroom_messages)).children) {
                                    val remoteChatMessage =
                                        snapshot.getValue(RemoteChatMessage::class.java)
                                    remoteChatMessage?.let {
                                        messagesList.add(it)
                                    }
                                }

                                //get the list of users who have joined the chatroom
                                val users = mutableListOf<String>()
                                for (snapshot in singleSnapshot
                                    .child(context.getString(R.string.field_users)).children) {
                                    snapshot.key?.let { users.add(it) }
                                }
                                val remoteChatRoom = RemoteChatRoom(
                                    chatRoomId = objectMap[context.getString(R.string.field_chatroom_id)].toString(),
                                    chatRoomName = objectMap[context.getString(R.string.field_chatroom_name)].toString(),
                                    creatorId = objectMap[context.getString(R.string.field_creator_id)].toString(),
                                    securityLevel = (objectMap[context.getString(R.string.field_security_level)] as Long).toInt(),
                                    chatRoomMessages = messagesList,
                                    users = users
                                )
                                Log.d(TAG, "getChatRooms: Found Room: $remoteChatRoom")
                                chatRoomList.add(remoteChatRoom.toDomain())
                            }
                        }
                        Resource.Success(chatRoomList)
                    }
                    is DataResponse.Error -> {
                        dataResponse.error.printStackTrace()
                        return@safeCall Resource.Error(
                            uiText = dataResponse.error.message?.let { UiText.DynamicString(it) }
                                ?: UiText.unknownError()
                        )
                    }
                }
            }
        }
    }
}
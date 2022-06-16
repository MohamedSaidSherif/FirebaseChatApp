package com.android.firebasechatapp.presentation.chat_screen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.firebasechatapp.R
import com.android.firebasechatapp.domain.model.chat.ChatRoom
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.layout_chat_rooms_list_item.view.*

class ChatRoomsAdapter(
    private val viewModel: ChatViewModel,
    private val glide: RequestManager
) : RecyclerView.Adapter<ChatRoomsAdapter.ChatRoomItemViewHolder>() {

    class ChatRoomItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<ChatRoom>() {
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem.chatRoomId == newItem.chatRoomId
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var chatRooms: List<ChatRoom>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomItemViewHolder {
        return ChatRoomItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_chat_rooms_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }

    override fun onBindViewHolder(holder: ChatRoomItemViewHolder, position: Int) {
        val chatRoom = chatRooms[position]
        holder.itemView.apply {
            name.text = chatRoom.chatRoomName
            number_chatmessages.text = chatRoom.chatRoomMessages.size.toString()
            viewModel.onEvent(
                ChatScreenEvent.GetUserById(chatRoom.creatorId) {
                    creator_name.text = it.name
                    glide.load(it.profileImage).into(profile_image)
                }
            )
        }
    }
}
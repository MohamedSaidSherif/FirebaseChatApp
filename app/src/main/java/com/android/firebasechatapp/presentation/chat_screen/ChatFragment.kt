package com.android.firebasechatapp.presentation.chat_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.firebasechatapp.databinding.FragmentChatBinding
import com.android.firebasechatapp.presentation.collectLatestLifecycleFlow
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatRoomsAdapter: ChatRoomsAdapter
    @Inject
    lateinit var requestManager: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setUpListenerForViews()
        subscribeToObservers()
    }

    private fun setRecyclerView() {
        chatRoomsAdapter = ChatRoomsAdapter(viewModel, requestManager)
        binding.recyclerView.apply {
            adapter = chatRoomsAdapter
            layoutManager = LinearLayoutManager(requireContext())
//            ItemTouchHelper(itemTouchCallback).attachToRecyclerView(this)
        }
    }

    private fun setUpListenerForViews() {
        with(binding) {
            fab.setOnClickListener {
                val newChatRoomDialog = NewChatRoomDialog()
                newChatRoomDialog.show(childFragmentManager, "newChatRoomDialog")
            }
        }
    }

    private fun subscribeToObservers() {
        collectLatestLifecycleFlow(viewModel.chatScreenState) {
            when (it) {
                is ChatScreenState.ChatRooms -> {
                    chatRoomsAdapter.chatRooms = it.chatRooms
                }
            }
        }
    }
}
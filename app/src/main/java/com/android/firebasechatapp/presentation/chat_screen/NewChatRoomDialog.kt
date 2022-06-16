package com.android.firebasechatapp.presentation.chat_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.android.firebasechatapp.databinding.DialogNewChatRoomBinding
import com.android.firebasechatapp.presentation.collectLatestLifecycleFlow
import com.android.firebasechatapp.presentation.showToast
import com.android.firebasechatapp.resource.asString

class NewChatRoomDialog : DialogFragment() {

    private lateinit var binding: DialogNewChatRoomBinding
    private val viewModel: ChatViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var userSecurityLevel: Int? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    /** The system calls this to get the DialogFragment's layout, regardless
    of whether it's being displayed as a dialog or an embedded fragment. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as dialog or embedded fragment
        binding = DialogNewChatRoomBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setUpListenerForViews()
        observeState()
    }

    private fun initViews() {
        with(binding) {
            securityLevel.text = "0"
        }
    }

    private fun setUpListenerForViews() {
        with(binding) {
            inputSecurityLevel.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    binding.securityLevel.text = p1.toString()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            createChatroom.setOnClickListener {
                viewModel.onEvent(
                    ChatScreenEvent.CreateNewChatRoom(
                        name = inputChatroomName.text.toString(),
                        securityLevel = inputSecurityLevel.progress
                    )
                )
            }
        }
    }

    private fun observeState() {
        collectLatestLifecycleFlow(viewModel.userSecurityLevel) {
            userSecurityLevel = it
        }
        collectLatestLifecycleFlow(viewModel.chatScreenState) {
            when (it) {
                ChatScreenState.NewChatRoomIsCreated -> {
                    showToast("Room is created successfully")
                    dismiss()
                }
            }
        }
        collectLatestLifecycleFlow(viewModel.progressVisibleState) {
            binding.progressBar.isVisible = it
        }
        collectLatestLifecycleFlow(viewModel.errorFlow) {
            showToast(it.asString(requireContext()))
        }
    }

    companion object {
        private val TAG = NewChatRoomDialog::class.simpleName.toString()
    }
}
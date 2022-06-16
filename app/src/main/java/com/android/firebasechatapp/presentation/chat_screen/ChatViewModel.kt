package com.android.firebasechatapp.presentation.chat_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.firebasechatapp.domain.use_case.chat.CreateNewChatRoomUseCase
import com.android.firebasechatapp.domain.use_case.chat.GetUserSecurityLevelUseCase
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getUserSecurityLevelUseCase: GetUserSecurityLevelUseCase,
    private val createNewChatRoomUseCase: CreateNewChatRoomUseCase
) : ViewModel() {

    private val _userSecurityLevel = MutableStateFlow<Int?>(null)
    val userSecurityLevel: StateFlow<Int?> = _userSecurityLevel.asStateFlow()

    private val _chatScreenState = MutableSharedFlow<ChatScreenState>()
    val chatScreenState: SharedFlow<ChatScreenState> = _chatScreenState.asSharedFlow()

    private val _progressVisibleState = MutableStateFlow(false)
    val progressVisibleState: StateFlow<Boolean> = _progressVisibleState.asStateFlow()

    private val _errorFlow = MutableSharedFlow<UiText>()
    val errorFlow: SharedFlow<UiText> = _errorFlow.asSharedFlow()

    init {
        getUserSecurityLevel()
    }

    fun onEvent(event: ChatScreenEvent) {
        when (event) {
            is ChatScreenEvent.CreateNewChatRoom -> {
                createNewChatRoom(event.name, event.securityLevel)
            }
        }
    }

    private fun getUserSecurityLevel() {
        viewModelScope.launch {
            _progressVisibleState.emit(true)
            when(val result = getUserSecurityLevelUseCase()) {
                is Resource.Success -> {
                    _progressVisibleState.emit(false)
                    _userSecurityLevel.emit(result.data)
                }
                is Resource.Error -> {
                    _progressVisibleState.emit(false)
                    _errorFlow.emit(result.uiText)
                }
            }
        }
    }

    private fun createNewChatRoom(name: String, securityLevel: Int) {
        viewModelScope.launch {
            userSecurityLevel.value?.let {
                _progressVisibleState.emit(true)
                when (val result = createNewChatRoomUseCase(
                    name = name,
                    securityLevel = securityLevel,
                    userSecurityLevel = it
                )) {
                    is Resource.Success -> {
                        _progressVisibleState.emit(false)
                        _chatScreenState.emit(ChatScreenState.NewChatRoomIsCreated)
                    }
                    is Resource.Error -> {
                        _progressVisibleState.emit(false)
                        _errorFlow.emit(result.uiText)
                    }
                }
            } ?: kotlin.run {
                _errorFlow.emit(
                    UiText.DynamicString("Failed to get user security level. please try again")
                )
            }
        }
    }
}
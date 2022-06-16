package com.android.firebasechatapp.presentation.chat_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.firebasechatapp.domain.model.User
import com.android.firebasechatapp.domain.use_case.account_settings.GetUserByIdUseCase
import com.android.firebasechatapp.domain.use_case.chat.CreateNewChatRoomUseCase
import com.android.firebasechatapp.domain.use_case.chat.GetChatRoomsUseCase
import com.android.firebasechatapp.domain.use_case.chat.GetUserSecurityLevelUseCase
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TAG = ChatViewModel::class.simpleName.toString()

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatRoomsUseCase: GetChatRoomsUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
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
        getChatRooms()
        getUserSecurityLevel()
    }

    fun onEvent(event: ChatScreenEvent) {
        when (event) {
            is ChatScreenEvent.CreateNewChatRoom -> {
                createNewChatRoom(event.name, event.securityLevel)
            }
            is ChatScreenEvent.GetUserById -> {
                getUserById(event.userId, event.onUserRetrieved)
            }
        }
    }

    private fun getChatRooms() {
        viewModelScope.launch {
            _progressVisibleState.emit(true)
            when (val result = getChatRoomsUseCase()) {
                is Resource.Success -> {
                    _progressVisibleState.emit(false)
                    _chatScreenState.emit(ChatScreenState.ChatRooms(result.data))
                }
                is Resource.Error -> {
                    _progressVisibleState.emit(false)
                    _errorFlow.emit(result.uiText)
                }
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

    private fun getUserById(userId: String, onUserRetrieved: (user: User) -> Unit) {
        viewModelScope.launch {
            when (val result = getUserByIdUseCase(userId)) {
                is Resource.Success -> {
                    onUserRetrieved(result.data)
                }
                is Resource.Error -> {
                    Log.e(TAG, "getUserById: Failed to get user with id [$userId]")
                }
            }
        }
    }
}
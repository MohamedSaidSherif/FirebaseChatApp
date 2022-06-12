package com.android.firebasechatapp.domain.model.authentication

sealed class AuthenticationState {
    object SignedIn: AuthenticationState()
    object NotVerified: AuthenticationState()
    object SignedOut: AuthenticationState()
}

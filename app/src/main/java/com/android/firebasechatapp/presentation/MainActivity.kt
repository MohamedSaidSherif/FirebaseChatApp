package com.android.firebasechatapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.android.firebasechatapp.R
import com.android.firebasechatapp.domain.model.authentication.AuthenticationState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

//    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        observeAuthenticationState()
    }

//    private fun observeAuthenticationState() {
//        collectLifecycleFlow(viewModel.authenticationState) {
//            println("observeAuthenticationState: $it")
//            when(it) {
//                AuthenticationState.SignedIn -> {
//
//                }
//                AuthenticationState.NotVerified -> {
//
//                }
//                AuthenticationState.SignedOut -> {
//
//                }
//            }
//        }
//    }
}
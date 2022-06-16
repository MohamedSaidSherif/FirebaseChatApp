package com.android.firebasechatapp.presentation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.android.firebasechatapp.R
import com.android.firebasechatapp.domain.model.authentication.AuthenticationState
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

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

    companion object {

        /** Use external media if it is available, our app's file directory otherwise */
        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }

}
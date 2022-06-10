package com.android.firebasechatapp.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.firebasechatapp.R
import com.android.firebasechatapp.presentation.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLoginState()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).apply {
            supportActionBar?.hide()
            window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).apply {
            supportActionBar?.show()
            window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    private fun observeLoginState() {
        collectLatestLifecycleFlow(viewModel.isUserSignedInState) { result ->
            result?.let {
                if (it) {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
                } else {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                }
            }
        }
    }
}
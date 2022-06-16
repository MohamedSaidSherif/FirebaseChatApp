package com.android.firebasechatapp.presentation.home

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.firebasechatapp.R
import com.android.firebasechatapp.databinding.FragmentHomeBinding
import com.android.firebasechatapp.presentation.collectLatestLifecycleFlow
import com.android.firebasechatapp.presentation.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeHomeState()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.optionIssues -> {
                true
            }
            R.id.optionChat -> {
                true
            }
            R.id.optionAccountSettings -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAccountSettingsFragment(null))
                return true
            }
            R.id.optionAdmin -> {
                return true
            }
            R.id.optionSignOut -> {
                viewModel.onEvent(HomeEvent.SignOut)
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun observeHomeState() {
        collectLatestLifecycleFlow(viewModel.homeState) {
            updateUI(it)
        }
    }

    private fun updateUI(state: HomeState) {
        with(binding) {
            progressBar.isVisible = state.progressBarVisible
        }
        if (state.isSignedOut) {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
        } else if(state.errorUiText != null) {
            showToast("Failed to sign out. please try again")
        }
    }
}
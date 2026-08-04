package com.example.swiftshare.presentation.splash.ui

import androidx.fragment.app.viewModels
import com.example.swiftshare.R
import com.example.swiftshare.presentation.common.NavPlaceholderFragment
import com.example.swiftshare.presentation.common.NavPlaceholderFragment.PlaceholderAction
import com.example.swiftshare.presentation.splash.viewmodels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : NavPlaceholderFragment(
    screenTitle = "SwiftShare",
    actions = listOf(PlaceholderAction("Continue") { navigate(R.id.action_splash_to_onboarding) })
) {
    private val viewModel: SplashViewModel by viewModels()
}
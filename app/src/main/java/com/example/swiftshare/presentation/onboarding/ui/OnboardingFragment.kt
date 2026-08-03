package com.example.swiftshare.presentation.onboarding.ui

import com.example.swiftshare.R
import com.example.swiftshare.presentation.common.NavPlaceholderFragment
import com.example.swiftshare.presentation.common.NavPlaceholderFragment.PlaceholderAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : NavPlaceholderFragment(
    screenTitle = "Onboarding — share files offline, instantly",
    actions = listOf(
        PlaceholderAction("Get Started") { navigate(R.id.action_onboarding_to_permissionRationale) }
    )
)
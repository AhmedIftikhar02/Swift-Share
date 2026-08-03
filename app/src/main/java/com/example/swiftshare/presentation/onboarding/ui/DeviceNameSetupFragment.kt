package com.example.swiftshare.presentation.onboarding.ui


import com.example.swiftshare.R
import com.example.swiftshare.presentation.common.NavPlaceholderFragment
import com.example.swiftshare.presentation.common.NavPlaceholderFragment.PlaceholderAction
import dagger.hilt.android.AndroidEntryPoint

/** Real device-name persistence wired in Phase 11 (shares its use case with Settings). */
@AndroidEntryPoint
class DeviceNameSetupFragment : NavPlaceholderFragment(
    screenTitle = "What should nearby devices call you?",
    actions = listOf(
        PlaceholderAction("Continue") { navigate(R.id.action_deviceNameSetup_to_discoveryGraph) }
    )
)
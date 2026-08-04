package com.example.swiftshare.presentation.settings.ui


import com.example.swiftshare.R
import com.example.swiftshare.presentation.common.NavPlaceholderFragment
import com.example.swiftshare.presentation.common.NavPlaceholderFragment.PlaceholderAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : NavPlaceholderFragment(
    screenTitle = "Settings",
    actions = listOf(
        PlaceholderAction("About") { navigate(R.id.action_settings_to_appInformation) }
    )
)
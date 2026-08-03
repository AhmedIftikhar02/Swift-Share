package com.example.swiftshare.presentation.history.ui

import com.example.swiftshare.R
import com.example.swiftshare.presentation.common.NavPlaceholderFragment
import com.example.swiftshare.presentation.common.NavPlaceholderFragment.PlaceholderAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferHistoryFragment : NavPlaceholderFragment(
    screenTitle = "History",
    actions = listOf(
        PlaceholderAction("Open a record") { navigate(R.id.action_transferHistory_to_historyDetail) }
    )
)
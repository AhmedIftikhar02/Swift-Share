package com.example.swiftshare.presentation.history.ui

import com.example.swiftshare.R
import com.example.swiftshare.presentation.common.NavPlaceholderFragment
import com.example.swiftshare.presentation.common.NavPlaceholderFragment.PlaceholderAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryDetailFragment : NavPlaceholderFragment(
    screenTitle = "Transfer Detail",
    actions = listOf(
        PlaceholderAction("Retry") { navigate(R.id.action_historyDetail_to_activeTransferDetailRetry) }
    )
)
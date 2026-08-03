package com.example.swiftshare.presentation.completion.ui

import com.example.swiftshare.R
import com.example.swiftshare.presentation.common.NavPlaceholderFragment
import com.example.swiftshare.presentation.common.NavPlaceholderFragment.PlaceholderAction
import dagger.hilt.android.AndroidEntryPoint

/** Real success/partial-failure summary wired in Phase 7/8. */
@AndroidEntryPoint
class CompletionFragment : NavPlaceholderFragment(
    screenTitle = "Transfer Complete",
    actions = listOf(
        PlaceholderAction("Done") { navigate(R.id.action_completion_to_discovery) },
        PlaceholderAction("Send more") { navigate(R.id.action_completion_to_fileQueueReview) }
    )
)
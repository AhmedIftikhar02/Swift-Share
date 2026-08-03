package com.example.swiftshare.presentation.activetransfer.ui

import com.example.swiftshare.R
import com.example.swiftshare.presentation.common.NavPlaceholderFragment
import com.example.swiftshare.presentation.common.NavPlaceholderFragment.PlaceholderAction
import dagger.hilt.android.AndroidEntryPoint

/** Real live progress UI wired starting Phase 7 (engine) through Phase 8 (controls).
 *  This single class serves both nav-graph destinations that point to it
 *  (discoveryGraph's activeTransferDetailFragment and historyGraph's retry destination). */
@AndroidEntryPoint
class ActiveTransferDetailFragment : NavPlaceholderFragment(
    screenTitle = "Transferring",
    actions = listOf(
        PlaceholderAction("Simulate completion") { navigate(R.id.action_activeTransferDetail_to_completion) }
    )
)
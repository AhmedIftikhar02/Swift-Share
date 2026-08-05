package com.example.swiftshare.presentation.activetransfer.ui

import androidx.navigation.NavDirections
import com.example.swiftshare.HistoryGraphDirections

public class ActiveTransferDetailFragmentDirections private constructor() {
  public companion object {
    public fun actionGlobalActiveTransferDetail(): NavDirections =
        HistoryGraphDirections.actionGlobalActiveTransferDetail()

    public fun actionGlobalCompletion(): NavDirections =
        HistoryGraphDirections.actionGlobalCompletion()
  }
}

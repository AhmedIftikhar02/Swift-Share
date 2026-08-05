package com.example.swiftshare

import androidx.navigation.NavDirections

public class DiscoveryGraphDirections private constructor() {
  public companion object {
    public fun actionGlobalActiveTransferDetail(): NavDirections =
        NavGraphDirections.actionGlobalActiveTransferDetail()

    public fun actionGlobalCompletion(): NavDirections = NavGraphDirections.actionGlobalCompletion()
  }
}

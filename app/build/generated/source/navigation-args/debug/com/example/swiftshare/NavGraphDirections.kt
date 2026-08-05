package com.example.swiftshare

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections

public class NavGraphDirections private constructor() {
  public companion object {
    public fun actionGlobalActiveTransferDetail(): NavDirections =
        ActionOnlyNavDirections(R.id.action_global_activeTransferDetail)

    public fun actionGlobalCompletion(): NavDirections =
        ActionOnlyNavDirections(R.id.action_global_completion)
  }
}

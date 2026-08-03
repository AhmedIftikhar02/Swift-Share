package com.example.swiftshare.presentation.activetransfer.ui

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.swiftshare.R

public class ActiveTransferDetailFragmentDirections private constructor() {
  public companion object {
    public fun actionActiveTransferDetailToCompletion(): NavDirections =
        ActionOnlyNavDirections(R.id.action_activeTransferDetail_to_completion)
  }
}

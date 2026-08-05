package com.example.swiftshare.presentation.onboarding.ui

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.swiftshare.NavGraphDirections
import com.example.swiftshare.R

public class DeviceNameSetupFragmentDirections private constructor() {
  public companion object {
    public fun actionDeviceNameSetupToDiscoveryGraph(): NavDirections =
        ActionOnlyNavDirections(R.id.action_deviceNameSetup_to_discoveryGraph)

    public fun actionGlobalActiveTransferDetail(): NavDirections =
        NavGraphDirections.actionGlobalActiveTransferDetail()

    public fun actionGlobalCompletion(): NavDirections = NavGraphDirections.actionGlobalCompletion()
  }
}

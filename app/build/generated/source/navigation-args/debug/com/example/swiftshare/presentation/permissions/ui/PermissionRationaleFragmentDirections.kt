package com.example.swiftshare.presentation.permissions.ui

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.swiftshare.NavGraphDirections
import com.example.swiftshare.R

public class PermissionRationaleFragmentDirections private constructor() {
  public companion object {
    public fun actionPermissionRationaleToDeviceNameSetup(): NavDirections =
        ActionOnlyNavDirections(R.id.action_permissionRationale_to_deviceNameSetup)

    public fun actionGlobalActiveTransferDetail(): NavDirections =
        NavGraphDirections.actionGlobalActiveTransferDetail()

    public fun actionGlobalCompletion(): NavDirections = NavGraphDirections.actionGlobalCompletion()
  }
}

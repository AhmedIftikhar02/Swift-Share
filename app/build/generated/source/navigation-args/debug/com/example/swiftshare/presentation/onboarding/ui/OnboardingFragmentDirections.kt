package com.example.swiftshare.presentation.onboarding.ui

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.swiftshare.NavGraphDirections
import com.example.swiftshare.R

public class OnboardingFragmentDirections private constructor() {
  public companion object {
    public fun actionOnboardingToPermissionRationale(): NavDirections =
        ActionOnlyNavDirections(R.id.action_onboarding_to_permissionRationale)

    public fun actionGlobalActiveTransferDetail(): NavDirections =
        NavGraphDirections.actionGlobalActiveTransferDetail()

    public fun actionGlobalCompletion(): NavDirections = NavGraphDirections.actionGlobalCompletion()
  }
}

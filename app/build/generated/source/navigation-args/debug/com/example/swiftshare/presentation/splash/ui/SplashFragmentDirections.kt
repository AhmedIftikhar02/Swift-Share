package com.example.swiftshare.presentation.splash.ui

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.swiftshare.NavGraphDirections
import com.example.swiftshare.R

public class SplashFragmentDirections private constructor() {
  public companion object {
    public fun actionSplashToOnboarding(): NavDirections =
        ActionOnlyNavDirections(R.id.action_splash_to_onboarding)

    public fun actionSplashToDiscoveryGraph(): NavDirections =
        ActionOnlyNavDirections(R.id.action_splash_to_discoveryGraph)

    public fun actionGlobalActiveTransferDetail(): NavDirections =
        NavGraphDirections.actionGlobalActiveTransferDetail()

    public fun actionGlobalCompletion(): NavDirections = NavGraphDirections.actionGlobalCompletion()
  }
}

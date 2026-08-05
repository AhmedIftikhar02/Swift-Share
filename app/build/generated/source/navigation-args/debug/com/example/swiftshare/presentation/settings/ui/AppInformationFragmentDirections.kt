package com.example.swiftshare.presentation.settings.ui

import androidx.navigation.NavDirections
import com.example.swiftshare.SettingsGraphDirections

public class AppInformationFragmentDirections private constructor() {
  public companion object {
    public fun actionGlobalActiveTransferDetail(): NavDirections =
        SettingsGraphDirections.actionGlobalActiveTransferDetail()

    public fun actionGlobalCompletion(): NavDirections =
        SettingsGraphDirections.actionGlobalCompletion()
  }
}

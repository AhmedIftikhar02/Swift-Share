package com.example.swiftshare.presentation.settings.ui

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.swiftshare.R

public class SettingsFragmentDirections private constructor() {
  public companion object {
    public fun actionSettingsToAppInformation(): NavDirections =
        ActionOnlyNavDirections(R.id.action_settings_to_appInformation)
  }
}

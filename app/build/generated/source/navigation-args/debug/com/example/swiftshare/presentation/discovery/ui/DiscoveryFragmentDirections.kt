package com.example.swiftshare.presentation.discovery.ui

import android.os.Bundle
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.swiftshare.R
import kotlin.Int
import kotlin.String

public class DiscoveryFragmentDirections private constructor() {
  private data class ActionDiscoveryToConnectionConfirmation(
    public val endpointId: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_discovery_to_connectionConfirmation

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("endpointId", this.endpointId)
        return result
      }
  }

  public companion object {
    public fun actionDiscoveryToConnectionConfirmation(endpointId: String = ""): NavDirections =
        ActionDiscoveryToConnectionConfirmation(endpointId)

    public fun actionDiscoveryToQrPairing(): NavDirections =
        ActionOnlyNavDirections(R.id.action_discovery_to_qrPairing)

    public fun actionDiscoveryToPinPairing(): NavDirections =
        ActionOnlyNavDirections(R.id.action_discovery_to_pinPairing)
  }
}

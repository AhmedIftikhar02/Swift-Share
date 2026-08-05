package com.example.swiftshare.presentation.pairing.ui

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.swiftshare.DiscoveryGraphDirections
import com.example.swiftshare.R
import kotlin.Int
import kotlin.String

public class ConnectionConfirmationDialogDirections private constructor() {
  private data class ActionConnectionConfirmationToTransferHub(
    public val endpointId: String = "",
    public val deviceName: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_connectionConfirmation_to_transferHub

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("endpointId", this.endpointId)
        result.putString("deviceName", this.deviceName)
        return result
      }
  }

  public companion object {
    public fun actionConnectionConfirmationToTransferHub(endpointId: String = "", deviceName: String
        = ""): NavDirections = ActionConnectionConfirmationToTransferHub(endpointId, deviceName)

    public fun actionGlobalActiveTransferDetail(): NavDirections =
        DiscoveryGraphDirections.actionGlobalActiveTransferDetail()

    public fun actionGlobalCompletion(): NavDirections =
        DiscoveryGraphDirections.actionGlobalCompletion()
  }
}

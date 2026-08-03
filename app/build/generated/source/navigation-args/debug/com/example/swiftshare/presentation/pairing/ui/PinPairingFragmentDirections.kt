package com.example.swiftshare.presentation.pairing.ui

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.swiftshare.R
import kotlin.Int
import kotlin.String

public class PinPairingFragmentDirections private constructor() {
  private data class ActionPinPairingToConnectionConfirmation(
    public val endpointId: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_pinPairing_to_connectionConfirmation

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("endpointId", this.endpointId)
        return result
      }
  }

  public companion object {
    public fun actionPinPairingToConnectionConfirmation(endpointId: String = ""): NavDirections =
        ActionPinPairingToConnectionConfirmation(endpointId)
  }
}

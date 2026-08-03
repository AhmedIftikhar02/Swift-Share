package com.example.swiftshare.presentation.transferhub.ui

import android.os.Bundle
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.swiftshare.R
import kotlin.Int
import kotlin.String

public class TransferHubFragmentDirections private constructor() {
  private data class ActionTransferHubToFileQueueReview(
    public val endpointId: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_transferHub_to_fileQueueReview

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("endpointId", this.endpointId)
        return result
      }
  }

  private data class ActionTransferHubToActiveTransferDetail(
    public val sessionId: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_transferHub_to_activeTransferDetail

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("sessionId", this.sessionId)
        return result
      }
  }

  public companion object {
    public fun actionTransferHubToQrPairing(): NavDirections =
        ActionOnlyNavDirections(R.id.action_transferHub_to_qrPairing)

    public fun actionTransferHubToPinPairing(): NavDirections =
        ActionOnlyNavDirections(R.id.action_transferHub_to_pinPairing)

    public fun actionTransferHubToFileQueueReview(endpointId: String = ""): NavDirections =
        ActionTransferHubToFileQueueReview(endpointId)

    public fun actionTransferHubToActiveTransferDetail(sessionId: String = ""): NavDirections =
        ActionTransferHubToActiveTransferDetail(sessionId)
  }
}

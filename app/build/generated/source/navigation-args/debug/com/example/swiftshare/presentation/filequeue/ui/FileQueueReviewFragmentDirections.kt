package com.example.swiftshare.presentation.filequeue.ui

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.swiftshare.DiscoveryGraphDirections
import com.example.swiftshare.R
import kotlin.Int
import kotlin.String

public class FileQueueReviewFragmentDirections private constructor() {
  private data class ActionFileQueueReviewToActiveTransferDetail(
    public val sessionId: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_fileQueueReview_to_activeTransferDetail

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("sessionId", this.sessionId)
        return result
      }
  }

  public companion object {
    public fun actionFileQueueReviewToActiveTransferDetail(sessionId: String = ""): NavDirections =
        ActionFileQueueReviewToActiveTransferDetail(sessionId)

    public fun actionGlobalActiveTransferDetail(): NavDirections =
        DiscoveryGraphDirections.actionGlobalActiveTransferDetail()

    public fun actionGlobalCompletion(): NavDirections =
        DiscoveryGraphDirections.actionGlobalCompletion()
  }
}

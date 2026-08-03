package com.example.swiftshare.presentation.history.ui

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.swiftshare.R
import kotlin.Int
import kotlin.String

public class HistoryDetailFragmentDirections private constructor() {
  private data class ActionHistoryDetailToActiveTransferDetailRetry(
    public val sessionId: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_historyDetail_to_activeTransferDetailRetry

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("sessionId", this.sessionId)
        return result
      }
  }

  public companion object {
    public fun actionHistoryDetailToActiveTransferDetailRetry(sessionId: String = ""): NavDirections
        = ActionHistoryDetailToActiveTransferDetailRetry(sessionId)
  }
}

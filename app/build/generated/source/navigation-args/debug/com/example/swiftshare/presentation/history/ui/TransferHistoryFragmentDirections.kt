package com.example.swiftshare.presentation.history.ui

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.swiftshare.R
import kotlin.Int
import kotlin.String

public class TransferHistoryFragmentDirections private constructor() {
  private data class ActionTransferHistoryToHistoryDetail(
    public val sessionId: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_transferHistory_to_historyDetail

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("sessionId", this.sessionId)
        return result
      }
  }

  public companion object {
    public fun actionTransferHistoryToHistoryDetail(sessionId: String = ""): NavDirections =
        ActionTransferHistoryToHistoryDetail(sessionId)
  }
}

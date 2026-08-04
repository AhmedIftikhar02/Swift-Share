package com.example.swiftshare.presentation.activetransfer.ui

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.swiftshare.R
import kotlin.Int
import kotlin.String

public class ActiveTransferDetailFragmentDirections private constructor() {
  private data class ActionActiveTransferDetailToCompletion(
    public val sessionId: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_activeTransferDetail_to_completion

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("sessionId", this.sessionId)
        return result
      }
  }

  public companion object {
    public fun actionActiveTransferDetailToCompletion(sessionId: String = ""): NavDirections =
        ActionActiveTransferDetailToCompletion(sessionId)
  }
}

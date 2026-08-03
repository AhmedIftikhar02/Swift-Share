package com.example.swiftshare.presentation.completion.ui

import android.os.Bundle
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.swiftshare.R
import kotlin.Int
import kotlin.String

public class CompletionFragmentDirections private constructor() {
  private data class ActionCompletionToFileQueueReview(
    public val endpointId: String = "",
  ) : NavDirections {
    public override val actionId: Int = R.id.action_completion_to_fileQueueReview

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("endpointId", this.endpointId)
        return result
      }
  }

  public companion object {
    public fun actionCompletionToDiscovery(): NavDirections =
        ActionOnlyNavDirections(R.id.action_completion_to_discovery)

    public fun actionCompletionToFileQueueReview(endpointId: String = ""): NavDirections =
        ActionCompletionToFileQueueReview(endpointId)
  }
}

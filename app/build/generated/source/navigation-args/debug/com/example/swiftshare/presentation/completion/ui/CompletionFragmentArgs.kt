package com.example.swiftshare.presentation.completion.ui

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class CompletionFragmentArgs(
  public val sessionId: String = "",
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("sessionId", this.sessionId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("sessionId", this.sessionId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): CompletionFragmentArgs {
      bundle.setClassLoader(CompletionFragmentArgs::class.java.classLoader)
      val __sessionId : String?
      if (bundle.containsKey("sessionId")) {
        __sessionId = bundle.getString("sessionId")
        if (__sessionId == null) {
          throw IllegalArgumentException("Argument \"sessionId\" is marked as non-null but was passed a null value.")
        }
      } else {
        __sessionId = ""
      }
      return CompletionFragmentArgs(__sessionId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): CompletionFragmentArgs {
      val __sessionId : String?
      if (savedStateHandle.contains("sessionId")) {
        __sessionId = savedStateHandle["sessionId"]
        if (__sessionId == null) {
          throw IllegalArgumentException("Argument \"sessionId\" is marked as non-null but was passed a null value")
        }
      } else {
        __sessionId = ""
      }
      return CompletionFragmentArgs(__sessionId)
    }
  }
}

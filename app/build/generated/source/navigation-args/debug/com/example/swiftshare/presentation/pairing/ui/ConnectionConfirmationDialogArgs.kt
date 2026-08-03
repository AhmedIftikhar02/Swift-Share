package com.example.swiftshare.presentation.pairing.ui

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class ConnectionConfirmationDialogArgs(
  public val endpointId: String = "",
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("endpointId", this.endpointId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("endpointId", this.endpointId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): ConnectionConfirmationDialogArgs {
      bundle.setClassLoader(ConnectionConfirmationDialogArgs::class.java.classLoader)
      val __endpointId : String?
      if (bundle.containsKey("endpointId")) {
        __endpointId = bundle.getString("endpointId")
        if (__endpointId == null) {
          throw IllegalArgumentException("Argument \"endpointId\" is marked as non-null but was passed a null value.")
        }
      } else {
        __endpointId = ""
      }
      return ConnectionConfirmationDialogArgs(__endpointId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle):
        ConnectionConfirmationDialogArgs {
      val __endpointId : String?
      if (savedStateHandle.contains("endpointId")) {
        __endpointId = savedStateHandle["endpointId"]
        if (__endpointId == null) {
          throw IllegalArgumentException("Argument \"endpointId\" is marked as non-null but was passed a null value")
        }
      } else {
        __endpointId = ""
      }
      return ConnectionConfirmationDialogArgs(__endpointId)
    }
  }
}

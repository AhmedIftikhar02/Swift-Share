package com.example.swiftshare.presentation.transferhub.ui

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class TransferHubFragmentArgs(
  public val endpointId: String = "",
  public val deviceName: String = "",
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("endpointId", this.endpointId)
    result.putString("deviceName", this.deviceName)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("endpointId", this.endpointId)
    result.set("deviceName", this.deviceName)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): TransferHubFragmentArgs {
      bundle.setClassLoader(TransferHubFragmentArgs::class.java.classLoader)
      val __endpointId : String?
      if (bundle.containsKey("endpointId")) {
        __endpointId = bundle.getString("endpointId")
        if (__endpointId == null) {
          throw IllegalArgumentException("Argument \"endpointId\" is marked as non-null but was passed a null value.")
        }
      } else {
        __endpointId = ""
      }
      val __deviceName : String?
      if (bundle.containsKey("deviceName")) {
        __deviceName = bundle.getString("deviceName")
        if (__deviceName == null) {
          throw IllegalArgumentException("Argument \"deviceName\" is marked as non-null but was passed a null value.")
        }
      } else {
        __deviceName = ""
      }
      return TransferHubFragmentArgs(__endpointId, __deviceName)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): TransferHubFragmentArgs {
      val __endpointId : String?
      if (savedStateHandle.contains("endpointId")) {
        __endpointId = savedStateHandle["endpointId"]
        if (__endpointId == null) {
          throw IllegalArgumentException("Argument \"endpointId\" is marked as non-null but was passed a null value")
        }
      } else {
        __endpointId = ""
      }
      val __deviceName : String?
      if (savedStateHandle.contains("deviceName")) {
        __deviceName = savedStateHandle["deviceName"]
        if (__deviceName == null) {
          throw IllegalArgumentException("Argument \"deviceName\" is marked as non-null but was passed a null value")
        }
      } else {
        __deviceName = ""
      }
      return TransferHubFragmentArgs(__endpointId, __deviceName)
    }
  }
}

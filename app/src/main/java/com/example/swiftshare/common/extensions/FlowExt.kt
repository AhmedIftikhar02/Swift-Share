package com.example.swiftshare.common.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Collect Flow inside Fragment lifecycle.
 *
 * Works for both normal Fragments and DialogFragments.
 */
fun <T> Flow<T>.collectLifecycleFlow(
    fragment: Fragment,
    action: suspend (T) -> Unit
) {
    fragment.lifecycleScope.launch {
        fragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@collectLifecycleFlow.collect {
                action(it)
            }
        }
    }
}

/**
 * Collect Flow inside Activity lifecycle.
 */
fun <T> Flow<T>.collectLifecycleFlowActivity(
    activity: AppCompatActivity,
    action: suspend (T) -> Unit
) {
    activity.lifecycleScope.launch {
        activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@collectLifecycleFlowActivity.collect {
                action(it)
            }
        }
    }
}

/**
 * Optional helper.
 */
fun <T> Flow<T>.flowWithFragmentLifecycle(
    fragment: Fragment
): Flow<T> =
    flowWithLifecycle(fragment.lifecycle, Lifecycle.State.STARTED)
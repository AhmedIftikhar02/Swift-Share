package com.example.swiftshare

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import com.example.swiftshare.base.BaseActivity
import com.example.swiftshare.databinding.ActivityMainBinding
import com.example.swiftshare.domain.model.ConnectionEvent
import com.example.swiftshare.domain.model.SessionStatus
import com.example.swiftshare.domain.model.TransferDirection
import com.example.swiftshare.domain.repository.NearbyRepository
import com.example.swiftshare.domain.usecase.transfer.ObserveActiveSessionUseCase
import com.example.swiftshare.permissions.PermissionManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TERMINAL_SESSION_STATUSES = setOf(
    SessionStatus.COMPLETED, SessionStatus.FAILED, SessionStatus.PARTIAL, SessionStatus.CANCELLED
)

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    @Inject lateinit var permissionManager: PermissionManager
    @Inject lateinit var nearbyRepository: NearbyRepository
    @Inject lateinit var observeActiveSessionUseCase: ObserveActiveSessionUseCase

    private val tabGraphIds = setOf(R.id.discoveryGraph, R.id.historyGraph, R.id.settingsGraph)

    // Phase 9: dedupes the auto-navigate-to-Completion trigger so it fires exactly once per
    // terminal session, but resets itself the moment a session leaves a terminal state again —
    // which is exactly what happens on Retry (status goes back to IN_PROGRESS, then terminal
    // again once the retried file finishes), so Retry correctly re-triggers navigation later.
    private var completionNavigatedSessionId: String? = null

    override fun setupViews() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val insideTabs = destination.parent?.id in tabGraphIds || destination.id in tabGraphIds
            binding.bottomNav.visibility = if (insideTabs) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    override fun observeData() {
        observeIncomingConnections()
        observeTransferNavigation()
    }

    private fun observeIncomingConnections() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                nearbyRepository.observeConnectionEvents()
                    .filterIsInstance<ConnectionEvent.ConnectionInitiated>()
                    .filter { it.isIncomingRequest }
                    .collect { event ->
                        val navController = findNavHostController()
                        if (navController.currentDestination?.id == R.id.connectionConfirmationDialog) {
                            return@collect
                        }
                        val bundle = Bundle().apply { putString("endpointId", event.endpointId) }
                        runCatching { navController.navigate(R.id.connectionConfirmationDialog, bundle) }
                    }
            }
        }
    }

    /**
     * Phase 9 (v2): navigates by destination ID directly — R.id.activeTransferDetailFragment /
     * R.id.completionFragment — the same technique observeIncomingConnections() above already
     * uses successfully for R.id.connectionConfirmationDialog. No nav_graph.xml action is
     * required for this to work from any tab/screen; NavController resolves a destination ID
     * against the whole graph, not just the current one.
     *
     * Two independent triggers, both driven off the same ObserveActiveSessionUseCase stream
     * TransferRepository already exposes:
     *
     * 1. Receiver-side: the instant a RECEIVED session goes IN_PROGRESS, pull this device onto
     *    the Active Transfer screen — regardless of which tab/screen it's currently on.
     *    Deliberately re-checked on every emission rather than a one-shot flag: if the user
     *    backs out mid-receive, the very next progress byte routes them straight back.
     *
     * 2. Both directions: the instant a session reaches a terminal status, route to Completion
     *    exactly once per "run" of that session.
     */
    private fun observeTransferNavigation() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                observeActiveSessionUseCase().collect { session ->
                    if (session == null) return@collect
                    val navController = findNavHostController()
                    val currentDestinationId = navController.currentDestination?.id
                    val isTerminal = session.status in TERMINAL_SESSION_STATUSES

                    if (session.direction == TransferDirection.RECEIVED &&
                        session.status == SessionStatus.IN_PROGRESS &&
                        currentDestinationId != R.id.activeTransferDetailFragment &&
                        currentDestinationId != R.id.completionFragment
                    ) {
                        runCatching { navController.navigate(R.id.activeTransferDetailFragment) }
                    }

                    if (!isTerminal) {
                        if (completionNavigatedSessionId == session.sessionId) {
                            completionNavigatedSessionId = null
                        }
                    } else if (completionNavigatedSessionId != session.sessionId) {
                        completionNavigatedSessionId = session.sessionId
                        val bundle = Bundle().apply { putString("sessionId", session.sessionId) }
                        runCatching { navController.navigate(R.id.completionFragment, bundle) }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!permissionManager.allCriticalPermissionsGranted()) {
            Snackbar.make(binding.root, R.string.permissions_revoked_banner, Snackbar.LENGTH_LONG)
                .setAction(R.string.permissions_fix_action) {
                    findNavHostController().navigate(R.id.permissionRationaleFragment)
                }
                .show()
        }
    }

    private fun findNavHostController() =
        (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment).navController
}
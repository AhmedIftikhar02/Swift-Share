package com.example.swiftshare

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.swiftshare.base.BaseActivity
import com.example.swiftshare.databinding.ActivityMainBinding
import com.example.swiftshare.domain.model.ConnectionEvent
import com.example.swiftshare.domain.repository.NearbyRepository
import com.example.swiftshare.permissions.PermissionManager
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Single-activity host for the whole app. Hosts one NavHostFragment for everything
 * (splash through the 3 bottom-nav tabs) — see res/navigation/nav_graph.xml for the full
 * PRD 6.1 hierarchy. The bottom nav bar is shown/hidden based on which destination is
 * currently active, since it should only be visible once inside the Discovery/History/
 * Settings tab section, not during Splash/Onboarding/Permissions/DeviceNameSetup.
 *
 * Phase 5 addition: also owns the app-wide listener for INCOMING Nearby connection requests
 * (see `observeIncomingConnections()`) — the initiating side navigates to the Confirmation
 * dialog itself from wherever it made the request, but the receiving side has no screen-local
 * trigger to do that, so it has to happen here instead.
 */
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    @Inject lateinit var permissionManager: PermissionManager
    @Inject lateinit var nearbyRepository: NearbyRepository

    private val tabGraphIds = setOf(R.id.discoveryGraph, R.id.historyGraph, R.id.settingsGraph)

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
        // Background-transfer session state (PRD 6.2 — "a transfer is in progress, exit
        // anyway?") is wired here in Phase 9 once the Foreground Service exists.
        observeIncomingConnections()
    }

    /** BUGFIX (Phase 5): navigates the RECEIVING device to the Confirmation dialog the moment
     *  an incoming connection request arrives, regardless of which screen is currently active. */
    private fun observeIncomingConnections() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                nearbyRepository.observeConnectionEvents()
                    .filterIsInstance<ConnectionEvent.ConnectionInitiated>()
                    .filter { it.isIncomingRequest }
                    .collect { event ->
                        val navController = findNavHostController()
                        // Don't stack a second dialog instance if one is already showing —
                        // e.g. a duplicate callback, or the user is already looking at it.
                        if (navController.currentDestination?.id == R.id.connectionConfirmationDialog) {
                            return@collect
                        }
                        val bundle = Bundle().apply { putString("endpointId", event.endpointId) }
                        runCatching { navController.navigate(R.id.connectionConfirmationDialog, bundle) }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // PRD 2.18 edge case: a permission revoked via system Settings while the app is
        // running must be detected on next resume. Full Discovery-screen-specific handling
        // arrives in Phase 4; for now this surfaces a generic, non-blocking notice.
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
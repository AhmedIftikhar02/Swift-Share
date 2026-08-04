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
        observeIncomingConnections()
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
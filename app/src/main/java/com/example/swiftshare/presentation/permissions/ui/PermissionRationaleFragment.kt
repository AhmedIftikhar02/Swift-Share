package com.example.swiftshare.presentation.permissions.ui


import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.common.extensions.collectLifecycleFlow
import com.example.swiftshare.databinding.PermissionsFragmentRationaleBinding
import com.example.swiftshare.permissions.AppSettingsNavigator
import com.example.swiftshare.presentation.permissions.viewmodels.PermissionRationaleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionRationaleFragment :
    BaseFragment<PermissionsFragmentRationaleBinding>(PermissionsFragmentRationaleBinding::inflate) {

    private val viewModel: PermissionRationaleViewModel by viewModels()

    /** True once at least one requested permission was denied AND the system says it
     *  won't show the rationale dialog again — i.e. "permanently denied" (PRD 12, "If Denied"). */
    private var permanentlyDenied = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permanentlyDenied = results.any { (permission, granted) ->
            !granted && !shouldShowRequestPermissionRationale(permission)
        }
        viewModel.refresh()
    }

    override fun setupViews() {
        viewModel.refresh()

        binding.btnContinue.setOnClickListener {
            val toRequest = viewModel.requiredManifestPermissions().filterNot {
                androidx.core.content.ContextCompat.checkSelfPermission(requireContext(), it) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
            }
            if (toRequest.isEmpty()) {
                findNavController().navigate(R.id.action_permissionRationale_to_deviceNameSetup)
            } else {
                permissionLauncher.launch(toRequest.toTypedArray())
            }
        }

        binding.btnOpenSettings.setOnClickListener {
            AppSettingsNavigator.openAppSettings(requireContext())
        }
    }

    override fun observeData() {
        viewModel.uiState.collectLifecycleFlow(this) { state ->
            binding.tvStatus.text = if (state.allGranted) {
                getString(R.string.permissions_all_granted)
            } else {
                getString(R.string.permissions_pending_count, state.pending.size)
            }
            binding.btnOpenSettings.visibility = if (permanentlyDenied) View.VISIBLE else View.GONE

            // Auto-advance once everything required is granted — covers both "granted via
            // the dialog just now" and "already granted on screen entry" (e.g. re-visiting
            // this screen from the MainActivity revocation banner, Section 2.3).
            if (state.allGranted) {
                findNavController().navigate(R.id.action_permissionRationale_to_deviceNameSetup)
            }
        }
    }
}
package com.example.swiftshare.presentation.permissions.viewmodels


import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.permissions.PermissionManager
import com.example.swiftshare.permissions.PermissionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PermissionRationaleViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : BaseViewModel() {

    data class UiState(
        val pending: List<PermissionType> = emptyList(),
        val allGranted: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun requiredManifestPermissions(): Array<String> =
        permissionManager.manifestPermissionsFor(PermissionType.onboardingSet())

    fun refresh() {
        val pending = permissionManager.pendingOnboardingPermissions()
        _uiState.value = UiState(pending = pending, allGranted = pending.isEmpty())
    }
}
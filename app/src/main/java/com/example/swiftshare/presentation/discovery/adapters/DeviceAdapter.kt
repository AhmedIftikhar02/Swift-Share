package com.example.swiftshare.presentation.discovery.adapters

import com.example.swiftshare.R
import com.example.swiftshare.base.BaseAdapter
import com.example.swiftshare.databinding.DiscoveryItemDeviceBinding
import com.example.swiftshare.domain.model.DeviceAvailability
import com.example.swiftshare.domain.model.DeviceModel
import com.example.swiftshare.domain.model.DeviceType

class DeviceAdapter(
    private val onDeviceClick: (DeviceModel) -> Unit
) : BaseAdapter<DeviceModel, DiscoveryItemDeviceBinding>(
    bindingInflater = DiscoveryItemDeviceBinding::inflate,
    areItemsTheSame = { old, new -> old.endpointId == new.endpointId },
    areContentsTheSame = { old, new -> old == new }
)  {
    /** BUGFIX (Phase 5) / UX hardening: reflects `DiscoveryUiState.connectingEndpointId` so the
     *  row a `requestConnection()` call is in flight for visibly shows "Connecting…" and can't
     *  be tapped a second time while the SDK call is pending — prevents a user from firing
     *  overlapping requestConnection() calls at the same endpoint by tapping repeatedly. */
    var connectingEndpointId: String? = null
        set(value) {
            if (field == value) return
            field = value
            notifyDataSetChanged()
        }

    override fun bind(binding: DiscoveryItemDeviceBinding, item: DeviceModel, position: Int) {
        binding.tvDeviceName.text = item.displayName
        binding.ivDeviceIcon.setImageResource(
            when (item.deviceType) {
                DeviceType.TABLET -> R.drawable.ic_device_tablet
                DeviceType.PHONE -> R.drawable.ic_device_phone
                DeviceType.UNKNOWN -> R.drawable.ic_device_phone
            }
        )

        val isConnectingThisRow = item.endpointId == connectingEndpointId
        val (statusText, statusColorRes, selectable) = when {
            isConnectingThisRow ->
                Triple(R.string.device_status_connecting, R.color.color_primary, false)
            item.availability == DeviceAvailability.AVAILABLE ->
                Triple(R.string.device_status_available, R.color.color_success, true)
            item.availability == DeviceAvailability.BUSY ->
                Triple(R.string.device_status_busy, R.color.color_warning, false)
            item.availability == DeviceAvailability.CONNECTING ->
                Triple(R.string.device_status_connecting, R.color.color_primary, false)
            else ->
                Triple(R.string.device_status_unavailable, R.color.color_error, false)
        }
        binding.tvStatus.setText(statusText)
        binding.tvStatus.setTextColor(binding.root.context.getColor(statusColorRes))
        binding.statusDot.backgroundTintList =
            android.content.res.ColorStateList.valueOf(binding.root.context.getColor(statusColorRes))

        // Also block taps on ANY row while a different connection request is already in
        // flight, not just the row being connected to.
        val tappable = selectable && connectingEndpointId == null
        binding.root.isEnabled = tappable
        binding.root.alpha = if (tappable) 1.0f else 0.55f
        binding.root.setOnClickListener { if (tappable) onDeviceClick(item) }
    }
}
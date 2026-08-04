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
        val tappable = selectable && connectingEndpointId == null
        binding.root.isEnabled = tappable
        binding.root.alpha = if (tappable) 1.0f else 0.55f
        binding.root.setOnClickListener { if (tappable) onDeviceClick(item) }
    }
}
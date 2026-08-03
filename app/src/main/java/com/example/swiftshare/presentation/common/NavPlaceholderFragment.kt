package com.example.swiftshare.presentation.common

import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.common.extensions.setDebouncedClickListener
import com.example.swiftshare.databinding.CommonFragmentPlaceholderBinding
import com.google.android.material.button.MaterialButton

/**
 * Phase 2 navigation-skeleton placeholder (PRD Section 5, empty-shell requirement). Every
 * leaf screen extends this with a title and its outgoing nav actions until the real UI for
 * that specific screen lands in its own phase — at which point that ONE screen stops
 * extending this class; the others keep using it until their turn comes.
 */
abstract class NavPlaceholderFragment(
    private val screenTitle: String,
    private val actions: List<PlaceholderAction> = emptyList()
) : BaseFragment<CommonFragmentPlaceholderBinding>(CommonFragmentPlaceholderBinding::inflate) {

    data class PlaceholderAction(val label: String, val onClick: NavController.() -> Unit)

    override fun setupViews() {
        binding.tvScreenTitle.text = screenTitle
        binding.buttonContainer.removeAllViews()
        actions.forEach { action ->
            MaterialButton(requireContext()).apply {
                text = action.label
                setDebouncedClickListener { action.onClick(findNavController()) }
            }.also { binding.buttonContainer.addView(it) }
        }
    }
}
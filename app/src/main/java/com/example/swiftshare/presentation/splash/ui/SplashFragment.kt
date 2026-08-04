package com.example.swiftshare.presentation.splash.ui

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.databinding.FragmentSplashBinding
import com.example.swiftshare.presentation.splash.viewmodels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MIN_SPLASH_DURATION_MS = 900L

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private val viewModel: SplashViewModel by viewModels()

    override fun setupViews() {
        binding.logoBadge.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.splash_logo_pop))
        binding.ivLogo.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.splash_logo_pop))
        binding.tvAppName.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_up))
        binding.tvTagline.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_up))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            val startTime = System.currentTimeMillis()
            val hasSeenOnboarding = viewModel.hasSeenOnboarding()
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < MIN_SPLASH_DURATION_MS) {
                delay(MIN_SPLASH_DURATION_MS - elapsed)
            }
            if (!isAdded) return@launch
            val destination = if (hasSeenOnboarding) {
                R.id.action_splash_to_discoveryGraph
            } else {
                R.id.action_splash_to_onboarding
            }
            runCatching { findNavController().navigate(destination) }
        }
    }
}
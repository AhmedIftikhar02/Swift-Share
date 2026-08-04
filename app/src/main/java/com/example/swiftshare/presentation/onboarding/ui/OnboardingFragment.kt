package com.example.swiftshare.presentation.onboarding.ui


import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.common.extensions.setDebouncedClickListener
import com.example.swiftshare.common.extensions.withPressBounce
import com.example.swiftshare.databinding.FragmentOnboardingBinding
import com.example.swiftshare.presentation.onboarding.model.OnboardingSlide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>(FragmentOnboardingBinding::inflate) {

    private val slides by lazy {
        listOf(
            OnboardingSlide(
                R.drawable.ic_illustration_offline_share,
                R.string.onboarding_title_1,
                R.string.onboarding_desc_1
            ),
            OnboardingSlide(
                R.drawable.ic_illustration_fast_transfer,
                R.string.onboarding_title_2,
                R.string.onboarding_desc_2
            ),
            OnboardingSlide(
                R.drawable.ic_illustration_secure,
                R.string.onboarding_title_3,
                R.string.onboarding_desc_3
            )
        )
    }

    private lateinit var dots: List<ImageView>

    override fun setupViews() {
        binding.viewPager.adapter = OnboardingPagerAdapter(slides)
        setupDots()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                binding.btnNext.setText(
                    if (position == slides.lastIndex) R.string.onboarding_get_started
                    else R.string.onboarding_next
                )
            }
        })

        binding.btnNext.withPressBounce()
        binding.btnNext.setDebouncedClickListener {
            val next = binding.viewPager.currentItem + 1
            if (next < slides.size) {
                binding.viewPager.currentItem = next
            } else {
                goToPermissions()
            }
        }

        binding.tvSkip.withPressBounce()
        binding.tvSkip.setDebouncedClickListener { goToPermissions() }
    }

    private fun goToPermissions() {
        runCatching { findNavController().navigate(R.id.action_onboarding_to_permissionRationale) }
    }

    private fun setupDots() {
        dots = slides.indices.map { index ->
            ImageView(requireContext()).apply {
                val size = resources.getDimensionPixelSize(R.dimen.dp_8)
                layoutParams = android.widget.LinearLayout.LayoutParams(size, size).apply {
                    marginStart = resources.getDimensionPixelSize(R.dimen.dp_4)
                    marginEnd = resources.getDimensionPixelSize(R.dimen.dp_4)
                }
                setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (index == 0) R.drawable.bg_dot_indicator_active else R.drawable.bg_dot_indicator_inactive
                    )
                )
            }
        }
        dots.forEach { binding.dotIndicatorContainer.addView(it) }
    }

    private fun updateDots(selectedIndex: Int) {
        dots.forEachIndexed { index, dot ->
            dot.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (index == selectedIndex) R.drawable.bg_dot_indicator_active else R.drawable.bg_dot_indicator_inactive
                )
            )
        }
    }

    override fun onDestroyView() {
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}
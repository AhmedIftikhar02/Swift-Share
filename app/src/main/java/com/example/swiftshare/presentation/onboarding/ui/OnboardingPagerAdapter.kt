package com.example.swiftshare.presentation.onboarding.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.swiftshare.databinding.ItemOnboardingSlideBinding
import com.example.swiftshare.presentation.onboarding.model.OnboardingSlide

class OnboardingPagerAdapter(
    private val slides: List<OnboardingSlide>
) : RecyclerView.Adapter<OnboardingPagerAdapter.SlideViewHolder>() {

    inner class SlideViewHolder(val binding: ItemOnboardingSlideBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val binding = ItemOnboardingSlideBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SlideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        val slide = slides[position]
        holder.binding.ivIllustration.setImageResource(slide.illustrationRes)
        holder.binding.tvTitle.setText(slide.titleRes)
        holder.binding.tvDescription.setText(slide.descriptionRes)
    }

    override fun getItemCount(): Int = slides.size
}
package com.example.swiftshare.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseDialog<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater) -> VB
) : DialogFragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = bindingInflater(layoutInflater)
        setupViews()
        observeData()
        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    protected abstract fun setupViews()
    protected open fun observeData() {}
}
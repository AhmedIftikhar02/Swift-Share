package com.example.swiftshare.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * Generic ViewBinding base for all Fragments. Extend like:
 *
 *   @AndroidEntryPoint
 *   class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
 *       private val viewModel: HomeViewModel by viewModels()
 *       override fun setupViews() { ... }
 *       override fun observeData() { ... }
 *   }
 *
 * Handles the #1 most common Fragment+ViewBinding bug: forgetting to null out the binding
 * in onDestroyView, which leaks the View hierarchy. We do it here once, correctly, so every
 * Fragment in the app gets it for free.
 */
abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // critical - prevents the View hierarchy leak mentioned above
    }

    protected abstract fun setupViews()
    protected open fun observeData() {}
}

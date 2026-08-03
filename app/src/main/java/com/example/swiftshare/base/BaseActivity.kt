package com.example.swiftshare.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * Generic ViewBinding base for all Activities. Extend like:
 *
 *   @AndroidEntryPoint
 *   class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
 *       override fun setupViews() { ... }
 *       override fun observeData() { ... }
 *   }
 *
 * Why a function reference (bindingInflater) instead of reflection: it's compile-time safe,
 * zero reflection overhead, and is the standard recommended pattern for generic ViewBinding
 * base classes in Kotlin.
 */
abstract class BaseActivity<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater) -> VB
) : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
        setupViews()
        observeData()
    }

    /** Set click listeners, RecyclerView adapters, toolbar config, etc. */
    protected abstract fun setupViews()

    /** Collect ViewModel StateFlow/LiveData here. Default no-op so simple screens
     *  without a ViewModel don't need to override it. */
    protected open fun observeData() {}
}

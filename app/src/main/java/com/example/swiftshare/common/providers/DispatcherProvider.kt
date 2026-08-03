package com.example.swiftshare.common.providers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Inject this instead of referencing Dispatchers.IO/Main/Default directly inside
 * Repositories/UseCases/ViewModels. Why: in unit tests you provide a fake implementation
 * that returns a TestDispatcher for everything, making coroutine code deterministic and
 * testable without real threading. Without this indirection, swapping dispatchers in tests
 * is much more awkward.
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

@Singleton
class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
}

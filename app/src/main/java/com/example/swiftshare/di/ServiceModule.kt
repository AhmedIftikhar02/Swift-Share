package com.example.swiftshare.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Reserved for dependencies scoped specifically to `TransferForegroundService` (Phase 9).
 * The service itself needs no entry here once built (it'll be `@AndroidEntryPoint`, and Hilt
 * constructs it automatically) — this module exists only for interface bindings or
 * Intentionally empty until Phase 9.
 */
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule
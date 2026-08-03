package com.example.swiftshare.presentation.splash.viewmodels

import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Proves the Phase 1 DI graph end-to-end: SettingsRepository -> SettingsRepositoryImpl ->
 * RepositoryModule -> here, all resolved by Hilt with no manual wiring. `SplashFragment`
 * (Phase 2) injects this but does not yet act on `settingsRepository` — real "route to
 * Onboarding vs. Discovery based on first launch" logic is deferred to Phase 2/3 polish
 * once Settings persistence is real (Phase 11), per the placeholder-navigation scope of Phase 2.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BaseViewModel()
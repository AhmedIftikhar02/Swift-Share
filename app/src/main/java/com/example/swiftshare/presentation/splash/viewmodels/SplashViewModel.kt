package com.example.swiftshare.presentation.splash.viewmodels

import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BaseViewModel()
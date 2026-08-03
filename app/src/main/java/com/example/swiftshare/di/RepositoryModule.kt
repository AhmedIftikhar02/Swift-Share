package com.example.swiftshare.di

import com.example.swiftshare.data.repository.HistoryRepositoryImpl
import com.example.swiftshare.data.repository.NearbyRepositoryImpl
import com.example.swiftshare.data.repository.SettingsRepositoryImpl
import com.example.swiftshare.data.repository.TransferRepositoryImpl
import com.example.swiftshare.domain.repository.HistoryRepository
import com.example.swiftshare.domain.repository.NearbyRepository
import com.example.swiftshare.domain.repository.SettingsRepository
import com.example.swiftshare.domain.repository.TransferRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNearbyRepository(impl: NearbyRepositoryImpl): NearbyRepository

    @Binds
    @Singleton
    abstract fun bindTransferRepository(impl: TransferRepositoryImpl): TransferRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
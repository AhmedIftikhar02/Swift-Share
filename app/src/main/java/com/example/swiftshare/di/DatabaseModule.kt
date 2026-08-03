package com.example.swiftshare.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Room scaffold DI. When the first @Entity/@Dao are added (Phase 7 — DeviceEntity,
 * TransferSessionEntity, FileTransferEntity per PRD Section 9), uncomment and add:
 *
 *   @Provides
 *   @Singleton
 *   fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
 *       Room.databaseBuilder(context, AppDatabase::class.java, "swiftshare_db")
 *           .build() // use a real Migration, not fallbackToDestructiveMigration — PRD 9.5
 *
 *   @Provides
 *   @Singleton
 *   fun provideTransferSessionDao(db: AppDatabase): TransferSessionDao = db.transferSessionDao()
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule
package com.example.swiftshare.di

import android.content.Context
import androidx.room.Room
import com.example.swiftshare.database.AppDatabase
import com.example.swiftshare.database.dao.TransferSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "swiftshare_db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides
    @Singleton
    fun provideTransferSessionDao(db: AppDatabase): TransferSessionDao = db.transferSessionDao()
}
package com.example.swiftshare.di

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides the Nearby Connections SDK client — the single injected object every data-layer
 * class that talks to Nearby Connections depends on (`NearbyConnectionsDataSource`, built in
 * Phase 4), exactly the same role `NetworkModule.provideRetrofit()` played in the old boilerplate.
 */
@Module
@InstallIn(SingletonComponent::class)
object NearbyModule {

    /** Shared by every advertise/discover call so both sides of a pairing recognize
     *  each other as the same app (PRD Section 10.1). */
    const val SERVICE_ID = "com.example.swiftshare.NEARBY_SERVICE"

    @Provides
    @Singleton
    fun provideConnectionsClient(@ApplicationContext context: Context): ConnectionsClient =
        Nearby.getConnectionsClient(context)
}
package com.example.swiftshare.data.repository;

import android.content.Context;
import com.example.swiftshare.common.providers.DispatcherProvider;
import com.example.swiftshare.data.nearby.NearbyConnectionsDataSource;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class NearbyRepositoryImpl_Factory implements Factory<NearbyRepositoryImpl> {
  private final Provider<NearbyConnectionsDataSource> dataSourceProvider;

  private final Provider<DispatcherProvider> dispatcherProvider;

  private final Provider<Context> contextProvider;

  public NearbyRepositoryImpl_Factory(Provider<NearbyConnectionsDataSource> dataSourceProvider,
      Provider<DispatcherProvider> dispatcherProvider, Provider<Context> contextProvider) {
    this.dataSourceProvider = dataSourceProvider;
    this.dispatcherProvider = dispatcherProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public NearbyRepositoryImpl get() {
    return newInstance(dataSourceProvider.get(), dispatcherProvider.get(), contextProvider.get());
  }

  public static NearbyRepositoryImpl_Factory create(
      Provider<NearbyConnectionsDataSource> dataSourceProvider,
      Provider<DispatcherProvider> dispatcherProvider, Provider<Context> contextProvider) {
    return new NearbyRepositoryImpl_Factory(dataSourceProvider, dispatcherProvider, contextProvider);
  }

  public static NearbyRepositoryImpl newInstance(NearbyConnectionsDataSource dataSource,
      DispatcherProvider dispatcherProvider, Context context) {
    return new NearbyRepositoryImpl(dataSource, dispatcherProvider, context);
  }
}

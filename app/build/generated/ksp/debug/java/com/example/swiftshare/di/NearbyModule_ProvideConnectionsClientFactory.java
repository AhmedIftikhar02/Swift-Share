package com.example.swiftshare.di;

import android.content.Context;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class NearbyModule_ProvideConnectionsClientFactory implements Factory<ConnectionsClient> {
  private final Provider<Context> contextProvider;

  public NearbyModule_ProvideConnectionsClientFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ConnectionsClient get() {
    return provideConnectionsClient(contextProvider.get());
  }

  public static NearbyModule_ProvideConnectionsClientFactory create(
      Provider<Context> contextProvider) {
    return new NearbyModule_ProvideConnectionsClientFactory(contextProvider);
  }

  public static ConnectionsClient provideConnectionsClient(Context context) {
    return Preconditions.checkNotNullFromProvides(NearbyModule.INSTANCE.provideConnectionsClient(context));
  }
}

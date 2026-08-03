package com.example.swiftshare.data.nearby;

import com.google.android.gms.nearby.connection.ConnectionsClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class NearbyConnectionsDataSource_Factory implements Factory<NearbyConnectionsDataSource> {
  private final Provider<ConnectionsClient> connectionsClientProvider;

  public NearbyConnectionsDataSource_Factory(
      Provider<ConnectionsClient> connectionsClientProvider) {
    this.connectionsClientProvider = connectionsClientProvider;
  }

  @Override
  public NearbyConnectionsDataSource get() {
    return newInstance(connectionsClientProvider.get());
  }

  public static NearbyConnectionsDataSource_Factory create(
      Provider<ConnectionsClient> connectionsClientProvider) {
    return new NearbyConnectionsDataSource_Factory(connectionsClientProvider);
  }

  public static NearbyConnectionsDataSource newInstance(ConnectionsClient connectionsClient) {
    return new NearbyConnectionsDataSource(connectionsClient);
  }
}

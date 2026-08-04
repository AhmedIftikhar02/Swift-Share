package com.example.swiftshare.data.transfer;

import android.content.ContentResolver;
import com.example.swiftshare.common.providers.DispatcherProvider;
import com.example.swiftshare.data.nearby.NearbyConnectionsDataSource;
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
public final class FileTransferDataSource_Factory implements Factory<FileTransferDataSource> {
  private final Provider<NearbyConnectionsDataSource> nearbyDataSourceProvider;

  private final Provider<ContentResolver> contentResolverProvider;

  private final Provider<ReceivedFileSaver> receivedFileSaverProvider;

  private final Provider<DispatcherProvider> dispatcherProvider;

  public FileTransferDataSource_Factory(
      Provider<NearbyConnectionsDataSource> nearbyDataSourceProvider,
      Provider<ContentResolver> contentResolverProvider,
      Provider<ReceivedFileSaver> receivedFileSaverProvider,
      Provider<DispatcherProvider> dispatcherProvider) {
    this.nearbyDataSourceProvider = nearbyDataSourceProvider;
    this.contentResolverProvider = contentResolverProvider;
    this.receivedFileSaverProvider = receivedFileSaverProvider;
    this.dispatcherProvider = dispatcherProvider;
  }

  @Override
  public FileTransferDataSource get() {
    return newInstance(nearbyDataSourceProvider.get(), contentResolverProvider.get(), receivedFileSaverProvider.get(), dispatcherProvider.get());
  }

  public static FileTransferDataSource_Factory create(
      Provider<NearbyConnectionsDataSource> nearbyDataSourceProvider,
      Provider<ContentResolver> contentResolverProvider,
      Provider<ReceivedFileSaver> receivedFileSaverProvider,
      Provider<DispatcherProvider> dispatcherProvider) {
    return new FileTransferDataSource_Factory(nearbyDataSourceProvider, contentResolverProvider, receivedFileSaverProvider, dispatcherProvider);
  }

  public static FileTransferDataSource newInstance(NearbyConnectionsDataSource nearbyDataSource,
      ContentResolver contentResolver, ReceivedFileSaver receivedFileSaver,
      DispatcherProvider dispatcherProvider) {
    return new FileTransferDataSource(nearbyDataSource, contentResolver, receivedFileSaver, dispatcherProvider);
  }
}
